package com.safiya.photogallery

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoRepository: PhotoRepository
    private lateinit var noPhotosMessage: TextView
    private lateinit var photoAdapter: PhotoAdapter // Объявляем адаптер
    private lateinit var currentPhotoPath: String

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

        setupRecyclerView() // Вызов метода для настройки RecyclerView
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager

        val photoList = photoRepository.getAllPhotos()
        photoAdapter = PhotoAdapter(this, photoList.toMutableList()) {
            noPhotosMessage.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        }
        recyclerView.adapter = photoAdapter

        updateVisibility(photoList)
    }

    private fun updateVisibility(photoList: List<Photo>) {
        if (photoList.isEmpty()) {
            noPhotosMessage.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            noPhotosMessage.visibility = TextView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE
        }
    }

    private fun showAddOptionsDialog() {
        val options = arrayOf("Photos", "Album")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
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
        val photoFile = createImageFile()
        currentPhotoPath = photoFile.absolutePath
        val photoURI = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val fullSizeBitmap = getCorrectlyOrientedBitmap(currentPhotoPath) // Используем метод для ориентации
            showPhotoDetailsDialog(fullSizeBitmap)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
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
        val imagePath = saveImageToFile(imageBitmap)
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        val photo = Photo(
            id = 0,
            deviceId = deviceId,
            createdAt = System.currentTimeMillis().toString(),
            imagePath = imagePath,
            title = title,
            tags = tags.split(",").toTypedArray()
        )

        photoRepository.insertPhoto(photo)

        // Обновляем данные в адаптере
        photoAdapter.updateData(photoRepository.getAllPhotos().toMutableList())
    }

    private fun saveImageToFile(imageBitmap: Bitmap): String {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs()
        }

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFile = File(storageDir, "IMG_$timeStamp.jpg")

        try {
            val outputStream = FileOutputStream(imageFile)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("PhotoGallery", "Image saved: ${imageFile.absolutePath}") // Логируем путь
        } catch (e: Exception) {
            Log.e("PhotoGallery", "Error saving image: ${e.message}")
        }

        return imageFile.absolutePath
    }

    private fun getCorrectlyOrientedBitmap(imagePath: String): Bitmap {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        val exif = ExifInterface(imagePath)

        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        var rotation = 0
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
        }

        return if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FeedReaderDbHelper(this).close()
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 101
    }
}