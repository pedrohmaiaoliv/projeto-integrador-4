package com.example.google_books_project.model

import com.example.google_books_project.model.BookItem

data class BooksResponse(
    val items: List<BookItem>?
)
