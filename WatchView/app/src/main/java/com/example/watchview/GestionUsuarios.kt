package com.example.watchview

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.skydoves.expandablelayout.ExpandableLayout


class GestionUsuarios : Fragment() {

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

        val view = inflater.inflate(R.layout.fragment_gestion_usuarios, container, false)

        val expandableLayout = view.findViewById<ExpandableLayout>(R.id.expandableLayout)
        val expandableLayout2 = view.findViewById<ExpandableLayout>(R.id.expandableLayout2)
        val expandableLayout3 = view.findViewById<ExpandableLayout>(R.id.expandableLayout3)

        // Crear una lista con los ExpandableLayouts
        val expandableLayouts = listOf(expandableLayout, expandableLayout2, expandableLayout3)

        val flechaVolver = view.findViewById<ImageView>(R.id.FlechaVolverUsuarios)

        val botonInsertar= view.findViewById<Button>(R.id.InsertarButtonUsuarios)
        val botonEliminar= view.findViewById<Button>(R.id.EliminarButtonUsuarios)
        val botonModificar= view.findViewById<Button>(R.id.ModificarButtonUsuarios)

        flechaVolver.setOnClickListener{
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_watch_view,MenuAdministrador())
                .commit()
        }


        botonInsertar.setOnClickListener {
            val db = BBDD(requireContext()) // Configurar el botón para insertar usuario
            val nombre = view.findViewById<EditText>(R.id.InsertarNombreUsuario).text.toString()
            val correo = view.findViewById<EditText>(R.id.InsertarCorreoUsuario).text.toString()
            val pass = view.findViewById<EditText>(R.id.InsertarPasswordUsuario).text.toString()

            // Validar el formato del correo
            if (!verificarCorreo(correo)) {
                Toast.makeText(requireContext(), "El correo no tiene un formato válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultado = db.insertarUsuario(correo, nombre, pass, "usuario")
            when (resultado) {
                0 -> Toast.makeText(requireContext(), "Usuario insertado correctamente", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(requireContext(), "Error: Correo ya registrado", Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(requireContext(), "Error: Nombre ya registrado", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(requireContext(), "Error al insertar usuario", Toast.LENGTH_SHORT).show()
            }
        }



        botonEliminar.setOnClickListener { // Configurar el botón para eliminar usuario
            val db = BBDD(requireContext())
            val correo = view.findViewById<EditText>(R.id.EliminarCodigoUsuario).text.toString()

            if (correo.isNotEmpty()) {

                // Validar el formato del correo
                if (!verificarCorreo(correo)) {
                    Toast.makeText(requireContext(), "El correo no tiene un formato válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val eliminado = db.eliminarUsuario(correo)
                if (eliminado) {
                    Toast.makeText(requireContext(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                } else {

                    Toast.makeText(requireContext(), "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, ingrese un correo válido", Toast.LENGTH_SHORT).show()
            }
        }


        // Configurar el botón para modificar usuario
        botonModificar.setOnClickListener {
            val db = BBDD(requireContext())
            val correo = view.findViewById<EditText>(R.id.ModificarCorreoUsuario).text.toString()
            val nombre = view.findViewById<EditText>(R.id.ModificarNombreUsuario).text.toString()
            val pass = view.findViewById<EditText>(R.id.ModificarPasswordUsuario).text.toString()

            if (correo.isNotEmpty()) {

                // Validar el formato del correo
                if (!verificarCorreo(correo)) {
                    Toast.makeText(requireContext(), "El correo no tiene un formato válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val modificado = db.modificarUsuario(correo, nombre, pass)

                if (modificado) {
                    Toast.makeText(requireContext(), "Usuario modificado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No se pudo modificar el usuario", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, ingrese un correo válido", Toast.LENGTH_SHORT).show()
            }
        }

        // Implementa el listener en todos los expandableLayouts y permite que estos se expandan y contraigan

        for(aux in expandableLayouts){
            aux.setOnClickListener {
                if (aux.isExpanded) aux.collapse() else aux.expand()
            }
        }
        return view
    }

    // Método para verificar si un correo tiene el formato adecuado
    fun verificarCorreo(correo: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }
}