package com.safiya.photogallery

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Menu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_nav_menu)

        val iconHome: ImageView = findViewById(R.id.icon_home)
        val iconFolder: ImageView = findViewById(R.id.icon_folder)
        val iconFilter: ImageView = findViewById(R.id.icon_filter)
        val iconAdd: ImageView = findViewById(R.id.icon_add)

        iconHome.setImageResource(R.drawable.home_black)
        iconFolder.setImageResource(R.drawable.folder_black)
        iconFilter.setImageResource(R.drawable.filtr_black)
        iconAdd.setImageResource(R.drawable.add_black)

        iconHome.setOnClickListener {
            iconHome.setImageResource(R.drawable.home_violet)
        }

        iconFolder.setOnClickListener {
            iconFolder.setImageResource(R.drawable.folder_violet)
        }

        iconFilter.setOnClickListener {
            iconFilter.setImageResource(R.drawable.filtr_violet)
        }

        iconAdd.setOnClickListener {
            iconAdd.setImageResource(R.drawable.add_violet)
        }
    }
}