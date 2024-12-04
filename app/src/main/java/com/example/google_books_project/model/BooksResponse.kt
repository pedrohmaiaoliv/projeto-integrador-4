package com.example.google_books_project.model
// Declaração do pacote onde o modelo é definido.

import com.example.google_books_project.model.BookItem
// Importação da classe `BookItem`, que será usada como elemento da lista na resposta.

data class BooksResponse(
    val items: List<BookItem>?
    // Lista de objetos do tipo `BookItem` que representa os livros retornados na resposta.
    // O operador `?` indica que a lista pode ser nula.
)
// Define uma classe de dados (`data class`) chamada `BooksResponse`, usada para modelar a resposta da API.
