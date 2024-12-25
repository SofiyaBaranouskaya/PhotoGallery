package com.safiya.photogallery

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun getAllPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val db = readableDatabase

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

                photos.add(Photo(id, deviceId, createdAt, imageData, title, tags))            }
        }
        cursor.close()
        return photos
    }

    fun deleteAllPhotos() {
        val db = writableDatabase
        db.execSQL("DELETE FROM ${FeedReaderContract.PhotoEntry.TABLE_NAME}")
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"

        // Определите строки SQL
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${FeedReaderContract.PhotoEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," + // Уникальный идентификатор
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID} VARCHAR(255) NOT NULL," + // ID устройства
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} TIMESTAMP DEFAULT CURRENT_TIMESTAMP," + // Дата добавления
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_DATA} BLOB NOT NULL," + // Данные изображения
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE} VARCHAR(255)," + // Название фотографии
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS} TEXT);" // Теги в виде строки

        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${FeedReaderContract.PhotoEntry.TABLE_NAME}"
    }
}