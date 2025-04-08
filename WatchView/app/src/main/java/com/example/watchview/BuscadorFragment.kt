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
import com.skydoves.expandablelayout.ExpandableLayout

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

        val expandableLayout = view.findViewById<ExpandableLayout>(R.id.expandableLayoutFiltro)

        // Crear una lista con los ExpandableLayouts
        val expandableLayouts = listOf(expandableLayout)

        val db=BBDD(requireContext())
        //val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        //recyclerView.layoutManager = LinearLayoutManager(context)

        //val listaVelas = db.listaVelas()

        /*val adaptador = Adaptador(listaVelas){
                vela->mostrarInfoVela(vela)
        }

        recyclerView.adapter = adaptador*/

        view.findViewById<ImageButton>(R.id.FlechaBuscador).setOnClickListener {
            requireActivity().finish()
        }

        // Implementa el listener en todos los expandableLayouts y permite que estos se expandan y contraigan

        for(aux in expandableLayouts){
            aux.setOnClickListener {
                if (aux.isExpanded) aux.collapse() else aux.expand()
            }
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