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

// Adapter para o RecyclerView que exibe uma lista de livros
class BookAdapter(
    private val books: List<BookItem>, // Lista de livros a ser exibida
    private val onFavoriteClick: (BookItem, Boolean) -> Unit, // Callback para cliques no ícone de favorito
    private val onItemClick: (BookItem) -> Unit // Callback para cliques em um item da lista
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    // Cria e infla o layout do item para o ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    // Liga os dados do livro ao ViewHolder para a posição atual
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
        holder.itemView.setOnClickListener { onItemClick(book) } // Configura o callback para cliques no item
    }

    // Retorna o número total de itens na lista
    override fun getItemCount(): Int = books.size

    // ViewHolder que representa cada item da lista
    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover) // Imagem da capa do livro
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle) // Título do livro
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon) // Ícone de favorito

        // Liga os dados do livro às views do layout
        fun bind(book: BookItem) {
            bookTitle.text = book.volumeInfo.title ?: "Título Indisponível" // Define o título do livro ou um texto padrão

            // Carrega a imagem da capa usando a biblioteca Picasso
            val imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://") // Garante HTTPS para a URL da imagem
            Log.d("BookAdapter", "URL da Imagem: $imageUrl")

            if (imageUrl != null) {
                Picasso.get().load(imageUrl).into(bookCover, object : Callback { // Callback para lidar com o carregamento da imagem
                    override fun onSuccess() {
                        Log.d("BookAdapter", "Imagem carregada com sucesso para URL: $imageUrl")
                    }

                    override fun onError(e: Exception?) {
                        Log.e("BookAdapter", "Erro ao carregar a imagem para URL: $imageUrl", e)
                    }
                })
            } else {
                bookCover.setImageDrawable(null) // Limpa a imagem caso a URL seja nula
                Log.w("BookAdapter", "URL da imagem é nula para o livro: ${book.volumeInfo.title}")
            }

            // Configura o comportamento do ícone de favorito
            var isFavorited = false // Estado inicial do favorito (pode ser recuperado de uma base de dados ou outra fonte)
            favoriteIcon.setImageResource(if (isFavorited) R.drawable.ic_favorite else R.drawable.ic_favorite_border)

            favoriteIcon.setOnClickListener {
                isFavorited = !isFavorited // Alterna o estado de favorito
                favoriteIcon.setImageResource(if (isFavorited) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
                onFavoriteClick(book, isFavorited) // Chama o callback para notificar a mudança no estado
            }
        }
    }
}
