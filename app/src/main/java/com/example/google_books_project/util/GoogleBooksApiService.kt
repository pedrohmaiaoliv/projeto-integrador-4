package com.example.google_books_project.util

import com.example.google_books_project.model.BooksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Interface para definir os métodos da API do Google Books utilizando Retrofit.
interface GoogleBooksApiService {

    // Método para buscar livros com base em uma consulta geral
    @GET("volumes")
    fun searchBooks(

        // Parâmetro `query` representa o termo de busca (pode ser título, autor, etc.).
        @Query("q") query: String,

        // Parâmetro `apiKey` é a chave da API que autoriza o acesso aos dados do Google Books.
        @Query("key") apiKey: String

    ): Call<BooksResponse>

    // Método para buscar livros por categoria específica
    @GET("volumes")
    fun searchBooksByCategory(

        // Parâmetro `category` representa a categoria dos livros a ser buscada.
        @Query("q") category: String,

        // Parâmetro `filter` filtra os resultados para mostrar apenas eBooks. O valor padrão é "ebooks".
        @Query("filter") filter: String = "ebooks",

        // Parâmetro `apiKey` é a chave da API para autenticar a requisição.
        @Query("key") apiKey: String

    ): Call<BooksResponse>

}
