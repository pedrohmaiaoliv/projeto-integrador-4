package com.example.google_books_project.model

data class User (
        private val email: String,
        private val password: String
) {
        fun getEmail(): String {
                return email
        }

        fun getPassword(): String {
                return password
        }
}