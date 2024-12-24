package com.safiya.photogallery

data class Photo(
    val id: Int,
    val deviceId: String,
    val createdAt: String,
    val imageData: ByteArray,
    val title: String,
    val tags: Array<String>
)