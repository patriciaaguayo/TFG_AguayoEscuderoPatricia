package com.example.watchview

import android.os.Parcel
import android.os.Parcelable

data class Genero(
    val idGenero: String,
    val nombreGenero: String,
    val idTitulo: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idGenero)
        parcel.writeString(nombreGenero)
        parcel.writeString(idTitulo)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Genero> {
        override fun createFromParcel(parcel: Parcel): Genero {
            return Genero(parcel)
        }

        override fun newArray(size: Int): Array<Genero?> {
            return arrayOfNulls(size)
        }
    }
}

