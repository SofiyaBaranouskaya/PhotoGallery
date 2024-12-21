//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import com.safiya.photogallery.PhotoItem
//import Album
//import android.util.Log
//import com.safiya.photogallery.R
//
//class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
//
//    override fun onCreate(db: SQLiteDatabase) {
//        // Создание таблицы фотографий
//        val createPhotosTable = """
//            CREATE TABLE photos (
//                id INTEGER PRIMARY KEY AUTOINCREMENT,
//                device_id TEXT NOT NULL,
//                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//                image_data BLOB NOT NULL,
//                title TEXT,
//                tags TEXT,
//                album_id INTEGER
//            )
//        """
//        db.execSQL(createPhotosTable)
//
//        // Создание таблицы альбомов
//        val createAlbumsTable = """
//            CREATE TABLE photo_albums (
//                id INTEGER PRIMARY KEY AUTOINCREMENT,
//                device_id TEXT NOT NULL,
//                title TEXT NOT NULL,
//                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//            )
//        """
//        db.execSQL(createAlbumsTable)
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS photos")
//        db.execSQL("DROP TABLE IF EXISTS photo_albums")
//        onCreate(db)
//    }
//
//    companion object {
//        private const val DATABASE_NAME = "photo_album.db"
//        private const val DATABASE_VERSION = 1
//    }
//
//    // Метод для получения фотографий по идентификатору альбома
//    fun getPhotosForAlbum(albumId: Int): List<PhotoItem> {
//        val photos = mutableListOf<PhotoItem>()
//        val db: SQLiteDatabase = this.readableDatabase
//
//        // Выполняем SQL запрос для получения фотографий по albumId
//        val cursor = db.query(
//            "photos",
//            arrayOf("image_data", "title"),
//            "album_id = ?",
//            arrayOf(albumId.toString()),
//            null,
//            null,
//            null
//        )
//
//        if (cursor.moveToFirst()) {
//            do {
//                val imageDataIndex = cursor.getColumnIndex("image_data")
//                val titleIndex = cursor.getColumnIndex("title")
//
//                // Проверяем индексы
//                if (imageDataIndex != -1 && titleIndex != -1) {
//                    val imageData = cursor.getBlob(imageDataIndex)
//                    val title = cursor.getString(titleIndex)
//                    photos.add(PhotoItem(imageData, title))
//                }
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        return photos
//    }
//
//    // Метод для получения всех альбомов
//    fun getAllAlbums(): List<Album> {
//        val albums = mutableListOf<Album>()
//        val db: SQLiteDatabase = this.readableDatabase
//
//        // Запрос для получения всех альбомов
//        val cursor = db.query(
//            "photo_albums",
//            arrayOf("id", "title", "created_at"),
//            null,
//            null,
//            null,
//            null,
//            null
//        )
//
//        // Проверка на наличие данных в курсоре
//        if (cursor.moveToFirst()) {
//            do {
//                // Получение индексов столбцов
//                val idIndex = cursor.getColumnIndex("id")
//                val titleIndex = cursor.getColumnIndex("title")
//                val dateIndex = cursor.getColumnIndex("created_at")
//
//                // Проверяем индексы
//                if (idIndex != -1 && titleIndex != -1 && dateIndex != -1) {
//                    val id = cursor.getInt(idIndex)
//                    val title = cursor.getString(titleIndex)
//                    val date = cursor.getString(dateIndex)
//
//                    // Укажите ресурс изображения по умолчанию или добавьте логику для его получения
//                    val imageResId = R.drawable.album2 // Замените на ваш ресурс изображения
//
//                    albums.add(Album(id, title, date, imageResId)) // Создание объекта Album
//                } else {
//                    // Логирование для отладки
//                    Log.e("DatabaseHelper", "Column index not found. idIndex: $idIndex, titleIndex: $titleIndex, dateIndex: $dateIndex")
//                }
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        return albums
//    }
//}