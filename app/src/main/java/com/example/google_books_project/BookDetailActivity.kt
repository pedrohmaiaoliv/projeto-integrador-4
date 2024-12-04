package com.example.google_books_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

// Activity que exibe os detalhes de um livro específico.
class BookDetailActivity : AppCompatActivity() {

    // Método chamado ao criar a Activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // Referências aos elementos da interface do usuário.
        val bookCoverImage: ImageView = findViewById(R.id.book_cover_image) // Imagem da capa do livro
        val bookTitle: TextView = findViewById(R.id.book_title) // Título do livro
        val bookAuthor: TextView = findViewById(R.id.book_author) // Autor do livro
        val bookRating: TextView = findViewById(R.id.book_rating) // Avaliação do livro
        val bookPublishDate: TextView = findViewById(R.id.book_publish_date) // Data de publicação do livro
        val bookPageCount: TextView = findViewById(R.id.book_page_count) // Contagem de páginas do livro
        val bookSynopsis: TextView = findViewById(R.id.book_synopsis) // Sinopse do livro

        // Referências aos botões de navegação.
        val homeButton: ImageButton = findViewById(R.id.home) // Botão Home
        val favoritosButton: ImageButton = findViewById(R.id.favoritos) // Botão Favoritos
        val configuracoesButton: ImageButton = findViewById(R.id.configuracoes) // Botão Configurações

        // Configura os listeners de clique para os botões de navegação.
        homeButton.setOnClickListener {
            val homeIntent = Intent(this, MainActivity::class.java) // Intent para a tela principal
            startActivity(homeIntent) // Inicia a MainActivity
        }

        favoritosButton.setOnClickListener {
            val favoritosIntent = Intent(this, FavoritesActivity::class.java) // Intent para a tela de favoritos
            startActivity(favoritosIntent) // Inicia a FavoritesActivity
        }

        configuracoesButton.setOnClickListener {
            val configuracoesIntent = Intent(this, SettingsActivity::class.java) // Intent para a tela de configurações
            startActivity(configuracoesIntent) // Inicia a SettingsActivity
        }

        // Recebe os dados do livro passados pela Intent.
        val title = intent.getStringExtra("title") ?: "Título não disponível" // Título do livro
        val author = intent.getStringExtra("author") ?: "Autor desconhecido" // Autor do livro
        val coverUrl = intent.getStringExtra("coverUrl")?.replace("http://", "https://") // URL da capa do livro
        val publishDate = intent.getStringExtra("publishDate") ?: "Data não disponível" // Data de publicação
        val pageCount = intent.getIntExtra("pageCount", 0) // Número de páginas
        val rating = intent.getFloatExtra("rating", 0f) // Avaliação média
        val description = intent.getStringExtra("description") ?: "Descrição indisponível" // Descrição do livro

        // Atualiza os elementos da interface com os dados do livro.
        bookTitle.text = title
        bookAuthor.text = author
        bookPublishDate.text = publishDate
        bookPageCount.text = "$pageCount páginas"
        bookRating.text = "⭐ $rating"
        bookSynopsis.text = description

        // Depuração: Exibe a URL da imagem da capa no log.
        Log.d("BookDetailActivity", "URL da imagem recebida: $coverUrl")

        // Carrega a imagem da capa do livro usando a biblioteca Picasso.
        if (!coverUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(coverUrl) // Carrega a imagem a partir da URL
                .into(bookCoverImage, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        Log.d("BookDetailActivity", "Imagem carregada com sucesso!") // Log para sucesso
                    }

                    override fun onError(e: Exception?) {
                        Log.e("BookDetailActivity", "Erro ao carregar imagem", e) // Log para erro
                    }
                })
        } else {
            Log.w("BookDetailActivity", "URL da imagem é nula ou vazia") // Log para URL inválida
            bookCoverImage.setImageDrawable(null) // Se a URL estiver vazia, define a imagem como nula
        }
    }
}
