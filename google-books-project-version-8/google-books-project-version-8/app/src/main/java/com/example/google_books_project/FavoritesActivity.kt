package com.example.google_books_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.google_books_project.adapter.FavoriteBookAdapter
import com.example.google_books_project.model.BookItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerViewFavorites: RecyclerView
    private lateinit var favoriteBookAdapter: FavoriteBookAdapter
    private var favoriteBooks: MutableList<BookItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites)
        recyclerViewFavorites.layoutManager = LinearLayoutManager(this)

        favoriteBookAdapter = FavoriteBookAdapter(favoriteBooks) { book ->
            removeFavoriteBook(book)
        }
        recyclerViewFavorites.adapter = favoriteBookAdapter

        // Configura os botões da barra inferior
        setupNavigationMenu()
        setupBackButton() // Configura o botão de voltar
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
        // Botão de Home
        val homeButton = findViewById<ImageButton>(R.id.home)
        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Fecha a FavoritesActivity
        }

        // Botão de Configurações
        val settingsButton = findViewById<ImageButton>(R.id.configuracoes)
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish() // Fecha a FavoritesActivity
        }

        // Botão de Favoritos (opcional, mantém na mesma tela)
        val favoritosButton = findViewById<ImageButton>(R.id.favoritos)
        favoritosButton.setOnClickListener {
            // Já estamos na tela de favoritos, nenhuma ação necessária
        }
    }

    private fun loadFavoriteBooks() {
        val sharedPref = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val json = sharedPref.getString("favorite_books", null)
        json?.let {
            val type = object : TypeToken<List<BookItem>>() {}.type
            favoriteBooks.addAll(Gson().fromJson(it, type))
        }
        favoriteBookAdapter.notifyDataSetChanged()
    }

    private fun removeFavoriteBook(book: BookItem) {
        // Encontrar a posição do livro na lista
        val position = favoriteBooks.indexOf(book)
        if (position >= 0) {
            // Remover da lista e notificar o adaptador
            favoriteBooks.removeAt(position)
            favoriteBookAdapter.notifyItemRemoved(position)

            // Atualizar o SharedPreferences com a lista de favoritos atualizada
            val sharedPref = getSharedPreferences("favorites", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val gson = Gson()
            editor.putString("favorite_books", gson.toJson(favoriteBooks))
            editor.apply()
        }
    }
}
