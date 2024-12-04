package com.example.google_books_project.model

// A classe `VolumeInfo` é uma classe de dados que representa as informações de um volume (livro) em um projeto relacionado ao Google Books.
data class VolumeInfo(

    // A propriedade `title` é uma string opcional que armazena o título do livro.
    val title: String?,

    // A propriedade `authors` é uma lista opcional de strings, onde cada string é o nome de um autor do livro.
    val authors: List<String>?,

    // A propriedade `description` é uma string opcional que armazena a descrição do livro.
    val description: String?,

    // A propriedade `categories` é uma lista opcional de strings, onde cada string representa uma categoria do livro.
    // Este campo pode ser utilizado para classificar o livro por gênero, tema, etc.
    val categories: List<String>?,

    // A propriedade `publishedDate` é uma string opcional que representa a data de publicação do livro.
    val publishedDate: String?,

    // A propriedade `pageCount` é um número inteiro opcional que representa a quantidade de páginas do livro.
    val pageCount: Int?,

    // A propriedade `averageRating` é um número de ponto flutuante opcional que armazena a avaliação média do livro.
    val averageRating: Float?,

    // A propriedade `imageLinks` é do tipo `ImageLinks?`, que pode conter links para as imagens relacionadas ao livro, como a capa.
    val imageLinks: ImageLinks?
) {
    // O corpo da classe está vazio, pois a `data class` gera automaticamente os métodos essenciais como `toString()`, `equals()`, etc.
}
