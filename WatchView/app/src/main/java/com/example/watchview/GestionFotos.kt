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

        val expandableLayout = view.findViewById<ExpandableLayout>(R.id.expandableLayout)
        val expandableLayout2 = view.findViewById<ExpandableLayout>(R.id.expandableLayout2)

        // Crear una lista con los ExpandableLayouts
        val expandableLayouts = listOf(expandableLayout, expandableLayout2)

        val flechaVolver = view.findViewById<ImageView>(R.id.FlechaVolverFotos)
        val botonInsertar = view.findViewById<Button>(R.id.InsertarButtonFotos)
        val botonEliminar = view.findViewById<Button>(R.id.EliminarButtonFotos)
        val botonSeleccionarImagen = view.findViewById<Button>(R.id.botonSeleccionarImagen)
        val imagenPreview = view.findViewById<ImageView>(R.id.previewImagen)

        // Seleccionar imagen del dispositivo
        botonSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Volver al menú administrador
        flechaVolver.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_watch_view, MenuAdministrador())
                .commit()
        }

        // Insertar foto en la base de datos
        botonInsertar.setOnClickListener {
            if (selectedImageUri != null) {
                val nombreFoto = view.findViewById<EditText>(R.id.InsertarNombreFoto).text.toString()

                // Verificar si el nombre de la foto ya existe en la base de datos
                val db = BBDD(requireContext())
                if (db.existeNombreFoto(nombreFoto)) {
                    Toast.makeText(requireContext(), "Ese nombre de foto ya existe", Toast.LENGTH_SHORT).show()
                } else {
                    // Insertar la foto en la base de datos (usando la URI de la imagen)
                    val resultado = db.insertarFoto(nombreFoto, selectedImageUri!!)
                    if (resultado) {
                        Toast.makeText(requireContext(), "Foto insertada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al insertar la foto", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, selecciona una foto", Toast.LENGTH_SHORT).show()
            }
        }

        botonEliminar.setOnClickListener { // Configurar el botón para eliminar usuario
            val db = BBDD(requireContext())
            val codigo = view.findViewById<EditText>(R.id.EliminarCodigoFoto).text.toString()

            if (codigo.isNotEmpty()) {
                val eliminado = db.eliminarFoto(codigo)
                if (eliminado) {
                    Toast.makeText(requireContext(), "Foto eliminada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No se encontró la foto", Toast.LENGTH_SHORT).show()
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

    // Manejar el resultado de la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            // Obtener la URI de la imagen seleccionada
            selectedImageUri = data?.data

            // Mostrar la imagen en el ImageView
            val imagenPreview = view?.findViewById<ImageView>(R.id.previewImagen)
            imagenPreview?.setImageURI(selectedImageUri)  // Muestra la imagen seleccionada en el ImageView
        }
    }
}
