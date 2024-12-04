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

    // Declaração das variáveis para autenticação e Firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ativa o modo de tela cheia
        setContentView(R.layout.activity_sing_up) // Configura o layout da atividade de cadastro

        // Inicializa as instâncias do FirebaseAuth e Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Configuração do botão de imagem para redirecionar ao LoginActivity
        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {
            // Ao clicar, o usuário é redirecionado para a tela de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Configura o botão de registro para criar uma conta de usuário
        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            // Obtém os valores dos campos de input (nome, email, senha, gênero favorito)
            val name = findViewById<EditText>(R.id.nameInput).text.toString()
            val email = findViewById<EditText>(R.id.emailInput).text.toString()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString()
            val favoriteGenre = findViewById<EditText>(R.id.favoriteGenreInput).text.toString()

            // Verifica se algum campo está vazio e exibe uma mensagem de erro
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || favoriteGenre.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cria um novo usuário com email e senha usando o Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // Verifica se o cadastro foi bem-sucedido
                    if (task.isSuccessful) {
                        // Se o registro foi bem-sucedido, obtém o ID do usuário
                        val userId = auth.currentUser?.uid
                        // Cria um mapa com as informações do usuário para salvar no Firestore
                        val user = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "favoriteGenre" to favoriteGenre,
                            "favoriteBooks" to emptyList<Map<String, String>>() // Gêneros e livros favoritos iniciais (vazio)
                        )

                        // Verifica se o ID do usuário é válido e grava os dados no Firestore
                        if (userId != null) {
                            firestore.collection("users").document(userId)
                                .set(user) // Salva as informações do usuário no Firestore
                                .addOnSuccessListener {
                                    // Se for bem-sucedido, redireciona o usuário para a tela principal (MainActivity)
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish() // Fecha a tela de cadastro
                                }
                        }
                    } else {
                        // Caso haja erro no processo de registro, exibe uma mensagem de erro
                        Toast.makeText(this, "Erro ao registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Configuração de barra de sistema para garantir que os componentes não fiquem atrás da barra do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Aplica os insets para ajustar o layout
        }
    }
}
