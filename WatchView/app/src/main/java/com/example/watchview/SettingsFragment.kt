package com.example.watchview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class SettingsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_settings, container, false)
        val fotoUsuario: ImageView = view.findViewById(R.id.fotoUsuario)
        val flecha:ImageView = view.findViewById(R.id.Flecha)
        val contenedor: LinearLayout = view.findViewById(R.id.ContenedorEditarPerfil)

        val fotoString= resources.getIdentifier(Usuario.fotoPerfil, "drawable", requireContext().packageName)

        Glide.with(requireContext())
            .load(if (fotoString != 0) fotoString else R.drawable.perfil1)
            .transform(CenterCrop(), RoundedCorners(100))
            .into(fotoUsuario)

        flecha.setOnClickListener(){
            requireActivity().finish()
        }

        contenedor.setOnClickListener(){
            cargarFragment(PerfilFragment());
        }

        return view
    }

    private fun cargarFragment(fragment:Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_watch_view, fragment)
            .addToBackStack(null)
            .commit()
    }
}