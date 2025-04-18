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

class AdaptadorBuscador(
    titulos: List<Titulo>,
    private val onItemClick: (Titulo) -> Unit
) : RecyclerView.Adapter<AdaptadorBuscador.ViewHolder>() {
    private val listaTitulos = titulos.toMutableList()

    // Clase interna ViewHolder que se encarga de gestionar las vistas individuales de cada elemento
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombreTitulo)
        val year: TextView = itemView.findViewById(R.id.yearTitulo)
        val tipo: TextView = itemView.findViewById(R.id.tipoTitulo)
        val imagen: ImageView = itemView.findViewById(R.id.imagenTitulo)
        val imagenAdd: ImageView = itemView.findViewById(R.id.imagenAdd)  // Agregar la referencia a la imagen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cv_titulo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaTitulos[position]
        holder.nombre.text = item.nombre
        holder.year.text = item.fechaInicio
        holder.tipo.text = if (item.tipo == "series") "Serie" else "Película"

        // Cargar la imagen de póster si existe
        val posterVertical = item.posters
            .filter { it.tipo == "vertical" }
            .first() // primero de la lista

        if (posterVertical != null) {
            Glide.with(holder.imagen.context)
                .load(posterVertical.urlPoster)
                .apply(RequestOptions().centerCrop()) // caso normal: se recorta
                .thumbnail(
                    Glide.with(holder.imagen.context)
                        .load(posterVertical.urlPoster)
                        .apply(RequestOptions().fitCenter()) // fallback visual
                )
                .into(holder.imagen)

        } else {
            holder.imagen.setImageResource(R.drawable.miercoles) // Default image if no poster
        }

        // Acción al hacer clic en el título
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        // Revisar si el título ya está en la base de datos
        val db = BBDD(holder.itemView.context)
        val usuarioCorreo = Usuario.correo  // Asume que tienes el correo del usuario almacenado
        val idTitulo = item.idTitulo  // El ID del título

        // Revisar si el título ya está agregado
        var isAdded = db.verificarTituloEnLista(usuarioCorreo, idTitulo)

        // Configurar el ícono dependiendo de si el título está agregado
        if (isAdded) {
            holder.imagenAdd.setImageResource(R.drawable.quitar)
        } else {
            holder.imagenAdd.setImageResource(R.drawable.agregar)
        }

        // Acción para agregar o quitar el título de la base de datos
        holder.imagenAdd.setOnClickListener {
            if (isAdded) {
                // Eliminar título de la base de datos y cambiar imagen a agregar
                db.eliminarTituloDeLista(usuarioCorreo, idTitulo)
                isAdded = false // Cambiar el estado local
                holder.imagenAdd.setImageResource(R.drawable.agregar)
            } else {
                // Agregar título a la base de datos y cambiar imagen a quitar
                db.agregarTituloALista(usuarioCorreo, idTitulo)
                isAdded = true // Cambiar el estado local
                holder.imagenAdd.setImageResource(R.drawable.quitar)
            }

            // Notificar que el estado del item ha cambiado
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return listaTitulos.size
    }

    fun actualizarLista(nuevaLista: List<Titulo>) {
        (listaTitulos as MutableList<Titulo>).clear()
        listaTitulos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
