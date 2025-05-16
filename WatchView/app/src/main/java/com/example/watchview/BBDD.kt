package com.example.watchview

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

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
                PRIMARY KEY (idEstreno, idTitulo),
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
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

        val tablaEstreno_Usuario = """
            CREATE TABLE Estreno_Usuario (
                idEstreno INTEGER,
                correo TEXT,
                PRIMARY KEY (idEstreno, correo),
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno),
                FOREIGN KEY (correo) REFERENCES Usuario(correo)
            );
        """

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

        val tablaTop10_Plataforma = """
            CREATE TABLE Top10_Plataforma (
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
            VALUES ("admin@gmail.com", "Admin", "Admin1!", "4", "admin");
        """

        val insertUsuario = """
            INSERT INTO Usuario (correo, nombre, pass, idFoto, privilegios)
            VALUES ("paco@gmail.com", "Paco", "Paco1!", "1", "usuario");
        """

        val insertPlataforma = """
            INSERT INTO Plataforma (idPlataforma, nombrePlataforma, urlPlataforma)
            VALUES ("netflix", "Netflix", "https://www.netflix.com/");
        """

        val insertTitulosTop10Series = """
            INSERT INTO Titulo (idTitulo, nombre, nombreOriginal, descripcion, fechaInicio, fechaFin, temporadas, tipo, rating)
            VALUES 

            ("17340436", "El jardinero", "El jardinero", "Aunque aparenta ser un hábil jardinero en Pontevedra, Elmer es en realidad un sicario a las órdenes de su madre... hasta que se enamora de su víctima y lo pone todo en peligro.", "2025", "2025", 1, "series", 50), 
            
            ("15415106", "La cúpula de cristal", "Glaskupan", "Léonie Vincent interpreta a una criminóloga que se alía con su padre adoptivo, un antiguo jefe de policía, para investigar la desaparición de una niña.", "2025", "2025", 1, "series", 50),
             
             ("15794504", "Nueva vida en Ransom Canyon", "Ransom Canyon", "Un ganadero viudo empieza a sentir algo por la mejor amiga de su difunta esposa. Pero ¿conseguirá proteger lo que más le importa de un rival que amenaza con quitárselo todo?","2025", "2025", 1, "series", 50),
             
             ("15415212", "Adolescencia", "Adolescence", "Este drama sobre un adolescente de 13 años acusado de asesinar a una compañera de clase.","2025", "2025", 1, "series", 50),
             
             ("16252965", "Pulso", "PULSE", "Un grupo de residentes de Urgencias lidia con sus problemas laborales y personales en el Maguire Medical Center, un hospital de Miami sacudido por una polémica acusación.", "2025", "2025", 1, "series", 50),
             
             ("16256534", "Manual para señoritas", "Manual para señoritas", "Una carabina debe encontrar marido a tres hermanas rebeldes mientras sortea las normas de la alta sociedad del siglo XIX... y oculta sus propios secretos.", "2025", "2025", 1, "series", 50);
        """

        val insertTitulosTop10Peliculas = """
            INSERT INTO Titulo (idTitulo, nombre, nombreOriginal, descripcion, fechaInicio, tipo, rating)
            VALUES 

            ("8954753", "Sin instrucciones", "Sin instrucciones", "Un soltero vive la vida sin preocupaciones hasta que su ex aparece y le deja a cargo de su bebé. Ochos años después, ella vuelve buscando el perdón... y recuperar a su hija.", "2024", "movie", 50), 
            
            ("15409348", "Mi lista de deseos", "The Life List", "Una joven debe cumplir su lista de deseos de adolescente para recibir una herencia en esta película basada en el bestseller del mismo nombre.","2025", "movie", 50);
        """

        val insertPostersTop10 = """
            INSERT INTO Poster_Titulo (urlPoster, tipo, calidad, idTitulo)
            VALUES 

            ("https://www.lavanguardia.com/peliculas-series/images/serie/poster/2025/4/w300/8nEYffm7XDQXSGoh1Ce0jvd45AO.jpg", "vertical", "w360","17340436"),
            ("https://media.senscritique.com/media/000022782080/0/el_jardinero.jpg", "vertical", "w720","17340436"),
            ("https://lovingseries.com/wp-content/uploads/2025/03/el-jardinero-netflix.jpg", "horizontal", "w360","17340436"),
            ("https://images.ctfassets.net/4cd45et68cgf/2t8jX03sfDRguYtT4fvQQM/e549677504be11be46ca49a1e398459d/El_Jardinero_S01_E02_23012024_Niete__DSC7768.jpg?w=2560", "horizontal", "w2560","17340436"),
            
            ("https://www.lavanguardia.com/peliculas-series/images/serie/poster/2025/4/w1280/szOo7iApDRChsHoyAlahYKu6G7R.jpg", "vertical", "w360","15415106"),
            ("https://images.abandomoviez.net/dbs/foto/dbs_5662_73.jpg", "vertical", "w720","15415106"),
            ("https://cdn.avpasion.com/wp-content/uploads/2025/04/cupula-de-cristal-01.jpg", "horizontal", "w360","15415106"),
            ("https://cdn.avpasion.com/wp-content/uploads/2025/04/cupula-de-cristal-03.jpg", "horizontal", "w720","15415106"),
            
            ("https://static.wikia.nocookie.net/doblaje/images/3/3b/Poster-NVRC.png/revision/latest?cb=20250417194416&path-prefix=es", "vertical", "w360","15794504"),
            ("https://static.wikia.nocookie.net/doblaje/images/3/3b/Poster-NVRC.png/revision/latest?cb=20250417194416&path-prefix=es", "vertical", "w720","15794504"),
            ("https://televitos.com/wp-content/uploads/2025/04/NuevaVidaEnRansomCanyon_08.webp", "horizontal", "w360","15794504"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/Z-WHgqd_TeJxSuha8aZ5WpyLcX8/AAAABSkNYfTYVHee6S9PsQPBEhBIizNWpMWNBPAGOxv618ISQZTtpzf2i_oL2FXyqxYeYYjCQTbxajM5KMilrpAT8gCayyyHcjjyR12r.jpg?r=482", "horizontal", "w720","15794504"),
            
            ("https://pics.filmaffinity.com/adolescence-497490057-large.jpg", "vertical", "w360","15415212"),
            ("https://es.web.img3.acsta.net/r_1280_720/img/71/2f/712f0d66f71b769f9b15f1dceed7b41a.jpg", "vertical", "w720","15415212"),
            ("https://s3.abcstatics.com/abc/www/multimedia/play/2025/03/18/adolescence.jpg", "horizontal", "w360","15415212"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/Z-WHgqd_TeJxSuha8aZ5WpyLcX8/AAAABTwtmCD6J5UhUMrHUJtpSA_iZ1SSDvny4PDwaNDcAPSDOMWI29xaC2XT9isnVsbmhRbrIMYtcv6jutBamcsizbjlqe2dYQEX11UB.jpg?r=92c", "horizontal", "w720","15415212"),
            
            ("https://www.infobae.com/resizer/v2/WMX4BXCYVFGWBCQG2VHBKSKBSM.jpg?auth=848a0f8ef83fce084b9eb224833f7f997ffd15b2e35801d6b0ad7e60caefdb1c&smart=true&width=350&height=467&quality=85", "vertical", "w360","16252965"),
            ("https://pics.filmaffinity.com/pulse-107075411-large.jpg", "vertical", "w720","16252965"),
            ("https://indiehoy.com/wp-content/uploads/2025/04/pulso-reparto-netflix.jpg", "horizontal", "w360","16252965"),
            ("https://www.magazinespain.com/wp-content/uploads/2025/03/pulso-portada-min.jpg", "horizontal", "w720","16252965"),
            
            ("https://m.media-amazon.com/images/M/MV5BNWE3NTFhYjItNzQ4Ny00MDYxLTg0MDMtYWUwNmYwOTVjYzkzXkEyXkFqcGc@._V1_.jpg", "vertical", "w360","16256534"),
            ("https://images.ctfassets.net/4cd45et68cgf/3vdcxT7SJu69aPoQUgQmWa/76fe8bff749fcbf834a7a5095bff6051/es-ES_es_mps_s1_main_main_key_art_vertical_27x40_rgb_pre_1.jpg?w=1200", "vertical", "w720","16256534"),
            ("https://www.lavanguardia.com/peliculas-series/images/all/serie/backdrops/2025/3/serie-280890/w1280/q9aTsvevLD6URZgUruQQI9D94oC.jpg", "horizontal", "w360","16256534"),
            ("https://fotografias.larazon.es/clipping/cmsimages01/2025/03/11/9A4E80A5-2AB4-46A2-9629-D3EFAF8C76FF/cartel-oficial-serie-manual-senoritas_69.jpg?crop=4594,2584,x48,y0&width=1280&height=720&optimize=low&format=jpg", "horizontal", "w720","16256534"),
            
            ("https://images.justwatch.com/poster/330110999/s718/sin-instrucciones-2024.jpg", "vertical", "w360","8954753"),
            ("https://pics.filmaffinity.com/Sin_instrucciones-513077765-large.jpg", "vertical", "w720","8954753"),
            ("https://content20.lecturas.com/medio/2025/04/22/sin-instrucciones_b7458678_250422092743_1280x720.webp", "horizontal", "w360","8954753"),
            ("https://hips.hearstapps.com/hmg-prod/images/sin-instrucciones-pelicula-paco-leon-676abe8a8a2c4.jpg?crop=1.00xw:1.00xh;0,0&resize=1200:*", "horizontal", "w720","8954753"),
            
            ("https://es.web.img3.acsta.net/img/30/c9/30c94497f1a8d225a1ce32eda24f21cc.jpg", "vertical", "w360","15409348"),
            ("https://pics.filmaffinity.com/the_life_list-567019967-large.jpg", "vertical", "w720","15409348"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/6AYY37jfdO6hpXcMjf9Yu5cnmO0/AAAABRJr1h76k5VLlX2U78mAHPXZScihTFLUCM7lv5ze-hLPL2jplOm-VATY_NZ16IqBHlUxcbLKBTY7xrkK8HSq3p7ZSw2WER3gye0B.jpg?r=e66", "horizontal", "w360","15409348"),
            ("https://media.revistagq.com/photos/67ea537b0c8ed04210db38c0/16:9/w_2560%2Cc_limit/LIFELIST_20240425_28639_R%2520(1).jpg", "horizontal", "w720","15409348");
        """

        val insertGenerosTop10 = """
            INSERT INTO Genero_Titulo (idGenero, idTitulo)
            VALUES 

            ("drama","17340436"),
            ("mystery","17340436"),
            ("thriller","17340436"),
            
            ("drama","15415106"),
            ("crime","15415106"),
            
            ("drama","15794504"),
            ("romance","15794504"),
            ("western","15794504"),
            
            ("drama","15415212"),
            ("crime","15415212"),
            ("mystery","15415212"),
            
            ("drama","16252965"),
            
            ("comedy","16256534"),
            ("history","16256534"),
            ("romance","16256534"),
            
            ("comedy","8954753"),
            
            ("comedy","15409348"),
            ("drama","15409348"),
            ("romance","15409348");
        """

        val insertPlataformasTop10 = """
            INSERT INTO Plataforma_Titulo (idPlataforma, idTitulo, pais, disponible)
            VALUES 

            ("netflix","17340436", "es", 1),
            ("netflix","15415106", "es", 1),
            ("netflix","15794504", "es", 1),
            ("netflix","15415212", "es", 1),
            ("netflix","16252965", "es", 1),
            ("netflix","16256534", "es", 1),
            ("netflix","8954753", "es", 1),
            ("netflix","15409348", "es", 1);
        """

        val insertFechaTop10 = """
            INSERT INTO Top10 (fechaTop)
            VALUES 
            
            ("23-04-2025"),
            ("23-04-2025");
        """

        val insertTopsSeparadosTop10 = """
            INSERT INTO Top10Separado (idTop, tipo)
            VALUES 

            (1,"series"),
            (2,"movie");
        """

        val insertPosicionesTop10 = """
            INSERT INTO Top10_Titulo (idTop, idTitulo, posicion)
            VALUES 

            (1,"17340436",1),
            (1,"15415106",2),
            (1,"922",3),
            (1,"15794504",4),
            (1,"16",5),
            (1,"15415212",6),
            (1,"13616",7),
            (1,"4058",8),
            (1,"16252965",9),
            (1,"16256534",10),
            
            (2,"8954753",1),
            (2,"6851",2),
            (2,"13193",3),
            (2,"12038",4),
            (2,"2887096",5),
            (2,"15409348",6),
            (2,"5021461",7),
            (2,"1864",8),
            (2,"3691",9),
            (2,"185277",10);
        """

        val insertPlataformaTop10Separado = """
            INSERT INTO Top10_Plataforma (idTop, idPlataforma)
            VALUES 

            (1,"netflix"),
            (2,"netflix");
        """

        db.execSQL(tablaUsuario)
        db.execSQL(tablaFotoPerfil)
        db.execSQL(tablaTitulo)
        db.execSQL(tablaPoster_Titulo)
        db.execSQL(tablaGenero)
        db.execSQL(tablaGenero_Titulo)
        db.execSQL(tablaPlataforma)
        db.execSQL(tablaPlataforma_Titulo)
        db.execSQL(tablaUsuario_Titulo)
        db.execSQL(tablaEstreno)
        db.execSQL(tablaEstreno_Serie)
        db.execSQL(tablaEstreno_Pelicula)
        db.execSQL(tablaEstreno_Titulo)
        db.execSQL(tablaEstreno_Plataforma)
        db.execSQL(tablaEstreno_Usuario)
        db.execSQL(tablaTop10)
        db.execSQL(tablaTop10Mezclado)
        db.execSQL(tablaTop10Separado)
        db.execSQL(tablaTop10_Titulo)
        db.execSQL(tablaTop10_Plataforma)
        //db.execSQL(tablaTop10Mezclado_Plataforma)
        db.execSQL(insertFotoPerfil)
        db.execSQL(insertAdmin)
        db.execSQL(insertUsuario)
        db.execSQL(insertPlataforma)
        /*db.execSQL(insertTitulosTop10Series)
        db.execSQL(insertTitulosTop10Peliculas)
        db.execSQL(insertPostersTop10)
        db.execSQL(insertGenerosTop10)
        db.execSQL(insertPlataformasTop10)
        db.execSQL(insertFechaTop10)
        db.execSQL(insertTopsSeparadosTop10)
        db.execSQL(insertPosicionesTop10)
        db.execSQL(insertPlataformaTop10Separado)*/


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
        db.execSQL("DROP TABLE IF EXISTS Estreno_Usuario")
        db.execSQL("DROP TABLE IF EXISTS Top10")
        db.execSQL("DROP TABLE IF EXISTS Top10Mezclado")
        db.execSQL("DROP TABLE IF EXISTS Top10Separado")
        db.execSQL("DROP TABLE IF EXISTS Top10_Titulo")
        db.execSQL("DROP TABLE IF EXISTS Top10_Plataforma")

        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    override fun close() {
        super.close()
    }

    /**
     * Intenta la insercion de un usuario,
     * Devuelve un Int:
     *  0: Insercion correcta
     *  1: Correo duplicado en bbdd
     *  2: Nombre duplicado en bbdd
     * */
    fun insertarUsuario(correo: String, nombre: String, pass: String, privilegios: String?): Int {
        val db = this.writableDatabase

        // Buscar la primera foto de perfil disponible (por menor idFoto)
        var idFotoPerfil = 1 // Valor por defecto por si no se encuentra ninguno
        val cursor = db.rawQuery("SELECT idFoto FROM FotoPerfil ORDER BY idFoto ASC LIMIT 1", null)
        if (cursor.moveToFirst()) {
            idFotoPerfil = cursor.getInt(0)
        }
        cursor.close()

        val values = ContentValues().apply {
            put("correo", correo)
            put("nombre", nombre)
            put("pass", pass)
            put("privilegios", privilegios)
            put("idFoto", idFotoPerfil)
        }

        return try {
            db.insertOrThrow("Usuario", null, values)
            0 // Inserción correcta
        } catch (e: SQLiteConstraintException) {
            if (e.message?.contains("UNIQUE constraint failed: Usuario.correo") == true) return 1
            if (e.message?.contains("UNIQUE constraint failed: Usuario.nombre") == true) return 2
            else return -1
        } finally {
            db.close()
        }
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

    fun modificarUsuario(correo: String, nuevaPass: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("pass", nuevaPass)
        }

        return try {
            val rowsUpdated = db.update("Usuario", values, "correo = ?", arrayOf(correo))
            db.close()
            rowsUpdated > 0 // Devuelve true si se modificó al menos una fila
        } catch (e: Exception) {
            e.printStackTrace()
            false
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
        val db = writableDatabase
        try {
            val stmt = db.compileStatement(
                "INSERT OR IGNORE INTO Genero (idGenero, nombreGenero) VALUES (?, ?)"
            )
            stmt.bindString(1, idGenero)
            stmt.bindString(2, nombreGenero)
            stmt.executeInsert()
            stmt.close()
        } catch (e: Exception) {
            Log.e("GeneroInsertado", "Error: ${e.message}")
        } finally {
            db.close()
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
            // Primero comprobar si ya existe el título
            val cursor = db.rawQuery(
                "SELECT idTitulo FROM Titulo WHERE idTitulo = ?",
                arrayOf(id)
            )

            val existe = cursor.moveToFirst()
            cursor.close()

            if (existe) {
                Log.d("TituloInsertado", "El título ya existe: $nombre")
                return  // Salir, no hace falta insertar
            }

            // No existe, insertar
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

            val result = db.insert("Titulo", null, values)

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
            // Comprobar si existe el idGenero en la tabla Genero
            val cursorGenero = db.rawQuery("SELECT 1 FROM Genero WHERE idGenero = ?", arrayOf(idGenero))
            val generoExiste = cursorGenero.moveToFirst()
            cursorGenero.close()

            // Comprobar si existe el idTitulo en la tabla Titulo
            val cursorTitulo = db.rawQuery("SELECT 1 FROM Titulo WHERE idTitulo = ?", arrayOf(idTitulo))
            val tituloExiste = cursorTitulo.moveToFirst()
            cursorTitulo.close()

            if (!generoExiste || !tituloExiste) {
                Log.e("GeneroTituloInsertado", "No se puede insertar: Género o Título no existen. idGenero=$idGenero, idTitulo=$idTitulo")
                return  // Volvemos para atrás sin insertar nada
            }

            // Insertar solo si existen ambos
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

    // Hay que retocar este método para que solo liste los títulos que no están por estrenarse
    fun listaTitulos(): List<Titulo> {
        val listaTitulos = mutableListOf<Titulo>()
        val db = this.readableDatabase

        val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())

        val query = """
        SELECT * FROM Titulo 
        WHERE idTitulo IN (
            SELECT et.idTitulo 
            FROM Estreno e
            INNER JOIN Estreno_Titulo et ON e.idEstreno = et.idEstreno
            WHERE e.fechaEstreno <= ?
        )
        OR idTitulo NOT IN (
            SELECT idTitulo FROM Estreno_Titulo
        )
    """

        val cursor = db.rawQuery(query, arrayOf(fechaHoy))

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

                val estrenos = mutableListOf<Estreno>()
                val estrenoCursor = db.rawQuery(
                    """
                SELECT e.idEstreno, e.fechaEstreno, et.idTitulo, ep.idPlataforma
                FROM Estreno e
                INNER JOIN Estreno_Titulo et ON e.idEstreno = et.idEstreno
                INNER JOIN Estreno_Plataforma ep ON e.idEstreno = ep.idEstreno
                WHERE et.idTitulo = ?
                """.trimIndent(), arrayOf(idTitulo)
                )
                if (estrenoCursor.moveToFirst()) {
                    do {
                        estrenos.add(
                            Estreno(
                                idEstreno = estrenoCursor.getInt(0),
                                fechaEstreno = estrenoCursor.getString(1),
                                idTitulo = estrenoCursor.getString(2),
                                idPlataforma = estrenoCursor.getString(3)
                            )
                        )
                    } while (estrenoCursor.moveToNext())
                }
                estrenoCursor.close()

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
                        plataformas = plataformas,
                        estrenos = estrenos
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

                // ---- Estrenos relacionados ----
                val estrenos = mutableListOf<Estreno>()
                val estrenoCursor = db.rawQuery(
                    """
                SELECT e.idEstreno, e.fechaEstreno, et.idTitulo, ep.idPlataforma
                FROM Estreno e
                INNER JOIN Estreno_Titulo et ON e.idEstreno = et.idEstreno
                INNER JOIN Estreno_Plataforma ep ON e.idEstreno = ep.idEstreno
                WHERE et.idTitulo = ?
                """.trimIndent(), arrayOf(idTitulo)
                )
                if (estrenoCursor.moveToFirst()) {
                    do {
                        estrenos.add(
                            Estreno(
                                idEstreno = estrenoCursor.getInt(0),
                                fechaEstreno = estrenoCursor.getString(1),
                                idTitulo = estrenoCursor.getString(2),
                                idPlataforma = estrenoCursor.getString(3)
                            )
                        )
                    } while (estrenoCursor.moveToNext())
                }
                estrenoCursor.close()

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
                        plataformas = plataformas,
                        estrenos = estrenos
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

    // Metodo para intertar los títulos del top 10 manualmente en la BBDD

    private val insertTitulosTop10Series = """
            INSERT OR IGNORE INTO Titulo (idTitulo, nombre, nombreOriginal, descripcion, fechaInicio, fechaFin, temporadas, tipo, rating)
            VALUES 

            ("17340436", "El jardinero", "El jardinero", "Aunque aparenta ser un hábil jardinero en Pontevedra, Elmer es en realidad un sicario a las órdenes de su madre... hasta que se enamora de su víctima y lo pone todo en peligro.", "2025", "2025", 1, "series", 50), 
            
            ("15415106", "La cúpula de cristal", "Glaskupan", "Léonie Vincent interpreta a una criminóloga que se alía con su padre adoptivo, un antiguo jefe de policía, para investigar la desaparición de una niña.", "2025", "2025", 1, "series", 50),
             
             ("15794504", "Nueva vida en Ransom Canyon", "Ransom Canyon", "Un ganadero viudo empieza a sentir algo por la mejor amiga de su difunta esposa. Pero ¿conseguirá proteger lo que más le importa de un rival que amenaza con quitárselo todo?","2025", "2025", 1, "series", 50),
             
             ("15415212", "Adolescencia", "Adolescence", "Este drama sobre un adolescente de 13 años acusado de asesinar a una compañera de clase.","2025", "2025", 1, "series", 50),
             
             ("16252965", "Pulso", "PULSE", "Un grupo de residentes de Urgencias lidia con sus problemas laborales y personales en el Maguire Medical Center, un hospital de Miami sacudido por una polémica acusación.", "2025", "2025", 1, "series", 50),
             
             ("16256534", "Manual para señoritas", "Manual para señoritas", "Una carabina debe encontrar marido a tres hermanas rebeldes mientras sortea las normas de la alta sociedad del siglo XIX... y oculta sus propios secretos.", "2025", "2025", 1, "series", 50);
        """.trimIndent()

    private val insertTitulosTop10Peliculas = """
            INSERT OR IGNORE INTO Titulo (idTitulo, nombre, nombreOriginal, descripcion, fechaInicio, tipo, rating)
            VALUES 

            ("8954753", "Sin instrucciones", "Sin instrucciones", "Un soltero vive la vida sin preocupaciones hasta que su ex aparece y le deja a cargo de su bebé. Ochos años después, ella vuelve buscando el perdón... y recuperar a su hija.", "2024", "movie", 50), 
            
            ("15409348", "Mi lista de deseos", "The Life List", "Una joven debe cumplir su lista de deseos de adolescente para recibir una herencia en esta película basada en el bestseller del mismo nombre.","2025", "movie", 50);
        """.trimIndent()

    private val insertPostersTop10 = """
            INSERT OR IGNORE INTO Poster_Titulo (urlPoster, tipo, calidad, idTitulo)
            VALUES 

            ("https://www.lavanguardia.com/peliculas-series/images/serie/poster/2025/4/w300/8nEYffm7XDQXSGoh1Ce0jvd45AO.jpg", "vertical", "w360","17340436"),
            ("https://media.senscritique.com/media/000022782080/0/el_jardinero.jpg", "vertical", "w720","17340436"),
            ("https://lovingseries.com/wp-content/uploads/2025/03/el-jardinero-netflix.jpg", "horizontal", "w360","17340436"),
            ("https://images.ctfassets.net/4cd45et68cgf/2t8jX03sfDRguYtT4fvQQM/e549677504be11be46ca49a1e398459d/El_Jardinero_S01_E02_23012024_Niete__DSC7768.jpg?w=2560", "horizontal", "w2560","17340436"),
            
            ("https://www.lavanguardia.com/peliculas-series/images/serie/poster/2025/4/w1280/szOo7iApDRChsHoyAlahYKu6G7R.jpg", "vertical", "w360","15415106"),
            ("https://images.abandomoviez.net/dbs/foto/dbs_5662_73.jpg", "vertical", "w720","15415106"),
            ("https://cdn.avpasion.com/wp-content/uploads/2025/04/cupula-de-cristal-01.jpg", "horizontal", "w360","15415106"),
            ("https://cdn.avpasion.com/wp-content/uploads/2025/04/cupula-de-cristal-03.jpg", "horizontal", "w720","15415106"),
            
            ("https://static.wikia.nocookie.net/doblaje/images/3/3b/Poster-NVRC.png/revision/latest?cb=20250417194416&path-prefix=es", "vertical", "w360","15794504"),
            ("https://static.wikia.nocookie.net/doblaje/images/3/3b/Poster-NVRC.png/revision/latest?cb=20250417194416&path-prefix=es", "vertical", "w720","15794504"),
            ("https://televitos.com/wp-content/uploads/2025/04/NuevaVidaEnRansomCanyon_08.webp", "horizontal", "w360","15794504"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/Z-WHgqd_TeJxSuha8aZ5WpyLcX8/AAAABSkNYfTYVHee6S9PsQPBEhBIizNWpMWNBPAGOxv618ISQZTtpzf2i_oL2FXyqxYeYYjCQTbxajM5KMilrpAT8gCayyyHcjjyR12r.jpg?r=482", "horizontal", "w720","15794504"),
            
            ("https://pics.filmaffinity.com/adolescence-497490057-large.jpg", "vertical", "w360","15415212"),
            ("https://es.web.img3.acsta.net/r_1280_720/img/71/2f/712f0d66f71b769f9b15f1dceed7b41a.jpg", "vertical", "w720","15415212"),
            ("https://s3.abcstatics.com/abc/www/multimedia/play/2025/03/18/adolescence.jpg", "horizontal", "w360","15415212"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/Z-WHgqd_TeJxSuha8aZ5WpyLcX8/AAAABTwtmCD6J5UhUMrHUJtpSA_iZ1SSDvny4PDwaNDcAPSDOMWI29xaC2XT9isnVsbmhRbrIMYtcv6jutBamcsizbjlqe2dYQEX11UB.jpg?r=92c", "horizontal", "w720","15415212"),
            
            ("https://www.infobae.com/resizer/v2/WMX4BXCYVFGWBCQG2VHBKSKBSM.jpg?auth=848a0f8ef83fce084b9eb224833f7f997ffd15b2e35801d6b0ad7e60caefdb1c&smart=true&width=350&height=467&quality=85", "vertical", "w360","16252965"),
            ("https://pics.filmaffinity.com/pulse-107075411-large.jpg", "vertical", "w720","16252965"),
            ("https://indiehoy.com/wp-content/uploads/2025/04/pulso-reparto-netflix.jpg", "horizontal", "w360","16252965"),
            ("https://www.magazinespain.com/wp-content/uploads/2025/03/pulso-portada-min.jpg", "horizontal", "w720","16252965"),
            
            ("https://m.media-amazon.com/images/M/MV5BNWE3NTFhYjItNzQ4Ny00MDYxLTg0MDMtYWUwNmYwOTVjYzkzXkEyXkFqcGc@._V1_.jpg", "vertical", "w360","16256534"),
            ("https://images.ctfassets.net/4cd45et68cgf/3vdcxT7SJu69aPoQUgQmWa/76fe8bff749fcbf834a7a5095bff6051/es-ES_es_mps_s1_main_main_key_art_vertical_27x40_rgb_pre_1.jpg?w=1200", "vertical", "w720","16256534"),
            ("https://www.lavanguardia.com/peliculas-series/images/all/serie/backdrops/2025/3/serie-280890/w1280/q9aTsvevLD6URZgUruQQI9D94oC.jpg", "horizontal", "w360","16256534"),
            ("https://fotografias.larazon.es/clipping/cmsimages01/2025/03/11/9A4E80A5-2AB4-46A2-9629-D3EFAF8C76FF/cartel-oficial-serie-manual-senoritas_69.jpg?crop=4594,2584,x48,y0&width=1280&height=720&optimize=low&format=jpg", "horizontal", "w720","16256534"),
            
            ("https://images.justwatch.com/poster/330110999/s718/sin-instrucciones-2024.jpg", "vertical", "w360","8954753"),
            ("https://pics.filmaffinity.com/Sin_instrucciones-513077765-large.jpg", "vertical", "w720","8954753"),
            ("https://content20.lecturas.com/medio/2025/04/22/sin-instrucciones_b7458678_250422092743_1280x720.webp", "horizontal", "w360","8954753"),
            ("https://hips.hearstapps.com/hmg-prod/images/sin-instrucciones-pelicula-paco-leon-676abe8a8a2c4.jpg?crop=1.00xw:1.00xh;0,0&resize=1200:*", "horizontal", "w720","8954753"),
            
            ("https://es.web.img3.acsta.net/img/30/c9/30c94497f1a8d225a1ce32eda24f21cc.jpg", "vertical", "w360","15409348"),
            ("https://pics.filmaffinity.com/the_life_list-567019967-large.jpg", "vertical", "w720","15409348"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/6AYY37jfdO6hpXcMjf9Yu5cnmO0/AAAABRJr1h76k5VLlX2U78mAHPXZScihTFLUCM7lv5ze-hLPL2jplOm-VATY_NZ16IqBHlUxcbLKBTY7xrkK8HSq3p7ZSw2WER3gye0B.jpg?r=e66", "horizontal", "w360","15409348"),
            ("https://media.revistagq.com/photos/67ea537b0c8ed04210db38c0/16:9/w_2560%2Cc_limit/LIFELIST_20240425_28639_R%2520(1).jpg", "horizontal", "w720","15409348");
        """.trimIndent()

    private val insertGenerosTop10 = """
            INSERT OR IGNORE INTO Genero_Titulo (idGenero, idTitulo)
            VALUES 

            ("drama","17340436"),
            ("mystery","17340436"),
            ("thriller","17340436"),
            
            ("drama","15415106"),
            ("crime","15415106"),
            
            ("drama","15794504"),
            ("romance","15794504"),
            ("western","15794504"),
            
            ("drama","15415212"),
            ("crime","15415212"),
            ("mystery","15415212"),
            
            ("drama","16252965"),
            
            ("comedy","16256534"),
            ("history","16256534"),
            ("romance","16256534"),
            
            ("comedy","8954753"),
            
            ("comedy","15409348"),
            ("drama","15409348"),
            ("romance","15409348");
        """.trimIndent()

    private val insertPlataformasTop10 = """
            INSERT OR IGNORE INTO Plataforma_Titulo (idPlataforma, idTitulo, pais, disponible)
            VALUES 

            ("netflix","17340436", "es", 1),
            ("netflix","15415106", "es", 1),
            ("netflix","15794504", "es", 1),
            ("netflix","15415212", "es", 1),
            ("netflix","16252965", "es", 1),
            ("netflix","16256534", "es", 1),
            ("netflix","8954753", "es", 1),
            ("netflix","15409348", "es", 1);
        """.trimIndent()

    private val insertFechaTop10 = """
            INSERT OR IGNORE INTO Top10 (fechaTop)
            VALUES 
            
            ("23-04-2025"),
            ("23-04-2025");
        """.trimIndent()

    private val insertTopsSeparadosTop10 = """
            INSERT OR IGNORE INTO Top10Separado (idTop, tipo)
            VALUES 

            (1,"series"),
            (2,"movie");
        """.trimIndent()

    private val insertPosicionesTop10 = """
            INSERT OR IGNORE INTO Top10_Titulo (idTop, idTitulo, posicion)
            VALUES 

            (1,"17340436",1),
            (1,"15415106",2),
            (1,"922",3),
            (1,"15794504",4),
            (1,"16",5),
            (1,"15415212",6),
            (1,"13616",7),
            (1,"4058",8),
            (1,"16252965",9),
            (1,"16256534",10),
            
            (2,"8954753",1),
            (2,"6851",2),
            (2,"13193",3),
            (2,"12038",4),
            (2,"2887096",5),
            (2,"15409348",6),
            (2,"5021461",7),
            (2,"1864",8),
            (2,"3691",9),
            (2,"185277",10);
        """.trimIndent()

    private val insertPosicionesTop102 = """
            INSERT OR IGNORE INTO Top10_Titulo (idTop, idTitulo, posicion)
            VALUES 

            (1,"17340436",1),
            (1,"15415106",2),
            (1,"15794504",4),
            (1,"15415212",6),
            (1,"16252965",9),
            (1,"16256534",10),
            
            (2,"8954753",1),
            (2,"15409348",6);
        """.trimIndent()

    private val insertPlataformaTop10Separado = """
            INSERT OR IGNORE INTO Top10_Plataforma (idTop, idPlataforma)
            VALUES 

            (1,"netflix"),
            (2,"netflix");
        """.trimIndent()

    fun insertAllTop10Data() {
        val db = writableDatabase
        val top10TituloIds = listOf(
            "17340436", "15415106", "15794504", "15415212",
            "16252965", "16256534", "8954753", "15409348"
        )

        db.beginTransaction()
        try {
            val existenTitulos = top10TituloIds.any { isIdTitleExists(db, it) }
            if (existenTitulos) {
                Log.d("BBDD", "Algunos títulos del Top 10 ya existen. No se insertaron nuevos títulos.")
                return
            }

            listOf(
                insertTitulosTop10Series,
                insertTitulosTop10Peliculas,
                insertPostersTop10,
                insertGenerosTop10,
                insertPlataformasTop10,
                insertFechaTop10,
                insertPlataformaTop10Separado,
                insertTopsSeparadosTop10,
                insertPosicionesTop10 // Si se quiere trabajar en local cambiar a insertPosicionesTop102
            ).forEach { db.execSQL(it.trimIndent()) }

            db.setTransactionSuccessful()
            Log.d("BBDD", "Datos del Top 10 insertados correctamente.")
        } catch (e: Exception) {
            Log.e("BBDD", "Error insertando datos del Top 10: ${e.message}")
        } finally {
            db.endTransaction()
            db.close()
        }
    }


    fun isIdTitleExists(db: SQLiteDatabase, idTitulo: String): Boolean {
        val cursor = db.rawQuery("SELECT 1 FROM Titulo WHERE idTitulo = ?", arrayOf(idTitulo))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }


    fun obtenerTop10PorTipoNetflix(tipo: String, pais: String = "es"): List<TopTitulo> {
        val db = this.readableDatabase
        val lista = mutableListOf<TopTitulo>()

        val query = """
        SELECT T.idTitulo, T.nombre, T.nombreOriginal, T.descripcion, T.fechaInicio, 
               T.fechaFin, T.temporadas, T.tipo, T.rating, TT.posicion
        FROM Top10Separado TS
        JOIN Top10_Plataforma TP ON TS.idTop = TP.idTop
        JOIN Top10_Titulo TT ON TS.idTop = TT.idTop
        JOIN Titulo T ON TT.idTitulo = T.idTitulo
        JOIN Plataforma_Titulo PT ON T.idTitulo = PT.idTitulo
        WHERE TS.tipo = ? AND TP.idPlataforma = 'netflix' AND PT.pais = ?
        ORDER BY TT.posicion ASC;
    """

        val cursor = db.rawQuery(query, arrayOf(tipo, pais))
        if (cursor.moveToFirst()) {
            do {
                val idTitulo = cursor.getString(0)
                val nombre = cursor.getString(1)
                val nombreOriginal = cursor.getString(2)
                val descripcion = cursor.getString(3)
                val fechaInicio = cursor.getString(4)
                val fechaFin = cursor.getString(5)
                val temporadas = cursor.getIntOrNull(6)
                val tipo = cursor.getString(7)
                val rating = cursor.getInt(8)
                val posicion = cursor.getInt(9)

                // Obtener posters asociados al título
                val posters = obtenerPostersPorTitulo(idTitulo)

                // Obtener géneros asociados al título
                val generos = obtenerGenerosPorTitulo(idTitulo)

                // Obtener plataformas asociadas al título
                val plataformas = obtenerPlataformasPorTitulo(idTitulo)

                val estrenos = obtenerEstrenosPorTitulo(idTitulo)

                // Crear objeto Titulo
                val titulo = Titulo(
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
                    plataformas = plataformas,
                    estrenos = estrenos
                )

                lista.add(TopTitulo(titulo, posicion))

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return lista
    }

    // Método para comprobar si un título extite en la base de datos

    fun existeTitulo(db: SQLiteDatabase, idTitulo: String): Boolean {
        val query = "SELECT 1 FROM Titulo WHERE idTitulo = ? LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(idTitulo))
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    fun existeTitulo2(idTitulo: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM Titulo WHERE idTitulo = ? LIMIT 1", arrayOf(idTitulo))
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    // Función para obtener los posters asociados a un título
    fun obtenerPostersPorTitulo(idTitulo: String): List<Poster> {
        val db = this.readableDatabase
        val posters = mutableListOf<Poster>()

        val query = "SELECT * FROM Poster_Titulo WHERE idTitulo = ?"
        val cursor = db.rawQuery(query, arrayOf(idTitulo))

        if (cursor.moveToFirst()) {
            do {
                posters.add(
                    Poster(
                        idPoster = cursor.getInt(cursor.getColumnIndexOrThrow("idPoster")),
                        urlPoster = cursor.getString(cursor.getColumnIndexOrThrow("urlPoster")),
                        tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        calidad = cursor.getString(cursor.getColumnIndexOrThrow("calidad")),
                        idTitulo = idTitulo
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return posters
    }

    // Función para obtener los géneros asociados a un título
    fun obtenerGenerosPorTitulo(idTitulo: String): List<Genero> {
        val db = this.readableDatabase
        val generos = mutableListOf<Genero>()

        val query = """
        SELECT g.idGenero, g.nombreGenero 
        FROM Genero g 
        INNER JOIN Genero_Titulo gt ON g.idGenero = gt.idGenero 
        WHERE gt.idTitulo = ?
    """
        val cursor = db.rawQuery(query, arrayOf(idTitulo))

        if (cursor.moveToFirst()) {
            do {
                generos.add(
                    Genero(
                        idGenero = cursor.getString(0),
                        nombreGenero = cursor.getString(1),
                        idTitulo = idTitulo
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return generos
    }

    // Función para obtener las plataformas asociadas a un título
    fun obtenerPlataformasPorTitulo(idTitulo: String): List<Plataforma> {
        val db = this.readableDatabase
        val plataformas = mutableListOf<Plataforma>()

        val query = """
        SELECT p.idPlataforma, p.nombrePlataforma 
        FROM Plataforma p 
        INNER JOIN Plataforma_Titulo pt ON p.idPlataforma = pt.idPlataforma 
        WHERE pt.idTitulo = ?
    """
        val cursor = db.rawQuery(query, arrayOf(idTitulo))

        if (cursor.moveToFirst()) {
            do {
                plataformas.add(
                    Plataforma(
                        idPlataforma = cursor.getString(0),
                        nombrePlataforma = cursor.getString(1),
                        idTitulo = idTitulo
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return plataformas
    }

    // Función para obtener las plataformas asociadas a un título
    fun obtenerEstrenosPorTitulo(idTitulo: String): List<Estreno> {
        val db = this.readableDatabase
        val estrenos = mutableListOf<Estreno>()

        val query = """
        SELECT e.idEstreno, e.fechaEstreno, ep.idPlataforma
        FROM Estreno e
        INNER JOIN Estreno_Titulo et ON e.idEstreno = et.idEstreno
        INNER JOIN Estreno_Plataforma ep ON e.idEstreno = ep.idEstreno
        WHERE et.idTitulo = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(idTitulo))

        if (cursor.moveToFirst()) {
            do {
                estrenos.add(
                    Estreno(
                        idEstreno = cursor.getInt(0),
                        fechaEstreno = cursor.getString(1),
                        idTitulo = idTitulo,
                        idPlataforma = cursor.getString(2)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return estrenos
    }

    // Verificación si la plataforma ya está en la tabla Top10_Plataforma
    private fun existeTop10SeparadoPlataforma(db: SQLiteDatabase, idPlataforma: String): Boolean {
        val cursor = db.query(
            "Top10_Plataforma",
            arrayOf("idPlataforma"),
            "idPlataforma = ?",
            arrayOf(idPlataforma),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // Método para manejar la fecha (formato ISO 8601)
    private fun formatearFecha(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    // Ejemplo de estructura de datos del Top 10
    data class Top10Item(
        val idTitulo: String,
        val nombre: String,
        val descripcion: String,
        val posicion: Int,
        val fecha: Date
    )

    // Verificación de la existencia del par idTop e idTitulo en Top10_Titulo
    private fun existeTop10Titulo(db: SQLiteDatabase, idTop: Int, idTitulo: String): Boolean {
        val cursor = db.query(
            "Top10_Titulo",
            arrayOf("idTop", "idTitulo"),
            "idTop = ? AND idTitulo = ?",
            arrayOf(idTop.toString(), idTitulo),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // Verificación de la existencia de la plataforma para un título
    private fun existePlataformaTitulo(db: SQLiteDatabase, idPlataforma: String, idTitulo: String): Boolean {
        val cursor = db.query(
            "Plataforma_Titulo",
            arrayOf("idPlataforma", "idTitulo"),
            "idPlataforma = ? AND idTitulo = ?",
            arrayOf(idPlataforma, idTitulo),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // Insertar los estrenos manualmente en la BBDD

    private val insertTitulosEstrenosSeries = """
            INSERT OR IGNORE INTO Titulo (idTitulo, nombre, nombreOriginal, descripcion, fechaInicio, fechaFin, temporadas, tipo, rating)
            VALUES 

            ("185", "You", "You", "Cuando un brillante administrador de librería se cruza con una aspirante a escritora, utiliza Internet y las redes sociales para reunir los detalles más intimos y acercarse a ella.", "2018", "2025", 5, "series", 77),
             
             ("18436084", "Serpientes y Escaleras", "Serpientes y Escaleras", "Una ambiciosa profesora infravalorada aspira a dirigir un prestigioso colegio, pero, para llegar a la cima, deberá trepar por una resbaladiza cuesta de corrupción y mentiras.", "2025", "2025", 1, "series", 40);
        """.trimIndent()

    private val insertTitulosEstrenosPeliculas = """
            INSERT OR IGNORE INTO Titulo (idTitulo, nombre, nombreOriginal, descripcion, fechaInicio, tipo, rating)
            VALUES 

            ("14625603", "Mala influencia", "Mala influencia", "Un expresidiario empieza de cero cuando lo contratan para proteger a una rica heredera de un acosador pero, a medida que se conocen, cuesta resistirse a su química.", "2025", "movie", 55), 
            
            ("16692284", "La calle del terror: La reina del baile", "Fear Street: Prom Queen", "¿Quién será la reina del baile de 1988 del instituto Shadyside? La competencia de la discreta Lori es feroz incluso antes de que alguien empiece a matar a las candidatas.","2025", "movie", 40);
        """.trimIndent()

    private val insertPostersEstrenos = """
            INSERT OR IGNORE INTO Poster_Titulo (urlPoster, tipo, calidad, idTitulo)
            VALUES 

            ("https://pbs.twimg.com/media/GpTpqB5XEAA0BRY.jpg", "vertical", "w360","185"),
            ("https://www.reddit.com/media?url=https%3A%2F%2Fpreview.redd.it%2Ffirst-poster-for-the-final-season-of-you-releasing-in-2025-v0-ydubkzph1u7e1.jpg%3Fwidth%3D640%26crop%3Dsmart%26auto%3Dwebp%26s%3Dda0ee5c2c37a0d08cd3ba0fb208337c54021631b&rdt=46870", "vertical", "w720","185"),
            ("https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AA1Cw07C.img?w=768&h=432&m=6&x=252&y=105&s=550&d=100", "horizontal", "w360","185"),
            ("https://occ-0-8407-92.1.nflxso.net/dnm/api/v6/Z-WHgqd_TeJxSuha8aZ5WpyLcX8/AAAABdAI-UJGlj9fhiyRZwAK_CDFVDxcEvPv6TyzlynQWS2nEg6-nJK_juP58OOIdAbB1Hc9H9BLZbXo--bVM2E7ZeCM6YJtu1qAFSX0.jpg?r=e5c", "horizontal", "w720","185"),
            
            ("https://m.media-amazon.com/images/M/MV5BNDVmZTliNWYtMjMwMC00NDJkLWFjZGItZmZlY2UzMGQwMDlmXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg", "vertical", "w360","18436084"),
            ("https://marieclaire.com.mx/wp-content/uploads/2025/04/serpientes-y-escaleras-serie-3-819x1024.jpg", "vertical", "w720","18436084"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/E8vDc_W8CLv7-yMQu8KMEC7Rrr8/AAAABTX6j5mitj4ZO2e3I8dZsM0Dfv7nhiuEdy3h2rhRXTybnGEkBE9DDhwAlXp2JeCLhRZys26gUGpVPK7UKuEeXp9sf7fTj96C7eFF.jpg?r=4da", "horizontal", "w360","18436084"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/9pS1daC2n6UGc3dUogvWIPMR_OU/AAAABSy6sfSVS9zVHtzpXfxXrX-RxKkTIDMkJpe5XypcSMzXb_IbpKsXlddQvKrgt57F6Nky11Z4KASS6bgGMold6RpTpN541ZARnK3XU6Z3uU8X87nG7ujRW_CRPQ.jpg?r=bab", "horizontal", "w720","18436084"),
            
            ("https://nadieesperfecto.com/wp-content/uploads/2024/12/MALA_INFLUENCIA_MAIN_KA_POST_INSTAGRAM-scaled.jpg", "vertical", "w360","14625603"),
            ("https://m.media-amazon.com/images/M/MV5BNzBkMDZiOTYtN2U0Zi00NWU4LWFkMmYtZTUwOGYwZGE2NjdhXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg", "vertical", "w720","14625603"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/E8vDc_W8CLv7-yMQu8KMEC7Rrr8/AAAABbcXq94ucNjJRfnhJPJGVnQy-SF5CQ6a2_GdTx8rZ6s29LCE7DEQsTzIw17G668-6HgvgD7n3DmWkBUDaL763pDXeA4rSCLw17jb.jpg?r=35c", "horizontal", "w360","14625603"),
            ("https://occ-0-8407-90.1.nflxso.net/dnm/api/v6/6AYY37jfdO6hpXcMjf9Yu5cnmO0/AAAABXLR0Usf2SeEpdRM1uORNcFKLaCxkXdQHvjZttPQeFyjTgOT3UKV-Lbw1dTUCKkEVwm8C-hbJCFSL2sB75rXx9loRaOr_OZCDsI6.jpg?r=e30", "horizontal", "w720","14625603"),
            
            ("https://m.media-amazon.com/images/M/MV5BNjJlMDJjZTctNDlkYi00YTNmLWIyZjUtZjdmZTFhNDQwMTQ4XkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg", "vertical", "w360","16692284"),
            ("https://img.aullidos.com/imagenes/varios/calle-terror-reina-baile-2.jpeg", "vertical", "w720","16692284"),
            ("https://cdn.pipocamoderna.com.br/storage/2025/04/fear_street_prom_queen.png", "horizontal", "w360","16692284"),
            ("https://occ-0-8407-92.1.nflxso.net/dnm/api/v6/6AYY37jfdO6hpXcMjf9Yu5cnmO0/AAAABdrkxnJbYtv4wqiFF5Qa2wWVLsCxeheiTGuyfHn__tUebYbg_DXKtE1EHLcLnVvWO39yH2r9IIIFP08Dr0ULG1dIZVnbJmBtzdPF.jpg?r=bff", "horizontal", "w720","16692284");
        """.trimIndent()

    private val insertGenerosEstrenos = """
            INSERT OR IGNORE INTO Genero_Titulo (idGenero, idTitulo)
            VALUES 

            ("comedy","18436084"),
            
            ("crime","185"),
            ("romance","185"),
            
            ("romance","14625603"),
            ("thriller","14625603"),
            
            ("mystery","16692284"),
            ("thriller","16692284"),
            ("horror","16692284");
        """.trimIndent()

    private val insertPlataformasEstrenos = """
            INSERT OR IGNORE INTO Plataforma_Titulo (idPlataforma, idTitulo, pais, disponible)
            VALUES 

            ("netflix","18436084", "es", 1),
            ("netflix","185", "es", 1),
            ("netflix","14625603", "es", 1),
            ("netflix","16692284", "es", 1);
        """.trimIndent()

    /*val tablaEstreno = """
            CREATE TABLE Estreno (
                idEstreno INTEGER PRIMARY KEY AUTOINCREMENT,
                fechaEstreno TEXT NOT NULL
            );
        """

     */

    private val insertEstrenos = """
            INSERT OR IGNORE INTO Estreno (fechaEstreno)
            VALUES 

            ("2025-05-09"),
            ("2025-05-25"),
            ("2025-05-09"),
            ("2025-05-14");
        """.trimIndent()

    /*val tablaEstreno_Serie = """
            CREATE TABLE Estreno_Serie (
                idEstreno INTEGER PRIMARY KEY,
                temporada INTEGER NOT NULL,
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno)
            );
        """

     */

    private val insertEstrenosSeries = """
            INSERT OR IGNORE INTO Estreno_Serie (idEstreno, temporada)
            VALUES 

            (3,5),
            (4,1); 
        """.trimIndent()

    /*val tablaEstreno_Pelicula = """
            CREATE TABLE Estreno_Pelicula (
                idEstreno INTEGER PRIMARY KEY,
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno)
            );
        """

     */

    private val insertEstrenosPeliculas = """
            INSERT OR IGNORE INTO Estreno_Pelicula (idEstreno)
            VALUES 

            (1),
            (2); 
        """.trimIndent()

    /*val tablaEstreno_Titulo = """
            CREATE TABLE Estreno_Titulo (
                idEstreno INTEGER,
                idTitulo TEXT,
                PRIMARY KEY (idEstreno, idTitulo),
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno),
                FOREIGN KEY (idTitulo) REFERENCES Titulo(idTitulo)
            );
        """
    */

    private val insertEstrenosTitulos = """
        INSERT OR IGNORE INTO Estreno_Titulo (idEstreno, idTitulo)
        VALUES 
        (1, "14625603"),
        (2, "16692284"),
        (3, "185"),
        (4, "18436084");
    """.trimIndent()

    /*val tablaEstreno_Plataforma = """
            CREATE TABLE Estreno_Plataforma (
                idEstreno INTEGER,
                idPlataforma TEXT,
                PRIMARY KEY (idEstreno, idPlataforma),
                FOREIGN KEY (idEstreno) REFERENCES Estreno(idEstreno),
                FOREIGN KEY (idPlataforma) REFERENCES Plataforma(idPlataforma)
            );
        """
    */

    private val insertEstrenosPlataforma = """
        INSERT OR IGNORE INTO Estreno_Plataforma (idEstreno, idPlataforma)
        VALUES 
        (1, "netflix"),
        (2, "netflix"),
        (3, "netflix"),
        (4, "netflix");
    """.trimIndent()

    fun insertAllEstrenosData() {
        val db = writableDatabase
        val titulosEstrenoIds = listOf("14625603", "16692284", "185", "18436084")

        db.beginTransaction()
        try {
            val existenTitulos = titulosEstrenoIds.any { isIdTitleExists(db, it) }
            if (existenTitulos) {
                Log.d("BBDD", "Algunos títulos ya existen. No se insertaron nuevos títulos.")
                return
            }

            listOf(
                insertTitulosEstrenosSeries,
                insertTitulosEstrenosPeliculas,
                insertPostersEstrenos,
                insertGenerosEstrenos,
                insertPlataformasEstrenos,
                insertEstrenos,
                insertEstrenosSeries,
                insertEstrenosPeliculas,
                insertEstrenosTitulos,
                insertEstrenosPlataforma
            ).forEach { db.execSQL(it.trimIndent()) }

            db.setTransactionSuccessful()
            Log.d("BBDD", "Estrenos insertados correctamente.")
        } catch (e: Exception) {
            Log.e("BBDD", "Error insertando estrenos: ${e.message}")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun insertGeneros() {
        val db = writableDatabase
        val generos = listOf(
            "action" to "Acción",
            "animation" to "Animación",
            "adventure" to "Aventura",
            "scifi" to "Ciencia Ficción",
            "comedy" to "Comedia",
            "crime" to "Crimen",
            "documentary" to "Documental",
            "drama" to "Drama",
            "family" to "Familia",
            "fantasy" to "Fantasía",
            "war" to "Guerra",
            "history" to "Historia",
            "mystery" to "Misterio",
            "music" to "Música",
            "news" to "Noticias",
            "talk" to "Programa de Entrevistas",
            "reality" to "Realidad",
            "romance" to "Romance",
            "thriller" to "Suspenso",
            "horror" to "Terror",
            "western" to "Western"
        )

        db.beginTransaction()
        try {
            generos.filterNot { existeGenero(db, it.first) }
                .forEach { (id, nombre) ->
                    db.execSQL("INSERT INTO Genero (idGenero, nombreGenero) VALUES (?, ?)", arrayOf(id, nombre))
                }

            db.setTransactionSuccessful()
            Log.d("BBDD", "Géneros insertados (evitando duplicados).")
        } catch (e: Exception) {
            Log.e("BBDD", "Error al insertar géneros: ${e.message}")
        } finally {
            db.endTransaction()
            db.close()
        }
    }


    fun existeGenero(db: SQLiteDatabase, idGenero: String): Boolean {
        val cursor = db.rawQuery("SELECT 1 FROM Genero WHERE idGenero = ?", arrayOf(idGenero))
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }


    // Funciones para verificar, guardar y eliminar un estreno

    fun usuarioHaGuardadoEstreno(correo: String, idEstreno: Int): Boolean {
        val db = this.readableDatabase
        val query = """
        SELECT 1 FROM Estreno_Usuario 
        WHERE correo = ? AND idEstreno = ?
    """
        val cursor = db.rawQuery(query, arrayOf(correo, idEstreno.toString()))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun guardarEstrenoUsuario(correo: String, idEstreno: Int) {
        val db = this.readableDatabase
        val stmt = db.compileStatement("INSERT OR IGNORE INTO Estreno_Usuario (idEstreno, correo) VALUES (?, ?)")
        stmt.bindLong(1, idEstreno.toLong())
        stmt.bindString(2, correo)
        stmt.executeInsert()
    }

    fun eliminarEstrenoGuardado(correo: String, idEstreno: Int) {
        val db = this.readableDatabase
        val stmt = db.compileStatement("DELETE FROM Estreno_Usuario WHERE correo = ? AND idEstreno = ?")
        stmt.bindString(1, correo)
        stmt.bindLong(2, idEstreno.toLong())
        stmt.executeUpdateDelete()
    }

    // Función para obtener la lista de los estrenos

    fun listarTitulosPorPlataformaConEstreno(idPlataforma: String): List<Titulo> {
        val db = this.readableDatabase
        val titulos = mutableListOf<Titulo>()
        val query = """
        SELECT T.idTitulo, T.nombre, T.nombreOriginal, T.descripcion, T.fechaInicio, T.fechaFin, T.temporadas, T.tipo, T.rating
        FROM Titulo T
        INNER JOIN Estreno_Titulo ET ON T.idTitulo = ET.idTitulo
        INNER JOIN Estreno E ON E.idEstreno = ET.idEstreno
        INNER JOIN Estreno_Plataforma EP ON E.idEstreno = EP.idEstreno
        WHERE EP.idPlataforma = ?
        AND date(E.fechaEstreno) > date('now')
        GROUP BY T.idTitulo
    """
        val cursor = db.rawQuery(query, arrayOf(idPlataforma))
        while (cursor.moveToNext()) {
            val idTitulo = cursor.getString(0)
            val titulo = Titulo(
                idTitulo = idTitulo,
                nombre = cursor.getString(1),
                nombreOriginal = cursor.getString(2),
                descripcion = cursor.getString(3),
                fechaInicio = cursor.getString(4),
                fechaFin = cursor.getString(5),
                temporadas = cursor.getInt(6),
                tipo = cursor.getString(7),
                rating = cursor.getInt(8),
                plataformas = obtenerPlataformasPorTitulo(idTitulo),
                estrenos = obtenerEstrenosPorTitulo(idTitulo),
                posters = obtenerPostersPorTitulo(idTitulo),
                generos = obtenerGenerosPorTitulo(idTitulo)
            )
            titulos.add(titulo)
        }
        cursor.close()
        return titulos
    }

    fun listarTitulosPorPlataformaConEstreno2(idPlataforma: String): List<Titulo> {
        val db = this.readableDatabase
        val titulos = mutableListOf<Titulo>()
        val query = """
    SELECT T.idTitulo, T.nombre, T.nombreOriginal, T.descripcion, T.fechaInicio, T.fechaFin, T.temporadas, T.tipo, T.rating
    FROM Titulo T
    INNER JOIN Estreno_Titulo ET ON T.idTitulo = ET.idTitulo
    INNER JOIN Estreno E ON E.idEstreno = ET.idEstreno
    INNER JOIN Estreno_Plataforma EP ON E.idEstreno = EP.idEstreno
    WHERE EP.idPlataforma = ?
    AND datetime(E.fechaEstreno) > datetime('now')
    GROUP BY T.idTitulo
    """
        val cursor = db.rawQuery(query, arrayOf(idPlataforma))
        while (cursor.moveToNext()) {
            val idTitulo = cursor.getString(0)
            val titulo = Titulo(
                idTitulo = idTitulo,
                nombre = cursor.getString(1),
                nombreOriginal = cursor.getString(2),
                descripcion = cursor.getString(3),
                fechaInicio = cursor.getString(4),
                fechaFin = cursor.getString(5),
                temporadas = cursor.getInt(6),
                tipo = cursor.getString(7),
                rating = cursor.getInt(8),
                plataformas = obtenerPlataformasPorTitulo(idTitulo),
                estrenos = obtenerEstrenosPorTitulo(idTitulo),
                posters = obtenerPostersPorTitulo(idTitulo),
                generos = obtenerGenerosPorTitulo(idTitulo)
            )
            titulos.add(titulo)
        }
        cursor.close()
        return titulos
    }


    // Función para revisar los estrenos y actualizar las listas

    fun revisarEstrenosYActualizarListas2(context: Context) {

        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoyDate = formatoFecha.parse(formatoFecha.format(java.util.Date()))
        val estrenos = obtenerEstrenos() // List<Estreno>

        this.writableDatabase.use { db -> // 'db' es la base de datos dentro de este bloque 'use'
            for (estreno in estrenos) {
                val idEstreno = estreno.idEstreno
                val idTitulo = estreno.idTitulo
                val fechaEstrenoDate = formatoFecha.parse(estreno.fechaEstreno)

                if (fechaEstrenoDate != null && fechaHoyDate != null) {
                    if (fechaEstrenoDate == fechaHoyDate) {
                        obtenerNombreTitulo(idTitulo)?.let {
                            mostrarNotificacionDeEstreno2(context, it)
                            actualizarTodosLosTitulosVistos()
                            listarTitulosPorPlataformaConEstreno("netflix")
                        }
                    }

                    if (fechaEstrenoDate <= fechaHoyDate) {
                        val cursor = db.rawQuery(
                            "SELECT correo FROM Estreno_Usuario WHERE idEstreno = ?",
                            arrayOf(idEstreno.toString())
                        )

                        while (cursor.moveToNext()) {
                            val correo = cursor.getString(cursor.getColumnIndexOrThrow("correo"))

                            val checkCursor = db.rawQuery(
                                "SELECT 1 FROM Usuario_Titulo WHERE correo = ? AND idTitulo = ?",
                                arrayOf(correo, idTitulo)
                            )
                            if (!checkCursor.moveToFirst()) {
                                val values = ContentValues().apply {
                                    put("correo", correo)
                                    put("idTitulo", idTitulo)
                                }
                                db.insert("Usuario_Titulo", null, values)
                            }
                            checkCursor.close()
                        }
                        cursor.close()

                    }
                }
            }
        }
    }

    fun mostrarNotificacionDeEstreno(context: Context, titulo: String) {
        val channelId = "estrenos_channel"
        val channelName = "Notificaciones de Estrenos"
        val notificationId = titulo.hashCode() // Para evitar duplicados

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal (obligatorio en Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifica cuando se estrenan títulos"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Cambia por tu icono
            .setContentTitle("¡Estreno disponible!")
            .setContentText("Ya está disponible: $titulo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Mostrar la notificación
        notificationManager.notify(notificationId, builder.build())
    }

    fun mostrarNotificacionDeEstreno2(context: Context, titulo: String) {
        val channelId = "estrenos_channel"
        val channelName = "Notificaciones de Estrenos"
        val notificationId = titulo.hashCode() // Para evitar duplicados

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal (obligatorio en Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifica cuando se estrenan títulos"
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("MostrarNotificacion", "Canal de notificación creado con éxito")
        }

        // Cargar el icono de la app desde los recursos
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.logoapp2) // Reemplaza con tu icono

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logoapp2) // Icono pequeño de la notificación (también puedes cambiarlo)
            .setContentTitle("¡Estreno disponible!")
            .setContentText("Ya está disponible: $titulo")
            .setLargeIcon(largeIcon)  // Establece el icono grande para la notificación
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Mostrar la notificación
        notificationManager.notify(notificationId, builder.build())
    }


    fun actualizarTodosLosTitulosVistos() {
        val db = this.writableDatabase
        val fechaHoy = LocalDate.now().toString() // Formato yyyy-MM-dd

        // Obtener todos los estrenos pasados o iguales a hoy junto con el correo del usuario
        val query = """
        SELECT EU.correo, ET.idTitulo, EU.idEstreno
        FROM Estreno_Usuario EU
        JOIN Estreno E ON EU.idEstreno = E.idEstreno
        JOIN Estreno_Titulo ET ON E.idEstreno = ET.idEstreno
        WHERE date(E.fechaEstreno) <= date(?)
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(fechaHoy))

        if (cursor.moveToFirst()) {
            do {
                val correo = cursor.getString(cursor.getColumnIndexOrThrow("correo"))
                val idTitulo = cursor.getString(cursor.getColumnIndexOrThrow("idTitulo"))
                val idEstreno = cursor.getInt(cursor.getColumnIndexOrThrow("idEstreno"))

                // Verificar si ya existe en Usuario_Titulo
                val checkCursor = db.rawQuery(
                    "SELECT 1 FROM Usuario_Titulo WHERE correo = ? AND idTitulo = ?",
                    arrayOf(correo, idTitulo)
                )

                val yaExiste = checkCursor.moveToFirst()
                checkCursor.close()

                if (!yaExiste) {
                    // Insertar en Usuario_Titulo si no existe
                    val values = ContentValues().apply {
                        put("correo", correo)
                        put("idTitulo", idTitulo)
                    }
                    db.insert("Usuario_Titulo", null, values)
                }

                // En ambos casos (exista o no), eliminar de Estreno_Usuario si el título se movió
                db.delete("Estreno_Usuario", "idEstreno = ? AND correo = ?", arrayOf(idEstreno.toString(), correo))

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
    }


    fun obtenerEstrenos(): List<Estreno> {
        val listaEstrenos = mutableListOf<Estreno>()
        val db = this.readableDatabase

        val query = """
        SELECT e.idEstreno, e.fechaEstreno, et.idTitulo, ep.idPlataforma, t.tipo
        FROM Estreno e
        INNER JOIN Estreno_Titulo et ON e.idEstreno = et.idEstreno
        INNER JOIN Estreno_Plataforma ep ON e.idEstreno = ep.idEstreno
        INNER JOIN Titulo t ON et.idTitulo = t.idTitulo
    """

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val idEstreno = cursor.getInt(cursor.getColumnIndexOrThrow("idEstreno"))
                val fechaEstreno = cursor.getString(cursor.getColumnIndexOrThrow("fechaEstreno"))
                val idTitulo = cursor.getString(cursor.getColumnIndexOrThrow("idTitulo"))
                val idPlataforma = cursor.getString(cursor.getColumnIndexOrThrow("idPlataforma"))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))

                listaEstrenos.add(Estreno(idEstreno, fechaEstreno, idTitulo, idPlataforma))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return listaEstrenos
    }

    fun eliminarEstrenoPorTipo(db: SQLiteDatabase, idEstreno: Int, idTitulo: String) {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT tipo FROM Titulo WHERE idTitulo = ?",
            arrayOf(idTitulo)
        )

        if (cursor.moveToFirst()) {
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))?.lowercase(Locale.getDefault())

            when (tipo) {
                "serie" -> db.delete("Estreno_Serie", "idEstreno = ?", arrayOf(idEstreno.toString()))
                "pelicula" -> db.delete("Estreno_Pelicula", "idEstreno = ?", arrayOf(idEstreno.toString()))
                else -> Log.w("EliminarEstreno", "Tipo de título desconocido: $tipo")
            }
        }

        cursor.close()
    }

    fun obtenerNombreTitulo(idTitulo: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM Titulo WHERE idTitulo = ?", arrayOf(idTitulo))
        var nombre: String? = null
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
        }
        cursor.close()
        db.close()
        return nombre
    }

    fun insertUsuarioEstreno(idEstreno: Int, correo: String) {
        val db = writableDatabase

        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("idEstreno", idEstreno)
                put("correo", correo)
            }

            // INSERT OR IGNORE
            db.insertWithOnConflict("Estreno_Usuario", null, values, SQLiteDatabase.CONFLICT_IGNORE)
            db.setTransactionSuccessful()

            Log.d("BBDD", "Estreno guardado correctamente (o ya existía)")
        } catch (e: Exception) {
            Log.e("BBDD", "Error al guardar estreno: ${e.message}")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // Función para guardar los títulos estrenados en la lista del usuario

    fun actualizarTitulosVistosPorUsuario(correoUsuario: String) {
        val db = this.writableDatabase
        val fechaHoy = LocalDate.now().toString() // Formato yyyy-MM-dd

        // Consulta los estrenos guardados por el usuario cuya fecha ya ha pasado o es hoy
        val query = """
        SELECT ET.idTitulo, EU.idEstreno
        FROM Estreno_Usuario EU
        JOIN Estreno E ON EU.idEstreno = E.idEstreno
        JOIN Estreno_Titulo ET ON E.idEstreno = ET.idEstreno
        WHERE EU.correo = ? AND date(E.fechaEstreno) <= date(?)
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(correoUsuario, fechaHoy))

        if (cursor.moveToFirst()) {
            do {
                val idTitulo = cursor.getString(cursor.getColumnIndexOrThrow("idTitulo"))
                val idEstreno = cursor.getInt(cursor.getColumnIndexOrThrow("idEstreno"))

                // Verifica si ya está guardado en Usuario_Titulo
                val checkCursor = db.rawQuery(
                    "SELECT 1 FROM Usuario_Titulo WHERE correo = ? AND idTitulo = ?",
                    arrayOf(correoUsuario, idTitulo)
                )

                if (!checkCursor.moveToFirst()) {
                    // Si el título no está guardado, lo agregamos a Usuario_Titulo
                    val values = ContentValues().apply {
                        put("correo", correoUsuario)
                        put("idTitulo", idTitulo)
                    }
                    db.insert("Usuario_Titulo", null, values)
                }

                // Si el título se guardó correctamente en Usuario_Titulo, eliminamos el registro de Estreno_Usuario
                if (checkCursor.moveToFirst()) {
                    db.delete("Estreno_Usuario", "idEstreno = ? AND correo = ?", arrayOf(idEstreno.toString(), correoUsuario))
                }

                checkCursor.close()
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
    }

}