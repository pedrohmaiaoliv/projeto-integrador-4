package com.example.google_books_project.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.google_books_project.R
import com.example.google_books_project.model.BookItem
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class BookAdapter(
    private val books: List<BookItem>,
    private val onFavoriteClick: (BookItem, Boolean) -> Unit,
    private val onItemClick: (BookItem) -> Unit // Callback para o clique no item
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
        holder.itemView.setOnClickListener { onItemClick(book) } // Usa o callback para o clique no item
    }

    override fun getItemCount(): Int = books.size

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover)
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)

        fun bind(book: BookItem) {
            bookTitle.text = book.volumeInfo.title ?: "Título Indisponível"

            // Carrega a imagem da capa usando Picasso
            val imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
            Log.d("BookAdapter", "URL da Imagem: $imageUrl")

            if (imageUrl != null) {
                Picasso.get().load(imageUrl).into(bookCover, object : Callback {
                    override fun onSuccess() {
                        Log.d("BookAdapter", "Imagem carregada com sucesso para URL: $imageUrl")
                    }

                    override fun onError(e: Exception?) {
                        Log.e("BookAdapter", "Erro ao carregar a imagem para URL: $imageUrl", e)
                    }
                })
            } else {
                bookCover.setImageDrawable(null) // Se a URL da imagem for nula, limpa a imagem
                Log.w("BookAdapter", "URL da imagem é nula para o livro: ${book.volumeInfo.title}")
            }

            // Lógica para alternar o ícone de favorito
            var isFavorited = false // Este valor pode ser recuperado de uma fonte persistente
            favoriteIcon.setImageResource(if (isFavorited) R.drawable.ic_favorite else R.drawable.ic_favorite_border)

            favoriteIcon.setOnClickListener {
                isFavorited = !isFavorited
                favoriteIcon.setImageResource(if (isFavorited) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
                onFavoriteClick(book, isFavorited) // Chama o callback com o novo estado de favorito
            }
        }
    }
}
