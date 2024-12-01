package com.example.google_books_project

import android.content.Context
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity principal responsável por exibir livros, buscar por gêneros ou termos, e gerenciar favoritos
class MainActivity : AppCompatActivity() {
    private lateinit var genreSpinner: Spinner // Spinner para selecionar gêneros
    private lateinit var adapter: ArrayAdapter<String> // Adapter para gerenciar os itens do Spinner
    private lateinit var recyclerViewBooks: RecyclerView // RecyclerView para exibir os livros
    private lateinit var bookAdapter: BookAdapter // Adapter para gerenciar os livros no RecyclerView
    private lateinit var searchEditText: EditText // Campo de entrada de texto para buscas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ativa suporte para layouts sem bordas
        setContentView(R.layout.activity_main)

        // Ajusta o layout para considerar as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuração inicial do Spinner de gêneros
        genreSpinner = findViewById(R.id.genre_spinner)
        adapter = ArrayAdapter(this, R.layout.spinner_item, mutableListOf("Gêneros"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genreSpinner.adapter = adapter

        // Listener para detectar seleção de itens no Spinner
        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) { // Ignora o item "Gêneros"
                    val selectedGenre = parent.getItemAtPosition(position).toString()
                    Toast.makeText(this@MainActivity, "Gênero selecionado: $selectedGenre", Toast.LENGTH_SHORT).show()
                    searchBooksByGenre(selectedGenre) // Busca livros pelo gênero selecionado
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        fetchGenres() // Carrega os gêneros na inicialização

        // Configuração do RecyclerView para exibir os livros
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks)
        recyclerViewBooks.layoutManager = GridLayoutManager(this, 3) // Define layout em 3 colunas

        // Inicializa o adapter do RecyclerView
        bookAdapter = BookAdapter(emptyList(), this::saveFavoriteBook) { book ->
            openBookDetail(book) // Callback para abrir detalhes de um livro
        }
        recyclerViewBooks.adapter = bookAdapter

        // Configuração do campo de busca
        searchEditText = findViewById(R.id.searchEditText)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchBooks(query) // Realiza busca pelo termo inserido
                }
                true
            } else {
                false
            }
        }

        setupNavigationMenu() // Configuração do menu de navegação inferior
    }

    // Configura os botões de navegação inferior
    private fun setupNavigationMenu() {
        val favoritesButton = findViewById<ImageButton>(R.id.favoritos)
        val settingsButton = findViewById<ImageButton>(R.id.configuracoes)

        favoritesButton.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java)) // Vai para a tela de favoritos
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)) // Vai para a tela de configurações
        }
    }

    // Faz uma requisição para buscar gêneros disponíveis
    private fun fetchGenres() {
        val apiKey = getString(R.string.google_books_api_key) // Obtém a chave da API

        RetrofitInstance.api.searchBooks("subject", apiKey).enqueue(object : Callback<BooksResponse> {
            override fun onResponse(call: Call<BooksResponse>, response: Response<BooksResponse>) {
                if (response.isSuccessful) {
                    val books = response.body()?.items ?: emptyList()
                    val genres = books
                        .flatMap { it.volumeInfo.categories ?: emptyList() } // Extrai os gêneros dos livros
                        .distinct()
                        .sorted()
                    updateGenreSpinner(genres) // Atualiza o Spinner com os gêneros
                }
            }

            override fun onFailure(call: Call<BooksResponse>, t: Throwable) {
                Log.e("MainActivity", "Erro ao buscar gêneros: ${t.message}")
            }
        })
    }

    // Atualiza o Spinner com os gêneros encontrados
    private fun updateGenreSpinner(genres: List<String>) {
        adapter.clear()
        adapter.add("Gêneros") // Adiciona o item padrão
        adapter.addAll(genres) // Adiciona os gêneros
        adapter.notifyDataSetChanged()
    }

    // Realiza busca de livros pelo termo inserido
    private fun searchBooks(query: String) {
        val apiKey = getString(R.string.google_books_api_key)
        RetrofitInstance.api.searchBooks(query, apiKey).enqueue(object : Callback<BooksResponse> {
            override fun onResponse(call: Call<BooksResponse>, response: Response<BooksResponse>) {
                if (response.isSuccessful) {
                    val books = response.body()?.items ?: emptyList()
                    updateRecyclerView(books) // Atualiza a lista de livros no RecyclerView
                }
            }

            override fun onFailure(call: Call<BooksResponse>, t: Throwable) {
                Log.e("MainActivity", "Erro na requisição de busca: ${t.message}")
            }
        })
    }

    // Realiza busca de livros pelo gênero selecionado
    private fun searchBooksByGenre(genre: String) {
        val apiKey = getString(R.string.google_books_api_key)
        RetrofitInstance.api.searchBooks("subject:$genre", apiKey).enqueue(object : Callback<BooksResponse> {
            override fun onResponse(call: Call<BooksResponse>, response: Response<BooksResponse>) {
                if (response.isSuccessful) {
                    val books = response.body()?.items ?: emptyList()
                    updateRecyclerView(books) // Atualiza a lista de livros no RecyclerView
                }
            }

            override fun onFailure(call: Call<BooksResponse>, t: Throwable) {
                Log.e("MainActivity", "Erro: ${t.message}")
            }
        })
    }

    // Atualiza o RecyclerView com os livros encontrados
    private fun updateRecyclerView(books: List<BookItem>) {
        bookAdapter = BookAdapter(
            books,
            { book, isFavorite -> saveFavoriteBook(book, isFavorite) }, // Callback para salvar favoritos
            { book -> openBookDetail(book) } // Callback para abrir detalhes
        )
        recyclerViewBooks.adapter = bookAdapter
        bookAdapter.notifyDataSetChanged()
    }

    // Salva ou remove um livro dos favoritos no Firebase
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
                    .update("favoriteBooks", FieldValue.arrayUnion(bookData)) // Adiciona aos favoritos
            } else {
                firestore.collection("users")
                    .document(userId)
                    .update("favoriteBooks", FieldValue.arrayRemove(bookData)) // Remove dos favoritos
            }
        }
    }

    // Abre os detalhes de um livro
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
