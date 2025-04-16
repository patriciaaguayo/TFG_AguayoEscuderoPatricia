package com.example.watchview

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.database.getStringOrNull
import java.io.File

class BBDD(context: Context) : SQLiteOpenHelper(context, "WatchViewBBDD.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        val tablaUsuario = """
            CREATE TABLE Usuario (
                correo TEXT PRIMARY KEY,
                nombre TEXT NOT NULL UNIQUE,
                pass TEXT NOT NULL,
                idFoto INTEGER DEFAULT 1,
                privilegios TEXT,
                FOREIGN KEY (idFoto) REFERENCES FotoPerfil(idFoto)
            );
        """

        val tablaFotoPerfil = """
            CREATE TABLE FotoPerfil (
               idFoto INTEGER PRIMARY KEY AUTOINCREMENT,
               nombreFoto TEXT NOT NULL UNIQUE
            );
        """

        val tablaTitulo ="""
            CREATE TABLE Titulo (
                idTitulo TEXT PRIMARY KEY,
                nombre TEXT NOT NULL,
                nombreOriginal TEXT,
                descripcion TEXT,
                fechaInicio TEXT NOT NULL,
                fechaFin TEXT,
                temporadas INTEGER,
                tipo TEXT NOT NULL CHECK (tipo IN ('movie', 'series')),
                rating INTEGER NOT NULL
            );
        """

        val tablaPoster_Titulo = """
            CREATE TABLE Poster_Titulo (
                idPoster INTEGER PRIMARY KEY AUTOINCREMENT,
                urlPoster TEXT NOT NULL,
                tipo TEXT NOT NULL CHECK (tipo IN ('vertical', 'horizontal')),
                calidad TEXT NOT NULL,
                idTitulo TEXT NOT NULL,
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
                );
        """

        val tablaGenero = """
            CREATE TABLE Genero (
               idGenero TEXT PRIMARY KEY,
                nombreGenero TEXT NOT NULL
            );
        """

        val tablaGenero_Titulo = """
            CREATE TABLE Genero_Titulo (
                idGenero TEXT,
                idTitulo TEXT,
                PRIMARY KEY (idGenero, idTitulo),
                FOREIGN KEY (idGenero) REFERENCES Genero(idGenero),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
            );
        """

        /*val tablaPersona = """
            CREATE TABLE Persona (
                idPersona INTEGER PRIMARY KEY AUTOINCREMENT,
                nombrePersona TEXT NOT NULL
            );
        """

        val tablaPersona_Titulo = """
            CREATE TABLE Persona_Titulo (
                idPersona INTEGER,
                idTitulo INTEGER,
                PRIMARY KEY (idPersona, idTitulo),
                FOREIGN KEY (idPersona) REFERENCES Persona(idPersona),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
            );
        """*/

        val tablaPlataforma = """
            CREATE TABLE Plataforma (
                idPlataforma TEXT PRIMARY KEY,
                nombrePlataforma TEXT NOT NULL UNIQUE,
                urlPlataforma TEXT NOT NULL
            );
        """

        val tablaPlataforma_Titulo = """
            CREATE TABLE Plataforma_Titulo (
                idPlataforma TEXT,
                idTitulo TEXT,
                pais TEXT NOT NULL DEFAULT 'es',
                disponible BOOLEAN NOT NULL DEFAULT 1,
                PRIMARY KEY (idPlataforma, idTitulo),
                FOREIGN KEY (idPlataforma) REFERENCES Plataforma(idPlataforma),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
            );
        """

        val tablaUsuario_Titulo = """
            CREATE TABLE Usuario_Titulo (
                correo TEXT,
                idTitulo TEXT,
                PRIMARY KEY (correo, idTitulo),
                FOREIGN KEY (correo) REFERENCES Usuario(correo),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
            );
        """

        val tablaEstreno = """
            CREATE TABLE Estreno (
                idEstreno INTEGER PRIMARY KEY AUTOINCREMENT,
                fechaEstreno TEXT NOT NULL
            );
        """

        val tablaEstreno_Serie = """
            CREATE TABLE Estreno_Serie (
                idEstreno INTEGER PRIMARY KEY,
                temporada INTEGER NOT NULL,
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno)
            );
        """

        val tablaEstreno_Pelicula = """
            CREATE TABLE Estreno_Pelicula (
                idEstreno INTEGER PRIMARY KEY,
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno)
            );
        """

        val tablaEstreno_Titulo = """
            CREATE TABLE Estreno_Titulo (
                idEstreno INTEGER,
                idTitulo TEXT,
                correo TEXT,
                PRIMARY KEY (idEstreno, idTitulo, correo),
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo),
                FOREIGN KEY (correo) REFERENCES Usuario(correo)
            );
        """

        val tablaEstreno_Plataforma = """
            CREATE TABLE Estreno_Plataforma (
                idEstreno INTEGER,
                idPlataforma TEXT,
                PRIMARY KEY (idEstreno, idPlataforma),
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno),
                FOREIGN KEY (idPlataforma) REFERENCES Plataforma(idPlataforma)
            );
        """

        /*val tablaEstreno_Usuario = """
            CREATE TABLE Estreno_Usuario (
                idEstreno INTEGER,
                correo TEXT,
                PRIMARY KEY (idEstreno, correo),
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno),
                FOREIGN KEY (correo) REFERENCES Usuario(correo)
            );
        """*/

        val tablaTop10 = """
            CREATE TABLE Top10 (
                idTop INTEGER PRIMARY KEY AUTOINCREMENT,
                fechaTop TEXT NOT NULL
            );
        """

        val tablaTop10Mezclado = """
            CREATE TABLE Top10Mezclado (
                idTop INTEGER PRIMARY KEY,
                FOREIGN KEY (idTop) REFERENCES Top10(idTop)
            );
        """

        val tablaTop10Separado = """
            CREATE TABLE Top10Separado (
                idTop INTEGER PRIMARY KEY,
                tipo TEXT NOT NULL CHECK (tipo IN ('movie', 'series')),
                FOREIGN KEY (idTop) REFERENCES Top10(idTop)
            );
        """

        val tablaTop10_Titulo = """
            CREATE TABLE Top10_Titulo (
                idTop INTEGER,
                idTitulo TEXT,
                posicion INTEGER NOT NULL CHECK (posicion BETWEEN 1 AND 10),
                PRIMARY KEY (idTop, idTitulo),
                FOREIGN KEY (idTop) REFERENCES Top10(idTop),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
            );
        """

        val tablaTop10Separado_Plataforma = """
            CREATE TABLE Top10Separado_Plataforma (
                idTop INTEGER,
                idPlataforma TEXT,
                PRIMARY KEY (idTop, idPlataforma),
                FOREIGN KEY (idTop) REFERENCES Top10(idTop),
                FOREIGN KEY (idPlataforma) REFERENCES Plataforma(idPlataforma)
            );
        """

        val tablaTop10Mezclado_Plataforma = """
            CREATE TABLE Top10Mezclado_Plataforma (
                idTop INTEGER,
                idPlataforma TEXT,
                PRIMARY KEY (idTop, idPlataforma),
                FOREIGN KEY (idTop) REFERENCES Top10(idTop),
                FOREIGN KEY (idPlataforma) REFERENCES Plataforma(idPlataforma)
            );
        """


        val insertFotoPerfil = """
            INSERT INTO FotoPerfil (nombreFoto)
            VALUES
            ('perfil1'),
            ('perfil2'),
            ('perfil3'),
            ('perfil4'),
            ('perfil5'); 

        """

        val insertAdmin = """
            INSERT INTO Usuario (correo, nombre, pass, idFoto, privilegios)
            VALUES ("admin@gmail.com", "Admin", "1234", "4", "admin");
        """

        val insertPlataforma = """
            INSERT INTO Plataforma (idPlataforma, nombrePlataforma, urlPlataforma)
            VALUES ("netflix", "Netflix", "https://www.netflix.com/");
        """

        db.execSQL(tablaUsuario)
        db.execSQL(tablaFotoPerfil)
        db.execSQL(tablaTitulo)
        db.execSQL(tablaPoster_Titulo)
        db.execSQL(tablaGenero)
        db.execSQL(tablaGenero_Titulo)
        //db.execSQL(tablaPersona)
        // db.execSQL(tablaPersona_Titulo)
        db.execSQL(tablaPlataforma)
        db.execSQL(tablaPlataforma_Titulo)
        db.execSQL(tablaUsuario_Titulo)
        db.execSQL(tablaEstreno)
        db.execSQL(tablaEstreno_Serie)
        db.execSQL(tablaEstreno_Pelicula)
        db.execSQL(tablaEstreno_Titulo)
        db.execSQL(tablaEstreno_Plataforma)
        //db.execSQL(tablaEstreno_Usuario)
        db.execSQL(tablaTop10)
        db.execSQL(tablaTop10Mezclado)
        db.execSQL(tablaTop10Separado)
        db.execSQL(tablaTop10_Titulo)
        db.execSQL(tablaTop10Separado_Plataforma)
        db.execSQL(tablaTop10Mezclado_Plataforma)
        db.execSQL(insertFotoPerfil)
        db.execSQL(insertAdmin)
        db.execSQL(insertPlataforma)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Usuario")
        db.execSQL("DROP TABLE IF EXISTS FotoPerfil")
        db.execSQL("DROP TABLE IF EXISTS Titulo")
        db.execSQL("DROP TABLE IF EXISTS Poster_Titulo")
        db.execSQL("DROP TABLE IF EXISTS Genero")
        db.execSQL("DROP TABLE IF EXISTS Genero_Titulo")
        db.execSQL("DROP TABLE IF EXISTS Persona")
        db.execSQL("DROP TABLE IF EXISTS Persona_Titulo")
        db.execSQL("DROP TABLE IF EXISTS Plataforma")
        db.execSQL("DROP TABLE IF EXISTS Plataforma_Titulo")
        db.execSQL("DROP TABLE IF EXISTS Usuario_Titulo")
        db.execSQL("DROP TABLE IF EXISTS Estreno")
        db.execSQL("DROP TABLE IF EXISTS Estreno_Serie")
        db.execSQL("DROP TABLE IF EXISTS Estreno_Pelicula")
        db.execSQL("DROP TABLE IF EXISTS Estreno_Titulo")
        db.execSQL("DROP TABLE IF EXISTS Estreno_Plataforma")
        //db.execSQL("DROP TABLE IF EXISTS Estreno_Usuario")
        db.execSQL("DROP TABLE IF EXISTS Top10")
        db.execSQL("DROP TABLE IF EXISTS Top10Mezclado")
        db.execSQL("DROP TABLE IF EXISTS Top10Separado")
        db.execSQL("DROP TABLE IF EXISTS Top10Separado_Titulo")
        db.execSQL("DROP TABLE IF EXISTS Top10Mezclado_Titulo")
        db.execSQL("DROP TABLE IF EXISTS Top10_Plataforma")

        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    /**
     * Intenta la insercion de un usuario,
     * Devuelve un Int:
     *  0: Insercion correcta
     *  1: Correo duplicado en bbdd
     *  2: Nombre duplicado en bbdd
     * */
    fun insertarUsuario(correo:String, nombre:String, pass:String, privilegios:String?):Int{

        val db=this.writableDatabase
        val values= ContentValues().apply {
            put("correo",correo)
            put("nombre",nombre)
            put("pass",pass)
            put("privilegios",privilegios)
            put("idFoto", 1)
        }
        try{
            db.insertOrThrow("Usuario",null,values)
        }catch (e:SQLiteConstraintException){

            if(e.message?.contains("UNIQUE constraint failed: Usuario.correo")==true) return 1
            if(e.message?.contains("UNIQUE constraint failed: Usuario.nombre")==true) return 2

        }finally{
            db.close()
        }
        return 0
    }
    /**
     * Busca un usuario por su nombre o correo.
     * Devuelve el nombre encontrado en BBDD, si no se encuentra, devolverá null.
     * */
    fun encontrarUsuario(nombre:String?):String?{
        val db = this.readableDatabase
        val query = "SELECT nombre FROM Usuario WHERE nombre = ? OR correo = ?"
        val cursor = db.rawQuery(query, arrayOf(nombre,nombre))
        var ret:String?=null

        if(cursor.moveToFirst()) ret=cursor.getString(0)

        cursor.close()
        db.close()

        return ret
    }

    /**
     * Dado un nombre y una contraseña, comprueba en BBDD
     * Devuelve:
     *  True: la contraseña es correcta
     *  False: la contraseña es incorrecta
     * */

    fun verificarUsuario(nombre: String, pass: String):Array<Boolean>{

        val db=this.readableDatabase
        val query="SELECT nombre, pass, privilegios FROM Usuario WHERE nombre = ?"
        val cursor = db.rawQuery(query, arrayOf(nombre))
        val ret = arrayOf(false, false)

        if(cursor.moveToFirst()){
            if(cursor.getString(1) == pass) ret[0]=true
            if(cursor.getString(2) == "admin") ret[1]=true
        }
        db.close()
        cursor.close()
        return ret
    }

    // Método para eliminar usuario

    fun eliminarUsuario(correo: String): Boolean {
        val db = this.writableDatabase
        return try {
            val rowsDeleted = db.delete("Usuario", "correo = ?", arrayOf(correo))
            db.close()
            rowsDeleted > 0 // Devuelve true si se eliminó al menos un usuario
        } catch (e: Exception) {
            e.printStackTrace()
            false // En caso de error, devuelve false
        }
    }

    // Método para poder modificar un usuario

    fun modificarUsuario(correo: String, nombre: String, pass: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("pass", pass)
        }
        return try {
            val rowsUpdated = db.update("Usuario", values, "correo = ?", arrayOf(correo))
            db.close()
            rowsUpdated > 0 // Devuelve true si se modificó al menos un usuario
        } catch (e: Exception) {
            e.printStackTrace()
            false // En caso de error, devuelve false
        }
    }

    fun establecerUsuario(nombre: String) {
        val db = this.readableDatabase
        val query = "SELECT * FROM Usuario WHERE nombre = ? OR correo = ?"
        val cursor = db.rawQuery(query, arrayOf(nombre))

        if (cursor.moveToFirst()) {
            Usuario.correo = cursor.getString(cursor.getColumnIndexOrThrow("correo"))
            Usuario.nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            Usuario.pass = cursor.getString(cursor.getColumnIndexOrThrow("pass"))
            val idFoto = cursor.getInt(cursor.getColumnIndexOrThrow("idFoto"))

            // Obtener el nombre de la imagen usando idFoto
            val fotoCursor = db.rawQuery(
                "SELECT nombreFoto FROM FotoPerfil WHERE idFoto = ?",
                arrayOf(idFoto.toString())
            )

            val nombreFoto = if (fotoCursor.moveToFirst()) fotoCursor.getString(0) else "perfil1"
            fotoCursor.close()

            Usuario.fotoPerfil = nombreFoto

            Usuario.privilegios = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("privilegios"))
        }
        cursor.close()
        db.close()
    }

    fun cerrarSesion(context: Context, activity: Activity) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        Usuario.correo = ""
        Usuario.nombre = null
        Usuario.pass = null
        Usuario.fotoPerfil = null
        Usuario.privilegios = null

        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        activity.finish()
    }

    fun guardarUsuarioEnSesion(context: Context) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("correo", Usuario.correo)
            putString("nombre", Usuario.nombre)
            putString("pass", Usuario.pass)
            putString("fotoPerfil", Usuario.fotoPerfil)
            putString("privilegios", Usuario.privilegios)
            apply()
        }
    }
    // Método para actualizar la contraseña del usuario

    fun actualizarContrasena(context: Context, correo: String, nuevaContrasena: String): Boolean {
        val db = this.writableDatabase
        val valores = ContentValues()

        // Obtener la contraseña actual
        val cursor = db.rawQuery("SELECT pass FROM Usuario WHERE correo = ?", arrayOf(correo))
        if (cursor.moveToFirst()) {
            val contrasenaActual = cursor.getString(0)
            if (contrasenaActual == nuevaContrasena) {
                Toast.makeText(context, "La nueva contraseña no puede ser igual a la anterior.", Toast.LENGTH_SHORT).show()
                cursor.close()
                db.close()
                return false
            }
        }
        cursor.close()

        // Si la contraseña es diferente, actualizarla
        valores.put("pass", nuevaContrasena)
        val resultado = db.update("Usuario", valores, "correo = ?", arrayOf(correo))
        db.close()

        return resultado > 0
    }

    // Método para eliminar una foto

    fun eliminarFoto(codigo: String): Boolean {
        val db = this.writableDatabase

        // Verificar si la foto no contiene caracteres no válidos

        if (!codigo.matches(Regex("\\d+"))) {
            Log.e("EliminarFoto", "Código inválido: $codigo")
            return false
        }

        return try {
            val rowsDeleted = db.delete("FotoPerfil", "idFoto = ?", arrayOf(codigo))
            db.close()
            rowsDeleted > 0 // Devuelve true si se eliminó al menos ua Foto
        } catch (e: Exception) {
            e.printStackTrace()
            false // En caso de error, devuelve false
        }
    }

    // Método para insertar una foto

    fun insertarFoto(nombreFoto: String, selectedImageUri: Uri): Boolean {
        val db = this.writableDatabase

        // Verificar si el nombre de la foto ya existe
        if (existeNombreFoto(nombreFoto)) {
            // Si ya existe, retorna false
            return false
        }

        // Convertir la imagen en un archivo
        val imagePath = selectedImageUri.path ?: return false
        val imageFile = File(imagePath)
        val imageBytes = imageFile.readBytes() // Convierte la imagen en bytes

        val values = ContentValues().apply {
            put("nombreFoto", nombreFoto)
        }

        return try {
            // Insertar los datos (nombre de la foto) en la tabla FotoPerfil
            db.insert("FotoPerfil", null, values)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // Métodos para obtener los datos de los títulos

    fun existeNombreFoto(nombre: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM FotoPerfil WHERE nombreFoto = ?", arrayOf(nombre))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    // Función para insertar un género en la base de datos
    fun insertGenero(idGenero: String, nombreGenero: String) {
        try {
            val db = writableDatabase
            val values = ContentValues()
            values.put("idGenero", idGenero)
            values.put("nombreGenero", nombreGenero)

            val result = db.insert("Genero", null, values)
            if (result != -1L) {
                Log.d("GeneroInsertado", "Inserción exitosa para el género: $nombreGenero")
            } else {
                Log.e("GeneroInsertado", "Error al insertar el género: $nombreGenero")
            }
        } catch (e: Exception) {
            Log.e("GeneroInsertado", "Error en insertGenero: ${e.message}")
        }
    }

    // Función para verificar si hay géneros guardados en la base de datos

    fun hayGenerosGuardados(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Genero", null)
        var hayDatos = false
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0)
            hayDatos = count > 0
        }
        cursor.close()
        return hayDatos
    }

    // Función para verificar su hay títulos guardados en la base de datos

    fun hayTitulosGuardados(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Titulo", null)
        var hayDatos = false
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0)
            hayDatos = count > 0
        }
        cursor.close()
        return hayDatos
    }

    // Método para cambiar la foto de perfil del usuario

    fun actualizarFotoPerfilUsuario(correo: String, idFoto: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("idFoto", idFoto)
        }

        val filasActualizadas = db.update(
            "Usuario",           // Nombre de la tabla
            values,              // Valores nuevos
            "correo = ?",        // WHERE
            arrayOf(correo)      // Argumentos del WHERE
        )

        db.close()
        return filasActualizadas > 0
    }

    fun listaFotos(): List<Foto> {
        val listaFotos = mutableListOf<Foto>()
        val db = this.readableDatabase

        val query = """
            SELECT
                FotoPerfil.idFoto AS id,
                FotoPerfil.nombreFoto AS nombre
            FROM FotoPerfil;
        """

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))

                listaFotos.add(
                    Foto(
                        idFoto = id,
                        nombreFoto = nombre,
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaFotos
    }

    // Método para contar las fotos

    fun contarFotos(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM FotoPerfil", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    // Métodos para obtener los usuarios que usan una foto

    fun obtenerUsuariosConFoto(idFoto: Int): List<UsuarioData> {
        val db = this.readableDatabase
        val listaUsuarios = mutableListOf<UsuarioData>()

        val cursor = db.rawQuery(
            "SELECT correo, nombre, pass, idFoto, privilegios FROM Usuario WHERE idFoto = ?",
            arrayOf(idFoto.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val usuario = UsuarioData(
                    correo = cursor.getString(0),
                    nombre = cursor.getString(1),
                    pass = cursor.getString(2),
                    idFoto = cursor.getInt(3),
                    privilegios = cursor.getString(4)
                )
                listaUsuarios.add(usuario)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return listaUsuarios
    }

    // Método para reasignar la foto de perfil a uno o varios usuarios

    fun reasignarFotoAUsuarios(idFotoEliminar: Int, usuarios: List<UsuarioData>) {
        if (usuarios.isEmpty()) return  // No hay usuarios que actualizar

        val db = this.writableDatabase

        // Buscar la primera idFoto distinta de la que se quiere eliminar
        val cursor = db.rawQuery(
            "SELECT idFoto FROM FotoPerfil WHERE idFoto != ? ORDER BY idFoto ASC LIMIT 1",
            arrayOf(idFotoEliminar.toString())
        )

        if (cursor.moveToFirst()) {
            val nuevaIdFoto = cursor.getInt(0)

            // Actualizar todos los usuarios que tenían la foto a eliminar
            for (usuario in usuarios) {
                val values = ContentValues().apply {
                    put("idFoto", nuevaIdFoto)
                }

                db.update(
                    "Usuario",
                    values,
                    "correo = ?",
                    arrayOf(usuario.correo)
                )
            }
        }

        cursor.close()
        db.close()
    }

    // Función para insertar un titulo
    /*
    * val tablaTitulo ="""
            CREATE TABLE Titulo (
                idTitulo TEXT PRIMARY KEY,
                nombre TEXT NOT NULL,
                nombreOriginal TEXT,
                descripcion TEXT,
                fechaInicio TEXT NOT NULL,
                fechaFin TEXT,
                temporadas INTEGER,
                tipo TEXT NOT NULL CHECK (tipo IN ('movie', 'series')),
                rating INTEGER NOT NULL
            );
        """
    * */
    fun insertTitulo(
        id: String,
        nombre: String,
        nombreOriginal: String?,
        descripcion: String?,
        fechaInicio: String,
        fechaFin: String?,
        temporadas: Int?,
        tipo: String,
        rating: Int
    ) {
        val db = this.writableDatabase

        try {
            val values = ContentValues().apply {
                put("idTitulo", id)
                put("nombre", nombre)
                put("nombreOriginal", nombreOriginal)
                put("descripcion", descripcion)
                put("fechaInicio", fechaInicio)
                put("fechaFin", fechaFin)
                put("temporadas", temporadas)
                put("tipo", tipo)
                put("rating", rating)
            }

            val result = db.insertWithOnConflict("Titulo", null, values, SQLiteDatabase.CONFLICT_REPLACE)

            if (result != -1L) {
                Log.d("TituloInsertado", "Inserción exitosa para el título: $nombre")
            } else {
                Log.e("TituloInsertado", "Error al insertar el título: $nombre")
            }
        } catch (e: Exception) {
            Log.e("TituloInsertado", "Error en insertTitulo: ${e.message}")
        } finally {
            db.close()
        }
    }

    // Método para insertar un poster

    fun insertPosterTitulo(idTitulo: String, tipo: String, calidad: String, url: String) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put("idTitulo", idTitulo)
                put("tipo", tipo) // "vertical" o "horizontal"
                put("calidad", calidad) // "w360" o "w720"
                put("urlPoster", url)
            }

            val result = db.insertWithOnConflict("Poster_Titulo", null, values, SQLiteDatabase.CONFLICT_IGNORE)
            if (result != -1L) {
                Log.d("PosterTituloInsertado", "Poster guardado: $tipo ($calidad) para $idTitulo")
            } else {
                Log.e("PosterTituloInsertado", "Error al insertar el poster de $idTitulo")
            }
        } catch (e: Exception) {
            Log.e("PosterTituloInsertado", "Error en insertPosterTitulo: ${e.message}")
        } finally {
            db.close()
        }
    }

    // Método para guardar los géneros de un titulo concreto

    fun insertGeneroTitulo(idGenero: String, idTitulo: String) {
        val db = this.writableDatabase
        try {
            val query = """
            INSERT OR IGNORE INTO Genero_Titulo (idGenero, idTitulo) 
            VALUES (?, ?)
        """
            val stmt = db.compileStatement(query)
            stmt.bindString(1, idGenero)
            stmt.bindString(2, idTitulo)
            stmt.executeInsert()

            Log.d("GeneroTituloInsertado", "Asociación guardada: Género = $idGenero, Título = $idTitulo")
        } catch (e: Exception) {
            Log.e("GeneroTituloInsertado", "Error en insertGeneroTitulo: ${e.message}")
        } finally {
            db.close()
        }
    }


    // Método para guardar las plataformas de un título concreto

    fun insertPlataformaTitulo(idTitulo: String, idPlataforma: String, pais: String, disponible: Boolean) {
        val db = this.writableDatabase
        val disponibleInt = if (disponible) 1 else 0
        try {
            val query = """
            INSERT OR IGNORE INTO Plataforma_Titulo 
            (idPlataforma, idTitulo, pais, disponible) 
            VALUES (?, ?, ?, ?)
        """
            val stmt = db.compileStatement(query)
            stmt.bindString(1, idPlataforma)
            stmt.bindString(2, idTitulo)
            stmt.bindString(3, pais)
            stmt.bindLong(4, disponibleInt.toLong())
            stmt.executeInsert()
            Log.d("PlataformaTituloInsertado", "Asociación guardada: $idTitulo ($pais)")
        } catch (e: Exception) {
            Log.e("PlataformaTituloInsertado", "Error en insertPlataformaTitulo: ${e.message}")
        }
    }

    fun checkPlataformaTituloExistente(idTitulo: String, idPlataforma: String): Boolean {
        val db = this.readableDatabase
        var existe = false
        try {
            val query = """
            SELECT COUNT(*) FROM Plataforma_Titulo 
            WHERE idTitulo = ? AND idPlataforma = ?
        """
            val cursor = db.rawQuery(query, arrayOf(idTitulo, idPlataforma))

            if (cursor.moveToFirst()) {
                // Si el resultado es mayor que 0, ya existe la relación
                existe = cursor.getInt(0) > 0
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("PlataformaTituloCheck", "Error al comprobar si la relación existe: ${e.message}")
        } finally {
            db.close()
        }
        return existe
    }

    // Método para obtener la lista de titulo

    fun listaTitulos(): List<Titulo> {
        val listaTitulos = mutableListOf<Titulo>()
        val db = this.readableDatabase

        val query = "SELECT * FROM Titulo"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val idTitulo = cursor.getString(cursor.getColumnIndexOrThrow("idTitulo"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val nombreOriginal = cursor.getString(cursor.getColumnIndexOrThrow("nombreOriginal"))
                val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                val fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio"))
                val fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fechaFin"))
                val temporadas = if (!cursor.isNull(cursor.getColumnIndexOrThrow("temporadas")))
                    cursor.getInt(cursor.getColumnIndexOrThrow("temporadas")) else null
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                val rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"))

                // ---- Posters relacionados ----
                val posters = mutableListOf<Poster>()
                val postersCursor = db.rawQuery(
                    "SELECT * FROM Poster_Titulo WHERE idTitulo = ?",
                    arrayOf(idTitulo)
                )
                if (postersCursor.moveToFirst()) {
                    do {
                        posters.add(
                            Poster(
                                idPoster = postersCursor.getInt(postersCursor.getColumnIndexOrThrow("idPoster")),
                                urlPoster = postersCursor.getString(postersCursor.getColumnIndexOrThrow("urlPoster")),
                                tipo = postersCursor.getString(postersCursor.getColumnIndexOrThrow("tipo")),
                                calidad = postersCursor.getString(postersCursor.getColumnIndexOrThrow("calidad")),
                                idTitulo = idTitulo
                            )
                        )
                    } while (postersCursor.moveToNext())
                }
                postersCursor.close()

                // ---- Géneros relacionados ----
                val generos = mutableListOf<Genero>()
                val generoCursor = db.rawQuery(
                    """
                    SELECT g.idGenero, g.nombreGenero 
                    FROM Genero g 
                    INNER JOIN Genero_Titulo gt ON g.idGenero = gt.idGenero 
                    WHERE gt.idTitulo = ?
                """.trimIndent(), arrayOf(idTitulo)
                )
                if (generoCursor.moveToFirst()) {
                    do {
                        generos.add(
                            Genero(
                                idGenero = generoCursor.getString(0),
                                nombreGenero = generoCursor.getString(1),
                                idTitulo = idTitulo
                            )
                        )
                    } while (generoCursor.moveToNext())
                }
                generoCursor.close()

                // ---- Plataformas relacionadas ----
                val plataformas = mutableListOf<Plataforma>()
                val plataformaCursor = db.rawQuery(
                    """
                    SELECT p.idPlataforma, p.nombrePlataforma 
                    FROM Plataforma p 
                    INNER JOIN Plataforma_Titulo pt ON p.idPlataforma = pt.idPlataforma 
                    WHERE pt.idTitulo = ?
                """.trimIndent(), arrayOf(idTitulo)
                )
                if (plataformaCursor.moveToFirst()) {
                    do {
                        plataformas.add(
                            Plataforma(
                                idPlataforma = plataformaCursor.getString(0),
                                nombrePlataforma = plataformaCursor.getString(1),
                                idTitulo = idTitulo
                            )
                        )
                    } while (plataformaCursor.moveToNext())
                }
                plataformaCursor.close()

                // ---- Crear objeto Titulo ----
                listaTitulos.add(
                    Titulo(
                        idTitulo = idTitulo,
                        nombre = nombre,
                        nombreOriginal = nombreOriginal,
                        descripcion = descripcion,
                        fechaInicio = fechaInicio,
                        fechaFin = fechaFin,
                        temporadas = temporadas,
                        tipo = tipo,
                        rating = rating,
                        posters = posters,
                        generos = generos,
                        plataformas = plataformas
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return listaTitulos
    }

    // Insertar título en la tabla Usuario_Titulo

    fun agregarTituloALista(correo: String, idTitulo: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("correo", correo)
            put("idTitulo", idTitulo)
        }
        return try {
            val rowId = db.insertWithOnConflict("Usuario_Titulo", null, values, SQLiteDatabase.CONFLICT_IGNORE)
            rowId != -1L
        } catch (e: SQLiteConstraintException) {
            false
        } finally {
            db.close()
        }
    }

    // Eliminar título de la tabla Usuario_Titulo

    fun eliminarTituloDeLista(correo: String, idTitulo: String): Boolean {
        val db = this.writableDatabase
        return try {
            val rowsDeleted = db.delete("Usuario_Titulo", "correo = ? AND idTitulo = ?", arrayOf(correo, idTitulo))
            rowsDeleted > 0
        } finally {
            db.close()
        }
    }

    // Obtener los títulos guardados en la base de datos por un usuario

    fun obtenerTitulosGuardados(correo: String): List<Titulo> {
        val db = this.readableDatabase
        val listaTitulos = mutableListOf<Titulo>()

        // Obtener los títulos guardados por el usuario
        val query = """
        SELECT T.idTitulo, T.nombre, T.nombreOriginal, T.descripcion, T.fechaInicio, T.fechaFin, 
               T.temporadas, T.tipo, T.rating 
        FROM Titulo T
        INNER JOIN Usuario_Titulo UT ON T.idTitulo = UT.idTitulo
        WHERE UT.correo = ?
    """
        val cursor = db.rawQuery(query, arrayOf(correo))

        if (cursor.moveToFirst()) {
            do {
                val idTitulo = cursor.getString(cursor.getColumnIndexOrThrow("idTitulo"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val nombreOriginal = cursor.getString(cursor.getColumnIndexOrThrow("nombreOriginal"))
                val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                val fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio"))
                val fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fechaFin"))
                val temporadas = if (!cursor.isNull(cursor.getColumnIndexOrThrow("temporadas")))
                    cursor.getInt(cursor.getColumnIndexOrThrow("temporadas")) else null
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                val rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"))

                // ---- Posters relacionados ----
                val posters = mutableListOf<Poster>()
                val postersCursor = db.rawQuery(
                    "SELECT * FROM Poster_Titulo WHERE idTitulo = ?",
                    arrayOf(idTitulo)
                )
                if (postersCursor.moveToFirst()) {
                    do {
                        posters.add(
                            Poster(
                                idPoster = postersCursor.getInt(postersCursor.getColumnIndexOrThrow("idPoster")),
                                urlPoster = postersCursor.getString(postersCursor.getColumnIndexOrThrow("urlPoster")),
                                tipo = postersCursor.getString(postersCursor.getColumnIndexOrThrow("tipo")),
                                calidad = postersCursor.getString(postersCursor.getColumnIndexOrThrow("calidad")),
                                idTitulo = idTitulo
                            )
                        )
                    } while (postersCursor.moveToNext())
                }
                postersCursor.close()

                // ---- Géneros relacionados ----
                val generos = mutableListOf<Genero>()
                val generoCursor = db.rawQuery(
                    """
                SELECT g.idGenero, g.nombreGenero 
                FROM Genero g 
                INNER JOIN Genero_Titulo gt ON g.idGenero = gt.idGenero 
                WHERE gt.idTitulo = ?
            """.trimIndent(), arrayOf(idTitulo)
                )
                if (generoCursor.moveToFirst()) {
                    do {
                        generos.add(
                            Genero(
                                idGenero = generoCursor.getString(0),
                                nombreGenero = generoCursor.getString(1),
                                idTitulo = idTitulo
                            )
                        )
                    } while (generoCursor.moveToNext())
                }
                generoCursor.close()

                // ---- Plataformas relacionadas ----
                val plataformas = mutableListOf<Plataforma>()
                val plataformaCursor = db.rawQuery(
                    """
                SELECT p.idPlataforma, p.nombrePlataforma 
                FROM Plataforma p 
                INNER JOIN Plataforma_Titulo pt ON p.idPlataforma = pt.idPlataforma 
                WHERE pt.idTitulo = ?
            """.trimIndent(), arrayOf(idTitulo)
                )
                if (plataformaCursor.moveToFirst()) {
                    do {
                        plataformas.add(
                            Plataforma(
                                idPlataforma = plataformaCursor.getString(0),
                                nombrePlataforma = plataformaCursor.getString(1),
                                idTitulo = idTitulo
                            )
                        )
                    } while (plataformaCursor.moveToNext())
                }
                plataformaCursor.close()

                // ---- Crear objeto Titulo ----
                listaTitulos.add(
                    Titulo(
                        idTitulo = idTitulo,
                        nombre = nombre,
                        nombreOriginal = nombreOriginal,
                        descripcion = descripcion,
                        fechaInicio = fechaInicio,
                        fechaFin = fechaFin,
                        temporadas = temporadas,
                        tipo = tipo,
                        rating = rating,
                        posters = posters,
                        generos = generos,
                        plataformas = plataformas
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return listaTitulos
    }



    fun verificarTituloEnLista(correo: String, idTitulo: String): Boolean {
        val db = this.readableDatabase
        val query = """
        SELECT 1 
        FROM Usuario_Titulo 
        WHERE correo = ? AND idTitulo = ?
    """
        val cursor = db.rawQuery(query, arrayOf(correo, idTitulo))
        val existe = cursor.count > 0
        cursor.close()
        db.close()

        return existe
    }

}