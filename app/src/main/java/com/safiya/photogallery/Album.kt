import android.os.Parcel
import android.os.Parcelable

data class Album(
    val title: String,
    val createdAt: String,
    val deviceId: String,
    val imageIds: List<Long>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "", // deviceId
        parcel.createLongArray()?.toList() ?: emptyList() // Преобразуем массив в список
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(createdAt) // Дата создания
        parcel.writeString(deviceId) // Идентификатор устройства
        parcel.writeLongArray(imageIds.toLongArray()) // Преобразуем список в массив
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Album> {
        override fun createFromParcel(parcel: Parcel): Album {
            return Album(parcel)
        }

        override fun newArray(size: Int): Array<Album?> {
            return arrayOfNulls(size)
        }
    }
}