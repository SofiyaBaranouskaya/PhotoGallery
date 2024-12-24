package com.safiya.photogallery

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoRepository: PhotoRepository
    private lateinit var noPhotosMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        noPhotosMessage = findViewById(R.id.no_photos_message)
        val iconAdd: ImageView = findViewById(R.id.icon_add)

        iconAdd.setImageResource(R.drawable.add_black)

        iconAdd.setOnClickListener {
            showAddOptionsDialog()
        }

        photoRepository = PhotoRepository(FeedReaderDbHelper(this))

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager

        val photoList = photoRepository.getAllPhotos()

        if (photoList.isEmpty()) {
            noPhotosMessage.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            noPhotosMessage.visibility = TextView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE

            val photoAdapter = PhotoAdapter(this, photoList) {
                noPhotosMessage.visibility = TextView.VISIBLE
                recyclerView.visibility = RecyclerView.GONE
            }
            recyclerView.adapter = photoAdapter
        }
    }

    private fun showAddOptionsDialog() {
        val options = arrayOf("Photos", "Album")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // Логика для добавления фотографий (открытие камеры)
                    openCamera()
                }
                1 -> {
                    // Логика для добавления альбома
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                showPhotoDetailsDialog(it) // Показываем диалог для ввода деталей
            }
        }
    }

    private fun showPhotoDetailsDialog(imageBitmap: Bitmap) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_photo_details, null)
        val titleEditText: EditText = dialogView.findViewById(R.id.title_edit_text)
        val tagsEditText: EditText = dialogView.findViewById(R.id.tags_edit_text)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введите детали фотографии")
        builder.setView(dialogView)
        builder.setPositiveButton("OK") { dialog, _ ->
            val title = titleEditText.text.toString().ifEmpty { "-" }
            val tags = tagsEditText.text.toString().ifEmpty { "-" }
            savePhotoToDatabase(imageBitmap, title, tags)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun savePhotoToDatabase(imageBitmap: Bitmap, title: String, tags: String) {
        // Преобразуем Bitmap в массив байтов
        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val imageData = byteArrayOutputStream.toByteArray()

        // Получаем deviceId
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // Создаем объект Photo
        val photo = Photo(
            id = 0, // ID будет сгенерирован автоматически
            deviceId = deviceId,
            createdAt = System.currentTimeMillis().toString(),
            imageData = imageData,
            title = title,
            tags = tags.split(",").toTypedArray() // Разделение тегов на массив
        )

        // Сохраняем фото в базе данных
        photoRepository.insertPhoto(photo)

        // Обновляем RecyclerView
        setupRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        FeedReaderDbHelper(this).close()
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 101
    }
}