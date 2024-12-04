package com.example.google_books_project.model
// Declaração do pacote onde o modelo é definido.

data class ImageLinks(
    val thumbnail: String?
    // URL da miniatura da capa do livro. O operador `?` indica que o valor pode ser nulo.
)
// Define uma classe de dados (`data class`) chamada `ImageLinks`, usada para modelar os links das imagens associadas a um livro.
