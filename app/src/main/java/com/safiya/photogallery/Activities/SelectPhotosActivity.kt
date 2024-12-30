package com.safiya.photogallery.Activities

import Album
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.EditText
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.safiya.photogallery.Photo
import com.safiya.photogallery.CustomPhotoAdapter
import com.safiya.photogallery.AlbumRepository
import com.safiya.photogallery.AlbumsActivity
import com.safiya.photogallery.R
import com.safiya.photogallery.FeedReaderDbHelper
import com.safiya.photogallery.PhotoRepository

class SelectPhotosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: CustomPhotoAdapter
    private lateinit var albumRepository: AlbumRepository
    private lateinit var photoRepository: PhotoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_photos)

        recyclerView = findViewById(R.id.recycler_view_photos)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        albumRepository = AlbumRepository(FeedReaderDbHelper(this))
        photoRepository = PhotoRepository(FeedReaderDbHelper(this))

        // Получаем все фотографии
        val photoList = photoRepository.getAllPhotos()
        Log.d("SelectPhotosActivity", "Loaded photos: ${photoList.size}")

        // Инициализация нового адаптера
        photoAdapter = CustomPhotoAdapter(this, photoList.toMutableList()) { photo ->
            // Обработка выбора фотографии
            Log.d("SelectPhotosActivity", "Photo selected: ${photo.id}, selected: ${photo.isSelected}")
        }

        recyclerView.adapter = photoAdapter

        findViewById<Button>(R.id.save_album_button).setOnClickListener {
            Log.d("SelectPhotosActivity", "Save button clicked")
            saveAlbum()
        }
    }

    private fun saveAlbum() {
        val albumTitleInput = findViewById<EditText>(R.id.album_title_edit_text).text.toString()
        val allAlbums = albumRepository.getAllAlbums()
        val albumCount = allAlbums.size

        val albumTitle = if (albumTitleInput.isNotBlank()) {
            albumTitleInput
        } else {
            if (albumCount == 0) {
                "Album1"
            } else {
                "Album${albumCount + 1}"
            }
        }

        Log.d("SelectPhotosActivity", "Album title: $albumTitle")

        // Получите выбранные ID фотографий
        val selectedImageIds = photoAdapter.getSelectedPhotoIds()
        Log.d("SelectPhotosActivity", "Selected image IDs: $selectedImageIds")

        // Создаем новый альбом
        val album = Album(
            title = albumTitle,
            createdAt = System.currentTimeMillis().toString(),
            deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID),
            imageIds = selectedImageIds // Используем выбранные ID
        )

        try {
            albumRepository.insertAlbum(album)
            Log.d("SelectPhotosActivity", "Album saved: $album")

            // Переход на страницу со всеми альбомами
            val intent = Intent(this, AlbumsActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("SelectPhotosActivity", "Error saving album", e)
        }
    }
}