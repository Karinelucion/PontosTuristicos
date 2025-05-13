package br.edu.utpr.pontoturistico.screens

import DatabaseHandler
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import br.edu.utpr.pontoturistico.adapter.PontoTuristicoAdapter
import br.edu.utpr.pontoturistico.R

class ListarPontosTuristicos : AppCompatActivity()  {
    private lateinit var lvPontosTuristicos : ListView

    private lateinit var banco : DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar)

        lvPontosTuristicos = findViewById( R.id.lvPontosTuristicos)

        banco = DatabaseHandler( this )


    }

    override fun onStart() {
        super.onStart()
        val registros = banco.listCursor()
        val adapter = PontoTuristicoAdapter( this, registros )
        lvPontosTuristicos.adapter = adapter
    }


}