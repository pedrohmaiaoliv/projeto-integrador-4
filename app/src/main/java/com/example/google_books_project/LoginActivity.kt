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

// Activity responsável pela tela de login.
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding // Referência ao binding da activity
    private lateinit var auth: FirebaseAuth // Instância do FirebaseAuth para autenticação

    // Método chamado ao criar a Activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Permite que a UI ocupe toda a tela, sem margens
        binding = ActivityLoginBinding.inflate(layoutInflater) // Inicializa o binding
        setContentView(binding.root) // Configura a view com o binding

        // Inicializa o FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Aplica o listener para ajustar as margens com base nas barras do sistema (status bar, nav bar).
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // Obtém as margens das barras do sistema
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // Ajusta o padding
            insets
        }

        // Configura a interface e os listeners de clique.
        setUpView()
    }

    // Configura as interações de UI.
    private fun setUpView() {
        // Configura o clique do botão de login.
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString() // Obtém o email inserido
            val password = binding.passwordInput.text.toString() // Obtém a senha inserida

            // Verifica se os campos de email e senha não estão vazios
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tenta autenticar o usuário com Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task -> // Ação após o login ser completado
                    if (task.isSuccessful) { // Se o login for bem-sucedido
                        val userId = auth.currentUser?.uid // Obtém o ID do usuário autenticado
                        if (userId != null) { // Se o ID do usuário for válido
                            createUserDocumentIfNotExists(userId) // Cria o documento do usuário no Firestore caso não exista
                        }
                        val intent = Intent(this, MainActivity::class.java) // Cria a intenção para a tela principal
                        startActivity(intent) // Inicia a MainActivity
                        finish() // Finaliza a LoginActivity
                    } else { // Caso o login falhe
                        Toast.makeText(this, "Erro ao fazer login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Configura o clique do botão de registro (para nova conta)
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java) // Cria a intenção para a tela de cadastro
            startActivity(intent) // Inicia a SingUpActivity
        }
    }

    // Método para criar o documento do usuário no Firestore se não existir.
    private fun createUserDocumentIfNotExists(userId: String) {
        val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId) // Referência ao documento do usuário no Firestore

        // Tenta obter o documento do usuário
        userDocument.get().addOnSuccessListener { document ->
            if (!document.exists()) { // Se o documento não existir
                val initialData = mapOf( // Dados iniciais do usuário
                    "favoriteBooks" to emptyList<Map<String, String>>() // Lista vazia de livros favoritos
                )
                userDocument.set(initialData) // Cria o documento com os dados iniciais
            }
        }.addOnFailureListener { e -> // Se houver erro ao obter o documento
            Toast.makeText(this, "Erro ao criar documento do usuário: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
