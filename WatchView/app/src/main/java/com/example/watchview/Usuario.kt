package com.example.watchview

object Usuario {
    var correo: String =""
    var nombre: String? = null
    var pass: String? = null
    var fotoPerfil: String? = null
    var privilegios: String? = null

    fun resetearDatos() {
        correo = ""
        nombre = null
        pass = null
        fotoPerfil = null
        privilegios = null
    }

}