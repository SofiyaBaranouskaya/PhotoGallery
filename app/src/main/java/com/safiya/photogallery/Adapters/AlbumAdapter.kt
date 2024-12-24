package com.safiya.photogallery

import Album
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlbumAdapter(
    private val context: Context,
    private val albums: List<Album>,
    private val onAlbumClick: (Album) -> Unit // Лямбда для обработки нажатий
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val albumImageView: ImageView = itemView.findViewById(R.id.albumImageView)
        private val albumTitleTextView: TextView = itemView.findViewById(R.id.albumTitleTextView)
        private val albumDateTextView: TextView = itemView.findViewById(R.id.albumDateTextView)

        fun bind(album: Album) {
            // Загрузка изображения из ресурсов
            albumImageView.setImageResource(album.imageResId)
            albumTitleTextView.text = album.title
            albumDateTextView.text = album.date

            // Обработка нажатия на элемент
            itemView.setOnClickListener {
                onAlbumClick(album) // Вызываем лямбда-функцию при нажатии
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount(): Int {
        return albums.size
    }
}