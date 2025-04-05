package com.example.watchview

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout


class MenuAdministrador : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_menu_administrador, container, false)

        val flechaVolver:ImageView=view.findViewById(R.id.FlechaAdmin)

        val contenedorfotos: LinearLayout =view.findViewById(R.id.Contenedorfotos)
        val contenedorusuario: LinearLayout =view.findViewById(R.id.Contenedorusuario)

        contenedorfotos.setOnClickListener{
            abrirFragment(GestionFotos())
        }

        contenedorusuario.setOnClickListener{
            abrirFragment(GestionUsuarios())
        }

        flechaVolver.setOnClickListener{
            val intent=Intent(requireContext(),AppActivityAdmin::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun abrirFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_watch_view,fragment)
            .commit()
    }
}