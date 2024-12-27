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
    private lateinit var photosAdapter: PhotoAdapter
    private lateinit var noPhotosText: TextView
    private lateinit var backButton: ImageView
    private lateinit var albumTitle: TextView
    private lateinit var albumDate: TextView
    private lateinit var albumRepository: AlbumRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opened_album)

        recyclerView = findViewById(R.id.recyclerViewPhotos)
        noPhotosText = findViewById(R.id.noPhotosText)
        backButton = findViewById(R.id.backButton)
        albumTitle = findViewById(R.id.albumTitle)
        albumDate = findViewById(R.id.albumDate)

        // Получите данные альбома из Intent
        val album = intent.getParcelableExtra<Album>("ALBUM")
        albumTitle.text = album?.title
        albumDate.text = album?.createdAt

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        albumRepository = AlbumRepository(FeedReaderDbHelper(this)) // Инициализация репозитория

        // Получаем список объектов Photo для альбома по названию и дате
        album?.let {
            val photos = fetchPhotosForAlbum(it)

            if (photos.isEmpty()) {
                noPhotosText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noPhotosText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                photosAdapter = PhotoAdapter(this, photos.toMutableList()) {
                    // Обновляем адаптер при удалении (если это нужно)
                    photosAdapter.updateData(fetchPhotosForAlbum(it))
                }
                recyclerView.adapter = photosAdapter
            }
        } ?: run {
            noPhotosText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchPhotosForAlbum(album: Album): List<Photo> {
        // Ищем альбом в базе данных по названию и дате создания
        val allAlbums = albumRepository.getAllAlbums()
        val matchedAlbum = allAlbums.find {
            it.title == album.title && it.createdAt == album.createdAt
        }

        // Если альбом найден, получаем его фотографии
        return matchedAlbum?.let { albumRepository.getPhotosForAlbum(it) } ?: emptyList()
    }
}