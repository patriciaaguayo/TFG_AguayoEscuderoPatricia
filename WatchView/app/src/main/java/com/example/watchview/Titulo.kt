package com.example.watchview

import android.os.Parcel
import android.os.Parcelable

data class Titulo(
    val idTitulo: String,
    val nombre: String,
    val nombreOriginal: String,
    val descripcion: String?,
    val fechaInicio: String?,
    val fechaFin: String?,
    val temporadas: Int?,
    val tipo: String?,
    val rating: Int,
    val posters: List<Poster>,
    val generos: List<Genero>,
    // val personas: List<Persona>,
    val plataformas: List<Plataforma>,
    // val estrenos: List<Estreno>,
    // val top10: List<Top10>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readInt(),
        parcel.createTypedArrayList(Poster.CREATOR) ?: emptyList(),
        parcel.createTypedArrayList(Genero.CREATOR) ?: emptyList(),
        // parcel.createTypedArrayList(Persona.CREATOR) ?: emptyList(),
        parcel.createTypedArrayList(Plataforma.CREATOR) ?: emptyList(),
        // parcel.createTypedArrayList(Estreno.CREATOR) ?: emptyList(),
        // parcel.createTypedArrayList(Top10.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idTitulo)
        parcel.writeString(nombre)
        parcel.writeString(nombreOriginal)
        parcel.writeString(descripcion)
        parcel.writeString(fechaInicio)
        parcel.writeString(fechaFin)
        parcel.writeValue(temporadas)
        parcel.writeString(tipo)
        parcel.writeInt(rating)
        parcel.writeTypedList(posters)
        parcel.writeTypedList(generos)
        // parcel.writeTypedList(personas)
        parcel.writeTypedList(plataformas)
        // parcel.writeTypedList(estrenos)
        // parcel.writeTypedList(top10)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Titulo> {
        override fun createFromParcel(parcel: Parcel): Titulo {
            return Titulo(parcel)
        }

        override fun newArray(size: Int): Array<Titulo?> {
            return arrayOfNulls(size)
        }
    }
}
