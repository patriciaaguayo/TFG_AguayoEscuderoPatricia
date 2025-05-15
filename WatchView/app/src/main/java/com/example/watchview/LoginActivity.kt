package com.example.watchview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Solicitar permiso de notificación
        solicitarPermisoNotificaciones()

        // Programar el Worker una sola vez al iniciar la app
        programarWorkerDeEstrenos(applicationContext)

        // Worker para actulizar la lista de estrenos
        iniciarWorkerActualizacion()

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

        lifecycleScope.launch {
            val listaSeries = listOf(
                "El descubrimiento de las brujas",
                "Black Mirror",
                "Sé quién eres",
                "Raw"
            )

            val listaPeliculas = listOf(
                "¡Vaya vacaciones!",
                "¡Rehén",
                "Siete días y una vida",
                "Sin malos rollos",
                "Sniper: E.I.R.G. – Equipo de inteligencia y respuesta global",
                "La pasión de Cristo",
                "Los dos Papa",
                "Sisu"
            )

            val db = BBDD(this@LoginActivity)

            if (!db.hayGenerosGuardados()) {
                ApiDataLoader.fetchGenresFromApi(this@LoginActivity)
            }

            //db.insertGeneros() // si se quiere trabajar en locar quitar de comentarios y comentar el if de arriba

            // Aquí se queda esperando a que terminen antes de seguir
            ApiDataLoader.guardarTitulosPorNombreSeries(this@LoginActivity, listaSeries) // Comentar para trabajar en local
            ApiDataLoader.guardarTitulosPorNombrePeliculas(this@LoginActivity, listaPeliculas) // Comentar para trabajar en local

            // Ahora ya han terminado, puedes insertar

            db.insertAllTop10Data()
            db.insertAllEstrenosData()
            db.insertUsuarioEstreno(3, "paco@gmail.com")

            Toast.makeText(this@LoginActivity, "Todo cargado correctamente", Toast.LENGTH_SHORT).show()
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

                // Cuando inicia el usuario se comprueba si algunos de los títulos se ha estrenado para guardarse en su lista
                bd.actualizarTitulosVistosPorUsuario(Usuario.correo)

                if (quienEs[1]) { // usuario administrador
                    val intent = Intent(this, ActivityWatchView::class.java)
                    intent.putExtra("fragmento", "MenuAdministrador")
                    startActivity(intent)
                } else {
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

    private fun programarWorkerDeEstrenos(context: Context) {
        val initialDelay = calcularDelayHastaProxima12PM()

        val request = PeriodicWorkRequestBuilder<RevisarEstrenosWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "RevisarEstrenosDiario",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun calcularDelayHastaProxima12PM(): Long {
        val ahora = Calendar.getInstance()
        val proxima3AM = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(ahora)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        return proxima3AM.timeInMillis - ahora.timeInMillis
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permiso = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permiso), 1001)
            }
        }
    }

    // Worker para actualizar la lista de estrenos

    private fun iniciarWorkerActualizacion() {
        val workRequest = PeriodicWorkRequestBuilder<EstrenosWorker>(24, TimeUnit.HOURS)
            .addTag("actualizar_estrenos")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "actualizar_estrenos",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

}