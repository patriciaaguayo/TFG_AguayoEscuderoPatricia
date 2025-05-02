package com.example.watchview

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

object ApiDataLoader {

    fun fetchGenresFromApi(context: Context) {
        val bd = BBDD(context)

        if (bd.hayGenerosGuardados()) {
            Log.d("GeneroInsertado", "Géneros ya presentes, no se llama a la API.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val apiKey = context.getString(R.string.api_key)
            val request = Request.Builder()
                .url("https://streaming-availability.p.rapidapi.com/genres?output_language=es")
                .get()
                .addHeader("x-rapidapi-host", "streaming-availability.p.rapidapi.com")
                .addHeader("x-rapidapi-key", apiKey)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    val genresArray = JSONArray(jsonResponse)
                    for (i in 0 until genresArray.length()) {
                        val g = genresArray.getJSONObject(i)
                        bd.insertGenero(g.getString("id"), g.getString("name"))
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Géneros cargados", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al obtener géneros", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error de red en géneros", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun guardarTitulosNetflix(context: Context) {
        val bd = BBDD(context)

        if (bd.hayTitulosGuardados()) {
            Log.d("TituloInsertado", "Títulos ya presentes.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val apiKey = context.getString(R.string.api_key)
            var hasMore = true
            var cursor: String? = null

            while (hasMore) {
                val urlBuilder = StringBuilder("https://streaming-availability.p.rapidapi.com/shows/search/filters")
                urlBuilder.append("?country=es&series_granularity=show&order_direction=asc&output_language=es&catalogs=netflix")
                if (cursor != null) urlBuilder.append("&show_cursor=${Uri.encode(cursor)}")

                val request = Request.Builder()
                    .url(urlBuilder.toString())
                    .get()
                    .addHeader("x-rapidapi-host", "streaming-availability.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", apiKey)
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        val (next, more) = leerJSONTitulos(context, json)
                        cursor = next
                        hasMore = more
                    } else {
                        hasMore = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    hasMore = false
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Títulos cargados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun leerJSONTitulos(context: Context, jsonResponse: String?): Pair<String?, Boolean> {
        val bd = BBDD(context)
        var nextCursor: String? = null
        var hasMore = false

        try {
            if (jsonResponse != null) {
                val root = JSONObject(jsonResponse)
                val titlesArray = root.getJSONArray("shows")
                hasMore = root.optBoolean("hasMore", false)
                nextCursor = root.optString("nextCursor", null)

                for (i in 0 until titlesArray.length()) {
                    val show = titlesArray.getJSONObject(i)

                    val idTitulo = show.optString("id")
                    val tipo = show.optString("showType", "movie")
                    val nombre = show.optString("title")
                    val nombreOriginal = show.optString("originalTitle", nombre)
                    val descripcion = show.optString("overview")
                    val rating = show.optInt("rating", 0)

                    val fechaInicio = if (tipo == "series") {
                        show.optInt("firstAirYear", 0).toString()
                    } else {
                        show.optInt("releaseYear", 0).toString()
                    }

                    val fechaFin = if (tipo == "series") {
                        show.optInt("lastAirYear", 0).takeIf { it != 0 }?.toString()
                    } else null

                    val temporadas = if (tipo == "series") {
                        show.optInt("seasonCount", 0).takeIf { it > 0 }
                    } else null

                    bd.insertTitulo(
                        id = idTitulo,
                        nombre = nombre,
                        nombreOriginal = nombreOriginal,
                        descripcion = descripcion,
                        fechaInicio = fechaInicio,
                        fechaFin = fechaFin,
                        temporadas = temporadas,
                        tipo = tipo,
                        rating = rating
                    )

                    val genres = show.optJSONArray("genres")
                    genres?.let {
                        for (j in 0 until it.length()) {
                            val genre = it.getJSONObject(j)
                            val idGenero = genre.optString("id")
                            bd.insertGeneroTitulo(idGenero, idTitulo)
                        }
                    }

                    val imageSet = show.optJSONObject("imageSet")
                    imageSet?.let {
                        val vertical = it.optJSONObject("verticalPoster")
                        val horizontal = it.optJSONObject("horizontalPoster")

                        vertical?.let { v ->
                            listOf("w360", "w720").forEach { calidad ->
                                val url = v.optString(calidad, null)
                                if (!url.isNullOrEmpty()) {
                                    bd.insertPosterTitulo(idTitulo, "vertical", calidad, url)
                                }
                            }
                        }

                        horizontal?.let { h ->
                            listOf("w360", "w720").forEach { calidad ->
                                val url = h.optString(calidad, null)
                                if (!url.isNullOrEmpty()) {
                                    bd.insertPosterTitulo(idTitulo, "horizontal", calidad, url)
                                }
                            }
                        }
                    }

                    val streamingOptions = show.optJSONObject("streamingOptions")
                    val opcionesEspaña = streamingOptions?.optJSONArray("es")
                    opcionesEspaña?.let {
                        for (k in 0 until it.length()) {
                            val plataforma = it.getJSONObject(k)
                            val service = plataforma.optJSONObject("service")
                            val idPlataforma = service?.optString("id") ?: continue
                            val disponible = plataforma.optBoolean("available", false)

                            // Comprobamos si ya existe la relación entre el título y la plataforma
                            val existe = bd.checkPlataformaTituloExistente(idTitulo, idPlataforma)

                            if (!existe) {
                                // Si no existe, insertamos la relación
                                bd.insertPlataformaTitulo(
                                    idTitulo = idTitulo,
                                    idPlataforma = idPlataforma,
                                    pais = "es",
                                    disponible = disponible
                                )
                                Log.d("PlataformaTitulo", "Relación insertada: $idTitulo ↔ $idPlataforma")
                            } else {
                                Log.d("PlataformaTitulo", "La relación ya existe: $idTitulo ↔ $idPlataforma")
                            }
                        }
                    }


                    Log.d("TituloInsertado", "Procesado título: $nombre")
                }

                Log.d("TituloInsertado", "Página procesada. hasMore: $hasMore, nextCursor: $nextCursor")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Error al procesar los títulos", Toast.LENGTH_SHORT).show()
            }
        }

        return Pair(nextCursor, hasMore)
    }

    fun guardarTitulosNetflix2(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            BBDD(context).use { bd ->

                if (bd.hayTitulosGuardados()) {
                    Log.d("TituloInsertado", "Títulos ya presentes.")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Títulos ya guardados previamente", Toast.LENGTH_SHORT).show()
                    }
                    return@use
                }

                val client = OkHttpClient()
                val apiKey = context.getString(R.string.api_key)
                var hasMore = true
                var cursor: String? = null
                var totalGuardados = 0
                val maxTitulos = 20

                while (hasMore && totalGuardados < maxTitulos) {
                    val urlBuilder = StringBuilder("https://streaming-availability.p.rapidapi.com/shows/search/filters")
                    urlBuilder.append("?country=es&series_granularity=show&order_direction=asc&output_language=es&catalogs=netflix")
                    if (cursor != null) urlBuilder.append("&show_cursor=${Uri.encode(cursor)}")

                    val request = Request.Builder()
                        .url(urlBuilder.toString())
                        .get()
                        .addHeader("x-rapidapi-host", "streaming-availability.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", apiKey)
                        .build()

                    try {
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val (next, more, guardadosEnEstaPagina) = leerJSONTitulos2(context, json, maxTitulos - totalGuardados)
                            totalGuardados += guardadosEnEstaPagina
                            cursor = next
                            hasMore = more
                        } else {
                            hasMore = false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        hasMore = false
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Títulos cargados", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    fun leerJSONTitulos2(context: Context, jsonResponse: String?, maxTitulos: Int): Triple<String?, Boolean, Int> {
        var nextCursor: String? = null
        var hasMore = false
        var titulosGuardados = 0

        try {
            if (jsonResponse != null) {
                BBDD(context).use { bd ->  // ← 👈🏻 Esto asegura el cierre automático

                    val root = JSONObject(jsonResponse)
                    val titlesArray = root.getJSONArray("shows")
                    hasMore = root.optBoolean("hasMore", false)
                    nextCursor = root.optString("nextCursor", null)

                    for (i in 0 until titlesArray.length()) {
                        if (titulosGuardados >= maxTitulos) break

                        val show = titlesArray.getJSONObject(i)

                        val idTitulo = show.optString("id")
                        val tipo = show.optString("showType", "movie")
                        val nombre = show.optString("title")
                        val nombreOriginal = show.optString("originalTitle", nombre)
                        val descripcion = show.optString("overview")
                        val rating = show.optInt("rating", 0)

                        val fechaInicio = if (tipo == "series") {
                            show.optInt("firstAirYear", 0).toString()
                        } else {
                            show.optInt("releaseYear", 0).toString()
                        }

                        val fechaFin = if (tipo == "series") {
                            show.optInt("lastAirYear", 0).takeIf { it != 0 }?.toString()
                        } else null

                        val temporadas = if (tipo == "series") {
                            show.optInt("seasonCount", 0).takeIf { it > 0 }
                        } else null

                        bd.insertTitulo(
                            id = idTitulo,
                            nombre = nombre,
                            nombreOriginal = nombreOriginal,
                            descripcion = descripcion,
                            fechaInicio = fechaInicio,
                            fechaFin = fechaFin,
                            temporadas = temporadas,
                            tipo = tipo,
                            rating = rating
                        )

                        titulosGuardados++

                        val genres = show.optJSONArray("genres")
                        genres?.let {
                            for (j in 0 until it.length()) {
                                val genre = it.getJSONObject(j)
                                val idGenero = genre.optString("id")
                                bd.insertGeneroTitulo(idGenero, idTitulo)
                            }
                        }

                        val imageSet = show.optJSONObject("imageSet")
                        imageSet?.let {
                            val vertical = it.optJSONObject("verticalPoster")
                            val horizontal = it.optJSONObject("horizontalPoster")

                            vertical?.let { v ->
                                listOf("w360", "w720").forEach { calidad ->
                                    val url = v.optString(calidad, null)
                                    if (!url.isNullOrEmpty()) {
                                        bd.insertPosterTitulo(idTitulo, "vertical", calidad, url)
                                    }
                                }
                            }

                            horizontal?.let { h ->
                                listOf("w360", "w720").forEach { calidad ->
                                    val url = h.optString(calidad, null)
                                    if (!url.isNullOrEmpty()) {
                                        bd.insertPosterTitulo(idTitulo, "horizontal", calidad, url)
                                    }
                                }
                            }
                        }

                        val streamingOptions = show.optJSONObject("streamingOptions")
                        val opcionesEspaña = streamingOptions?.optJSONArray("es")
                        opcionesEspaña?.let {
                            for (k in 0 until it.length()) {
                                val plataforma = it.getJSONObject(k)
                                val service = plataforma.optJSONObject("service")
                                val idPlataforma = service?.optString("id") ?: continue

                                if (idPlataforma != "netflix") continue

                                val disponible = plataforma.optBoolean("available", false)
                                val existe = bd.checkPlataformaTituloExistente(idTitulo, idPlataforma)

                                if (!existe) {
                                    bd.insertPlataformaTitulo(
                                        idTitulo = idTitulo,
                                        idPlataforma = idPlataforma,
                                        pais = "es",
                                        disponible = disponible
                                    )
                                    Log.d("PlataformaTitulo", "Relación insertada: $idTitulo ↔ $idPlataforma")
                                } else {
                                    Log.d("PlataformaTitulo", "La relación ya existe: $idTitulo ↔ $idPlataforma")
                                }
                            }
                        }

                        Log.d("TituloInsertado", "Procesado título: $nombre")
                    }
                }

                Log.d("TituloInsertado", "Página procesada. hasMore: $hasMore, nextCursor: $nextCursor")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Error al procesar los títulos", Toast.LENGTH_SHORT).show()
            }
        }

        return Triple(nextCursor, hasMore, titulosGuardados)
    }


    // Métodos para buscar y guardar títulos por nombre

    suspend fun guardarTitulosPorNombreSeries(context: Context, listaTitulos: List<String>) {
        val bd = BBDD(context)
        val client = OkHttpClient()
        val apiKey = context.getString(R.string.api_key)

        withContext(Dispatchers.IO) {
            for (titulo in listaTitulos) {
                try {
                    val url = HttpUrl.Builder()
                        .scheme("https")
                        .host("streaming-availability.p.rapidapi.com")
                        .addPathSegments("shows/search/title")
                        .addQueryParameter("country", "es")
                        .addQueryParameter("title", titulo)
                        .addQueryParameter("series_granularity", "show")
                        .addQueryParameter("show_type", "series")
                        .addQueryParameter("output_language", "es")
                        .build()

                    val request = Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("x-rapidapi-host", "streaming-availability.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", apiKey)
                        .build()

                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        leerJSONTituloUnico(context, json)
                    } else {
                        Log.e("BusquedaTitulo", "Fallo al buscar: $titulo")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun guardarTitulosPorNombrePeliculas(context: Context, listaTitulos: List<String>) {
        val bd = BBDD(context)
        val client = OkHttpClient()
        val apiKey = context.getString(R.string.api_key)

        withContext(Dispatchers.IO) {
            for (titulo in listaTitulos) {
                try {
                    val url = HttpUrl.Builder()
                        .scheme("https")
                        .host("streaming-availability.p.rapidapi.com")
                        .addPathSegments("shows/search/title")
                        .addQueryParameter("country", "es")
                        .addQueryParameter("title", titulo)
                        .addQueryParameter("show_type", "movie")
                        .addQueryParameter("output_language", "es")
                        .build()

                    val request = Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("x-rapidapi-host", "streaming-availability.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", apiKey)
                        .build()

                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        leerJSONTituloUnico(context, json)
                    } else {
                        Log.e("BusquedaTitulo", "Fallo al buscar: $titulo")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun leerJSONTituloUnico(context: Context, jsonResponse: String?): Boolean {
        var insertadoConExito = false

        try {
            if (jsonResponse != null) {
                val showsArray = JSONArray(jsonResponse)
                val show = showsArray.getJSONObject(0)

                BBDD(context).use { bd ->
                    val idTitulo = show.optString("id")

                    // Comprobación: si ya existe, no lo insertamos
                    if (bd.existeTitulo2(idTitulo)) {
                        Log.d("TituloInsertado", "El título ya existe y no será insertado: $idTitulo")
                        return false
                    }

                    val tipo = show.optString("showType", "movie")
                    val nombre = show.optString("title")
                    val nombreOriginal = show.optString("originalTitle", nombre)
                    val descripcion = show.optString("overview")
                    val rating = show.optInt("rating", 0)

                    val fechaInicio = if (tipo == "series") {
                        show.optInt("firstAirYear", 0).toString()
                    } else {
                        show.optInt("releaseYear", 0).toString()
                    }

                    val fechaFin = if (tipo == "series") {
                        show.optInt("lastAirYear", 0).takeIf { it != 0 }?.toString()
                    } else null

                    val temporadas = if (tipo == "series") {
                        show.optInt("seasonCount", 0).takeIf { it > 0 }
                    } else null

                    bd.insertTitulo(
                        id = idTitulo,
                        nombre = nombre,
                        nombreOriginal = nombreOriginal,
                        descripcion = descripcion,
                        fechaInicio = fechaInicio,
                        fechaFin = fechaFin,
                        temporadas = temporadas,
                        tipo = tipo,
                        rating = rating
                    )

                    val genres = show.optJSONArray("genres")
                    genres?.let {
                        for (j in 0 until it.length()) {
                            val genre = it.getJSONObject(j)
                            val idGenero = genre.optString("id")
                            bd.insertGeneroTitulo(idGenero, idTitulo)
                        }
                    }

                    val imageSet = show.optJSONObject("imageSet")
                    imageSet?.let {
                        val vertical = it.optJSONObject("verticalPoster")
                        val horizontal = it.optJSONObject("horizontalPoster")

                        vertical?.let { v ->
                            listOf("w360", "w720").forEach { calidad ->
                                val url = v.optString(calidad, null)
                                if (!url.isNullOrEmpty()) {
                                    bd.insertPosterTitulo(idTitulo, "vertical", calidad, url)
                                }
                            }
                        }

                        horizontal?.let { h ->
                            listOf("w360", "w720").forEach { calidad ->
                                val url = h.optString(calidad, null)
                                if (!url.isNullOrEmpty()) {
                                    bd.insertPosterTitulo(idTitulo, "horizontal", calidad, url)
                                }
                            }
                        }
                    }

                    val streamingOptions = show.optJSONObject("streamingOptions")
                    val opcionesEspaña = streamingOptions?.optJSONArray("es")
                    opcionesEspaña?.let {
                        for (k in 0 until it.length()) {
                            val plataforma = it.getJSONObject(k)
                            val service = plataforma.optJSONObject("service")
                            val idPlataforma = service?.optString("id") ?: continue

                            if (idPlataforma != "netflix") continue

                            val disponible = plataforma.optBoolean("available", false)
                            val existe = bd.checkPlataformaTituloExistente(idTitulo, idPlataforma)

                            if (!existe) {
                                bd.insertPlataformaTitulo(
                                    idTitulo = idTitulo,
                                    idPlataforma = idPlataforma,
                                    pais = "es",
                                    disponible = disponible
                                )
                                Log.d("PlataformaTitulo", "Relación insertada: $idTitulo ↔ $idPlataforma")
                            } else {
                                Log.d("PlataformaTitulo", "La relación ya existe: $idTitulo ↔ $idPlataforma")
                            }
                        }
                    }

                    Log.d("TituloInsertado", "Título procesado correctamente: $nombre")
                    insertadoConExito = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Error al procesar el título", Toast.LENGTH_SHORT).show()
            }
        }

        return insertadoConExito
    }

}