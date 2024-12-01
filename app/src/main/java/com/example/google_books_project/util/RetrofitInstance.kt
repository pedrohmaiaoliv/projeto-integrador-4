package com.example.google_books_project.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto singleton para gerenciar a instância do Retrofit
object RetrofitInstance {

    // URL base da API do Google Books
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    // Propriedade para acessar a interface GoogleBooksApiService
    val api: GoogleBooksApiService by lazy {
        // Configuração do Retrofit
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Define a URL base para as requisições
            .addConverterFactory(GsonConverterFactory.create()) // Adiciona o conversor Gson para processar JSON
            .build() // Cria a instância do Retrofit
            .create(GoogleBooksApiService::class.java) // Cria a implementação da interface GoogleBooksApiService
    }
}
