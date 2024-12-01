package com.example.google_books_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.google_books_project.adapter.BookAdapter
import com.example.google_books_project.model.BookItem
import com.example.google_books_project.model.BooksResponse
import com.example.google_books_project.util.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var genreSpinner: Spinner
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var recyclerViewBooks: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        genreSpinner = findViewById(R.id.genre_spinner)
        adapter = ArrayAdapter(this, R.layout.spinner_item, mutableListOf("Gêneros"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genreSpinner.adapter = adapter

        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val selectedGenre = parent.getItemAtPosition(position).toString()
                    Toast.makeText(this@MainActivity, "Gênero selecionado: $selectedGenre", Toast.LENGTH_SHORT).show()
                    searchBooksByGenre(selectedGenre)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        fetchGenres()
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks)
        recyclerViewBooks.layoutManager = GridLayoutManager(this, 3)

        bookAdapter = BookAdapter(emptyList(), this::saveFavoriteBook) { book ->
            openBookDetail(book)
        }
        recyclerViewBooks.adapter = bookAdapter

        searchEditText = findViewById(R.id.searchEditText)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    if (isCategory(query)) {
                        searchBooksByGenre(query) // Busca por categoria
                    } else {
                        searchBooks(query) // Busca geral
                    }
                }
                true
            } else {
                false
            }
        }

        setupNavigationMenu()
    }

    private fun setupNavigationMenu() {
        val favoritesButton = findViewById<ImageButton>(R.id.favoritos)
        val settingsButton = findViewById<ImageButton>(R.id.configuracoes)

        favoritesButton.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun fetchGenres() {
        val apiKey = getString(R.string.google_books_api_key)

        RetrofitInstance.api.searchBooks("subject", apiKey).enqueue(object : Callback<BooksResponse> {
            override fun onResponse(call: Call<BooksResponse>, response: Response<BooksResponse>) {
                if (response.isSuccessful) {
                    val books = response.body()?.items ?: emptyList()
                    val genres = books
                        .flatMap { it.volumeInfo.categories ?: emptyList() }
                        .distinct()
                        .sorted()
                    updateGenreSpinner(genres)
                }
            }

            override fun onFailure(call: Call<BooksResponse>, t: Throwable) {
                Log.e("MainActivity", "Erro ao buscar gêneros: ${t.message}")
            }
        })
    }

    private fun updateGenreSpinner(genres: List<String>) {
        adapter.clear()
        adapter.add("Gêneros")
        adapter.addAll(genres)
        adapter.notifyDataSetChanged()
    }

    private fun isCategory(query: String): Boolean {
        val genres = (0 until adapter.count).map { adapter.getItem(it).toString() }
        return genres.contains(query)
    }

    private fun searchBooks(query: String) {
        val apiKey = getString(R.string.google_books_api_key)
        RetrofitInstance.api.searchBooks(query, apiKey).enqueue(object : Callback<BooksResponse> {
            override fun onResponse(call: Call<BooksResponse>, response: Response<BooksResponse>) {
                if (response.isSuccessful) {
                    val books = response.body()?.items ?: emptyList()
                    updateRecyclerView(books)
                }
            }

            override fun onFailure(call: Call<BooksResponse>, t: Throwable) {
                Log.e("MainActivity", "Erro na requisição de busca: ${t.message}")
            }
        })
    }

    private fun searchBooksByGenre(genre: String) {
        val apiKey = getString(R.string.google_books_api_key)
        RetrofitInstance.api.searchBooks("subject:$genre", apiKey).enqueue(object : Callback<BooksResponse> {
            override fun onResponse(call: Call<BooksResponse>, response: Response<BooksResponse>) {
                if (response.isSuccessful) {
                    val books = response.body()?.items ?: emptyList()
                    updateRecyclerView(books)
                }
            }

            override fun onFailure(call: Call<BooksResponse>, t: Throwable) {
                Log.e("MainActivity", "Erro: ${t.message}")
            }
        })
    }

    private fun updateRecyclerView(books: List<BookItem>) {
        bookAdapter = BookAdapter(
            books,
            { book, isFavorite -> saveFavoriteBook(book, isFavorite) },
            { book -> openBookDetail(book) }
        )
        recyclerViewBooks.adapter = bookAdapter
        bookAdapter.notifyDataSetChanged()
    }

    private fun saveFavoriteBook(book: BookItem, isFavorite: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        val bookData = mapOf(
            "title" to (book.volumeInfo.title ?: "Título não disponível"),
            "author" to (book.volumeInfo.authors?.joinToString(", ") ?: "Autor desconhecido"),
            "description" to (book.volumeInfo.description ?: "Descrição indisponível"),
            "thumbnail" to (book.volumeInfo.imageLinks?.thumbnail ?: "")
        )

        if (userId != null) {
            if (isFavorite) {
                firestore.collection("users")
                    .document(userId)
                    .update("favoriteBooks", FieldValue.arrayUnion(bookData))
            } else {
                firestore.collection("users")
                    .document(userId)
                    .update("favoriteBooks", FieldValue.arrayRemove(bookData))
            }
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
