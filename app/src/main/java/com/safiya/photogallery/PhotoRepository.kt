package com.safiya.photogallery

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

class PhotoRepository(private val dbHelper: FeedReaderDbHelper) {

    fun getAllPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_DATA,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS
        )

        val cursor = db.query(
            FeedReaderContract.PhotoEntry.TABLE_NAME,
            projection,
            null, // no selection
            null, // no selection args
            null, // don't group the rows
            null, // don't filter by row groups
            null  // the sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val deviceId = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID))
                val createdAt = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT))
                val imageData = getBlob(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_DATA))
                val title = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE))
                val tagsString = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS))
                val tags = tagsString.split(",").toTypedArray() // Преобразуем строку в массив

                photos.add(Photo(id.toInt(), deviceId, createdAt, imageData, title, tags))
            }
        }
        cursor.close()
        return photos
    }

    fun insertPhoto(photo: Photo) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID, photo.deviceId)
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT, photo.createdAt)
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_DATA, photo.imageData)
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE, photo.title)
            put(FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS, photo.tags.joinToString(",")) // Преобразуем массив в строку
        }

        db.insert(FeedReaderContract.PhotoEntry.TABLE_NAME, null, values)
    }

    fun deleteAllPhotos() {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM ${FeedReaderContract.PhotoEntry.TABLE_NAME}")
    }
}