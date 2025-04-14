package com.example.watchview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.skydoves.expandablelayout.ExpandableLayout

class GestionFotos : Fragment() {

    // Constante para seleccionar la imagen
    private val PICK_IMAGE_REQUEST = 1001
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gestion_fotos, container, false)

        val expandableLayout = view.findViewById<ExpandableLayout>(R.id.expandableLayoutEliminarFoto)

        // Crear una lista con los ExpandableLayouts
        val expandableLayouts = listOf(expandableLayout)

        val flechaVolver = view.findViewById<ImageView>(R.id.FlechaVolverFotos)
        val botonEliminar = view.findViewById<Button>(R.id.EliminarButtonFotos)

        // Volver al menú administrador
        flechaVolver.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_watch_view, MenuAdministrador())
                .commit()
        }

        botonEliminar.setOnClickListener {
            val db = BBDD(requireContext())
            val codigoTexto = view.findViewById<EditText>(R.id.EliminarCodigoFoto).text.toString()

            if (codigoTexto.isNotEmpty()) {
                val id = codigoTexto.toIntOrNull()

                if (id != null) {
                    if (db.contarFotos() > 1) {
                        val listaUsuarios = db.obtenerUsuariosConFoto(id)

                        // Reasignar foto a usuarios si es necesario
                        db.reasignarFotoAUsuarios(id, listaUsuarios)

                        // Intentar eliminar la foto
                        val eliminado = db.eliminarFoto(id.toString())

                        if (eliminado) {
                            Toast.makeText(
                                requireContext(),
                                "Foto eliminada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No se encontró la foto",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No se puede eliminar la única foto disponible",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Código inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, ingrese un código válido", Toast.LENGTH_SHORT).show()
            }
        }


        // Implementa el listener en todos los expandableLayouts y permite que estos se expandan y contraigan

        for (aux in expandableLayouts) {
            aux.setOnClickListener {
                if (aux.isExpanded) aux.collapse() else aux.expand()
            }
        }

        return view
    }

}
