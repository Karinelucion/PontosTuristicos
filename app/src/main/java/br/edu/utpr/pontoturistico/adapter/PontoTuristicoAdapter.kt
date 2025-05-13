package br.edu.utpr.pontoturistico.adapter

import DatabaseHandler
import DatabaseHandler.Companion.DESCRICAO
import DatabaseHandler.Companion.ID
import DatabaseHandler.Companion.IMG
import DatabaseHandler.Companion.LAT
import DatabaseHandler.Companion.LON
import DatabaseHandler.Companion.NOME
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import br.edu.utpr.pontoturistico.R
import br.edu.utpr.pontoturistico.entity.PontoTuristico
import br.edu.utpr.pontoturistico.screens.IncluirPontoTuristico
import java.text.DecimalFormat

class PontoTuristicoAdapter (val context : Context, var cursor : Cursor) : BaseAdapter()  {
    private lateinit var banco : DatabaseHandler

    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int): Any {
        cursor.moveToPosition(position)

        return PontoTuristico(
            cursor.getInt(ID),
            cursor.getString(DESCRICAO),
            cursor.getString(NOME),
            cursor.getDouble(LAT),
            cursor.getDouble(LON),
            cursor.getBlob(IMG)
        )
    }

    override fun getItemId(position: Int): Long {
        cursor.moveToPosition(position)

        return cursor.getInt(ID).toLong()
    }

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = p1 ?: inflater.inflate(R.layout.item_ponto_turistico, p2, false)

        if (!cursor.moveToPosition(position)) {
            return v
        }
        val tvNomeElementoLista = v.findViewById<TextView>(R.id.tvNome)
        val tvDescricaoElementoLita = v.findViewById<TextView>(R.id.tvDescricao)
        val btnExcluir = v.findViewById<Button>(R.id.btnExcluir)
        val btnEditar = v.findViewById<Button>(R.id.btnEditar)



        banco = DatabaseHandler( context )

        tvNomeElementoLista.setText(cursor.getString(NOME))
        tvDescricaoElementoLita.setText(cursor.getString(DESCRICAO))

        btnExcluir.setOnClickListener{
            cursor.moveToPosition(position)
            val id = cursor.getInt(ID)
            banco.excluir(id)
            updateCursor()

            Toast.makeText(context, "Ponto turistico excluído com sucesso!", Toast.LENGTH_SHORT).show()
        }

        btnEditar.setOnClickListener {
            cursor.moveToPosition(position)
            val ponto = PontoTuristico(
                cursor.getInt(ID),
                cursor.getString(DESCRICAO),
                cursor.getString(NOME),
                cursor.getDouble(LAT),
                cursor.getDouble(LON),
                cursor.getBlob(IMG)
            )

            val intent = Intent(context, IncluirPontoTuristico::class.java).apply {
                putExtra("ID", ponto._id)
                putExtra("NOME", ponto.nome)
                putExtra("DESCRICAO", ponto.descricao)
                putExtra("LATITUDE", ponto.lat)
                putExtra("LONGITUDE", ponto.lon)
                putExtra("IMG", ponto.img)
            }
            context.startActivity(intent)
        }

        return v
    }

    private fun updateCursor(){
        val newCursor = banco.listCursor()
        cursor.close()
        cursor = newCursor
        notifyDataSetChanged()
    }
}