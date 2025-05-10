package com.example.watchview

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit


class Inicio : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /*override fun onResume() {
        super.onResume()
        actualizarEstrenosRecycler()
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        val db = BBDD(requireContext())

        // OBTENER TOP 10 SERIES Y PELÍCULAS
        val topSeriesNetflix: List<TopTitulo> = db.obtenerTop10PorTipoNetflix("series")
        val topPeliculasNetflix: List<TopTitulo> = db.obtenerTop10PorTipoNetflix("movie")

        // RECYCLER SERIES
        val recyclerSeries = view.findViewById<RecyclerView>(R.id.recyclerTopSeries)
        recyclerSeries.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerSeries.adapter = AdaptadorInicioTop(topSeriesNetflix) { titulo ->
            mostrarInfoTitulo(titulo)
        }

        // RECYCLER PELÍCULAS
        val recyclerPeliculas = view.findViewById<RecyclerView>(R.id.recyclerTopPeliculas)
        recyclerPeliculas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerPeliculas.adapter = AdaptadorInicioTop(topPeliculasNetflix) { titulo ->
            mostrarInfoTitulo(titulo)
        }
        Log.d("TopSeries", "Cantidad de series: ${topSeriesNetflix.size}")
        Log.d("TopPeliculas", "Cantidad de películas: ${topPeliculasNetflix.size}")

        // RECYCLER ESTRENOS
        val listaEstrenos: List<Titulo> = db.listarTitulosPorPlataformaConEstreno2("netflix")
        val recyclerEstrenos = view.findViewById<RecyclerView>(R.id.recyclerEstrenosNetflix)
        recyclerEstrenos.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerEstrenos.adapter = AdaptadorInicioEstreno(listaEstrenos)

        return view
    }

    private fun mostrarInfoTitulo(titulo: Titulo) {
        val fragment = InformacionTitulo()
        val bundle = Bundle()
        bundle.putParcelable("titulo", titulo)
        fragment.arguments = bundle
        cargarFragment(fragment)
    }

    private fun cargarFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Actualiza la lista de estrenos
    private fun actualizarEstrenosRecycler() {
        val db = BBDD(requireContext())
        val listaEstrenos: List<Titulo> = db.listarTitulosPorPlataformaConEstreno("netflix")
        val recyclerEstrenos = view?.findViewById<RecyclerView>(R.id.recyclerEstrenosNetflix)
        recyclerEstrenos?.adapter = AdaptadorInicioEstreno(listaEstrenos)
    }

}
