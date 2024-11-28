package com.example.google_books_project.util

import com.example.google_books_project.model.BooksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String // Adiciona a chave de API como parâmetro
    ): Call<BooksResponse>
}
