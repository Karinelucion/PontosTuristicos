package br.edu.utpr.pontoturistico

import DatabaseHandler
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.utpr.pontoturistico.databinding.ActivityMapaBinding
import br.edu.utpr.pontoturistico.screens.IncluirPontoTuristico
import br.edu.utpr.pontoturistico.screens.ListarPontosTuristicos
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val dbHandler = DatabaseHandler(this)
        val cursor = dbHandler.listCursor()

        if (cursor.moveToFirst()) {
            do {
                val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
                val descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"))
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"))
                val lon = cursor.getDouble(cursor.getColumnIndexOrThrow("lon"))

                val local = LatLng(lat, lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(local)
                        .title(nome)
                        .snippet(descricao)
                )
            } while (cursor.moveToNext())

            cursor.moveToFirst()
            val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"))
            val lon = cursor.getDouble(cursor.getColumnIndexOrThrow("lon"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 12f))
        }

        cursor.close()
    }

    fun btIncluirOnClick(view: View) {
        val intent = Intent(this, IncluirPontoTuristico::class.java)
        startActivity(intent)
    }
    fun btListarOnClick(view: View) {
        val intent = Intent(this, ListarPontosTuristicos::class.java)
        startActivity(intent)

    }
}