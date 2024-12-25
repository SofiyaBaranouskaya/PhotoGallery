package com.safiya.photogallery

import android.os.Parcel
import android.os.Parcelable

data class Photo(
    val id: Long,
    val deviceId: String,
    val createdAt: String,
    val imageData: ByteArray,
    val title: String,
    val tags: Array<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createByteArray() ?: ByteArray(0),
        parcel.readString() ?: "",
        parcel.createStringArray() ?: arrayOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(deviceId)
        parcel.writeString(createdAt)
        parcel.writeByteArray(imageData)
        parcel.writeString(title)
        parcel.writeStringArray(tags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}