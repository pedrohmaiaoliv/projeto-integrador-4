package com.example.google_books_project.util

import com.example.google_books_project.model.BooksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApiService {
    // Método para buscar livros com base em uma consulta geral
    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String, // Termo de busca (título, autor, etc.)
        @Query("key") apiKey: String // Chave de API
    ): Call<BooksResponse>

    // Método para buscar livros por categoria específica
    @GET("volumes")
    fun searchBooksByCategory(
        @Query("q") category: String, // Categoria dos livros
        @Query("filter") filter: String = "ebooks", // Filtra apenas eBooks
        @Query("key") apiKey: String // Chave de API
    ): Call<BooksResponse>


}
