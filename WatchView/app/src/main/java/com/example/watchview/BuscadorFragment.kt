package com.example.watchview

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.expandablelayout.ExpandableLayout

class BuscadorFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_buscador, container, false)

        val expandableLayout = view.findViewById<ExpandableLayout>(R.id.expandableLayoutFiltro)

        // Crear una lista con los ExpandableLayouts
        val expandableLayouts = listOf(expandableLayout)

        val db=BBDD(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerTitulos)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val listaTitulos = db.listaTitulos()

        val adaptador = AdaptadorBuscador(listaTitulos){
                titulo->mostrarInfoTitulo(titulo)
        }

        recyclerView.adapter = adaptador

        view.findViewById<ImageButton>(R.id.FlechaBuscador).setOnClickListener {
            requireActivity().finish()
        }

        // Implementa el listener en todos los expandableLayouts y permite que estos se expandan y contraigan

        for(aux in expandableLayouts){
            aux.setOnClickListener {
                if (aux.isExpanded) aux.collapse() else aux.expand()
            }
        }

        val mensajeNoResultados = view.findViewById<TextView>(R.id.mensajeNoResultados)

        val editTextBuscador = view.findViewById<android.widget.EditText>(R.id.buscador)
        val botonBuscar = view.findViewById<android.widget.ImageView>(R.id.botonBuscador)

        editTextBuscador.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()

                if (query.isEmpty()) {
                    adaptador.actualizarLista(listaTitulos)
                    recyclerView.visibility = View.VISIBLE
                    mensajeNoResultados.visibility = View.GONE
                } else {
                    val resultadosFiltrados = listaTitulos.filter { titulo ->
                        val nombre = titulo.nombre.lowercase()
                        val original = titulo.nombreOriginal?.lowercase() ?: ""
                        nombre.contains(query) || original.contains(query)
                    }

                    if (resultadosFiltrados.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        mensajeNoResultados.visibility = View.VISIBLE
                    } else {
                        adaptador.actualizarLista(resultadosFiltrados)
                        recyclerView.visibility = View.VISIBLE
                        mensajeNoResultados.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })



        return view
    }

    // Método para mostrar la información del título

    private fun mostrarInfoTitulo(titulo: Titulo) {
        val fragment=InformacionTitulo()
        val bundle=Bundle()
        bundle.putParcelable("titulo",titulo)
        fragment.arguments=bundle
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

    private fun fragmentLoader2(fragment:Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}