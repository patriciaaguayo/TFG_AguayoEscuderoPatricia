package com.example.watchview

import android.app.Activity
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


}