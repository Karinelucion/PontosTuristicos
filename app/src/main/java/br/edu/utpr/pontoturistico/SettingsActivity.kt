package br.edu.utpr.pontoturistico

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.utpr.pontoturistico.screens.ListarPontosTuristicos
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var seekBarZoom: SeekBar
    private lateinit var tvZoomValue: TextView
    private lateinit var radioNormal: RadioButton
    private lateinit var radioSatellite: RadioButton
    private lateinit var radioHybrid: RadioButton
    private lateinit var radioTerrain: RadioButton
    private lateinit var radioNone: RadioButton
    private lateinit var btnSaveConfig: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        seekBarZoom = findViewById(R.id.seekBarZoom)
        tvZoomValue = findViewById(R.id.tvZoomValue)
        radioNormal = findViewById(R.id.radioNormal)
        radioSatellite = findViewById(R.id.radioSatellite)
        radioHybrid = findViewById(R.id.radioHybrid)
        radioTerrain = findViewById(R.id.radioTerrain)
        radioNone = findViewById(R.id.radioNone)
        btnSaveConfig = findViewById(R.id.btnSaveConfig)

        sharedPref = getSharedPreferences("map_prefs", Context.MODE_PRIVATE)

        // Carrega configurações salvas
        loadSavedSettings()

        // Configura SeekBar do Zoom
        seekBarZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvZoomValue.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Botão Salvar
        btnSaveConfig.setOnClickListener {
            saveSettings()
            Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MapaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadSavedSettings() {
        // Carrega zoom
        val zoom = sharedPref.getInt("default_zoom", 12)
        seekBarZoom.progress = zoom
        tvZoomValue.text = zoom.toString()

        // Carrega tipo de mapa
        val mapType = sharedPref.getInt("map_type", GoogleMap.MAP_TYPE_NORMAL)
        when (mapType) {
            GoogleMap.MAP_TYPE_NORMAL -> radioNormal.isChecked = true
            GoogleMap.MAP_TYPE_SATELLITE -> radioSatellite.isChecked = true
            GoogleMap.MAP_TYPE_HYBRID -> radioHybrid.isChecked = true
            GoogleMap.MAP_TYPE_TERRAIN -> radioTerrain.isChecked = true
            GoogleMap.MAP_TYPE_NONE -> radioNone.isChecked = true
        }
    }

    private fun saveSettings() {
        with(sharedPref.edit()) {
            // Salva zoom
            putInt("default_zoom", seekBarZoom.progress)

            // Salva tipo de mapa
            val mapType = when {
                radioNormal.isChecked -> GoogleMap.MAP_TYPE_NORMAL
                radioSatellite.isChecked -> GoogleMap.MAP_TYPE_SATELLITE
                radioHybrid.isChecked -> GoogleMap.MAP_TYPE_HYBRID
                radioTerrain.isChecked -> GoogleMap.MAP_TYPE_TERRAIN
                radioNone.isChecked -> GoogleMap.MAP_TYPE_NONE
                else -> GoogleMap.MAP_TYPE_NORMAL
            }
            putInt("map_type", mapType)
            apply()
        }

    }
}