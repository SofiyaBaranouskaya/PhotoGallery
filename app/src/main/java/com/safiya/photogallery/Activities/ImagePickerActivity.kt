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
                selectedImages.add(uri)
                (gridView.adapter as ImageAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun addPhotosToDatabase() {
        selectedImages.forEach { uri ->
            val imageData = contentResolver.openInputStream(uri)?.readBytes() ?: ByteArray(0)
            val photo = Photo(0, deviceId, System.currentTimeMillis().toString(), imageData, "", arrayOf())
            photoRepository.insertPhoto(photo)
        }
        Toast.makeText(this, "Photos added", Toast.LENGTH_SHORT).show()
        finish() // Закрываем Activity
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }
}