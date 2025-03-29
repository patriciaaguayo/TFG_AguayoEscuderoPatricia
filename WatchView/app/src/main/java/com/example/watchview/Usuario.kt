package com.example.watchview

object Usuario {
    var correo: String =""
    var nombre: String? = null
    var pass: String? = null
    var direccion: String? = null
    var fotoPerfil: String? = null
    var privilegios: String? = null
    var tlfn: String? = null

    fun resetearDatos() {
        correo = ""
        nombre = null
        pass = null
        direccion = null
        fotoPerfil = null
        privilegios = null
        tlfn = null
    }

}