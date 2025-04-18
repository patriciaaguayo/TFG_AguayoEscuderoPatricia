package com.example.watchview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val volver: ImageButton = findViewById(R.id.volverButton)
        val registro: Button = findViewById(R.id.registerButton)

        volver.setOnClickListener {
            val intent= Intent(
                this@RegisterActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
        }

        registro.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.registerNombre).text.toString()
            val email = findViewById<EditText>(R.id.registerEmail).text.toString()
            val pass = findViewById<EditText>(R.id.registerContrasenia).text.toString()
            val repPass = findViewById<EditText>(R.id.registerRepetirContrasenia).text.toString()

            if (!verificarCorreo(email)) {
                Toast.makeText(this, "El correo no tiene un formato válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bbdd=BBDD(this);

            val usuarioExistente = bbdd.encontrarUsuario(nombre)
            val correoExistente = bbdd.encontrarUsuario(email)

            if (usuarioExistente != null || correoExistente != null) {
                Toast.makeText(this, "El nombre de usuario o el correo ya están registrados.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!empiezaConMayuscula(nombre)) {
                Toast.makeText(this, "El nombre de usuario debe empezar por mayúscula", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!validarContrasenas(this, pass, repPass)) return@setOnClickListener

            when (bbdd.insertarUsuario(email, nombre, pass, "usuario")) {
                0 ->Toast.makeText(this, "Usuario registrado con éxito.", Toast.LENGTH_SHORT).show()
                1 ->Toast.makeText(this, "Este correo ya está registrado.", Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(this, "Este usuario ya está registrado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para verificar si un correo tiene el formato adecuado

    fun verificarCorreo(correo: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    // Método para verificar si un texto empieza por mayúscula

    fun empiezaConMayuscula(texto: String): Boolean {
        return texto.isNotEmpty() && texto.first().isUpperCase()
    }

    // Método para validar la contraseña

    fun validarContrasenas(context: Context, pass: String, repPass: String): Boolean {
        if (pass != repPass) {
            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        if (pass.length < 6) {
            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!pass.first().isUpperCase()) {
            Toast.makeText(context, "La contraseña debe empezar por mayúscula", Toast.LENGTH_SHORT).show()
            return false
        }

        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{6,}$")
        if (!regex.containsMatchIn(pass)) {
            Toast.makeText(context, "La contraseña debe contener al menos una minúscula, una mayúscula, un número y un carácter especial", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}