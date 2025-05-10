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
import java.text.SimpleDateFormat
import java.util.Locale

/*
* Extiende a la clase Adapter de RecyclerView
* recibe la lista de titulos a mostrar
* recibe tambien una funcion lambda que se ejecutará al darle click a un titulo
* */

class AdaptadorInicioEstreno (
    titulos: List<Titulo>,
) : RecyclerView.Adapter<AdaptadorInicioEstreno.ViewHolder>() {
    private val listaTitulos = titulos.toMutableList()

    // Clase interna ViewHolder que se encarga de gestionar las vistas individuales de cada elemento
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombreTituloEstreno)
        val sinopsis: TextView = itemView.findViewById(R.id.descripcionTituloEstreno)
        val estreno: TextView = itemView.findViewById(R.id.fechaTituloEstreno)
        val tipo: TextView = itemView.findViewById(R.id.tipoTituloEstreno)
        val plataforma: TextView = itemView.findViewById(R.id.plataformaTituloEstreno)
        val imagen: ImageView = itemView.findViewById(R.id.imagenTituloEstreno)
        val imagenCampana: ImageView = itemView.findViewById(R.id.imagenCampana)  // Agregar la referencia a la imagen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cv_titulo_estreno, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaTitulos[position]

        holder.nombre.text = item.nombre
        holder.sinopsis.text = item.descripcion
        holder.plataforma.text = item.plataformas.firstOrNull()?.nombrePlataforma ?: ""

        // Mostrar la fecha de estreno en la plataforma con id "netflix"
        val estrenoPlataforma = item.estrenos.firstOrNull { it.idPlataforma == "netflix" }
        val fechaOriginal = estrenoPlataforma?.fechaEstreno

        val fechaMostrada = if (!fechaOriginal.isNullOrEmpty()) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val date = inputFormat.parse(fechaOriginal)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                "Fecha inválida"
            }
        } else {
            "Sin fecha"
        }

        holder.estreno.text = fechaMostrada

        holder.tipo.text = if (item.tipo == "series") "Serie" else "Película"

        // Cargar la imagen de póster si existe
        val posterHorizontal = item.posters
            .filter { it.tipo == "horizontal" }
            .lastOrNull() // último de la lista

        if (posterHorizontal != null) {
            Glide.with(holder.imagen.context)
                .load(posterHorizontal.urlPoster)
                .apply(
                    RequestOptions()
                        .centerCrop()
                )
                .thumbnail(
                    Glide.with(holder.imagen.context)
                        .load(posterHorizontal.urlPoster)
                        .apply(
                            RequestOptions()
                                .fitCenter() // También aplicar en el thumbnail
                        )
                )
                .into(holder.imagen)
        } else {
            holder.imagen.setImageResource(R.drawable.trailer)
        }

        // Revisar si el título ya está en la base de datos
        val db = BBDD(holder.itemView.context)
        val usuarioCorreo = Usuario.correo  // Correo del usuario almacenado
        val idEstreno = item.estrenos.firstOrNull { it.idPlataforma == "netflix" }?.idEstreno ?: -1 // El ID del estreno

        // Revisar si el título ya está agregado
        var isAdded = db.usuarioHaGuardadoEstreno(usuarioCorreo, idEstreno)

        // Configurar el ícono dependiendo de si el título está agregado
        if (isAdded) {
            holder.imagenCampana.setImageResource(R.drawable.quitar)
        } else {
            holder.imagenCampana.setImageResource(R.drawable.campana)
        }

        // Acción para agregar o quitar el título de la base de datos
        holder.imagenCampana.setOnClickListener {
            if (isAdded) {
                // Eliminar título de la base de datos y cambiar imagen a campana
                db.eliminarEstrenoGuardado(usuarioCorreo, idEstreno)
                isAdded = false // Cambiar el estado local
                holder.imagenCampana.setImageResource(R.drawable.campana)
            } else {
                // Agregar título a la base de datos y cambiar imagen a quitar
                db.guardarEstrenoUsuario(usuarioCorreo, idEstreno)
                isAdded = true // Cambiar el estado local
                holder.imagenCampana.setImageResource(R.drawable.quitar)
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
