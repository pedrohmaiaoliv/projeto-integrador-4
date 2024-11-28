package com.example.google_books_project

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura o clique do botão para voltar para a MainActivity
        val backButton = findViewById<ImageButton>(R.id.imageButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a SettingsActivity
        }

        // Configura os botões da barra inferior
        setupBottomBarButtons()

        profileImageView = findViewById(R.id.profile_image)

        // Configura o launcher para pegar a imagem selecionada na galeria
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    profileImageView.setImageURI(uri)
                    saveProfileImageUri(uri) // Salva o URI da imagem
                }
            }
        }

        // Configura o click listener para abrir a galeria
        profileImageView.setOnClickListener {
            checkGalleryPermissionAndOpenGallery()
        }

        // Carrega a imagem salva ao iniciar a atividade
        loadProfileImage()
    }

    private fun setupBottomBarButtons() {
        // Botão de Favoritos
        val favoritosButton = findViewById<ImageButton>(R.id.favoritos)
        favoritosButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
            finish() // Fecha a SettingsActivity
        }

        // Botão de Home
        val homeButton = findViewById<ImageButton>(R.id.home)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a SettingsActivity
        }
    }

    private fun checkGalleryPermissionAndOpenGallery() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                Toast.makeText(this, "Permissão para acessar a galeria é necessária", Toast.LENGTH_SHORT).show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun saveProfileImageUri(uri: Uri) {
        val sharedPref = getPreferences(MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("profile_image_uri", uri.toString())
            apply()
        }
    }

    private fun loadProfileImage() {
        val sharedPref = getPreferences(MODE_PRIVATE)
        val uriString = sharedPref.getString("profile_image_uri", null)
        uriString?.let {
            selectedImageUri = Uri.parse(it)
            profileImageView.setImageURI(selectedImageUri)
        }
    }
}
