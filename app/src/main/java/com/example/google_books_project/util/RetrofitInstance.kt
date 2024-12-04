package com.example.google_books_project.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto singleton que gerencia a instância do Retrofit.
object RetrofitInstance {

    // A constante `BASE_URL` define a URL base para as requisições à API do Google Books.
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    // A propriedade `api` é a instância da interface `GoogleBooksApiService`.
    // A instância do Retrofit é criada de forma preguiçosa (lazy) quando a propriedade for acessada pela primeira vez.
    val api: GoogleBooksApiService by lazy {

        // Constrói a instância do Retrofit com a URL base e o conversor Gson para converter os dados JSON.
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Define a URL base da API.
            .addConverterFactory(GsonConverterFactory.create()) // Adiciona o conversor Gson para manipulação de dados JSON.
            .build() // Constrói a instância do Retrofit.
            .create(GoogleBooksApiService::class.java) // Cria a implementação da interface `GoogleBooksApiService`.
    }
}
