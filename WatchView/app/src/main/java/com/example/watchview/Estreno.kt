package com.example.watchview

import android.os.Parcel
import android.os.Parcelable

data class Estreno(
    val idEstreno: Int,
    val fechaEstreno: String,
    val idTitulo: String,
    val idPlataforma: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idEstreno)
        parcel.writeString(fechaEstreno)
        parcel.writeString(idTitulo)
        parcel.writeString(idPlataforma)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Estreno> {
        override fun createFromParcel(parcel: Parcel): Estreno {
            return Estreno(parcel)
        }

        override fun newArray(size: Int): Array<Estreno?> {
            return arrayOfNulls(size)
        }
    }
}

