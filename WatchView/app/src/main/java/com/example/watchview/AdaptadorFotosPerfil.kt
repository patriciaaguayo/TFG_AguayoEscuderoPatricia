package com.example.watchview

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AdaptadorFotosPerfil(
    private val listaFotos: List<Foto>,
    private val onItemClick: (Foto) -> Unit
) : RecyclerView.Adapter<AdaptadorFotosPerfil.ViewHolder>() {

    // ViewHolder para manejar cada celda de la lista
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenPerfil: ImageView = itemView.findViewById(R.id.imagenPerfil) // Imagen donde se mostrará la foto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_foto_perfil, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaFotos[position]

        // Usamos Glide para cargar la imagen desde el ID del recurso (idFoto)
        Glide.with(holder.imagenPerfil.context)
            .load(holder.imagenPerfil.context.resources.getIdentifier(item.nombreFoto, "drawable", holder.imagenPerfil.context.packageName))
            .transform(CenterCrop(), RoundedCorners(100))  // Recorte y bordes redondeados
            .into(holder.imagenPerfil)


        // Configuramos el listener para el click sobre la foto
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return listaFotos.size
    }
}
