package com.safiya.photogallery

import android.provider.BaseColumns

object FeedReaderContract {
    object PhotoEntry : BaseColumns {
        const val TABLE_NAME = "photos"

        const val COLUMN_NAME_DEVICE_ID = "device_id"
        const val COLUMN_NAME_CREATED_AT = "created_at"
        const val COLUMN_NAME_IMAGE_PATH = "image_path" // Путь к изображению
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_TAGS = "tags"
    }

    object AlbumEntry : BaseColumns {
        const val TABLE_NAME = "photo_albums"

        const val COLUMN_NAME_DEVICE_ID = "device_id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_CREATED_AT = "created_at"
        const val COLUMN_NAME_IMAGE_IDS = "image_ids" // Массив идентификаторов изображений
    }
}

// SQL-запрос для создания таблицы
private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${FeedReaderContract.PhotoEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," + // Уникальный идентификатор
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID} TEXT NOT NULL," + // ID устройства
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} TEXT DEFAULT CURRENT_TIMESTAMP," + // Дата добавления
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_PATH} TEXT NOT NULL," + // Путь к изображению
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE} TEXT," + // Название фотографии
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS} TEXT);" // Теги (в виде строки)

private const val SQL_CREATE_ALBUMS =
    "CREATE TABLE ${FeedReaderContract.AlbumEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "${FeedReaderContract.AlbumEntry.COLUMN_NAME_DEVICE_ID} TEXT NOT NULL," +
            "${FeedReaderContract.AlbumEntry.COLUMN_NAME_TITLE} TEXT NOT NULL," +
            "${FeedReaderContract.AlbumEntry.COLUMN_NAME_CREATED_AT} TEXT DEFAULT CURRENT_TIMESTAMP," +
            "${FeedReaderContract.AlbumEntry.COLUMN_NAME_IMAGE_IDS} TEXT);"