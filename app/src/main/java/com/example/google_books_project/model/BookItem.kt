package com.example.google_books_project.model

// Classe de dados representando um item de livro
data class BookItem(
    val title: String, // Título do livro
    val author: String, // Autor do livro
    val volumeInfo: VolumeInfo // Informações adicionais do livro encapsuladas na classe VolumeInfo
)
