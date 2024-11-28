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

class SingUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sing_up)

        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.nameInput).text.toString()
            val email = findViewById<EditText>(R.id.emailInput).text.toString()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString()
            val favoriteGenre = findViewById<EditText>(R.id.favoriteGenreInput).text.toString() // Get favorite genre

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || favoriteGenre.isEmpty()) { // Validation
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("FAVORITE_GENRE", favoriteGenre) // Pass favorite genre
                startActivity(intent)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets}
    }
}