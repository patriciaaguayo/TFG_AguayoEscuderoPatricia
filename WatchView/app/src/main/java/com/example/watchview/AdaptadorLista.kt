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

class AdaptadorLista(
    titulos: List<Titulo>,
    private val onItemClick: (Titulo) -> Unit,
    private val onListaVacia: () -> Unit,
    private val onListaNoVacia: () -> Unit
) : RecyclerView.Adapter<AdaptadorLista.ViewHolder>() {

    private val listaTitulos = titulos.toMutableList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombreTituloLista)
        val year: TextView = itemView.findViewById(R.id.yearTituloLista)
        val tipo: TextView = itemView.findViewById(R.id.tipoTituloLista)
        val imagen: ImageView = itemView.findViewById(R.id.imagenTituloLista)
        val imagenQuitar: ImageView = itemView.findViewById(R.id.imagenQuitarLista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cv_lista, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaTitulos[position]
        holder.nombre.text = item.nombre
        holder.year.text = item.fechaInicio
        holder.tipo.text = if (item.tipo == "series") "Serie" else "Película"

        // Cargar imagen de póster

        // Cargar la imagen de póster si existe
        val posterVertical = item.posters
            .filter { it.tipo == "vertical" }
            .first() // primero de la lista

        if (posterVertical != null) {
            Glide.with(holder.imagen.context)
                .load(posterVertical.urlPoster)
                .apply(RequestOptions().transform(CenterCrop()))
                .into(holder.imagen)
        } else {
            holder.imagen.setImageResource(R.drawable.miercoles) // Default image if no poster
        }

        // Acción al hacer clic en el título
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        // Acción al hacer clic en el ícono de "quitar"
        holder.imagenQuitar.setOnClickListener {
            val db = BBDD(holder.itemView.context)
            db.eliminarTituloDeLista(Usuario.correo, item.idTitulo)

            // Asegúrate de que la posición sea válida antes de quitar
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < listaTitulos.size) {
                listaTitulos.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }

            // Verificar si la lista quedó vacía
            if (listaTitulos.isEmpty()) {
                onListaVacia()
            }
        }
    }

    override fun getItemCount(): Int {
        return listaTitulos.size
    }

    fun actualizarLista(nuevaLista: List<Titulo>) {
        listaTitulos.clear()
        listaTitulos.addAll(nuevaLista)
        notifyDataSetChanged()

        // Si la lista no está vacía, invocar el callback onListaNoVacia
        if (listaTitulos.isNotEmpty()) {
            onListaNoVacia()
        }
    }

}

