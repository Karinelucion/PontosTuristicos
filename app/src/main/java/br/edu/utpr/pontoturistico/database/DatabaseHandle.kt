import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.edu.utpr.pontoturistico.entity.PontoTuristico

class DatabaseHandler  (context : Context) : SQLiteOpenHelper(context, DATABESE_NAME,null, DATABESE_VERSION){
    companion object{
        private const val  DATABESE_NAME = "dbfile.sqlite"
        private const val  DATABESE_VERSION = 1
        public const val  TABLE_NAME = "pontosturisticos"
        public const val  ID = 0
        public const val  DESCRICAO = 1
        public const val  NOME = 2
        public const val  LAT = 3
        public const val  LON = 4
        public const val  IMG = 5
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL( "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " descricao TEXT, nome TEXT, lat REAL, lon REAL, img BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${TABLE_NAME}")
        onCreate(db)
    }

    fun insert(pt_turistico: PontoTuristico ){
        val db = this.writableDatabase

        val registro = ContentValues()

        registro.put("descricao", pt_turistico.descricao)
        registro.put("nome", pt_turistico.nome)
        registro.put("lat", pt_turistico.lat)
        registro.put("lon", pt_turistico.lon)
        registro.put("img", pt_turistico.img)

        db.insert("pontosturisticos", null, registro)

    }

    fun listCursor() : Cursor {
        val db = this.writableDatabase
        val registro = db.query(TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null)

        return registro
    }
}