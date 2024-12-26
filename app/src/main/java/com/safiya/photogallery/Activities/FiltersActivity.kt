package com.safiya.photogallery

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class FiltersActivity : AppCompatActivity() {
    private lateinit var iconHome: ImageView
    private lateinit var iconFolder: ImageView
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
        recyclerView = findViewById(R.id.recyclerView3)
        photoAdapter = PhotoAdapter(this, mutableListOf()) {} // Пустой лямбда, если не требуется обработка клика
        recyclerView.adapter = photoAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 5)

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
        val dateFiltrText: TextView = findViewById(R.id.dateFiltrText)
        val currentDate = System.currentTimeMillis() // Текущая дата в формате Unix timestamp

        // Конвертация введенных дат в timestamp
        val startTimestamp = startDate?.let { convertToTimestamp(it) }
        val endTimestamp = endDate?.let { convertToTimestamp(it) }

        // Проверка условий
        if (endTimestamp != null && endTimestamp > currentDate) {
            Toast.makeText(this, "Invalid dates.", Toast.LENGTH_SHORT).show()
            return
        }

        if (startTimestamp != null && endTimestamp != null && startTimestamp > endTimestamp) {
            Toast.makeText(this, "Invalid dates.", Toast.LENGTH_SHORT).show()
            return
        }

        // Установка текста фильтрации
        dateFiltrText.text = when {
            startDate != null && endDate != null -> "$startDate - $endDate"
            startDate != null -> "$startDate - now"
            endDate != null -> "start - $endDate"
            else -> "Choose dates"
        }

        // Обработка конечной даты для включения всего дня
        val adjustedEndTimestamp = endTimestamp?.plus(24 * 60 * 60 * 1000 - 1) // Добавить почти целый день

        // Получение фотографий за выбранный период
        val photos = getPhotosByDateRange(startDate, adjustedEndTimestamp?.let { convertTimestampToDateString(it) })
        photoAdapter.updateData(photos)

        // Показать RecyclerView, если есть фотографии
        recyclerView.visibility = if (photos.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun convertTimestampToDateString(timestamp: Long): String {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return format.format(Date(timestamp))
    }

    private fun getPhotosByDateRange(start: String?, end: String?): List<Photo> {
        val dbHelper = FeedReaderDbHelper(this)
        val photos = mutableListOf<Photo>()
        val db = dbHelper.readableDatabase

        if (start == null && end == null) {
            return photos // No dates selected, return empty
        }

        val selection = StringBuilder()
        val selectionArgs = mutableListOf<String>()

        // Преобразование введенных дат в Unix timestamp
        val startTimestamp = start?.let { convertToTimestamp(it) }
        var endTimestamp = end?.let { convertToTimestamp(it) }

        // Добавление времени до конца дня для конечной даты
        if (endTimestamp != null) {
            endTimestamp += 24 * 60 * 60 * 1000 - 1 // Увеличиваем на почти целый день
        }

        when {
            startTimestamp != null && endTimestamp != null -> {
                // Формат: 12.03.2024 - 14.03.2024
                selection.append("${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} BETWEEN ? AND ?")
                selectionArgs.add(startTimestamp.toString())
                selectionArgs.add(endTimestamp.toString())
            }
            startTimestamp != null -> {
                // Формат: 12.03.2024 - now
                selection.append("${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} >= ?")
                selectionArgs.add(startTimestamp.toString())
            }
            endTimestamp != null -> {
                // Формат: start - 12.03.2024
                selection.append("${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} <= ?")
                selectionArgs.add(endTimestamp.toString())
            }
        }

        // Запрос к базе данных
        val cursor = db.query(
            FeedReaderContract.PhotoEntry.TABLE_NAME,
            null, // Get all columns
            selection.toString(),
            selectionArgs.toTypedArray(),
            null, // Group by
            null, // Having
            null  // Order by
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val deviceId = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID))
                val createdAt = getLong(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT))
                val imagePath = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH))
                val title = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE))
                val tagsString = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS))
                val tags = tagsString.split(",").toTypedArray()

                photos.add(Photo(id, deviceId, createdAt.toString(), imagePath, title, tags))
            }
        }
        cursor.close()
        return photos
    }

    private fun convertToTimestamp(dateString: String): Long {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return format.parse(dateString)?.time ?: 0L
    }

    private fun cancelChanges() {
        // Скрыть контейнер с датами
        openedFirstContainer.visibility = View.GONE

        // Сбросить текстовые поля для дат
        editTextDate.setText("")
        editTextDate2.setText("")

        // Сбросить переменные для хранения дат
        startDate = null
        endDate = null

        // Вернуть текст в dateFiltrText к исходному значению
        val dateFiltrText: TextView = findViewById(R.id.dateFiltrText)
        dateFiltrText.text = "Choose dates" // Или любое другое исходное значение
        dateFiltrText.setTextColor(resources.getColor(R.color.black)) // Убедитесь, что цвет возвращается к исходному

        // Получить все фотографии (без фильтрации)
        val allPhotos = getPhotosByDateRange(null, null)
        photoAdapter.updateData(allPhotos)

        // Скрыть RecyclerView, если нет фотографий
        recyclerView.visibility = if (allPhotos.isNotEmpty()) View.VISIBLE else View.GONE
    }
}