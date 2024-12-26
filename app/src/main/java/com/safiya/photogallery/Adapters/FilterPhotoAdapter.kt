package com.safiya.photogallery.Adapters

import com.safiya.photogallery.Photo
import com.safiya.photogallery.PhotoDetailActivity
import com.safiya.photogallery.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FilterPhotoAdapter(
    private val context: Context,
    private var photoList: MutableList<Photo>,
    private val onDeleteClick: () -> Unit
) : RecyclerView.Adapter<FilterPhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.photo_image)

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, PhotoDetailActivity::class.java)
                intent.putParcelableArrayListExtra("photoList", ArrayList(photoList))
                intent.putExtra("currentIndex", adapterPosition)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        // Используйте item_photo_filters.xml для отображения фото
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo_filters, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]
        // Загрузка изображения с использованием Glide
        Glide.with(context)
            .load(photo.imagePath) // Замените на photo.imagePath
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = photoList.size

    // Метод для обновления данных в адаптере
    fun updateData(newPhotoList: List<Photo>) {
        photoList.clear()
        photoList.addAll(newPhotoList)
        notifyDataSetChanged()
    }
}