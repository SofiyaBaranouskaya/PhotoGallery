package com.safiya.photogallery

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class FiltersActivity : AppCompatActivity() {
    private lateinit var iconHome: ImageView
    private lateinit var iconFolder: ImageView
    private lateinit var iconFilter: ImageView
    private lateinit var iconAdd: ImageView
    private lateinit var arrowDown: ImageView
    private lateinit var openedFirstContainer: View
    private lateinit var editTextDate: EditText
    private lateinit var editTextDate2: EditText  // Добавляем второе поле для даты
    private lateinit var iconCalendarSince: ImageView  // Иконка для первого календаря
    private lateinit var iconCalendarTo: ImageView  // Иконка для второго календаря

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filters)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация элементов интерфейса
        iconHome = findViewById(R.id.icon_home)
        iconFolder = findViewById(R.id.icon_folder)
        iconFilter = findViewById(R.id.icon_filter)
        iconAdd = findViewById(R.id.icon_add)
        arrowDown = findViewById(R.id.imageView)
        openedFirstContainer = findViewById(R.id.opened_first_container)
        editTextDate = findViewById(R.id.editTextDate)
        editTextDate2 = findViewById(R.id.editTextDate2)  // Инициализация второго поля для даты
        iconCalendarSince = findViewById(R.id.icon_calendarSince)  // Иконка для первого календаря
        iconCalendarTo = findViewById(R.id.icon_calendarTo)  // Иконка для второго календаря

        val iconHome: ImageView = findViewById(R.id.icon_home)
        val iconFolder: ImageView = findViewById(R.id.icon_folder)
        val iconFilter: ImageView = findViewById(R.id.icon_filter)
        val iconAdd: ImageView = findViewById(R.id.icon_add)

        // Устанавливаем начальные изображения для иконок
        iconHome.setImageResource(R.drawable.home_black)
        iconFolder.setImageResource(R.drawable.folder_black)
        iconFilter.setImageResource(R.drawable.filtr_violet)
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

        }

        iconAdd.setOnClickListener {
            // Логика для добавления фотографий
        }

        arrowDown.setOnClickListener {
            openedFirstContainer.visibility = if (openedFirstContainer.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        // Устанавливаем обработчик для первого календаря
        iconCalendarSince.setOnClickListener {
            openDatePicker(editTextDate)
        }

        // Устанавливаем обработчик для второго календаря
        iconCalendarTo.setOnClickListener {
            openDatePicker(editTextDate2)
        }
    }

    private fun openDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                editText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)

        datePickerDialog.show()
    }
}