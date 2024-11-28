package com.example.google_books_project.model

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val categories: List<String>?, // Adicione esta propriedade se ainda não existir
    val publishedDate: String?,
    val pageCount: Int?,
    val averageRating: Float?,
    val imageLinks: ImageLinks?
) {

}