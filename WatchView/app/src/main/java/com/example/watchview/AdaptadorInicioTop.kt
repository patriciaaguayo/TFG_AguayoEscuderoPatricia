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

class AdaptadorInicioTop(
    private val listaTop: List<TopTitulo>,
    private val onItemClick: (Titulo) -> Unit
) : RecyclerView.Adapter<AdaptadorInicioTop.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombreTituloTop)
        val imagen: ImageView = itemView.findViewById(R.id.imagenTituloTop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cv_titulo_top, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topItem = listaTop[position]
        val titulo = topItem.titulo

        holder.nombre.text = "${topItem.posicion}. ${titulo.nombre}"

        val posterVertical = titulo.posters.firstOrNull { it.tipo == "vertical" }

        Glide.with(holder.imagen.context)
            .load(posterVertical?.urlPoster)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(20)))
            .into(holder.imagen)

        holder.itemView.setOnClickListener {
            onItemClick(titulo)
        }
    }

    override fun getItemCount(): Int = listaTop.size
}
