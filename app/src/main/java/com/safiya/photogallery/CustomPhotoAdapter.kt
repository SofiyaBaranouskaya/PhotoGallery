package com.safiya.photogallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Импортируйте Glide для загрузки изображений

class CustomPhotoAdapter(
    private val context: Context,
    private var photos: MutableList<Photo>,
    private val onPhotoClick: (Photo) -> Unit
) : RecyclerView.Adapter<CustomPhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.photo_image_view)
        private val checkBox: CheckBox = itemView.findViewById(R.id.photo_checkbox)

        init {
            // Обработка изменений состояния чекбокса
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                photos[adapterPosition].isSelected = isChecked // Обновляем состояние выбора
                onPhotoClick(photos[adapterPosition]) // Вызываем коллбек
            }

            // Обработка клика по элементу
            itemView.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked // Переключаем состояние чекбокса
                checkBox.performClick() // Вызываем обработчик изменения состояния
            }
        }

        // Метод для привязки данных к элементам
        fun bind(photo: Photo) {
            // Загрузка изображения с использованием Glide
            Glide.with(context)
                .load(photo.imagePath) // Замените на правильный путь к изображению
                .into(imageView)

            checkBox.isChecked = photo.isSelected // Устанавливаем состояние чекбокса
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.bind(photo) // Привязываем данные к элементу
    }

    override fun getItemCount(): Int = photos.size

    // Метод для получения выбранных идентификаторов
    fun getSelectedPhotoIds(): List<Long> {
        return photos.filter { it.isSelected }.map { it.id }
    }

    // Метод для обновления данных в адаптере
    fun updateData(newPhotoList: List<Photo>) {
        photos.clear()
        photos.addAll(newPhotoList)
        notifyDataSetChanged()
    }
}