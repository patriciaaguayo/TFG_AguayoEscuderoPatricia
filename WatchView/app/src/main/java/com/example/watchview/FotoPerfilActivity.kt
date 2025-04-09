package com.example.watchview

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment

class FotoPerfilActivity : AppCompatActivity() {

    private lateinit var db: BBDD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_foto_perfil)

        // Ajustar insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerFotos)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val volver: ImageButton = findViewById(R.id.volverButtonFoto)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerFotos)
        db = BBDD(this)

        val listaFotos = db.listaFotos()

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adaptador = AdaptadorFotosPerfil(listaFotos) { fotoSeleccionada ->
            actualizarFotoPerfilUsuario(fotoSeleccionada.idFoto)

            Usuario.fotoPerfil=fotoSeleccionada.nombreFoto
        }

        recyclerView.adapter = adaptador

        volver.setOnClickListener {
            finish()
        }
    }

    private fun actualizarFotoPerfilUsuario(idFoto: Int) {
        val correoUsuario = Usuario.correo // Asegurate de tener esto definido en una clase global o sesión

        val exito = db.actualizarFotoPerfilUsuario(correoUsuario, idFoto)
        if (exito) {
            Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al actualizar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun mostrarInfoFoto(foto: Foto) {
        val fragment=PerfilFragment()
        val bundle=Bundle()
        bundle.putParcelable("foto",foto)
        fragment.arguments=bundle
        fragmentLoader(fragment)
    }

    private fun fragmentLoader(fragment: Fragment) {
        val parentFragmentManager
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }*/
}
