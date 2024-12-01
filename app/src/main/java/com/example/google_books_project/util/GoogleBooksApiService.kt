package com.example.google_books_project.util

import com.example.google_books_project.model.BooksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Interface para definir as chamadas da API do Google Books usando Retrofit
interface GoogleBooksApiService {

    // Método para realizar uma busca de livros
    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String, // Parâmetro de consulta para a busca (ex.: título ou autor)
        @Query("key") apiKey: String // Parâmetro que especifica a chave de API para autenticação
    ): Call<BooksResponse>

    // Método para buscar livros por categoria
    @GET("volumes")
    fun searchBooksByCategory(
        @Query("q") category: String, // Parâmetro para a categoria
        @Query("filter") filter: String = "ebooks", // Filtra apenas livros digitais
        @Query("key") apiKey: String // Chave de API para autenticação
    ): Call<BooksResponse>

    // Método para obter detalhes de um livro específico pelo ID
    @GET("volumes")
    fun getBookDetails(
        @Query("id") bookId: String, // ID único do livro
        @Query("key") apiKey: String // Chave de API para autenticação
    ): Call<BooksResponse>
}
