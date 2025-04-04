package com.example.watchview

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.core.database.getStringOrNull

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
                idTitulo INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                fecha TEXT NOT NULL,
                fechaFin TEXT,
                temporadas INTEGER,
                duracion INTEGER NOT NULL,
                tipo TEXT NOT NULL CHECK (tipo IN ('pelicula', 'serie')),
                rating INTEGER NOT NULL
            );
        """

        val tablaPoster_Titulo = """
            CREATE TABLE Poster_Titulo (
                idPoster INTEGER PRIMARY KEY AUTOINCREMENT,
                urlPoster TEXT NOT NULL,
                idTitulo INTEGER,
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
                );
        """

        val tablaGenero = """
            CREATE TABLE Genero (
               idGenero INTEGER PRIMARY KEY AUTOINCREMENT,
                nombreGenero TEXT NOT NULL
            );
        """

        val tablaGenero_Titulo = """
            CREATE TABLE Genero_Titulo (
                idGenero INTEGER,
                idTitulo INTEGER,
                PRIMARY KEY (idGenero, idTitulo),
                FOREIGN KEY (idGenero) REFERENCES Genero(idGenero),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
            );
        """

        val tablaPersona = """
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
        """

        val tablaPlataforma = """
            CREATE TABLE Plataforma (
                idPlataforma INTEGER PRIMARY KEY AUTOINCREMENT,
                nombrePlataforma TEXT NOT NULL UNIQUE,
                urlPlataforma TEXT NOT NULL
            );
        """

        val tablaPlataforma_Titulo = """
            CREATE TABLE Plataforma_Titulo (
                idPlataforma INTEGER,
                idTitulo INTEGER,
                pais TEXT NOT NULL DEFAULT 'España',
                disponible BOOLEAN NOT NULL DEFAULT 1,
                PRIMARY KEY (idPlataforma, idTitulo),
                FOREIGN KEY (idPlataforma) REFERENCES Plataforma(idPlataforma),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
            );
        """

        val tablaUsuario_Titulo = """
            CREATE TABLE Usuario_Titulo (
                correo TEXT,
                idTitulo INTEGER,
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
                idTitulo INTEGER,
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
                idPlataforma INTEGER,
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
                tipo TEXT NOT NULL CHECK (tipo IN ('pelicula', 'serie')),
                FOREIGN KEY (idTop) REFERENCES Top10(idTop)
            );
        """

        val tablaTop10_Titulo = """
            CREATE TABLE Top10_Titulo (
                idTop INTEGER,
                idTitulo INTEGER,
                posicion INTEGER NOT NULL CHECK (posicion BETWEEN 1 AND 10),
                PRIMARY KEY (idTop, idTitulo),
                FOREIGN KEY (idTop) REFERENCES Top10(idTop),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
            );
        """

        val tablaTop10Separado_Plataforma = """
            CREATE TABLE Top10Separado_Plataforma (
                idTop INTEGER,
                idPlataforma INTEGER,
                PRIMARY KEY (idTop, idPlataforma),
                FOREIGN KEY (idTop) REFERENCES Top10(idTop),
                FOREIGN KEY (idPlataforma) REFERENCES Plataforma(idPlataforma)
            );
        """

        val tablaTop10Mezclado_Plataforma = """
            CREATE TABLE Top10Mezclado_Plataforma (
                idTop INTEGER,
                idPlataforma INTEGER,
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
            VALUES ("admin@admin.com", "Admin", "1234", "4", "admin");
        """

        db.execSQL(tablaUsuario)
        db.execSQL(tablaFotoPerfil)
        db.execSQL(tablaTitulo)
        db.execSQL(tablaPoster_Titulo)
        db.execSQL(tablaGenero)
        db.execSQL(tablaGenero_Titulo)
        db.execSQL(tablaPersona)
        db.execSQL(tablaPersona_Titulo)
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

    fun modificarUsuario(correo: String, pass: String, nombreFoto: String): Boolean {
        val db = this.writableDatabase

        // Obtener el idFoto correspondiente a la imagen
        val cursor = db.rawQuery(
            "SELECT idFoto FROM FotoPerfil WHERE nombreFoto = ?", arrayOf(nombreFoto)
        )

        val idFoto = if (cursor.moveToFirst()) cursor.getInt(0) else null
        cursor.close()

        // Si la imagen no existe en la tabla FotoPerfil, devuelve false
        if (idFoto == null) {
            db.close()
            return false
        }

        // Actualizar la contraseña y el idFoto en la tabla Usuario
        val values = ContentValues().apply {
            put("pass", pass)
            put("idFoto", idFoto)  // Guardamos el ID de la imagen en Usuario
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
            Usuario.fotoPerfil = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("idFoto"))
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
            putString("idFoto", Usuario.fotoPerfil)
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

    // Métodos para obtener los datos de los títulos

    fun insertarTitulo(db: SQLiteDatabase, titulo: Titulo) {
        val values = ContentValues().apply {
            put("idTitulo", titulo.idTitulo)
            put("nombre", titulo.nombre)
            put("descripcion", titulo.descripcion)
            put("fecha", titulo.fecha)
            put("duracion", titulo.duracion ?: 0) // Si es null, asignamos 0
            put("tipo", titulo.tipo)
            put("rating", titulo.rating)
            put("temporadas", titulo.temporadas ?: 0) // Si es null, asignamos 0
        }
        db.insert("Titulo", null, values)

        // Insertar temporadas si es una serie
        if (titulo.tipo == "serie" && titulo.seasonInfo != null) {
            titulo.seasonInfo.forEach { temporada ->
                val valuesTemporada = ContentValues().apply {
                    put("idTitulo", titulo.idTitulo)
                    put("temporada", temporada.numero)
                    put("fechaEstreno", temporada.fechaEstreno ?: "Desconocida")
                }
                db.insert("Estreno_Serie", null, valuesTemporada)
            }
        }
    }


}