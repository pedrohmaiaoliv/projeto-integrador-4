package com.example.google_books_project.adapter
// Declaração do pacote onde o adaptador é definido, organizando as classes relacionadas.

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
// Importações necessárias para funcionalidade do adaptador.

class BookAdapter(
    // Lista de livros a ser exibida.
    private val books: List<BookItem>,
    // Callback para cliques no ícone de favorito.
    private val onFavoriteClick: (BookItem, Boolean) -> Unit,
    // Callback para cliques no item.
    private val onItemClick: (BookItem) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        // Método chamado para criar uma nova ViewHolder.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        // Infla o layout do item.
        return BookViewHolder(view)
        // Retorna a ViewHolder associada ao layout inflado.
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        // Método chamado para vincular dados à ViewHolder existente.
        val book = books[position]
        // Obtém o livro da posição atual.
        holder.bind(book)
        // Associa os dados do livro ao ViewHolder.
        holder.itemView.setOnClickListener { onItemClick(book) }
        // Configura o clique no item chamando o callback correspondente.
    }

    override fun getItemCount(): Int = books.size
    // Retorna o número total de itens na lista.

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Classe interna que representa cada item da lista.
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover)
        // Referência à imagem da capa do livro.
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        // Referência ao título do livro.
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
        // Referência ao ícone de favorito.

        fun bind(book: BookItem) {
            // Método para associar os dados do livro aos elementos da UI.
            bookTitle.text = book.volumeInfo.title ?: "Título Indisponível"
            // Define o título do livro, ou usa um valor padrão se indisponível.

            val imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
            // Obtém a URL da capa do livro e substitui HTTP por HTTPS para maior segurança.
            Log.d("BookAdapter", "URL da Imagem: $imageUrl")
            // Log para depuração, mostrando a URL carregada.

            if (imageUrl != null) {
                Picasso.get().load(imageUrl).into(bookCover, object : Callback {
                    override fun onSuccess() {
                        Log.d("BookAdapter", "Imagem carregada com sucesso para URL: $imageUrl")
                        // Log para indicar sucesso no carregamento da imagem.
                    }

                    override fun onError(e: Exception?) {
                        Log.e("BookAdapter", "Erro ao carregar a imagem para URL: $imageUrl", e)
                        // Log para registrar erro no carregamento da imagem.
                    }
                })
            } else {
                bookCover.setImageDrawable(null)
                // Remove a imagem se a URL for nula.
                Log.w("BookAdapter", "URL da imagem é nula para o livro: ${book.volumeInfo.title}")
                // Log de advertência indicando que a URL da imagem é nula.
            }

            var isFavorited = false
            // Inicializa o estado de favorito (pode ser recuperado de uma fonte persistente).
            favoriteIcon.setImageResource(if (isFavorited) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
            // Configura o ícone de favorito com base no estado inicial.

            favoriteIcon.setOnClickListener {
                isFavorited = !isFavorited
                // Alterna o estado de favorito.
                favoriteIcon.setImageResource(if (isFavorited) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
                // Atualiza o ícone de favorito de acordo com o novo estado.
                onFavoriteClick(book, isFavorited)
                // Chama o callback passando o estado atualizado de favorito.
            }
        }
    }
}
