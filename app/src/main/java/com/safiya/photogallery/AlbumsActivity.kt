package com.safiya.photogallery

import Album
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AlbumsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_albums)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val iconHome: ImageView = findViewById(R.id.icon_home)
        val iconFolder: ImageView = findViewById(R.id.icon_folder)
        val iconFilter: ImageView = findViewById(R.id.icon_filter)
        val iconAdd: ImageView = findViewById(R.id.icon_add)

        // Устанавливаем начальные изображения для иконок
        iconHome.setImageResource(R.drawable.home_black)
        iconFolder.setImageResource(R.drawable.folder_violet)
        iconFilter.setImageResource(R.drawable.filtr_black)
        iconAdd.setImageResource(R.drawable.add_black)

        // Устанавливаем обработчики нажатий
        iconHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0) // Отключаем анимацию
        }

        iconFolder.setOnClickListener {
            // Ничего не делаем, так как мы уже на этой активности
        }

        iconFilter.setOnClickListener {
            startActivity(Intent(this, FiltersActivity::class.java))
            overridePendingTransition(0, 0) // Отключаем анимацию
        }

        iconAdd.setOnClickListener {
            // Логика для добавления фотографий
        }

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAlbums)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 колонки

        // Создание списка альбомов (пример)
        val albums = listOf(
            Album("Название 1", "01.01.2023", R.drawable.album1), // Замените на реальные ресурсы
            Album("Название 2", "02.01.2023", R.drawable.album2),
            Album("Название 3", "03.01.2023", R.drawable.album3),
            Album("Название 4", "04.01.2023", R.drawable.album4),
            Album("Название 5", "02.01.2023", R.drawable.photo1),
            Album("Название 6", "03.01.2023", R.drawable.photo2)
        )

        // Инициализация адаптера
        albumAdapter = AlbumAdapter(this, albums) { album -> // Передаем контекст
            val intent = Intent(this, OpenedAlbumActivity::class.java) // Открываем OpenedAlbumActivity
            intent.putExtra("ALBUM", album) // Передаем альбом в новую активность
            startActivity(intent)
        }
        recyclerView.adapter = albumAdapter
    }
}