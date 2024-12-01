package com.example.google_books_project

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Activity responsável por gerenciar as configurações do usuário, como alterar dados, imagem de perfil, senha e logout.
class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth // Instância do Firebase Authentication
    private lateinit var firestore: FirebaseFirestore // Instância do Firestore para salvar dados do usuário
    private lateinit var profileImageView: ImageView // Imagem de perfil do usuário

    private val PICK_IMAGE_REQUEST = 1 // Código de solicitação para selecionar imagem
    private var selectedImageUri: Uri? = null // Armazena o URI da imagem selecionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ativa suporte para layouts sem bordas
        setContentView(R.layout.activity_settings)

        // Inicializa Firebase Auth e Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Ajusta o layout para considerar as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa o ImageView de perfil
        profileImageView = findViewById(R.id.profile_image)

        // Carrega a imagem de perfil do Firestore
        loadProfileImage()

        // Abre o seletor de imagens ao clicar na imagem de perfil
        profileImageView.setOnClickListener {
            openImagePicker()
        }

        // Botão para voltar à MainActivity
        val backButton = findViewById<ImageButton>(R.id.imageButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a SettingsActivity
        }

        // Configura botões da barra inferior (Home e Favoritos)
        setupBottomBarButtons()

        // Botão para alterar dados do perfil
        val alterarDadosButton = findViewById<Button>(R.id.alterarDadosButton)
        alterarDadosButton.setOnClickListener {
            val nameInput = findViewById<EditText>(R.id.nameInput).text.toString()
            val genreInput = findViewById<EditText>(R.id.alterarGeneroInput).text.toString()
            val passwordInput = findViewById<EditText>(R.id.emailInput).text.toString()

            if (nameInput.isNotEmpty() || genreInput.isNotEmpty() || passwordInput.isNotEmpty()) {
                if (nameInput.isNotEmpty() || genreInput.isNotEmpty()) {
                    updateUserProfile(nameInput, genreInput) // Atualiza nome ou gênero favorito
                }
                if (passwordInput.isNotEmpty()) {
                    updateUserPassword(passwordInput) // Atualiza a senha do usuário
                }
            } else {
                Toast.makeText(this, "Preencha pelo menos um campo para atualizar.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão para excluir a conta do usuário
        val excluirDadosButton = findViewById<Button>(R.id.excluirDadosButton)
        excluirDadosButton.setOnClickListener {
            showDeleteConfirmationDialog() // Exibe o diálogo de confirmação antes de excluir a conta
        }

        // Botão para logout
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Realiza logout
            Toast.makeText(this, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Fecha a SettingsActivity
        }
    }

    // Configura os botões da barra inferior (Home e Favoritos)
    private fun setupBottomBarButtons() {
        val favoritosButton = findViewById<ImageButton>(R.id.favoritos)
        favoritosButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
            finish() // Fecha a SettingsActivity
        }

        val homeButton = findViewById<ImageButton>(R.id.home)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a SettingsActivity
        }
    }

    // Atualiza nome e gênero favorito do usuário no Firestore
    private fun updateUserProfile(name: String, genre: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val updates = mutableMapOf<String, Any>()
            if (name.isNotEmpty()) updates["name"] = name
            if (genre.isNotEmpty()) updates["favoriteGenre"] = genre

            firestore.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao atualizar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Atualiza a senha do usuário autenticado
    private fun updateUserPassword(newPassword: String) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao alterar senha: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Exibe diálogo de confirmação para exclusão da conta
    private fun showDeleteConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Confirmação")
            .setMessage("Tem certeza que deseja excluir sua conta? Essa ação é irreversível.")
            .setPositiveButton("Excluir") { _, _ ->
                deleteAccount() // Exclui a conta do usuário
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    // Exclui a conta do usuário e os dados no Firestore
    private fun deleteAccount() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            firestore.collection("users").document(userId)
                .delete()
                .addOnSuccessListener {
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Conta excluída com sucesso!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Erro ao excluir conta: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao excluir dados do Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Abre o seletor de imagens para alterar a imagem de perfil
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Lida com o resultado da seleção de imagem
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri) // Atualiza a imagem no ImageView
            saveImageToFirestore() // Salva a URI da imagem no Firestore
        }
    }

    // Salva a URI da imagem de perfil no Firestore
    private fun saveImageToFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null && selectedImageUri != null) {
            val updates = mapOf("profileImageUri" to selectedImageUri.toString())
            firestore.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Imagem de perfil atualizada!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Carrega a imagem de perfil do Firestore ao iniciar a Activity
    private fun loadProfileImage() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val imageUri = document.getString("profileImageUri")
                    if (!imageUri.isNullOrEmpty()) {
                        profileImageView.setImageURI(Uri.parse(imageUri)) // Define a URI no ImageView
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar imagem de perfil.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
