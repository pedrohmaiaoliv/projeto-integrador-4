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

// Adapter para exibir a lista de livros favoritos em um RecyclerView
class FavoriteBookAdapter(
    private val favoriteBooks: MutableList<BookItem>, // Lista mutável de livros favoritos
    private val onUnfavoriteClick: (BookItem) -> Unit, // Callback para cliques no botão de desfavoritar
    private val onBookClick: (BookItem) -> Unit // Callback para cliques em um livro
) : RecyclerView.Adapter<FavoriteBookAdapter.FavoriteBookViewHolder>() {

    // Cria e infla o layout do item para o ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteBookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_book, parent, false)
        return FavoriteBookViewHolder(view)
    }

    // Liga os dados do livro ao ViewHolder na posição atual
    override fun onBindViewHolder(holder: FavoriteBookViewHolder, position: Int) {
        val book = favoriteBooks[position]
        holder.bind(book)
    }

    // Retorna o número total de livros favoritos na lista
    override fun getItemCount(): Int = favoriteBooks.size

    // ViewHolder que representa cada item da lista de livros favoritos
    inner class FavoriteBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover) // Imagem da capa do livro
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle) // Título do livro
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor) // Autor do livro
        private val unfavoriteButton: ImageButton = itemView.findViewById(R.id.unfavoriteButton) // Botão de desfavoritar

        // Liga os dados do livro às views do layout
        fun bind(book: BookItem) {
            bookTitle.text = book.volumeInfo.title ?: "Título não disponível" // Define o título ou texto padrão
            bookAuthor.text = book.volumeInfo.authors?.joinToString(", ") ?: "Autor desconhecido" // Define os autores ou texto padrão

            // Carrega a imagem da capa do livro, caso exista
            val imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .into(bookCover) // Carrega a imagem na ImageView
            }

            // Configura o clique no botão de desfavoritar
            unfavoriteButton.setOnClickListener {
                onUnfavoriteClick(book) // Chama o callback ao desfavoritar
            }

            // Configura o clique na imagem do livro
            bookCover.setOnClickListener {
                onBookClick(book) // Chama o callback ao clicar na capa do livro
            }
        }
    }
}
