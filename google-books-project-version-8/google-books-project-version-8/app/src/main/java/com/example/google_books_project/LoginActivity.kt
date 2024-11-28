package com.example.google_books_project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.ui.semantics.text
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.google_books_project.databinding.ActivityLoginBinding
import com.example.google_books_project.model.User

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aplicar o listener à View raiz (binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpView()
    }

    private fun setUpView() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            // Verifica se o email e a senha estão cadastrados
            val isUserRegistered = registeredUsers.any { it.getEmail() == email && it.getPassword() == password }

            if (isUserRegistered) {
                // Inicia a MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Exibe uma mensagem de erro
                Toast.makeText(this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        }
        // Adicione este bloco de código para o botão "Registrar"
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }
    }
    val registeredUsers = listOf(
        User("usuario1@email.com", "senha123"),
        User("usuario2@email.com", "senha456")
    )
}