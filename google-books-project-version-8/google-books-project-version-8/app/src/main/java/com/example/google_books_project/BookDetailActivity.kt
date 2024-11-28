package com.example.google_books_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // Referências aos elementos da interface
        val bookCoverImage: ImageView = findViewById(R.id.book_cover_image)
        val bookTitle: TextView = findViewById(R.id.book_title)
        val bookAuthor: TextView = findViewById(R.id.book_author)
        val bookRating: TextView = findViewById(R.id.book_rating)
        val bookPublishDate: TextView = findViewById(R.id.book_publish_date)
        val bookPageCount: TextView = findViewById(R.id.book_page_count)
        val bookSynopsis: TextView = findViewById(R.id.book_synopsis)

        // Navegação dos botões
        val homeButton: ImageButton = findViewById(R.id.home)
        val favoritosButton: ImageButton = findViewById(R.id.favoritos)
        val configuracoesButton: ImageButton = findViewById(R.id.configuracoes)

        homeButton.setOnClickListener {
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
        }

        favoritosButton.setOnClickListener {
            val favoritosIntent = Intent(this, FavoritesActivity::class.java)
            startActivity(favoritosIntent)
        }

        configuracoesButton.setOnClickListener {
            val configuracoesIntent = Intent(this, SettingsActivity::class.java)
            startActivity(configuracoesIntent)
        }

        // Recebendo dados do livro através da Intent
        val title = intent.getStringExtra("title") ?: "Título não disponível"
        val author = intent.getStringExtra("author") ?: "Autor desconhecido"
        val coverUrl = intent.getStringExtra("coverUrl")?.replace("http://", "https://")
        val publishDate = intent.getStringExtra("publishDate") ?: "Data não disponível"
        val pageCount = intent.getIntExtra("pageCount", 0)
        val rating = intent.getFloatExtra("rating", 0f)
        val description = intent.getStringExtra("description") ?: "Descrição indisponível"

        // Configurando os elementos da interface com os dados do livro
        bookTitle.text = title
        bookAuthor.text = author
        bookPublishDate.text = publishDate
        bookPageCount.text = "$pageCount páginas"
        bookRating.text = "⭐ $rating"
        bookSynopsis.text = description

        // Depuração: verificar URL recebida
        Log.d("BookDetailActivity", "URL da imagem recebida: $coverUrl")

        // Carregar a imagem da capa usando Picasso
        if (!coverUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(coverUrl)
                .into(bookCoverImage, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        Log.d("BookDetailActivity", "Imagem carregada com sucesso!")
                    }

                    override fun onError(e: Exception?) {
                        Log.e("BookDetailActivity", "Erro ao carregar imagem", e)
                    }
                })
        } else {
            Log.w("BookDetailActivity", "URL da imagem é nula ou vazia")
            bookCoverImage.setImageDrawable(null) // Opcional: deixa o ImageView vazio
        }
    }
}
