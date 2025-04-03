package com.example.watchview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class ActivityWatchView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_watch_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        /*
        * La variable "fragment" recogerá un valor extra proporcionado el cual corresponderá
        * con el nombre del fragmento a mostrar
        * */
        val fragment= intent.getStringExtra("fragmento")

        if(fragment=="MenuAdministrador") cargarFragment(savedInstanceState, MenuAdministrador())
        if(fragment=="SettingsFragment") cargarFragment(savedInstanceState,SettingsFragment())
        if(fragment=="ListaFragment") cargarFragment(savedInstanceState,ListaFragment())
        if(fragment=="BuscadorFragment") cargarFragment(savedInstanceState,BuscadorFragment())
    }

    private fun cargarFragment(savedInstanceState: Bundle?, fragment: Fragment) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_watch_view, fragment)
                .commit()
        }
    }

}