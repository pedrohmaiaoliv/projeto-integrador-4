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

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var profileImageView: ImageView

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Inicializa Firebase Auth e Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa o ImageView de perfil
        profileImageView = findViewById(R.id.profile_image)

        // Carrega a imagem de perfil ao abrir a atividade
        loadProfileImage()

        // Configura clique no ImageView para alterar a imagem
        profileImageView.setOnClickListener {
            openImagePicker()
        }

        // Botão Voltar
        val backButton = findViewById<ImageButton>(R.id.imageButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botões da Barra Inferior
        setupBottomBarButtons()

        // Botões de ação
        val alterarDadosButton = findViewById<Button>(R.id.alterarDadosButton)
        alterarDadosButton.setOnClickListener {
            val nameInput = findViewById<EditText>(R.id.nameInput).text.toString()
            val genreInput = findViewById<EditText>(R.id.alterarGeneroInput).text.toString()
            val passwordInput = findViewById<EditText>(R.id.emailInput).text.toString()

            if (nameInput.isNotEmpty() || genreInput.isNotEmpty() || passwordInput.isNotEmpty()) {
                if (nameInput.isNotEmpty() || genreInput.isNotEmpty()) {
                    updateUserProfile(nameInput, genreInput)
                }

                if (passwordInput.isNotEmpty()) {
                    updateUserPassword(passwordInput)
                }
            } else {
                Toast.makeText(this, "Preencha pelo menos um campo para atualizar.", Toast.LENGTH_SHORT).show()
            }
        }

        val excluirDadosButton = findViewById<Button>(R.id.excluirDadosButton)
        excluirDadosButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Botão de Logout
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Desloga o usuário
            Toast.makeText(this, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Fecha a tela de configurações
        }
    }

    private fun setupBottomBarButtons() {
        // Botão de Favoritos
        val favoritosButton = findViewById<ImageButton>(R.id.favoritos)
        favoritosButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botão de Home
        val homeButton = findViewById<ImageButton>(R.id.home)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

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

    private fun showDeleteConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Confirmação")
            .setMessage("Tem certeza que deseja excluir sua conta? Essa ação é irreversível.")
            .setPositiveButton("Excluir") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

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

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri)
            saveImageToFirestore()
        }
    }

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

    private fun loadProfileImage() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val imageUri = document.getString("profileImageUri")
                    if (!imageUri.isNullOrEmpty()) {
                        profileImageView.setImageURI(Uri.parse(imageUri))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar imagem de perfil.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
