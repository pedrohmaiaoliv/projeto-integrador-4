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

// Activity que exibe a lista de livros favoritos do usuário.
class FavoritesActivity : AppCompatActivity() {

    // Variáveis para o RecyclerView, Adapter e a lista de livros favoritos.
    private lateinit var recyclerViewFavorites: RecyclerView
    private lateinit var favoriteBookAdapter: FavoriteBookAdapter
    private var favoriteBooks: MutableList<BookItem> = mutableListOf()

    // Método que é chamado ao criar a Activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita a interface de tela cheia com bordas (Edge-to-Edge).
        setContentView(R.layout.activity_favorites)

        // Ajusta as margens da tela para não sobrepor as barras de sistema (status bar, navigation bar, etc.).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa o RecyclerView com um layout linear.
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites)
        recyclerViewFavorites.layoutManager = LinearLayoutManager(this)

        // Inicializa o Adapter do RecyclerView com callbacks para ações de desfavoritar e abrir detalhes do livro.
        favoriteBookAdapter = FavoriteBookAdapter(
            favoriteBooks,
            onUnfavoriteClick = { book -> removeFavoriteBook(book) },
            onBookClick = { book -> openBookDetail(book) }
        )
        recyclerViewFavorites.adapter = favoriteBookAdapter

        // Configura o menu de navegação (botões de voltar, home, configurações, favoritos).
        setupNavigationMenu()
        setupBackButton()

        // Carrega os livros favoritos do usuário.
        loadFavoriteBooks()
    }

    // Configura o botão de voltar, que retorna à MainActivity.
    private fun setupBackButton() {
        val backButton = findViewById<ImageButton>(R.id.imageButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a FavoritesActivity.
        }
    }

    // Configura os botões de navegação (Home, Configurações, Favoritos).
    private fun setupNavigationMenu() {
        val homeButton = findViewById<ImageButton>(R.id.home)
        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Fecha a FavoritesActivity.
        }

        val settingsButton = findViewById<ImageButton>(R.id.configuracoes)
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish() // Fecha a FavoritesActivity.
        }

        val favoritosButton = findViewById<ImageButton>(R.id.favoritos)
        favoritosButton.setOnClickListener {
            // Nenhuma ação necessária, pois já estamos na tela de favoritos.
        }
    }

    // Carrega os livros favoritos do Firebase para o usuário autenticado.
    private fun loadFavoriteBooks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        // Verifica se o usuário está autenticado.
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val books = document.get("favoriteBooks") as? List<Map<String, Any>>
                        books?.let {
                            favoriteBooks.clear()
                            // Mapeia os dados dos livros favoritos para objetos `BookItem`.
                            favoriteBooks.addAll(it.map { bookMap ->
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
                            // Atualiza o adapter para refletir os livros carregados.
                            favoriteBookAdapter.notifyDataSetChanged()
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

    // Remove um livro da lista de favoritos do Firebase e da lista local.
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
                .update("favoriteBooks", FieldValue.arrayRemove(bookData))
                .addOnSuccessListener {
                    // Remove o livro da lista local e atualiza o adapter.
                    favoriteBooks.remove(book)
                    favoriteBookAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Livro removido dos favoritos!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao remover do Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    // Abre a tela de detalhes do livro ao clicar em um livro na lista.
    private fun openBookDetail(book: BookItem) {
        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra("title", book.volumeInfo.title)
        intent.putExtra("author", book.volumeInfo.authors?.joinToString(", ") ?: "Autor desconhecido")
        intent.putExtra("coverUrl", book.volumeInfo.imageLinks?.thumbnail)
        intent.putExtra("publishDate", book.volumeInfo.publishedDate ?: "Data não disponível")
        intent.putExtra("pageCount", book.volumeInfo.pageCount ?: 0)
        intent.putExtra("rating", book.volumeInfo.averageRating ?: 0f)
        intent.putExtra("description", book.volumeInfo.description ?: "Descrição indisponível")
        startActivity(intent)
    }
}
