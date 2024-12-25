package com.safiya.photogallery.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.safiya.photogallery.FeedReaderDbHelper
import com.safiya.photogallery.ImageAdapter
import com.safiya.photogallery.Photo
import com.safiya.photogallery.PhotoRepository
import com.safiya.photogallery.R

class ImagePickerActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var selectedImages: MutableList<Uri>
    private lateinit var photoRepository: PhotoRepository
    private lateinit var deviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)

        gridView = findViewById(R.id.grid_view)
        selectedImages = mutableListOf()
        photoRepository = PhotoRepository(FeedReaderDbHelper(this))
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // Настройка адаптера
        val adapter = ImageAdapter(this, selectedImages) { uri, isSelected ->
            if (isSelected) {
                selectedImages.add(uri)
            } else {
                selectedImages.remove(uri)
            }
        }
        gridView.adapter = adapter

        val buttonOk: Button = findViewById(R.id.button_ok)
        buttonOk.setOnClickListener {
            addPhotosToDatabase()
        }

        loadImages()
    }

    private fun loadImages() {
        // Загружаем изображения из галереи
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Добавляем выбранное изображение в список
                if (!selectedImages.contains(uri)) {
                    selectedImages.add(uri)
                }
                (gridView.adapter as ImageAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun addPhotosToDatabase() {
        selectedImages.forEach { uri ->
            val imagePath = uriToFile(uri)
            if (imagePath.isNotEmpty()) {
                val photo = Photo(
                    id = 0,
                    deviceId = deviceId,
                    createdAt = System.currentTimeMillis().toString(),
                    imagePath = imagePath,
                    title = "",
                    tags = arrayOf()
                )
                photoRepository.insertPhoto(photo)
            } else {
                Toast.makeText(this, "Unable to get path for image", Toast.LENGTH_SHORT).show()
            }
        }
        Toast.makeText(this, "Photos added", Toast.LENGTH_SHORT).show()
        finish() // Закрываем Activity
    }

    private fun uriToFile(uri: Uri): String {
        // Метод для получения пути к файлу из Uri
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex) // Возвращаем путь к файлу
        }
        return "" // Возвращаем пустую строку, если не удалось получить путь
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }
}