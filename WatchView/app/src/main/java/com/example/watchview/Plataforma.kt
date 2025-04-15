package com.example.watchview

import android.os.Parcel
import android.os.Parcelable

data class Plataforma(
    val idPlataforma: String,
    val nombrePlataforma: String,
    val idTitulo: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idPlataforma)
        parcel.writeString(nombrePlataforma)
        parcel.writeString(idTitulo)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Plataforma> {
        override fun createFromParcel(parcel: Parcel): Plataforma {
            return Plataforma(parcel)
        }

        override fun newArray(size: Int): Array<Plataforma?> {
            return arrayOfNulls(size)
        }
    }
}

