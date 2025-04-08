package com.example.watchview

import android.os.Parcel
import android.os.Parcelable

data class Foto(
    val idFoto: Int,
    val nombreFoto: String,
    val imagen: ByteArray
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createByteArray() ?: byteArrayOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idFoto)
        parcel.writeString(nombreFoto)
        parcel.writeByteArray(imagen)
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

