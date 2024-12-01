package com.example.google_books_project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.google_books_project.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Activity de Login para autenticação do usuário com Firebase
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding // Usando View Binding para facilitar o acesso às views
    private lateinit var auth: FirebaseAuth // Instância do FirebaseAuth para autenticação

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ativa suporte para layouts sem bordas
        binding = ActivityLoginBinding.inflate(layoutInflater) // Inicializa o binding com o layout
        setContentView(binding.root)

        // Inicializa a instância do FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Ajusta o layout para considerar as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpView() // Configura os eventos de clique e funcionalidades
    }

    // Configuração dos eventos e lógica da interface
    private fun setUpView() {
        // Botão de login usando Firebase Authentication
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString() // Obtém o texto do campo de email
            val password = binding.passwordInput.text.toString() // Obtém o texto do campo de senha

            if (email.isEmpty() || password.isEmpty()) {
                // Exibe um aviso se os campos estiverem vazios
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Autenticação do usuário com email e senha
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid // Obtém o ID do usuário autenticado
                        if (userId != null) {
                            createUserDocumentIfNotExists(userId) // Cria o documento do usuário no Firestore, se necessário
                        }
                        // Navega para a MainActivity após login bem-sucedido
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Finaliza a LoginActivity
                    } else {
                        // Exibe mensagem de erro caso o login falhe
                        Toast.makeText(this, "Erro ao fazer login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Botão para redirecionar à SingUpActivity
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent) // Abre a tela de registro
        }
    }

    // Cria o documento do usuário no Firestore, se ainda não existir
    private fun createUserDocumentIfNotExists(userId: String) {
        val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId)

        userDocument.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // Se o documento do usuário não existir, cria um com dados iniciais
                val initialData = mapOf(
                    "favoriteBooks" to emptyList<Map<String, String>>() // Inicializa a lista de favoritos vazia
                )
                userDocument.set(initialData) // Salva o documento no Firestore
            }
        }.addOnFailureListener { e ->
            // Exibe uma mensagem de erro caso falhe ao criar o documento
            Toast.makeText(this, "Erro ao criar documento do usuário: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
