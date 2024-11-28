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

class SingUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sing_up)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
            val favoriteGenre = findViewById<EditText>(R.id.favoriteGenreInput).text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || favoriteGenre.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val user = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "favoriteGenre" to favoriteGenre,
                            "favoriteBooks" to emptyList<Map<String, String>>()
                        )

                        if (userId != null) {
                            firestore.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Erro ao registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
