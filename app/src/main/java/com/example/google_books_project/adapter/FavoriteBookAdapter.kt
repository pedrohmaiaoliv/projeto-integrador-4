package com.example.google_books_project.adapter
// Declaração do pacote onde o adaptador é definido.

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
// Importações necessárias para funcionalidade do adaptador.

class FavoriteBookAdapter(
    private val favoriteBooks: MutableList<BookItem>,
    // Lista mutável de livros favoritos.
    private val onUnfavoriteClick: (BookItem) -> Unit,
    // Callback para tratar cliques no botão de desfavoritar.
    private val onBookClick: (BookItem) -> Unit
    // Callback para tratar cliques na imagem do livro.
) : RecyclerView.Adapter<FavoriteBookAdapter.FavoriteBookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteBookViewHolder {
        // Método chamado para criar uma nova ViewHolder.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_book, parent, false)
        // Infla o layout do item favorito.
        return FavoriteBookViewHolder(view)
        // Retorna a ViewHolder associada ao layout inflado.
    }

    override fun onBindViewHolder(holder: FavoriteBookViewHolder, position: Int) {
        // Método chamado para vincular dados à ViewHolder existente.
        val book = favoriteBooks[position]
        // Obtém o livro da posição atual na lista.
        holder.bind(book)
        // Associa os dados do livro ao ViewHolder.
    }

    override fun getItemCount(): Int = favoriteBooks.size
    // Retorna o número total de itens na lista de favoritos.

    inner class FavoriteBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Classe interna que representa a ViewHolder para cada item da lista.
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover)
        // Referência à imagem da capa do livro.
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        // Referência ao título do livro.
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        // Referência ao autor do livro.
        private val unfavoriteButton: ImageButton = itemView.findViewById(R.id.unfavoriteButton)
        // Referência ao botão de desfavoritar.

        fun bind(book: BookItem) {
            // Método para associar os dados do livro aos elementos da UI.
            bookTitle.text = book.volumeInfo.title ?: "Título não disponível"
            // Define o título do livro ou usa um valor padrão se não estiver disponível.
            bookAuthor.text = book.volumeInfo.authors?.joinToString(", ") ?: "Autor desconhecido"
            // Define o(s) autor(es) do livro ou usa um valor padrão se não houver.

            val imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
            // Obtém a URL da capa do livro e substitui HTTP por HTTPS para maior segurança.
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .into(bookCover)
                // Carrega a imagem da URL usando Picasso, caso exista.
            }

            unfavoriteButton.setOnClickListener {
                onUnfavoriteClick(book)
                // Chama o callback para tratar o clique no botão de desfavoritar.
            }

            bookCover.setOnClickListener {
                onBookClick(book)
                // Chama o callback para tratar o clique na imagem do livro.
            }
        }
    }
}
