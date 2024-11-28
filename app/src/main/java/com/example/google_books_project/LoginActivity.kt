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

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Aplicar o listener à View raiz
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpView()
    }

    private fun setUpView() {
        // Login com Firebase Authentication
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            createUserDocumentIfNotExists(userId)
                        }
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Erro ao fazer login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Redirecionar para a SingUpActivity
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUserDocumentIfNotExists(userId: String) {
        val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId)

        userDocument.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val initialData = mapOf(
                    "favoriteBooks" to emptyList<Map<String, String>>()
                )
                userDocument.set(initialData)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Erro ao criar documento do usuário: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
