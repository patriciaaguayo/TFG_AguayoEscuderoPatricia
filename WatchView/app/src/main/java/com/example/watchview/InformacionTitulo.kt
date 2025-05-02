package com.example.watchview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


lateinit var titulo: Titulo

class InformacionTitulo : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titulo = it.getParcelable("titulo") ?: throw IllegalArgumentException("Titulo no encontrado")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_informacion_titulo, container, false)
        val db=BBDD(requireContext())
        // Referencias a los elementos de la vista

        val imagenTitulo: ImageView = view.findViewById(R.id.imageTitulo)
        val nombreTitulo: TextView = view.findViewById(R.id.nombreTitulo)
        val nombreOriginal: TextView = view.findViewById(R.id.nomnbreOriginal)
        val descripcionTitulo: TextView = view.findViewById(R.id.sinopsisTitulo)
        val tipoTitulo: TextView = view.findViewById(R.id.tipo)
        val generosTitulo: TextView = view.findViewById(R.id.generosTitulo)
        val yearTitulo: TextView = view.findViewById(R.id.fechaInicio)
        val temporadasTitulo: TextView = view.findViewById(R.id.temporadas)
        val plataformaTitulo: TextView = view.findViewById(R.id.plataforma)
        val ratingTitulo: TextView = view.findViewById(R.id.rating)

        // Filtra los pósters de tipo "horizontal" y toma el último (mayor calidad)
        val posterHorizontal = titulo.posters
            .filter { it.tipo == "horizontal" }
            .lastOrNull()

        if (posterHorizontal != null) {
            Glide.with(imagenTitulo.context)
                .load(posterHorizontal.urlPoster)
                .apply(
                    RequestOptions()
                        .transform(CenterCrop())
                )
                .into(imagenTitulo)
        } else {
            imagenTitulo.setImageResource(R.drawable.fondo_register)
        }

        nombreTitulo.text=titulo.nombre
        nombreOriginal.text= titulo.nombreOriginal
        descripcionTitulo.text=titulo.descripcion

        if (titulo.tipo == "series"){
            tipoTitulo.text="Serie"
        }else{
            tipoTitulo.text="Película"
        }

        if (titulo.temporadas != null && titulo.tipo == "series") {
            temporadasTitulo.text = titulo.temporadas.toString()
        } else {
            // Ocultar ambos TextViews
            temporadasTitulo.visibility = View.GONE
            val tituloTemporadas: TextView = view.findViewById(R.id.tituloTemporadas)
            tituloTemporadas.visibility = View.GONE

            // Si usas un LinearLayout o RelativeLayout, puedes cambiar el tamaño del espacio
            val layoutParams = tituloTemporadas.layoutParams
            layoutParams.height = 0 // Esto hace que no ocupe espacio vertical
            tituloTemporadas.layoutParams = layoutParams
        }


        ratingTitulo.text=titulo.rating.toString()
        generosTitulo.text = titulo.generos.joinToString(", ") { it.nombreGenero }
        plataformaTitulo.text = titulo.plataformas.joinToString(", ") { it.nombrePlataforma }

        val inicio = titulo.fechaInicio
        val fin = titulo.fechaFin

        if (titulo.fechaFin != null) {
            if (inicio == fin){
                yearTitulo.text = inicio
            }else{
                val texto = inicio + " - " + fin
                yearTitulo.text = texto
            }

        }else{
            yearTitulo.text = titulo.fechaInicio
        }

        view.findViewById<ImageButton>(R.id.volverButton).setOnClickListener {
            // Regresar al fragmento anterior en la pila de retroceso
            parentFragmentManager.popBackStack()
        }

        return view


    }
}