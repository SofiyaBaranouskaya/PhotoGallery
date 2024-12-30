package com.safiya.photogallery.Activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.safiya.photogallery.Photo
import com.safiya.photogallery.R

class PhotoDetailActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var buttonLeft: ImageView
    private lateinit var buttonRight: ImageView

    private var currentIndex: Int = 0
    private lateinit var photoList: List<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)

        imageView = findViewById(R.id.fullscreen_image)
        buttonLeft = findViewById(R.id.button_left)
        buttonRight = findViewById(R.id.button_right)

        // Получение списка фотографий и текущего индекса
        photoList = intent.getParcelableArrayListExtra("photoList") ?: emptyList()
        currentIndex = intent.getIntExtra("currentIndex", 0)

        loadPhoto()

        buttonLeft.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                loadPhoto()
            }
        }

        buttonRight.setOnClickListener {
            if (currentIndex < photoList.size - 1) {
                currentIndex++
                loadPhoto()
            }
        }

        imageView.setOnClickListener {
            finish()
        }
    }

    private fun loadPhoto() {
        val imagePath = photoList[currentIndex].imagePath // Используем путь к изображению
        val bitmap = BitmapFactory.decodeFile(imagePath) // Декодируем изображение из файла
        imageView.setImageBitmap(bitmap)
    }
}