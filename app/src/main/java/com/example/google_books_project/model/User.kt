package com.example.google_books_project.model
// Declaração do pacote onde o modelo `User` é definido.

data class User (
        private val email: String,
        // Campo privado que armazena o email do usuário.
        private val password: String
        // Campo privado que armazena a senha do usuário.
) {
        fun getEmail(): String {
                return email
                // Método público para acessar o valor do email.
        }

        fun getPassword(): String {
                return password
                // Método público para acessar o valor da senha.
        }
        // A classe contém getters explícitos para os campos privados.
}
