package com.example.watchview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BuscadorFragment : Fragment() {
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

        val view = inflater.inflate(R.layout.fragment_buscador, container, false)

        val db=BBDD(requireContext())
        //val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        //recyclerView.layoutManager = LinearLayoutManager(context)

        //val listaVelas = db.listaVelas()

        /*val adaptador = Adaptador(listaVelas){
                vela->mostrarInfoVela(vela)
        }

        recyclerView.adapter = adaptador*/

        view.findViewById<ImageButton>(R.id.botonBuscador).setOnClickListener {
            // Regresar al fragmento anterior en la pila de retroceso
            parentFragmentManager.popBackStack()
        }

        return view
    }

    // Método para mostrar la información del curso

    /*private fun mostrarInfoVela(vela:Vela) {
        val fragment=InformacionVela()
        val bundle=Bundle()
        bundle.putParcelable("vela",vela)
        fragment.arguments=bundle
        fragmentLoader(fragment)
    }

    private fun fragmentLoader(fragment:Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }*/
}