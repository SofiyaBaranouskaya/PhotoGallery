package com.safiya.photogallery

import Album
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OpenedAlbumActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photosAdapter: PhotoAdapter // Ваш адаптер для фотографий
    private lateinit var noPhotosText: TextView
    private lateinit var backButton: ImageView
    private lateinit var albumTitle: TextView
    private lateinit var albumDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opened_album) // Убедитесь, что имя макета верное

        // Инициализируем элементы интерфейса
        recyclerView = findViewById(R.id.recyclerViewPhotos)
        noPhotosText = findViewById(R.id.noPhotosText)
        backButton = findViewById(R.id.backButton)
        albumTitle = findViewById(R.id.albumTitle)
        albumDate = findViewById(R.id.albumDate)

        // Получите данные альбома из Intent
        val album = intent.getParcelableExtra<Album>("ALBUM")
        albumTitle.text = album?.title // Устанавливаем название альбома
        albumDate.text = album?.date // Устанавливаем дату альбома

        // Инициализация RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Получите список идентификаторов изображений
        val photos = getPhotosForAlbum(album) // Реализуйте метод для получения списка идентификаторов

        // Проверьте наличие фотографий
        if (photos.isEmpty()) {
            noPhotosText.visibility = View.VISIBLE
        } else {
            noPhotosText.visibility = View.GONE
            photosAdapter = PhotoAdapter(this, photos) // Передаем контекст и список идентификаторов
            recyclerView.adapter = photosAdapter
        }

        // Обработчик нажатия на кнопку "Назад"
        backButton.setOnClickListener {
            finish() // Завершает текущую активность и возвращает к предыдущей
        }
    }

    private fun getPhotosForAlbum(album: Album?): List<Int> {
        // Реализуйте логику получения идентификаторов фотографий для данного альбома
        return listOf(R.drawable.photo1, R.drawable.photo2, R.drawable.photo3) // Пример списка идентификаторов
    }
}