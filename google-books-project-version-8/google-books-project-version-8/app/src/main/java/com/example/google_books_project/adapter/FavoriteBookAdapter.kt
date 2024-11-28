package com.example.google_books_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.google_books_project.R
import com.example.google_books_project.model.BookItem
import com.squareup.picasso.Picasso

class FavoriteBookAdapter(
    private val favoriteBooks: MutableList<BookItem>,
    private val onUnfavoriteClick: (BookItem) -> Unit
) : RecyclerView.Adapter<FavoriteBookAdapter.FavoriteBookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteBookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_book, parent, false)
        return FavoriteBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteBookViewHolder, position: Int) {
        val book = favoriteBooks[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int = favoriteBooks.size

    inner class FavoriteBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover)
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        private val unfavoriteButton: ImageButton = itemView.findViewById(R.id.unfavoriteButton)

        fun bind(book: BookItem) {
            bookTitle.text = book.volumeInfo.title
            bookAuthor.text = book.volumeInfo.authors?.joinToString(", ")

            // Carrega a imagem do livro, caso exista
            val imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
            if (imageUrl != null && imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .into(bookCover)
            } else {
                bookCover.setImageDrawable(null)
            }

            unfavoriteButton.setOnClickListener {
                onUnfavoriteClick(book)
            }
        }
    }
}
