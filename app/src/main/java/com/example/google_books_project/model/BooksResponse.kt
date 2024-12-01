package com.example.google_books_project.model

// Classe de dados representando a resposta de uma requisição que retorna livros
data class BooksResponse(
    val items: List<BookItem>? // Lista de itens do tipo BookItem retornados na resposta; pode ser nula
)