package com.safiya.photogallery

import Album
import android.content.ContentValues
import android.database.Cursor
import android.provider.BaseColumns
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlbumRepository(private val dbHelper: FeedReaderDbHelper) {

    // Функция для форматирования даты из миллисекунд
    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    fun getAllAlbums(): List<Album> {
        val albums = mutableListOf<Album>()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.AlbumEntry.COLUMN_NAME_DEVICE_ID,
            FeedReaderContract.AlbumEntry.COLUMN_NAME_TITLE,
            FeedReaderContract.AlbumEntry.COLUMN_NAME_CREATED_AT,
            FeedReaderContract.AlbumEntry.COLUMN_NAME_IMAGE_IDS
        )

        var cursor: Cursor? = null
        try {
            cursor = db.query(
                FeedReaderContract.AlbumEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
            )

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val deviceId = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.AlbumEntry.COLUMN_NAME_DEVICE_ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.AlbumEntry.COLUMN_NAME_TITLE))
                    val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(FeedReaderContract.AlbumEntry.COLUMN_NAME_CREATED_AT))
                    val formattedDate = formatDate(createdAt) // Форматируем дату
                    val imageIdsString = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.AlbumEntry.COLUMN_NAME_IMAGE_IDS))

                    val imageIds = if (imageIdsString.isNotEmpty()) {
                        imageIdsString.split(",").mapNotNull { it.toLongOrNull() } // Преобразование в Long
                    } else {
                        emptyList()
                    }

                    albums.add(Album(title, formattedDate, deviceId, imageIds)) // Используем formattedDate
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("AlbumRepository", "Error fetching albums", e)
        } finally {
            cursor?.close()
        }

        return albums
    }

    fun insertAlbum(album: Album) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FeedReaderContract.AlbumEntry.COLUMN_NAME_TITLE, album.title)
            put(FeedReaderContract.AlbumEntry.COLUMN_NAME_CREATED_AT, System.currentTimeMillis()) // Сохраняем текущее время
            put(FeedReaderContract.AlbumEntry.COLUMN_NAME_DEVICE_ID, album.deviceId)
            put(FeedReaderContract.AlbumEntry.COLUMN_NAME_IMAGE_IDS, album.imageIds.joinToString(",")) // Сохраняем идентификаторы как строку
        }

        try {
            val newRowId = db.insert(FeedReaderContract.AlbumEntry.TABLE_NAME, null, values)
            if (newRowId == -1L) {
                Log.e("AlbumRepository", "Error inserting album")
            } else {
                Log.d("AlbumRepository", "Album inserted: ID = $newRowId, $album")
            }
        } catch (e: Exception) {
            Log.e("AlbumRepository", "Error inserting album", e)
        } finally {
            db.close()
        }
    }

    fun deleteAllAlbums() {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM ${FeedReaderContract.AlbumEntry.TABLE_NAME}")
    }

    // Новый метод для получения фотографий для альбома
    fun getPhotosForAlbum(album: Album): List<Photo> {
        // Получаем список изображений по идентификаторам
        return album.imageIds.mapNotNull { getPhotoById(it) } // Получаем объект Photo по ID
    }

    private fun getPhotoById(id: Long): Photo? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            FeedReaderContract.PhotoEntry.TABLE_NAME,
            null,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            // Извлечение данных из курсора и создание объекта Photo
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH))
            val deviceId = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID))
            val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE))
            val tagsString = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS))
            val tags = tagsString.split(",").toTypedArray() // Преобразуем строку в массив тегов

            Photo(
                id = id,
                imagePath = imagePath,
                deviceId = deviceId,
                createdAt = createdAt,
                title = title,
                tags = tags // Передаем теги
            )
        } else {
            null
        }.also {
            cursor.close()
        }
    }
}