package br.edu.utpr.pontoturistico.repository

import DatabaseHandler

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import br.edu.utpr.pontoturistico.entity.PontoTuristico

import java.io.ByteArrayOutputStream

class PontoTuristicoRepository(context: Context) {

    private val dbHelper = DatabaseHandler(context)
    fun salvarPontoTuristico( pontoTuristico : PontoTuristico): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("nome", pontoTuristico.nome)
            put("descricao", pontoTuristico.descricao)
            put("lat", pontoTuristico.lat)
            put("lon", pontoTuristico.lon)
            put("img", pontoTuristico.img)
        }

        // Inserir no banco
        return db.insert(DatabaseHandler.TABLE_NAME, null, values)
    }
}
