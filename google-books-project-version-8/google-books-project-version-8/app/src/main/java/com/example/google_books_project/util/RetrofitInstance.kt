package com.example.google_books_project.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    val api: GoogleBooksApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApiService::class.java)
    }
}