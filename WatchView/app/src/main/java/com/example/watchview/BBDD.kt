package com.example.watchview

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import androidx.core.database.getStringOrNull

class BBDD(context: Context) : SQLiteOpenHelper(context, "WatchViewBBDD.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        val tablaUsuario = """
            CREATE TABLE Usuario (
                correo TEXT PRIMARY KEY,
                nombre TEXT NOT NULL UNIQUE,
                pass TEXT NOT NULL,
                idFoto INTEGER,
                privilegios TEXT,
                FOREIGN KEY (idFoto) REFERENCES FotoPerfil(idFoto)
            );
        """

        val tablaFotoPerfil = """
            CREATE TABLE FotoPerfil (
               idFoto INTEGER PRIMARY KEY AUTOINCREMENT,
                nombreFoto TEXT NOT NULL
            );
        """

        val tablaTitulo ="""
            CREATE TABLE Titulo (
                idTitulo INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                fecha TEXT NOT NULL,
                fechaFin TEXT,
                poster TEXT NOT NULL, 
                temporadas INTEGER,
                duracion INTEGER NOT NULL,
                tipo TEXT NOT NULL CHECK (tipo IN ('pelicula', 'serie')),
                calificacion REAL NOT NULL
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

        val tablaTop10_Plataforma = """
            CREATE TABLE Top10_Plataforma (
                idTop INTEGER,
                idPlataforma INTEGER,
                PRIMARY KEY (idTop, idPlataforma),
                FOREIGN KEY (idTop) REFERENCES Top10(idTop),
                FOREIGN KEY (idPlataforma) REFERENCES Plataforma(idPlataforma)
            );
        """

        /*val tablaCestaProducto = """
            CREATE TABLE Cesta_Producto (
                idCesta INTEGER,
                idProducto INTEGER,
                cantidad INTEGER,
                PRIMARY KEY (idCesta, idProducto),
                FOREIGN KEY (idCesta) REFERENCES Cesta(idCesta),
                FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
            );
        """

        val tablaPedido = """
            CREATE TABLE Pedido (
                idPedido INTEGER PRIMARY KEY AUTOINCREMENT,
                correo TEXT NOT NULL,
                estado TEXT NOT NULL,
                fecha TEXT NOT NULL,
                importe REAL NOT NULL,
                FOREIGN KEY (correo) REFERENCES Usuario(correo)
            );
        """

        val tablaPedidosProductos = """
        CREATE TABLE Pedido_Producto (
            idPedido INTEGER NOT NULL,
            idProducto INTEGER NOT NULL,
            cantidad INTEGER,
            PRIMARY KEY (idPedido, idProducto),
            FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido),
            FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
        );
        """
        val tablaCursos = """
        CREATE TABLE Curso (
            idProducto INTEGER PRIMARY KEY,
            plazasDisponibles INTEGER NOT NULL,
            plazasMaximas INTEGER NOT NULL,
            FOREIGN KEY (idProducto) REFERENCES Producto(idProducto) ON DELETE CASCADE
        );
        """

        val tablaVelas = """
        CREATE TABLE Vela (
            idProducto INTEGER PRIMARY KEY,
            tamano TEXT NOT NULL,
            aromas TEXT NOT NULL,
            FOREIGN KEY (idProducto) REFERENCES Producto(idProducto) ON DELETE CASCADE
        );
        """*/
/*
        val insertVelas = """
            INSERT INTO Producto  (precio,descripcion,informacion, nombre, imagen)
            VALUES
            (9.99,'Disfruta de nuestra vela aromática con esencia de lavanda, que llena el ambiente de serenidad y frescura. Perfecta para acompañarte en momentos de relajación, ya sea mientras lees, meditas o simplemente te desconectas del mundo. Su delicado aroma floral transforma cualquier espacio en un remanso de paz, invitándote a respirar profundo y dejar atrás el estrés del día. ¡Haz de cada instante un oasis de calma y bienestar!','Todas nuestras velas y todos sus componentes, independientemente de su aroma, son 100% veganas y cruelty free.
Ingredientes: cera 100% de soja sin aditivos, mecha de algodón ecológico, aceites esenciales y esencias aromáticas de calidad premium, certificados por la IFRA.

260 gr / 450 gr

Horas de encendido: 60/90 horas', 'Vela con esencia de lavanda', 'https://i.pinimg.com/originals/66/82/d3/6682d3b1da1a78b3843b951613be0288.jpg'),

            (12.99,'Descubre nuestra vela perfumada de vainilla, diseñada para envolver tus sentidos en una cálida y dulce fragancia. Ideal para crear un ambiente acogedor en tus momentos de descanso o reflexión. Su aroma envolvente transforma cualquier espacio en un refugio de confort y tranquilidad, perfecto para tardes de lectura, un baño relajante o una cena íntima. ¡Llena tu hogar de magia y serenidad con cada chispa!','Todas nuestras velas y todos sus componentes, independientemente de su aroma, son 100% veganas y cruelty free.
Ingredientes: cera 100% de soja sin aditivos, mecha de algodón ecológico, aceites esenciales y esencias aromáticas de calidad premium, certificados por la IFRA.

260 gr / 450 gr

Horas de encendido: 60/90 horas', 'Vela perfumada de vainilla', 'https://i.pinimg.com/originals/66/82/d3/6682d3b1da1a78b3843b951613be0288.jpg'),

            (15.99,'Sumérgete en la elegancia de nuestra vela con fragancia de rosa, que llena tus espacios con un aroma floral y sofisticado. Perfecta para crear un ambiente romántico o para momentos de autocuidado, su delicado perfume inspira calma y belleza. Ya sea acompañando una cena especial, un baño relajante o una tarde tranquila, esta vela transforma cualquier rincón en un oasis de encanto y armonía. ¡Añade un toque de refinamiento y serenidad a tu día!','Todas nuestras velas y todos sus componentes, independientemente de su aroma, son 100% veganas y cruelty free.
Ingredientes: cera 100% de soja sin aditivos, mecha de algodón ecológico, aceites esenciales y esencias aromáticas de calidad premium, certificados por la IFRA.

260 gr / 450 gr

Horas de encendido: 60/90 horas', 'Vela con fragancia de rosa', 'https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmejorconsalud.as.com%2Fwp-content%2Fuploads%2F2019%2F05%2Fhacer-velas-arom%25C3%25A1ticas-de-gel.jpg&f=1&nofb=1&ipt=9520c5f7bc035d20b99fd4c49ec9007622ff70f2c18c619b4e3b4f762646794c&ipo=images'),

            (10.99,'Déjate llevar por el encanto exótico de nuestra vela de coco con aroma tropical, que trae a tu hogar la calidez y frescura de un paraíso lejano. Perfecta para momentos de relajación o para añadir un toque vibrante a tus espacios, su fragancia te transporta a playas de arena blanca y brisas tropicales. Ya sea para una tarde de descanso, una cena especial o simplemente para disfrutar el momento, esta vela convierte cualquier ambiente en un rincón de verano eterno. ¡Haz de cada día una escapada tropical!','Todas nuestras velas y todos sus componentes, independientemente de su aroma, son 100% veganas y cruelty free.
Ingredientes: cera 100% de soja sin aditivos, mecha de algodón ecológico, aceites esenciales y esencias aromáticas de calidad premium, certificados por la IFRA.

260 gr / 450 gr

Horas de encendido: 60/90 horas', 'Vela de coco con aroma tropical', 'https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmejorconsalud.as.com%2Fwp-content%2Fuploads%2F2019%2F05%2Fhacer-velas-arom%25C3%25A1ticas-de-gel.jpg&f=1&nofb=1&ipt=9520c5f7bc035d20b99fd4c49ec9007622ff70f2c18c619b4e3b4f762646794c&ipo=images'),

            (14.99,'Enciende la calidez de nuestra vela de canela y especias, que envuelve tu hogar en un abrazo aromático lleno de confort. Su fragancia rica y especiada es ideal para crear un ambiente acogedor en días frescos o para añadir un toque especial a tus momentos de relajación. Perfecta para acompañar tardes de lectura, reuniones íntimas o una pausa con tu bebida favorita, esta vela transforma cualquier espacio en un refugio cálido y nostálgico. ¡Haz que cada instante sea único con su magia especiada!','Todas nuestras velas y todos sus componentes, independientemente de su aroma, son 100% veganas y cruelty free.
Ingredientes: cera 100% de soja sin aditivos, mecha de algodón ecológico, aceites esenciales y esencias aromáticas de calidad premium, certificados por la IFRA.

260 gr / 450 gr

Horas de encendido: 60/90 horas', 'Vela de canela y especias', 'https://cdn.shopify.com/s/files/1/0732/7734/1971/files/R0M__VELA_RUSTIQUE_NO_AROMATICA_5.5X5.5_MARFIL_c.jpg?v=1686316324'),

            (15.99,'Disfruta de la pureza y frescura de nuestra vela con fragancia de algodón, que llena tus espacios de una sensación de limpieza y tranquilidad. Ideal para crear un ambiente sereno y relajante, su delicado aroma evoca la suavidad de las sábanas recién lavadas y la brisa fresca de un día soleado. Perfecta para tus momentos de descanso, meditación o simplemente para renovar el ambiente, esta vela convierte cualquier rincón en un oasis de calma y frescura. ¡Lleva la esencia de lo simple y lo puro a tu hogar!','Todas nuestras velas y todos sus componentes, independientemente de su aroma, son 100% veganas y cruelty free.
Ingredientes: cera 100% de soja sin aditivos, mecha de algodón ecológico, aceites esenciales y esencias aromáticas de calidad premium, certificados por la IFRA.

260 gr / 450 gr

Horas de encendido: 60/90 horas', 'Vela con fragancia de algodón', 'https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.pinimg.com%2Foriginals%2F7d%2Ff4%2F1a%2F7df41a57de92a482e68e279b07726563.png&f=1&nofb=1&ipt=2662a365556b941c61b1c75e572e002e7e40475172b6e8bdca758718da691f18&ipo=images');

        """

        val insertVelas2 = """

            INSERT INTO Vela (idProducto, tamano, aromas)
            VALUES
            (1, 'Pequeña', 'Lavanda'),
            (2, 'Mediana', 'Vainilla'),
            (3, 'Grande', 'Rosa'),
            (4, 'Pequeña', 'Coco'),
            (5, 'Mediana', 'Canela'),
            (6, 'Pequeña', 'Coco');

        """
        val insertAdmin = """
            INSERT INTO Usuario (correo, nombre, pass, direccion, fotoPerfil, privilegios, tlfn)
            VALUES ("admin@admin.com", "", "", "Calle 123", "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fimg.unocero.com%2F2018%2F08%2FAndroid-historique.jpg&f=1&nofb=1&ipt=fe7d3590f306c214cf695d66f776cb9fb34c0caaceddf20240a0b349bf154b8e&ipo=images", "admin", "623456789");
        """

        val insertCurso = """
            INSERT INTO Producto  (precio,descripcion,informacion, nombre, imagen)
            VALUES
            (39.99,'Disfruta de nuestros cursos de creación de velas dispuestos para todo el mundo de la mano de nuestros trabajadores expertos. Además, no es necesario tener ningún tipo de conocimiento previo, tan solo tener ganas de aprender como nosotros de enseñarte. También, tendrás la oportunidad de obtener una vela creada por tí realizada con la guía de nuestros profesores.'
            ,'Este curso  tiene una duración de 3 horas en el que se dispondrá de un profesor que explique y guíe durante el proceso de hacer una vela. Además, acompañará a cada uno para llevar a cabo su propia vela, sirviendo de una actividad artesanal aromática relajante.

Los cursos se darán todos los martes y jueves salvo festivos y vacaciones de 17h a 20h.

Las clases tienen una capacidad de 10 personas y se registrarán por orden de llegada.

Precio: 39,99€', 'Curso: Crea tu vela',
            'https://www.velastierradealba.com/wp-content/uploads/2017/11/aprende-a-hacer-velas-gratis.-curso-online.jpg');

        """

        val insertCurso2 = """

            INSERT INTO Curso (idProducto, plazasDisponibles, plazasMaximas)
            VALUES
            (7, 10, 10);

        """
*/

        db.execSQL(tablaUsuario)
        db.execSQL(tablaFotoPerfil)
        db.execSQL(tablaTitulo)
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
        db.execSQL(tablaTop10_Plataforma)


       /* db.execSQL(tablaVelas)
        db.execSQL(insertVelas)
        db.execSQL(insertVelas2)
        db.execSQL(insertAdmin)
        db.execSQL(insertCurso)
        db.execSQL(insertCurso2)*/

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Usuario")
        db.execSQL("DROP TABLE IF EXISTS FotoPerfil")
        db.execSQL("DROP TABLE IF EXISTS Titulo")
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
        db.execSQL("DROP TABLE IF EXISTS Top10_Titulo")
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
    fun insertarUsuario(correo:String,nombre:String, pass:String, privilegios:String?):Int{

        val db=this.writableDatabase
        val values= ContentValues().apply {
            put("correo",correo)
            put("nombre",nombre)
            put("pass",pass)
            put("privilegios",privilegios)
        }
        try{
            db.insertOrThrow("usuario",null,values)
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

    fun modificarUsuario(correo: String, nombre: String, pass: String, tlfn: String, fotoPerfil: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("pass", pass)
            put("tlfn", tlfn)
            put("fotoPerfil", fotoPerfil)
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

    // Método para añadir un nuevo usuario

    fun insertarUsuario2(correo: String, nombre: String, pass: String, tlfn: String, fotoPerfil: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("correo", correo)
            put("nombre", nombre)
            put("pass", pass)
            put("tlfn", tlfn)
            put("fotoPerfil", fotoPerfil)
        }
        return try {
            db.insertOrThrow("Usuario", null, values)
            db.close()
            0 // Inserción correcta
        } catch (e: SQLiteConstraintException) {
            when {
                e.message?.contains("UNIQUE constraint failed: Usuario.correo") == true -> 1 // Correo duplicado
                e.message?.contains("UNIQUE constraint failed: Usuario.nombre") == true -> 2 // Nombre duplicado
                else -> -1 // Otro error
            }
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
            Usuario.direccion = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("direccion"))
            Usuario.fotoPerfil = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("fotoPerfil"))
            Usuario.privilegios = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("privilegios"))
            Usuario.tlfn = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("tlfn"))
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
        Usuario.direccion = null
        Usuario.fotoPerfil = null
        Usuario.privilegios = null
        Usuario.tlfn = null

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
            putString("direccion", Usuario.direccion)
            putString("fotoPerfil", Usuario.fotoPerfil)
            putString("privilegios", Usuario.privilegios)
            putString("tlfn", Usuario.tlfn)
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








}