package com.example.google_books_project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.google_books_project.adapter.FavoriteBookAdapter
import com.example.google_books_project.model.BookItem
import com.example.google_books_project.model.ImageLinks
import com.example.google_books_project.model.VolumeInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

// Activity responsável por exibir os livros favoritos do usuário
class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerViewFavorites: RecyclerView // RecyclerView para exibir a lista de favoritos
    private lateinit var favoriteBookAdapter: FavoriteBookAdapter // Adapter para gerenciar os dados do RecyclerView
    private var favoriteBooks: MutableList<BookItem> = mutableListOf() // Lista mutável para armazenar os livros favoritos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ativa o suporte para bordas sem margens no layout
        setContentView(R.layout.activity_favorites)

        // Ajusta as margens do layout para considerar as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialização do RecyclerView e do adapter
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites)
        recyclerViewFavorites.layoutManager = LinearLayoutManager(this) // Define layout linear
        favoriteBookAdapter = FavoriteBookAdapter(
            favoriteBooks,
            onUnfavoriteClick = { book -> removeFavoriteBook(book) }, // Callback para desfavoritar
            onBookClick = { book -> openBookDetail(book) } // Callback para abrir detalhes do livro
        )
        recyclerViewFavorites.adapter = favoriteBookAdapter

        // Configuração dos botões de navegação e botão de voltar
        setupNavigationMenu()
        setupBackButton()

        // Carrega os livros favoritos do Firestore
        loadFavoriteBooks()
    }

    // Configura o botão de voltar para a MainActivity
    private fun setupBackButton() {
        val backButton = findViewById<ImageButton>(R.id.imageButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a FavoritesActivity
        }
    }

    // Configura os botões do menu de navegação
    private fun setupNavigationMenu() {
        val homeButton = findViewById<ImageButton>(R.id.home)
        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Fecha a FavoritesActivity
        }

        val settingsButton = findViewById<ImageButton>(R.id.configuracoes)
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish() // Fecha a FavoritesActivity
        }

        val favoritosButton = findViewById<ImageButton>(R.id.favoritos)
        favoritosButton.setOnClickListener {
            // Nenhuma ação necessária, já está na tela de favoritos
        }
    }

    // Carrega os livros favoritos do Firestore para o RecyclerView
    private fun loadFavoriteBooks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtém o ID do usuário autenticado
        val firestore = FirebaseFirestore.getInstance()

        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val books = document.get("favoriteBooks") as? List<Map<String, Any>> // Obtém os livros favoritos
                        books?.let {
                            favoriteBooks.clear()
                            favoriteBooks.addAll(it.map { bookMap -> // Mapeia os dados recebidos para BookItem
                                BookItem(
                                    title = bookMap["title"] as? String ?: "Título não disponível",
                                    author = bookMap["author"] as? String ?: "Autor desconhecido",
                                    volumeInfo = VolumeInfo(
                                        title = bookMap["title"] as? String,
                                        authors = listOf(
                                            bookMap["author"] as? String ?: "Autor desconhecido"
                                        ),
                                        description = bookMap["description"] as? String,
                                        categories = emptyList(),
                                        publishedDate = null,
                                        pageCount = null,
                                        averageRating = null,
                                        imageLinks = ImageLinks(
                                            thumbnail = bookMap["thumbnail"] as? String
                                        )
                                    )
                                )
                            })
                            favoriteBookAdapter.notifyDataSetChanged() // Atualiza o RecyclerView
                        }
                    } else {
                        Toast.makeText(this, "Nenhum favorito encontrado.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar favoritos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    // Remove um livro favorito do Firestore e atualiza a lista local
    private fun removeFavoriteBook(book: BookItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val bookData = mapOf(
                "title" to (book.volumeInfo.title ?: "Título não disponível"),
                "author" to (book.volumeInfo.authors?.joinToString(", ") ?: "Autor desconhecido"),
                "description" to (book.volumeInfo.description ?: "Descrição indisponível"),
                "thumbnail" to (book.volumeInfo.imageLinks?.thumbnail ?: "")
            )

            FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .update("favoriteBooks", FieldValue.arrayRemove(bookData)) // Remove o livro dos favoritos
                .addOnSuccessListener {
                    favoriteBooks.remove(book) // Remove da lista local
                    favoriteBookAdapter.notifyDataSetChanged() // Atualiza o RecyclerView
                    Toast.makeText(this, "Livro removido dos favoritos!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao remover do Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    // Abre os detalhes de um livro favorito
    private fun openBookDetail(book: BookItem) {
        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra("title", book.volumeInfo.title)
        intent.putExtra("author", book.volumeInfo.authors?.joinToString(", ") ?: "Autor desconhecido")
        intent.putExtra("coverUrl", book.volumeInfo.imageLinks?.thumbnail)
        intent.putExtra("publishDate", book.volumeInfo.publishedDate ?: "Data não disponível")
        intent.putExtra("pageCount", book.volumeInfo.pageCount ?: 0)
        intent.putExtra("rating", book.volumeInfo.averageRating ?: 0f)
        intent.putExtra("description", book.volumeInfo.description ?: "Descrição indisponível")
        startActivity(intent) // Inicia a atividade de detalhes do livro
    }
}
