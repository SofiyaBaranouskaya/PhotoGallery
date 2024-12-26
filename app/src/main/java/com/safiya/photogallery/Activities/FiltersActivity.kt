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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.safiya.photogallery.Adapters.FilterPhotoAdapter
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
    private lateinit var recyclerView5: RecyclerView
    private lateinit var photoAdapter: FilterPhotoAdapter

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
        photoAdapter = FilterPhotoAdapter(this, mutableListOf()) {}
        recyclerView.adapter = photoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Инициализация RecyclerView для тегов
        recyclerView5 = findViewById(R.id.recyclerView5)
        val tagPhotoAdapter = FilterPhotoAdapter(this, mutableListOf()) {}
        recyclerView5.adapter = tagPhotoAdapter
        recyclerView5.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

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

        // Устанавливаем обработчик для поиска по тегам
        val imageView2: ImageView = findViewById(R.id.imageView2)
        imageView2.setOnClickListener {
            val tagsFiltrText: TextView = findViewById(R.id.editTextTags)
            val tagsInput = tagsFiltrText.text.toString().trim()
            val filteredPhotos = getPhotosByTags(tagsInput)
            tagPhotoAdapter.updateData(filteredPhotos)

            // Показать RecyclerView, если есть фотографии
            recyclerView5.visibility = if (filteredPhotos.isNotEmpty()) View.VISIBLE else View.GONE
            // Отображение textView10 в зависимости от результатов
            val textView10: TextView = findViewById(R.id.textView10)
            if (filteredPhotos.isEmpty()) {
                textView10.visibility = View.VISIBLE
            } else {
                textView10.visibility = View.GONE
            }
            // Скрыть textView10, если поле для ввода тегов пустое
            if (tagsInput.isEmpty()) {
                textView10.visibility = View.GONE
            }
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
        val noPhotosTextView: TextView = findViewById(R.id.textView9)
        val currentDate = System.currentTimeMillis()

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

        // Установка цвета текста
        dateFiltrText.setTextColor(
            if (dateFiltrText.text == "Choose dates") {
                ContextCompat.getColor(this, android.R.color.darker_gray)
            } else {
                ContextCompat.getColor(this, android.R.color.black)
            }
        )

        // Обработка конечной даты для включения всего дня
        val adjustedEndTimestamp = endTimestamp?.plus(24 * 60 * 60 * 1000 - 1)

        // Получение фотографий за выбранный период
        val photos = getPhotosByDateRange(startDate, adjustedEndTimestamp?.let { convertTimestampToDateString(it) })
        photoAdapter.updateData(photos)

        // Показать или скрыть сообщения о фотографиях
        if (photos.isEmpty()) {
            noPhotosTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            noPhotosTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        // Обновляем привязку second_filter_container в зависимости от видимости
        updateSecondFilterContainerConstraints(noPhotosTextView.isVisible, recyclerView.isVisible)
    }

    private fun updateSecondFilterContainerConstraints(isNoPhotosVisible: Boolean, isPhotosVisible: Boolean) {
        val secondFilterContainer: View = findViewById(R.id.second_filter_container)
        val layoutParams = secondFilterContainer.layoutParams as ConstraintLayout.LayoutParams

        if (isNoPhotosVisible) {
            layoutParams.topToBottom = R.id.textView9
        } else if (isPhotosVisible) {
            layoutParams.topToBottom = R.id.recyclerView3
        }

        secondFilterContainer.layoutParams = layoutParams
        secondFilterContainer.requestLayout()
    }

    private fun cancelChanges() {
        openedFirstContainer.visibility = View.GONE
        editTextDate.setText("")
        editTextDate2.setText("")
        startDate = null
        endDate = null

        val dateFiltrText: TextView = findViewById(R.id.dateFiltrText)
        dateFiltrText.text = "Choose dates"
        dateFiltrText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))

        val noPhotosTextView: TextView = findViewById(R.id.textView9)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView3)

        // Скрываем textView9 и recyclerView3 при отмене
        noPhotosTextView.visibility = View.GONE
        recyclerView.visibility = View.GONE

        // Получаем все фотографии
        val allPhotos = getPhotosByDateRange(null, null)
        photoAdapter.updateData(allPhotos)

        // Показать или скрыть сообщения о фотографиях
        if (allPhotos.isEmpty()) {
            // Здесь мы не показываем noPhotosTextView, так как он уже скрыт
            noPhotosTextView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
        }

        // Обновляем привязку второго контейнера
        updateSecondFilterContainerConstraints(noPhotosTextView.isVisible, recyclerView.isVisible)
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
            return photos
        }

        val selection = StringBuilder()
        val selectionArgs = mutableListOf<String>()

        val startTimestamp = start?.let { convertToTimestamp(it) }
        var endTimestamp = end?.let { convertToTimestamp(it) }

        if (endTimestamp != null) {
            endTimestamp += 24 * 60 * 60 * 1000 - 1
        }

        when {
            startTimestamp != null && endTimestamp != null -> {
                selection.append("${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} BETWEEN ? AND ?")
                selectionArgs.add(startTimestamp.toString())
                selectionArgs.add(endTimestamp.toString())
            }
            startTimestamp != null -> {
                selection.append("${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} >= ?")
                selectionArgs.add(startTimestamp.toString())
            }
            endTimestamp != null -> {
                selection.append("${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} <= ?")
                selectionArgs.add(endTimestamp.toString())
            }
        }

        val cursor = db.query(
            FeedReaderContract.PhotoEntry.TABLE_NAME,
            null,
            selection.toString(),
            selectionArgs.toTypedArray(),
            null,
            null,
            null
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

    private fun getPhotosByTags(tags: String): List<Photo> {
        val dbHelper = FeedReaderDbHelper(this)
        val photos = mutableListOf<Photo>()
        val db = dbHelper.readableDatabase

        if (tags.isEmpty()) return photos

        val selection = "${FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS} LIKE ?"
        val selectionArgs = arrayOf("%$tags%")

        val cursor = db.query(
            FeedReaderContract.PhotoEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val deviceId = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID))
                val createdAt = getLong(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT))
                val imagePath = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH))
                val title = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE))
                val tagsString = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS))
                val tagsArray = tagsString.split(",").toTypedArray()

                photos.add(Photo(id, deviceId, createdAt.toString(), imagePath, title, tagsArray))
            }
        }
        cursor.close()
        return photos
    }

    private fun convertToTimestamp(dateString: String): Long {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return format.parse(dateString)?.time ?: 0L
    }
}