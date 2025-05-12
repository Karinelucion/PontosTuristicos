package br.edu.utpr.pontoturistico

import DatabaseHandler
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.edu.utpr.pontoturistico.databinding.ActivityMapaBinding
import br.edu.utpr.pontoturistico.screens.IncluirPontoTuristico
import br.edu.utpr.pontoturistico.screens.ListarPontosTuristicos
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContentProviderCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapaBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var ultimaLocalizacao: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val dbHandler = DatabaseHandler(this)
        val cursor = dbHandler.listCursor()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            startLocationUpdates()
        }

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
        applyMapSettings()
        cursor.close()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            100
        ).apply {
            setMinUpdateIntervalMillis(5000)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location = locationResult.lastLocation ?: return
                ultimaLocalizacao = location

                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun btIncluirOnClick(view: View) {
        val intent = Intent(this, IncluirPontoTuristico::class.java)

        ultimaLocalizacao?.let {
            intent.putExtra("LATITUDE", it.latitude)
            intent.putExtra("LONGITUDE", it.longitude)
        }

        startActivity(intent)
    }
    fun btListarOnClick(view: View) {
        val intent = Intent(this, ListarPontosTuristicos::class.java)
        startActivity(intent)
    }

    fun btConfigOnClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun applyMapSettings() {
        val sharedPref = getSharedPreferences("map_prefs", MODE_PRIVATE)

        val defaultZoom = sharedPref.getInt("default_zoom", 12).toFloat()
        mMap.moveCamera(CameraUpdateFactory.zoomTo(defaultZoom))

        val mapType = sharedPref.getInt("map_type", GoogleMap.MAP_TYPE_NORMAL)
        mMap.mapType = mapType
    }

}