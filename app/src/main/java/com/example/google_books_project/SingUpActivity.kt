package com.example.google_books_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Activity responsável pelo cadastro de novos usuários
class SingUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth // Instância do Firebase Authentication
    private lateinit var firestore: FirebaseFirestore // Instância do Firestore para salvar dados do usuário

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ativa suporte para layouts sem bordas
        setContentView(R.layout.activity_sing_up)

        // Inicializa Firebase Auth e Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Botão para voltar à tela de login
        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) // Navega para a tela de login
        }

        // Botão para registrar um novo usuário
        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.nameInput).text.toString() // Nome do usuário
            val email = findViewById<EditText>(R.id.emailInput).text.toString() // Email do usuário
            val password = findViewById<EditText>(R.id.passwordInput).text.toString() // Senha do usuário
            val favoriteGenre = findViewById<EditText>(R.id.favoriteGenreInput).text.toString() // Gênero favorito do usuário

            // Verifica se todos os campos foram preenchidos
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || favoriteGenre.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cria uma nova conta com email e senha
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid // Obtém o ID do usuário recém-criado
                        val user = hashMapOf(
                            "name" to name, // Nome do usuário
                            "email" to email, // Email do usuário
                            "favoriteGenre" to favoriteGenre, // Gênero favorito
                            "favoriteBooks" to emptyList<Map<String, String>>() // Lista inicial de livros favoritos (vazia)
                        )

                        // Salva os dados do usuário no Firestore
                        if (userId != null) {
                            firestore.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener {
                                    // Redireciona para a tela principal após o cadastro
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish() // Fecha a tela de cadastro
                                }
                        }
                    } else {
                        // Exibe mensagem de erro caso o cadastro falhe
                        Toast.makeText(this, "Erro ao registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Ajusta o layout para considerar as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
