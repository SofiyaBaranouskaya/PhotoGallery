package com.safiya.photogallery

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class FiltersActivity : AppCompatActivity() {
    private lateinit var iconHome: ImageView
    private lateinit var iconFolder: ImageView
    private lateinit var iconAdd: ImageView
    private lateinit var arrowDown: ImageView
    private lateinit var openedFirstContainer: View
    private lateinit var editTextDate: EditText
    private lateinit var editTextDate2: EditText
    private lateinit var iconCalendarSince: ImageView
    private lateinit var iconCalendarTo: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter

    private var startDate: String? = null
    private var endDate: String? = null

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
        arrowDown = findViewById(R.id.imageView)
        openedFirstContainer = findViewById(R.id.opened_first_container)
        editTextDate = findViewById(R.id.editTextDate)
        editTextDate2 = findViewById(R.id.editTextDate2)
        iconCalendarSince = findViewById(R.id.icon_calendarSince)
        iconCalendarTo = findViewById(R.id.icon_calendarTo)

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        photoAdapter = PhotoAdapter(this, mutableListOf()) {
            // Здесь можно добавить логику для удаления, если потребуется
        }
        recyclerView.adapter = photoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Устанавливаем обработчики нажатий
        iconHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
        }

        iconFolder.setOnClickListener {
            startActivity(Intent(this, AlbumsActivity::class.java))
            overridePendingTransition(0, 0)
        }

        arrowDown.setOnClickListener {
            if (openedFirstContainer.visibility == View.GONE) {
                openedFirstContainer.visibility = View.VISIBLE
                arrowDown.setImageResource(R.drawable.arrow_up)
            } else {
                openedFirstContainer.visibility = View.GONE
                arrowDown.setImageResource(R.drawable.arrow_down)
            }
        }

        // Устанавливаем обработчики для календарей
        iconCalendarSince.setOnClickListener {
            openDatePicker(editTextDate, true)
        }

        iconCalendarTo.setOnClickListener {
            openDatePicker(editTextDate2, false)
        }

        // Устанавливаем обработчик для кнопки "Confirm"
        val buttonConfirm: Button = findViewById(R.id.buttonConfirm)
        buttonConfirm.setOnClickListener {
            confirmDates()
            openedFirstContainer.visibility = View.GONE
        }

        // Устанавливаем обработчик для кнопки "Cancel"
        val buttonCancel: Button = findViewById(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            cancelChanges()
        }
    }

    private fun openDatePicker(editText: EditText, isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d.%02d.%04d", selectedDay, selectedMonth + 1, selectedYear)
                if (isStartDate) {
                    startDate = formattedDate
                    editText.setText(formattedDate)
                } else {
                    endDate = formattedDate
                    editText.setText(formattedDate)
                }
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun confirmDates() {
        // Логика для отображения выбранных дат
        val dateFiltrText: TextView = findViewById(R.id.dateFiltrText)
        dateFiltrText.text = when {
            startDate != null && endDate != null -> "$startDate - $endDate"
            startDate != null -> "$startDate - now"
            endDate != null -> "start - $endDate"
            else -> "No dates selected"
        }

        // Получение фотографий за выбранный период
        val photos = getPhotosByDateRange(startDate, endDate)
        photoAdapter.updateData(photos)

        // Показать RecyclerView, если есть фотографии
        recyclerView.visibility = if (photos.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun getPhotosByDateRange(start: String?, end: String?): List<Photo> {
        // Логика для получения фотографий из базы данных или другого источника
        return listOf() // Замените на реальную логику получения фотографий
    }

    private fun cancelChanges() {
        openedFirstContainer.visibility = View.GONE
        editTextDate.setText("")
        editTextDate2.setText("")
    }
}