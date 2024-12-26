package com.safiya.photogallery

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log

class PhotoRepository(private val dbHelper: FeedReaderDbHelper) {

    fun getAllPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH, // Изменено на путь к файлу
            FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS
        )

        var cursor: Cursor? = null
        try {
            cursor = db.query(
                FeedReaderContract.PhotoEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
            )

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                    val deviceId = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID))
                    val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT))
                    val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH)) // Изменено на путь к файлу
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE))
                    val tagsString = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS))
                    val tags = tagsString.split(",").toTypedArray()

                    photos.add(Photo(id, deviceId, createdAt, imagePath, title, tags))
                }
            }
        } catch (e: Exception) {
            Log.e("PhotoRepository", "Error fetching photos", e)
        } finally {
            cursor?.close()
        }

        return photos
    }

    fun insertPhoto(photo: Photo) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID, photo.deviceId)
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT, photo.createdAt)
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH, photo.imagePath) // Измените это
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE, photo.title)
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS, photo.tags.joinToString(","))
        }

        db.insert(FeedReaderContract.PhotoEntry.TABLE_NAME, null, values)
    }

    fun deleteAllPhotos() {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM ${FeedReaderContract.PhotoEntry.TABLE_NAME}")
    }

    fun getPhotosByDateRange(start: String?, end: String?): List<Photo> {
        val photos = mutableListOf<Photo>()
        val db = dbHelper.readableDatabase

        // Определяем проекцию столбцов
        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS
        )

        // Формируем условия для запроса
        val selection = "${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} BETWEEN ? AND ?"
        val selectionArgs = arrayOf(start, end)

        var cursor: Cursor? = null
        try {
            cursor = db.query(
                FeedReaderContract.PhotoEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                    val deviceId = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID))
                    val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT))
                    val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE))
                    val tagsString = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS))
                    val tags = tagsString.split(",").toTypedArray()

                    photos.add(Photo(id, deviceId, createdAt, imagePath, title, tags))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("PhotoRepository", "Error fetching photos by date range", e)
        } finally {
            cursor?.close()
        }

        return photos
    }
}