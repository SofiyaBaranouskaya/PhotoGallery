package com.safiya.photogallery

import Album
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES) // Создание таблицы photos
        db.execSQL(SQL_CREATE_ALBUMS) // Создание таблицы photo_albums
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES) // Удаление таблицы photos
        db.execSQL(SQL_DELETE_ALBUMS) // Удаление таблицы photo_albums
        onCreate(db) // Создание таблиц заново
    }

    fun getAllPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val db = readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT,
            FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH,
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
                val imagePath = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH))
                val title = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE))
                val tagsString = getString(getColumnIndexOrThrow(FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS))
                val tags = tagsString.split(",").toTypedArray()

                photos.add(Photo(id, deviceId, createdAt, imagePath, title, tags))
            }
        }
        cursor.close()
        return photos
    }

    fun getAllAlbums(): List<Album> {
        val albums = mutableListOf<Album>()
        val db = readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.AlbumEntry.COLUMN_NAME_DEVICE_ID,
            FeedReaderContract.AlbumEntry.COLUMN_NAME_TITLE,
            FeedReaderContract.AlbumEntry.COLUMN_NAME_CREATED_AT,
            FeedReaderContract.AlbumEntry.COLUMN_NAME_IMAGE_IDS
        )

        val cursor = db.query(
            FeedReaderContract.AlbumEntry.TABLE_NAME,
            projection,
            null, // no selection
            null, // no selection args
            null, // don't group the rows
            null, // don't filter by row groups
            null  // the sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val deviceId = getString(getColumnIndexOrThrow(FeedReaderContract.AlbumEntry.COLUMN_NAME_DEVICE_ID))
                val title = getString(getColumnIndexOrThrow(FeedReaderContract.AlbumEntry.COLUMN_NAME_TITLE))
                val createdAt = getString(getColumnIndexOrThrow(FeedReaderContract.AlbumEntry.COLUMN_NAME_CREATED_AT))
                val imageIdsString = getString(getColumnIndexOrThrow(FeedReaderContract.AlbumEntry.COLUMN_NAME_IMAGE_IDS))
                val imageIds = imageIdsString.split(",").map { it.toLong() } // Преобразуем строку в список идентификаторов

                albums.add(Album(title, createdAt, deviceId, imageIds)) // Передаем все необходимые данные
            }
        }
        cursor.close()
        return albums
    }

    fun deleteAllPhotos() {
        val db = writableDatabase
        db.execSQL("DELETE FROM ${FeedReaderContract.PhotoEntry.TABLE_NAME}")
    }

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "FeedReader.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${FeedReaderContract.PhotoEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID} TEXT NOT NULL," +
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH} TEXT NOT NULL," +
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE} TEXT," +
                    "${FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS} TEXT);"

        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${FeedReaderContract.PhotoEntry.TABLE_NAME}"

        private const val SQL_CREATE_ALBUMS =
            "CREATE TABLE ${FeedReaderContract.AlbumEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${FeedReaderContract.AlbumEntry.COLUMN_NAME_DEVICE_ID} TEXT NOT NULL," +
                    "${FeedReaderContract.AlbumEntry.COLUMN_NAME_TITLE} TEXT NOT NULL," +
                    "${FeedReaderContract.AlbumEntry.COLUMN_NAME_CREATED_AT} TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "${FeedReaderContract.AlbumEntry.COLUMN_NAME_IMAGE_IDS} TEXT);"

        private const val SQL_DELETE_ALBUMS =
            "DROP TABLE IF EXISTS ${FeedReaderContract.AlbumEntry.TABLE_NAME}"
    }
}