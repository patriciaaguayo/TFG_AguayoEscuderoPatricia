package com.example.watchview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class PasswordActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.password)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val volver: ImageButton = findViewById(R.id.volverPassButton)
        val registro: Button = findViewById(R.id.passwordButton)

        volver.setOnClickListener {
            finish()
        }

        registro.setOnClickListener {
            val pass = findViewById<EditText>(R.id.passwordContrasenia).text.toString()
            val repPass = findViewById<EditText>(R.id.passwordRepetirContrasenia).text.toString()

            if (!validarContrasenas(this, pass, repPass)) return@setOnClickListener

            val bbdd=BBDD(this);

            if(bbdd.actualizarContrasena(this, Usuario.correo, pass)){
                Toast.makeText(this,"Contraseña actualizada con éxito",Toast.LENGTH_LONG).show()
            }
        }
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