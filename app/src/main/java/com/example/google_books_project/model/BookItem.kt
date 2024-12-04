package com.example.google_books_project.model
// Declaração do pacote onde o modelo é definido.

data class BookItem(
    val title: String,
    // Campo que representa o título do livro.
    val author: String,
    // Campo que representa o autor do livro.
    val volumeInfo: VolumeInfo
    // Objeto que contém informações adicionais sobre o livro (representado por uma classe `VolumeInfo`).
)
// Define uma classe de dados (`data class`) chamada `BookItem`, usada para modelar os dados de um livro.
