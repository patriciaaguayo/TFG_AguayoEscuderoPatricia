package com.example.watchview

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class AppActivityInvitado : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app_invitado)

        //Comentar para trabajar en local

        if(!BBDD(this).hayTitulosGuardados()){
            ApiDataLoader.guardarTitulosNetflix2(this)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.app)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // se carga el fragment de inicio al empezar la actividad
        cargarFragment(savedInstanceState,Inicio())

        // declaracion de los botones

        val botonInicio: ImageButton =findViewById(R.id.botonInicioInvitado)
        val botonLista: ImageButton =findViewById(R.id.botonListaInvitado)
        val botonAjustes: ImageButton =findViewById(R.id.botonAjustesInvitado)
        val botonBuscador: ImageButton =findViewById(R.id.botonBuscadorInvitado)

        // click listeners para viajar entre fragmentos

        botonInicio.setOnClickListener{
            cargarFragment(savedInstanceState,Inicio())
        }

        botonLista.setOnClickListener{
            Toast.makeText(this,"Esta opción no está disponible en modo invitado...",Toast.LENGTH_SHORT).show()
        }

        botonAjustes.setOnClickListener{
            Toast.makeText(this,"Esta opción no está disponible en modo invitado...",Toast.LENGTH_SHORT).show()
        }

        botonBuscador.setOnClickListener{
            Toast.makeText(this,"Esta opción no está disponible en modo invitado...",Toast.LENGTH_SHORT).show()
        }

    }

    // metodo para mostrar fragments

    private fun cargarFragment(savedInstanceState: Bundle?, fragment: Fragment) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}