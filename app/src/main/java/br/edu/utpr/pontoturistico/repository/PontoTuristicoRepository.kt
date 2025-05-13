package br.edu.utpr.pontoturistico.repository

import DatabaseHandler
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.edu.utpr.pontoturistico.entity.PontoTuristico

class PontoTuristicoRepository(context: Context) {

    private val dbHelper = DatabaseHandler(context)

    fun salvarPontoTuristico(pontoTuristico: PontoTuristico): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("nome", pontoTuristico.nome)
            put("descricao", pontoTuristico.descricao)
            put("lat", pontoTuristico.lat)
            put("lon", pontoTuristico.lon)
            put("img", pontoTuristico.img)
        }

        return db.insert(DatabaseHandler.TABLE_NAME, null, values)
    }

    fun atualizarPontoTuristico(pontoTuristico: PontoTuristico): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("nome", pontoTuristico.nome)
            put("descricao", pontoTuristico.descricao)
            put("lat", pontoTuristico.lat)
            put("lon", pontoTuristico.lon)
            put("img", pontoTuristico.img)
        }

        return db.update(
            DatabaseHandler.TABLE_NAME,
            values,
            "_id = ?",
            arrayOf(pontoTuristico._id.toString())
        )
    }

    fun getById(id: Int): PontoTuristico {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHandler.TABLE_NAME} WHERE _id = ?", arrayOf(id.toString()))
        var ponto: PontoTuristico? = null

        if (cursor.moveToFirst()) {
            ponto = PontoTuristico(
                cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("descricao")),
                cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("lat")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("lon")),
                cursor.getBlob(cursor.getColumnIndexOrThrow("img"))
            )
        }

        cursor.close()
        return ponto ?: throw IllegalArgumentException("Ponto com id $id não encontrado")
    }
}
