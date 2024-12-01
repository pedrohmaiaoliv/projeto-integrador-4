package com.example.google_books_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

// Activity que exibe os detalhes de um livro selecionado
class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // Referências aos elementos da interface do usuário (UI)
        val bookCoverImage: ImageView = findViewById(R.id.book_cover_image) // Imagem da capa do livro
        val bookTitle: TextView = findViewById(R.id.book_title) // Título do livro
        val bookAuthor: TextView = findViewById(R.id.book_author) // Autor do livro
        val bookRating: TextView = findViewById(R.id.book_rating) // Avaliação do livro
        val bookPublishDate: TextView = findViewById(R.id.book_publish_date) // Data de publicação do livro
        val bookPageCount: TextView = findViewById(R.id.book_page_count) // Quantidade de páginas do livro
        val bookSynopsis: TextView = findViewById(R.id.book_synopsis) // Sinopse ou descrição do livro

        // Botões de navegação na parte inferior da tela
        val homeButton: ImageButton = findViewById(R.id.home) // Botão para ir para a página inicial
        val favoritosButton: ImageButton = findViewById(R.id.favoritos) // Botão para acessar os favoritos
        val configuracoesButton: ImageButton = findViewById(R.id.configuracoes) // Botão para abrir as configurações

        // Configurações dos botões para navegação entre telas
        homeButton.setOnClickListener {
            val homeIntent = Intent(this, MainActivity::class.java) // Intent para abrir a tela principal
            startActivity(homeIntent)
        }

        favoritosButton.setOnClickListener {
            val favoritosIntent = Intent(this, FavoritesActivity::class.java) // Intent para abrir os favoritos
            startActivity(favoritosIntent)
        }

        configuracoesButton.setOnClickListener {
            val configuracoesIntent = Intent(this, SettingsActivity::class.java) // Intent para abrir as configurações
            startActivity(configuracoesIntent)
        }

        // Recebendo os dados do livro enviados pela Intent
        val title = intent.getStringExtra("title") ?: "Título não disponível" // Título do livro ou padrão
        val author = intent.getStringExtra("author") ?: "Autor desconhecido" // Autor do livro ou padrão
        val coverUrl = intent.getStringExtra("coverUrl")?.replace("http://", "https://") // URL da imagem (convertida para HTTPS)
        val publishDate = intent.getStringExtra("publishDate") ?: "Data não disponível" // Data de publicação ou padrão
        val pageCount = intent.getIntExtra("pageCount", 0) // Número de páginas ou padrão (0)
        val rating = intent.getFloatExtra("rating", 0f) // Avaliação ou padrão (0.0)
        val description = intent.getStringExtra("description") ?: "Descrição indisponível" // Descrição ou padrão

        // Configuração dos elementos da UI com os dados recebidos
        bookTitle.text = title
        bookAuthor.text = author
        bookPublishDate.text = publishDate
        bookPageCount.text = "$pageCount páginas"
        bookRating.text = "⭐ $rating"
        bookSynopsis.text = description

        // Depuração: registra a URL da imagem recebida
        Log.d("BookDetailActivity", "URL da imagem recebida: $coverUrl")

        // Carregando a imagem da capa do livro usando Picasso
        if (!coverUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(coverUrl) // URL da imagem
                .into(bookCoverImage, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        Log.d("BookDetailActivity", "Imagem carregada com sucesso!") // Log de sucesso
                    }

                    override fun onError(e: Exception?) {
                        Log.e("BookDetailActivity", "Erro ao carregar imagem", e) // Log de erro
                    }
                })
        } else {
            Log.w("BookDetailActivity", "URL da imagem é nula ou vazia") // Log de aviso se a URL for inválida
            bookCoverImage.setImageDrawable(null) // Deixa a ImageView vazia caso a URL seja nula
        }
    }
}
