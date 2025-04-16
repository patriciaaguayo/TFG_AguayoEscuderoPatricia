package com.example.watchview

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.expandablelayout.ExpandableLayout
import android.view.animation.AnimationUtils
import android.text.Editable
import android.text.TextWatcher

class ListaFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_lista, container, false)

        val db = BBDD(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerTitulosLista)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Obtener los títulos guardados de la base de datos
        val listaTitulos = db.obtenerTitulosGuardados(Usuario.correo)

        val mensajeNoResultados = view.findViewById<TextView>(R.id.mensajeNoTitulos)

        val adaptador = AdaptadorLista(
            listaTitulos,
            onItemClick = { titulo -> mostrarInfoTitulo(titulo) },
            onListaVacia = {
                mensajeNoResultados.visibility = View.VISIBLE
            },
            onListaNoVacia = {
                mensajeNoResultados.visibility = View.GONE
            }
        )


        recyclerView.adapter = adaptador

        // Si no hay títulos guardados, mostrar el mensaje
        if (listaTitulos.isEmpty()) {
            mensajeNoResultados.visibility = View.VISIBLE
        } else {
            mensajeNoResultados.visibility = View.GONE
        }

        // Acción para cerrar el fragmento
        view.findViewById<ImageButton>(R.id.FlechaLista).setOnClickListener {
            requireActivity().finish()
        }

        return view
    }

    private fun mostrarInfoTitulo(titulo: Titulo) {
        val fragment = InformacionTitulo()
        val bundle = Bundle()
        bundle.putParcelable("titulo", titulo)
        fragment.arguments = bundle
        fragmentLoader(fragment)
    }

    private fun fragmentLoader(fragment: Fragment) {
        val activity = requireActivity()

        if (activity.findViewById<View>(R.id.fragment_container_watch_view) != null) {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_watch_view, fragment)
                .addToBackStack(null)
                .commit()
        } else {
            Log.e("FragmentLoader", "No se encontró fragment_container en esta actividad")
        }
    }
}
