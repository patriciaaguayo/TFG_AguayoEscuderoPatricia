package com.example.watchview

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

            if(pass != repPass){
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar el formato del correo
            if (!verificarCorreo(email)) {
                Toast.makeText(this, "El correo no tiene un formato válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val bbdd=BBDD(this);

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
}