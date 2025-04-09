package com.example.watchview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PerfilFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_perfil, container, false)
        val fotoPerfil: ImageView = view.findViewById(R.id.fotoPerfil)
        val flechaVolver: ImageView = view.findViewById(R.id.FlechaVolverPerfil)
        val nombreUsuario: TextView = view.findViewById(R.id.nombreUsuario)
        val contenedorCambiarFoto: LinearLayout = view.findViewById(R.id.ContenedorCambiarFoto)
        val contenedorCambiarPass: LinearLayout = view.findViewById(R.id.ContenedorCambiarPass)
        val cerrarSesionbutton: Button = view.findViewById(R.id.cerrarSesionbutton)

        val db=BBDD(requireContext())

        cerrarSesionbutton.setOnClickListener(){
            db.cerrarSesion(requireContext(),requireActivity())
        }

        contenedorCambiarPass.setOnClickListener(){
            val intent = Intent(requireContext(), PasswordActivity::class.java)
            startActivity(intent)
        }

        contenedorCambiarFoto.setOnClickListener(){
            val intent = Intent(requireContext(), FotoPerfilActivity::class.java)
            startActivity(intent)
        }

        flechaVolver.setOnClickListener(){
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val fotoString= resources.getIdentifier(Usuario.fotoPerfil, "drawable", requireContext().packageName)

        Glide.with(requireContext())
            .load(if (fotoString != 0) fotoString else R.drawable.perfil1) // por si no se encuentra
            .transform(CenterCrop(), RoundedCorners(200))
            .into(fotoPerfil)

        nombreUsuario.setText(if(Usuario.nombre.isNullOrBlank()) "Desconocido" else Usuario.nombre)
        return view
    }

    private fun cargarFragment(fragment:Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_watch_view, fragment)
            .addToBackStack(null)
            .commit()
    }
}