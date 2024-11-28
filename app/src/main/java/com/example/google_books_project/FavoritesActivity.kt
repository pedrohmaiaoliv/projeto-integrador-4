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

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerViewFavorites: RecyclerView
    private lateinit var favoriteBookAdapter: FavoriteBookAdapter
    private var favoriteBooks: MutableList<BookItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)

        // Ajusta as margens para considerar as barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialização do RecyclerView
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites)
        recyclerViewFavorites.layoutManager = LinearLayoutManager(this)

        // Inicializa o adapter com os callbacks
        favoriteBookAdapter = FavoriteBookAdapter(
            favoriteBooks,
            onUnfavoriteClick = { book -> removeFavoriteBook(book) },
            onBookClick = { book -> openBookDetail(book) }
        )
        recyclerViewFavorites.adapter = favoriteBookAdapter

        // Configura botões da barra inferior e volta
        setupNavigationMenu()
        setupBackButton()

        // Carrega os livros favoritos
        loadFavoriteBooks()
    }

    private fun setupBackButton() {
        val backButton = findViewById<ImageButton>(R.id.imageButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a FavoritesActivity
        }
    }

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
            // Já estamos na tela de favoritos, nenhuma ação necessária
        }
    }

    private fun loadFavoriteBooks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val books = document.get("favoriteBooks") as? List<Map<String, Any>>
                        books?.let {
                            favoriteBooks.clear()
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
