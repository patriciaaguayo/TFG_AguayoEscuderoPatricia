package com.example.watchview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/*
* Extiende a la clase Adapter de RecyclerView
* recibe la lista de titulos a mostrar
* recibe tambien una funcion lambda que se ejecutará al darle click a un titulo
* */

class AdaptadorBuscador (
    private val listaTitulos: List<Titulo>,
    private val onItemClick: (Titulo) -> Unit
            ): RecyclerView.Adapter<AdaptadorBuscador.ViewHolder>() {

    /*
     * Clase interna ViewHolder que se encarga de gestionar las vistas individuales
     * de cada elemento del RecyclerView.
     * */

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombreTitulo)
        val year: TextView = itemView.findViewById(R.id.yearTitulo)
        val tipo: TextView = itemView.findViewById(R.id.tipoTitulo)
        val imagen: ImageView = itemView.findViewById(R.id.imagenTitulo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cv_titulo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaTitulos[position]
        holder.nombre.text = item.nombre
        holder.year.text = item.fechaInicio
        holder.tipo.text = item.tipo

        // Filtra los pósters de tipo "vertical" y toma el último (mayor calidad)
        val posterVertical = item.posters
            .filter { it.tipo == "vertical" }
            .lastOrNull()  // último de la lista

        if (posterVertical != null) {
            Glide.with(holder.imagen.context)
                .load(posterVertical.urlPoster)
                .apply(
                    RequestOptions()
                        .transform(CenterCrop())
                )
                .into(holder.imagen)
        } else {
            holder.imagen.setImageResource(R.drawable.miercoles) // Por si no hay póster
        }


        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return listaTitulos.size
    }

}