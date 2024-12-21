package com.safiya.photogallery

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var photoList: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val iconHome: ImageView = findViewById(R.id.icon_home)
        val iconFolder: ImageView = findViewById(R.id.icon_folder)
        val iconFilter: ImageView = findViewById(R.id.icon_filter)
        val iconAdd: ImageView = findViewById(R.id.icon_add)

        // Устанавливаем начальные изображения
        iconHome.setImageResource(R.drawable.home_violet)
        iconFolder.setImageResource(R.drawable.folder_black)
        iconFilter.setImageResource(R.drawable.filtr_black)
        iconAdd.setImageResource(R.drawable.add_black)

        // Устанавливаем обработчики нажатий
        iconHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0) // Отключаем анимацию
        }

        iconFolder.setOnClickListener {
            startActivity(Intent(this, AlbumsActivity::class.java))
            overridePendingTransition(0, 0) // Отключаем анимацию
        }

        iconFilter.setOnClickListener {
            startActivity(Intent(this, FiltersActivity::class.java))
            overridePendingTransition(0, 0) // Отключаем анимацию
        }

        iconAdd.setOnClickListener {
            //setIconState(iconAdd, R.drawable.add_violet)
            // Логика для добавления фотографий
        }

        // Подключаем RecyclerView
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)

        // Устанавливаем GridLayoutManager с 3 колонками
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager

        photoList = listOf(
            R.drawable.photo1,
            R.drawable.photo2,
            R.drawable.photo3,
            R.drawable.photo4,
        )

        photoAdapter = PhotoAdapter(this, photoList)
        recyclerView.adapter = photoAdapter
    }

}