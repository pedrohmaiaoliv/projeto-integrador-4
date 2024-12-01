package com.example.google_books_project.model

// Classe de dados representando informações detalhadas sobre um volume (livro)
data class VolumeInfo(
    val title: String?, // Título do livro; pode ser nulo
    val authors: List<String>?, // Lista de autores do livro; pode ser nula
    val description: String?, // Descrição ou resumo do livro; pode ser nula
    val categories: List<String>?, // Lista de categorias do livro; pode ser nula
    val publishedDate: String?, // Data de publicação do livro; pode ser nula
    val pageCount: Int?, // Número de páginas do livro; pode ser nulo
    val averageRating: Float?, // Avaliação média do livro; pode ser nula
    val imageLinks: ImageLinks? // Links para imagens relacionadas ao livro; pode ser nulo
)
