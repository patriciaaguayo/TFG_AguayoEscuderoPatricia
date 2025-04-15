package com.example.watchview

import android.os.Parcel
import android.os.Parcelable

data class Poster(
    val idPoster: Int,
    val urlPoster: String,
    val tipo: String,
    val calidad: String,
    val idTitulo: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idPoster)
        parcel.writeString(urlPoster)
        parcel.writeString(tipo)
        parcel.writeString(calidad)
        parcel.writeString(idTitulo)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Poster> {
        override fun createFromParcel(parcel: Parcel): Poster {
            return Poster(parcel)
        }

        override fun newArray(size: Int): Array<Poster?> {
            return arrayOfNulls(size)
        }
    }
}

