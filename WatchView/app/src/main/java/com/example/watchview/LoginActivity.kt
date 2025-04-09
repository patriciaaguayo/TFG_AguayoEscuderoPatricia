package com.example.watchview

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val user=cargarUsuarioDesdeSesion(this)

        if (user=="usuario") {
            val intent = Intent(this, AppActivity::class.java)
            startActivity(intent)
        }else if(user=="admin"){
            val intent = Intent(this, ActivityWatchView::class.java)
            intent.putExtra("fragmento","MenuAdministrador")
            startActivity(intent)
        }

        enableEdgeToEdge()

        setContentView(R.layout.login_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Traigo los botones del layout
        val registro: TextView = findViewById(R.id.registro)
        val inicio: Button = findViewById(R.id.entrarButton)
        val invitado: Button= findViewById(R.id.invitadoButton)

        // Click listeners
        registro.setOnClickListener {
            abrirActivity(RegisterActivity::class.java)
        }
        invitado.setOnClickListener{
            abrirActivity(AppActivityInvitado::class.java)
        }

        inicio.setOnClickListener{
            iniciarSesion()
        }
    }

    private fun <T : Activity> abrirActivity(clase: Class<T>) {
        val intent = Intent(this, clase)
        startActivity(intent)
    }

    private fun iniciarSesion() {
        var nombre:String? = findViewById<EditText>(R.id.loginEmail).text.toString()
        val pass = findViewById<EditText>(R.id.loginPass).text.toString()
        val bd = BBDD(this)

        nombre = bd.encontrarUsuario(nombre)

        if (nombre != null) {
            val quienEs = bd.verificarUsuario(nombre, pass)

            if (quienEs[0]) { // la contraseña es correcta
                bd.establecerUsuario(nombre)
                bd.guardarUsuarioEnSesion(this)
                fetchGenresFromApi()

                if (quienEs[1]) { // usuario administrador
                    val intent = Intent(this, ActivityWatchView::class.java)
                    intent.putExtra("fragmento", "MenuAdministrador")
                    startActivity(intent)
                } else {

                    // Activar el Worker para actualizar la base de datos
                   /* val workRequest = OneTimeWorkRequestBuilder<ActualizarBBDDWorker>()
                        .build()
                    WorkManager.getInstance(this).enqueue(workRequest)*/
                    abrirActivity(AppActivity::class.java)
                }
            } else {
                Toast.makeText(this, "Contraseña incorrecta...", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_LONG).show()
        }
    }


    fun saveUser(context: Context) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("userId", Usuario.correo)
            putString("userName", Usuario.nombre)
            apply()
        }
    }

    fun cargarUsuarioDesdeSesion(context: Context): String? {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val correo = sharedPref.getString("correo", null)

        return if (correo != null) {
            Usuario.correo = correo
            Usuario.nombre = sharedPref.getString("nombre", null)
            Usuario.pass = sharedPref.getString("pass", null)
            Usuario.fotoPerfil = sharedPref.getString("fotoPerfil", null)
            Usuario.privilegios = sharedPref.getString("privilegios", null)

            Usuario.privilegios
        } else {
            null
        }
    }

    // Métodos para cargar y obtener los géneros de la API

    fun fetchGenresFromApi() {
        val bd = BBDD(this)

        // Verificar si ya hay géneros guardados
        if (bd.hayGenerosGuardados()) {
            Log.d("GeneroInsertado", "Ya existen géneros en la base de datos. No se hace la llamada a la API.")
            return // Salir de la función si ya hay datos
        }

        // Si no hay datos, continuar con la llamada a la API
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val apiKey = getString(R.string.api_key)
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
                    parseAndStoreGenres(jsonResponse)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Error en la solicitud", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun parseAndStoreGenres(jsonResponse: String?) {
        Log.d("GeneroInsertado", "Iniciando la inserción de géneros.") // Log para verificar que la función está siendo llamada
        val bd = BBDD(this)
        try {
            if (jsonResponse != null) {
                Log.d("GeneroInsertado", "Respuesta JSON recibida: $jsonResponse") // Verifica que el JSON no esté vacío
                // Parseamos el JSON y obtenemos el array de géneros
                val genresArray = JSONArray(jsonResponse)

                // Recorremos el array de géneros y almacenamos los datos en la base de datos
                for (i in 0 until genresArray.length()) {
                    val genreObject = genresArray.getJSONObject(i)  // Obtenemos el objeto de género
                    val idGenero = genreObject.getString("id")      // Extraemos el ID del género
                    val nombreGenero = genreObject.getString("name") // Extraemos el nombre del género

                    // Llamamos a un método para insertar el género en la base de datos
                    bd.insertGenero(idGenero, nombreGenero)

                    // Log para confirmar que el género se insertó correctamente
                    Log.d("GeneroInsertado", "Género insertado: ID = $idGenero, Nombre = $nombreGenero")
                }

                // Cambiar al hilo principal para mostrar el Toast
                // Usando conContext(Dispatchers.Main) para cambiar al hilo principal
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@LoginActivity, "Géneros actualizados exitosamente.", Toast.LENGTH_SHORT).show()
                }

                // Log para indicar que se completó la inserción de todos los géneros
                Log.d("GeneroInsertado", "Inserción de géneros completada.")
            } else {
                Log.e("GeneroInsertado", "El JSON recibido es nulo.")
            }
        } catch (e: Exception) {
            e.printStackTrace()

            // Cambiar al hilo principal para mostrar el Toast
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(this@LoginActivity, "Error al procesar los géneros", Toast.LENGTH_SHORT).show()
            }
        }
    }

}