package com.safiya.photogallery

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhotoAdapter(
    private val context: Context,
    private val photoList: List<Photo>,
    private val onDeleteClick: () -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.photo_image)

        init {
            itemView.setOnClickListener {
                val byteArray = photoList[adapterPosition].imageData
                val intent = Intent(context, PhotoDetailActivity::class.java)
                intent.putParcelableArrayListExtra("photoList", ArrayList(photoList)) // Передаем список фотографий
                intent.putExtra("currentIndex", adapterPosition) // Передаем текущий индекс
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]
        holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(photo.imageData, 0, photo.imageData.size))
    }

    override fun getItemCount(): Int = photoList.size
}