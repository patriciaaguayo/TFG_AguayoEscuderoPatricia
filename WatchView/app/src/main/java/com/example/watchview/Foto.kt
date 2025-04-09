package com.example.watchview

import android.os.Parcel
import android.os.Parcelable

data class Foto(
    val idFoto: Int,
    val nombreFoto: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idFoto)
        parcel.writeString(nombreFoto)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Foto> {
        override fun createFromParcel(parcel: Parcel): Foto {
            return Foto(parcel)
        }

        override fun newArray(size: Int): Array<Foto?> {
            return arrayOfNulls(size)
        }
    }
}


