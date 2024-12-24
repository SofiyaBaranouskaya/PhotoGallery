package com.safiya.photogallery

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class ImageAdapter(private val context: Context, private val images: List<Uri>, private val onImageSelected: (Uri, Boolean) -> Unit) : BaseAdapter() {

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Uri = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView = if (convertView == null) {
            LayoutInflater.from(context).inflate(R.layout.grid_item_image, parent, false) as ImageView
        } else {
            convertView as ImageView
        }

        imageView.setImageURI(images[position])

        imageView.setOnClickListener {
            val isSelected = images.contains(images[position])
            onImageSelected(images[position], !isSelected)
            imageView.alpha = if (isSelected) 1.0f else 0.5f // Изменение прозрачности для выбора
        }

        return imageView
    }
}