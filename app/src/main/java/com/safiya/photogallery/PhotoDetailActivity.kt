package com.safiya.photogallery

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PhotoDetailActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var buttonLeft: ImageView
    private lateinit var buttonRight: ImageView
    private lateinit var textViewName: TextView
    private lateinit var textViewTags: TextView

    private var currentIndex: Int = 0
    private lateinit var photoList: List<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)

        imageView = findViewById(R.id.fullscreen_image)
        buttonLeft = findViewById(R.id.button_left)
        buttonRight = findViewById(R.id.button_right)
        textViewName = findViewById(R.id.textViewName)
        textViewTags = findViewById(R.id.textViewTags)

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
            finish() // Закрытие активности при нажатии на изображение
        }
    }

    private fun loadPhoto() {
        val photo = photoList[currentIndex]
        val imagePath = photo.imagePath // Используем путь к изображению
        val bitmap = BitmapFactory.decodeFile(imagePath) // Декодируем изображение из файла
        imageView.setImageBitmap(bitmap)

        // Устанавливаем название и теги, если они не равны "-"
        if (photo.title != "-") {
            textViewName.text = photo.title // Название фотографии
            textViewName.visibility = View.VISIBLE // Показываем TextView
        } else {
            textViewName.visibility = View.GONE // Скрываем TextView
        }

        if (photo.tags.joinToString(", ") != "-") {
            textViewTags.text = photo.tags.joinToString(", ") // Теги, разделенные запятыми
            textViewTags.visibility = View.VISIBLE // Показываем TextView
        } else {
            textViewTags.visibility = View.GONE // Скрываем TextView
        }
    }
}