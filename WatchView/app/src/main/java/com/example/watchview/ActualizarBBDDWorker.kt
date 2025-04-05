package com.example.watchview

/*import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ActualizarBBDDWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val dbHelper = BBDD(applicationContext)
        val db = dbHelper.writableDatabase

        try {
            val datosAPI = obtenerDatosAPI() // Obtiene datos desde la API
            if (datosAPI != null) {
                for (titulo in datosAPI) {
                    insertarTitulo(db, titulo) // Inserta o actualiza los datos en SQLite
                }
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("ActualizarBBDDWorker", "Error al actualizar la BBDD", e)
            return Result.retry() // Reintentar en caso de error
        } finally {
            db.close()
        }
    }

    /**
     * Obtiene los datos de la API de Streaming Availability
     */
    private fun obtenerDatosAPI(): List<Titulo>? {
        val url =
            "https://streaming-availability.p.rapidapi.com/search/basic?country=ES&services=netflix,prime,max&output_language=es"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("X-RapidAPI-Key", "28888f078fmshd897f3917e4d0e4p14c168jsnd6444299fc66") // Reemplaza con tu clave de API
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return null
            val json = JSONObject(responseBody)

            // Extraer la lista de títulos desde el JSON
            val listaTitulos = mutableListOf<Titulo>()
            val resultados = json.getJSONArray("results")

            for (i in 0 until resultados.length()) {
                val item = resultados.getJSONObject(i)

                val id = item.getInt("id")
                val nombre = item.getString("title")
                val descripcion = item.getString("overview")
                val tipo = if (item.getString("itemType") == "show") "serie" else "pelicula"
                val fecha = item.getInt("firstAirYear").toString()
                val fechaFin =
                    if (item.has("lastAirYear") && !item.isNull("lastAirYear")) item.getInt("lastAirYear")
                        .toString() else null
                val temporadas =
                    if (tipo == "serie" && item.has("seasons")) item.optInt("seasons", 0) else null
                val duracion =
                    if (tipo == "pelicula" && item.has("runtime")) item.optInt("runtime", 0) else 0
                val rating =
                    if (item.has("imdbRating")) item.optDouble("imdbRating", 0.0).toInt() else 0

                val titulo = Titulo(
                    id,
                    nombre,
                    descripcion,
                    fecha,
                    fechaFin,
                    temporadas,
                    duracion,
                    tipo,
                    rating
                )
                listaTitulos.add(titulo)
            }
            listaTitulos
        } catch (e: Exception) {
            Log.e("ActualizarBBDDWorker", "Error obteniendo datos de la API", e)
            null
        }
    }

    /**
     * Inserta o actualiza un título en la base de datos
     */
    private fun insertarTitulo(db: SQLiteDatabase, titulo: Titulo) {
        val insertQuery = """
        INSERT OR IGNORE INTO Titulo (idTitulo, nombre, descripcion, fecha, fechaFin, temporadas, duracion, tipo, rating)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
    """.trimIndent()

        val updateQuery = """
        UPDATE Titulo SET
            nombre = ?, descripcion = ?, fecha = ?, fechaFin = ?, temporadas = ?, duracion = ?, tipo = ?, rating = ?
        WHERE idTitulo = ?;
    """.trimIndent()

        val insertStatement = db.compileStatement(insertQuery)
        insertStatement.bindLong(1, titulo.idTitulo.toLong())
        insertStatement.bindString(2, titulo.nombre)
        insertStatement.bindString(3, titulo.descripcion)
        insertStatement.bindString(4, titulo.fecha)

        if (titulo.fechaFin != null) insertStatement.bindString(5, titulo.fechaFin) else insertStatement.bindNull(5)
        if (titulo.temporadas != null) insertStatement.bindLong(6, titulo.temporadas.toLong()) else insertStatement.bindNull(6)

        insertStatement.bindLong(7, titulo.duracion.toLong())
        insertStatement.bindString(8, titulo.tipo)
        insertStatement.bindLong(9, titulo.rating.toLong())

        insertStatement.execute()

        // Si ya existe, entonces actualizamos
        val updateStatement = db.compileStatement(updateQuery)
        updateStatement.bindString(1, titulo.nombre)
        updateStatement.bindString(2, titulo.descripcion)
        updateStatement.bindString(3, titulo.fecha)

        if (titulo.fechaFin != null) updateStatement.bindString(4, titulo.fechaFin) else updateStatement.bindNull(4)
        if (titulo.temporadas != null) updateStatement.bindLong(5, titulo.temporadas.toLong()) else updateStatement.bindNull(5)

        updateStatement.bindLong(6, titulo.duracion.toLong())
        updateStatement.bindString(7, titulo.tipo)
        updateStatement.bindLong(8, titulo.rating.toLong())
        updateStatement.bindLong(9, titulo.idTitulo.toLong())

        updateStatement.execute()
    }


    /**
     * Modelo de datos para Titulo
     */
    data class Titulo(
        val idTitulo: Int,
        val nombre: String,
        val descripcion: String,
        val fecha: String,
        val fechaFin: String?,
        val temporadas: Int?,
        val duracion: Int,
        val tipo: String,
        val rating: Int
    )
}*/