package com.safiya.photogallery

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhotoAdapter(
    private val context: Context,
    private val photoList: List<Photo>,
    private val onEmpty: () -> Unit // Функция для обработки пустого списка
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]

        if (photo.imageData.isNotEmpty()) {
            holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(photo.imageData, 0, photo.imageData.size))
            holder.imageView.visibility = View.VISIBLE // Показываем ImageView
        } else {
            holder.imageView.visibility = View.GONE // Скрываем ImageView, если данных нет
        }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.photo_image)
    }
}