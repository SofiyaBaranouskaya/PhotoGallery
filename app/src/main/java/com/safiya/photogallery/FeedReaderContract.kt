package com.safiya.photogallery

import android.provider.BaseColumns

object FeedReaderContract {
    object PhotoEntry : BaseColumns {
        const val TABLE_NAME = "photos"
        const val COLUMN_NAME_DEVICE_ID = "device_id"
        const val COLUMN_NAME_CREATED_AT = "created_at"
        const val COLUMN_NAME_IMAGE_DATA = "image_data"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_TAGS = "tags"
    }
}

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${FeedReaderContract.PhotoEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} SERIAL PRIMARY KEY," +  // Уникальный идентификатор
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_DEVICE_ID} VARCHAR(255) NOT NULL," + // ID устройства
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_CREATED_AT} TIMESTAMP DEFAULT CURRENT_TIMESTAMP," + // Дата добавления
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_IMAGE_DATA} BYTEA NOT NULL," + // Данные изображения
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_TITLE} VARCHAR(255)," + // Название фотографии
            "${FeedReaderContract.PhotoEntry.COLUMN_NAME_TAGS} TEXT[]);" // Теги