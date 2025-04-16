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

class BuscadorFragment : Fragment() {

    // IDs de los checkboxes centralizados para evitar repetición
    private val idsCheckboxes = listOf(
        R.id.checkAccion, R.id.checkAnimacion, R.id.checkAventura, R.id.checkCienciaFiccion,
        R.id.checkComedia, R.id.checkCrimen, R.id.checkDocumental, R.id.checkDrama,
        R.id.checkFamilia, R.id.checkFantasia, R.id.checkGuerra, R.id.checkHistoria,
        R.id.checkMisterio, R.id.checkMusica, R.id.checkNoticias, R.id.checkEntrevistas,
        R.id.checkReality, R.id.checkRomance, R.id.checkThriller, R.id.checkHorror,
        R.id.checkWestern
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_buscador, container, false)

        val expandableLayout = view.findViewById<ExpandableLayout>(R.id.expandableLayoutFiltro)
        val expandableLayouts = listOf(expandableLayout)

        val db = BBDD(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerTitulos)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val listaTitulos = db.listaTitulos()

        val adaptador = AdaptadorBuscador(listaTitulos) { titulo ->
            mostrarInfoTitulo(titulo)
        }

        recyclerView.adapter = adaptador

        view.findViewById<ImageButton>(R.id.FlechaBuscador).setOnClickListener {
            requireActivity().finish()
        }

        for (aux in expandableLayouts) {
            aux.setOnClickListener {
                if (aux.isExpanded) aux.collapse() else aux.expand()
            }
        }

        val mensajeNoResultados = view.findViewById<TextView>(R.id.mensajeNoResultados)
        val editTextBuscador = view.findViewById<EditText>(R.id.buscador)
        val botonBuscar = view.findViewById<ImageView>(R.id.botonBuscador)
        val botonAplicarFiltros = view.findViewById<Button>(R.id.ButtonBuscarFiltro)
        val botonLimpiarFiltros = view.findViewById<Button>(R.id.botonLimpiarFiltros)

        // Buscar mientras se escribe
        editTextBuscador.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aplicarFiltros(view, listaTitulos, adaptador, recyclerView, mensajeNoResultados, s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // Botón aplicar filtros por género
        botonAplicarFiltros.setOnClickListener {
            val textoActual = editTextBuscador.text.toString()
            aplicarFiltros(view, listaTitulos, adaptador, recyclerView, mensajeNoResultados, textoActual)
        }

        // Botón para limpiar filtros
        botonLimpiarFiltros.setOnClickListener {
            editTextBuscador.text.clear()
            desmarcarTodosLosGeneros(view)
            aplicarFiltros(view, listaTitulos, adaptador, recyclerView, mensajeNoResultados, "")
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

    private fun obtenerGenerosSeleccionados(view: View): List<String> {
        return idsCheckboxes.mapNotNull { id ->
            val checkBox = view.findViewById<CheckBox>(id)
            if (checkBox.isChecked) checkBox.text.toString() else null
        }
    }

    private fun desmarcarTodosLosGeneros(view: View) {
        for (id in idsCheckboxes) {
            val checkBox = view.findViewById<CheckBox>(id)
            checkBox.isChecked = false
        }
    }

    private fun aplicarFiltros(
        view: View,
        listaTitulos: List<Titulo>,
        adaptador: AdaptadorBuscador,
        recyclerView: RecyclerView,
        mensajeNoResultados: TextView,
        textoBuscado: String
    ) {
        val query = textoBuscado.trim().lowercase()
        val generosSeleccionados = obtenerGenerosSeleccionados(view)

        val resultados = when {
            query.isNotEmpty() -> {
                listaTitulos.filter { titulo ->
                    val nombre = titulo.nombre.lowercase()
                    val original = titulo.nombreOriginal?.lowercase() ?: ""
                    nombre.contains(query) || original.contains(query)
                }
            }
            generosSeleccionados.isNotEmpty() -> {
                listaTitulos.filter { titulo ->
                    generosSeleccionados.any { genero ->
                        titulo.generos.any { it.nombreGenero == genero }
                    }
                }
            }
            else -> listaTitulos
        }

        if (resultados.isEmpty()) {
            recyclerView.visibility = View.GONE
            val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            mensajeNoResultados.startAnimation(fadeIn)
            mensajeNoResultados.visibility = View.VISIBLE
        } else {
            adaptador.actualizarLista(resultados)
            recyclerView.visibility = View.VISIBLE
            mensajeNoResultados.visibility = View.GONE
        }
    }
}
