package br.edu.utpr.pontoturistico.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.edu.utpr.pontoturistico.MapaActivity
import br.edu.utpr.pontoturistico.R
import br.edu.utpr.pontoturistico.databinding.ElementoIncluirBinding
import br.edu.utpr.pontoturistico.entity.PontoTuristico
import br.edu.utpr.pontoturistico.repository.PontoTuristicoRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection

class IncluirPontoTuristico : AppCompatActivity() {

    private lateinit var binding: ElementoIncluirBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var fotoSelecionada: Bitmap? = null
    private var idPonto: Int? = null
    private lateinit var pontoTuristicoRepository: PontoTuristicoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ElementoIncluirBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pontoTuristicoRepository = PontoTuristicoRepository(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        idPonto = intent.getIntExtra("ID", -1).takeIf { it != -1 }

        if (idPonto != null) {
            carregarDadosParaEdicao(idPonto!!)
        } else {
            val latitude  = intent.getDoubleExtra("LATITUDE", 0.0)
            val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)

            val etLatitude = findViewById<EditText>(R.id.editTextLatitude)
            val etLongitude = findViewById<EditText>(R.id.editTextLongitude)

            etLatitude.setText(latitude.toString())
            etLongitude.setText(longitude.toString())


            getDescricao(latitude.toString(), longitude.toString())

        }


        pontoTuristicoRepository = PontoTuristicoRepository(this)

        binding.buttonAnexarFoto.setOnClickListener {
            abrirGaleria()
        }
    }

    private fun carregarDadosParaEdicao(id: Int) {
        val ponto = pontoTuristicoRepository.getById(id)
        binding.editTextNome.setText(ponto.nome)
        binding.editTextDescricao.setText(ponto.descricao)
        binding.editTextLatitude.setText(ponto.lat.toString())
        binding.editTextLongitude.setText(ponto.lon.toString())

        ponto.img?.let {
            val bmp = android.graphics.BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.ivFoto.setImageBitmap(bmp)
            fotoSelecionada = bmp
        }
    }

    val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
            image : Bitmap? -> image?.let {
            fotoSelecionada = image
            binding.ivFoto.setImageBitmap(image)
        }
    }


    private fun abrirGaleria() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).let{
            register.launch(null)
        }
    }

    fun btSalvarOnClick(view: View) {
        val nome = binding.editTextNome.text.toString()
        val descricao = binding.editTextDescricao.text.toString()
        val latitude = binding.editTextLatitude.text.toString().toDoubleOrNull() ?: 0.0
        val longitude = binding.editTextLongitude.text.toString().toDoubleOrNull() ?: 0.0
        val imgBT = fotoSelecionada?.let { bitmapToBlob(it) }

        if (idPonto != null) {
            val pontoExistente = pontoTuristicoRepository.getById(idPonto!!)
            val pontoAtualizado = pontoExistente.copy(
                nome = nome,
                descricao = descricao,
                lat = latitude,
                lon = longitude,
                img = imgBT ?: pontoExistente.img
            )
            pontoTuristicoRepository.atualizarPontoTuristico(pontoAtualizado)
        } else {
            val novoPonto = PontoTuristico(
                _id = 0,
                descricao = descricao,
                nome = nome,
                lat = latitude,
                lon = longitude,
                img = imgBT
            )
            pontoTuristicoRepository.salvarPontoTuristico(novoPonto)
        }

        val intent = Intent(this, MapaActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun bitmapToBlob( bitmap: Bitmap) : ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun getDescricao(latitude: String, longitude: String) {
        Thread(Runnable {
            try {
                val endereco = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=${latitude},${longitude}&key=AIzaSyD81GKg5pc-hPxmVIazmsiCb6PI1PXvpJY"
                val url = URL(endereco)
                val con: URLConnection = url.openConnection()

                val inputStream = con.getInputStream()
                val entrada = BufferedReader(InputStreamReader(inputStream))
                val msgSaida = StringBuilder()

                var linha: String? = entrada.readLine()
                while (linha != null) {
                    msgSaida.append(linha)
                    linha = entrada.readLine()
                }

                entrada.close()
                inputStream.close()

                val response = msgSaida.toString()


                val startTag = "<formatted_address>"
                val endTag = "</formatted_address>"

                val startIndex = response.indexOf(startTag)
                val endIndex = response.indexOf(endTag)

                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    val formattedAddress = response.substring(
                        startIndex + startTag.length,
                        endIndex
                    ).trim()

                    runOnUiThread {
                        binding.editTextDescricao.setText(formattedAddress)
                    }
                } else {
                    Log.e("Geocoding", "Endereço não encontrado na resposta")
                    runOnUiThread {
                        binding.editTextDescricao.setText("Endereço não encontrado")
                    }
                }

            } catch (e: Exception) {
                Log.e("GeocodingError", "Erro ao obter endereço: ${e.message}")
                runOnUiThread {
                    binding.editTextDescricao.setText("Erro ao buscar endereço")
                }
            }
        }).start()
    }

}