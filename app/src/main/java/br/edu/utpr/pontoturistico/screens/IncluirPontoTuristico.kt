package br.edu.utpr.pontoturistico.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.edu.utpr.pontoturistico.R
import br.edu.utpr.pontoturistico.databinding.ElementoIncluirBinding
import br.edu.utpr.pontoturistico.entity.PontoTuristico
import br.edu.utpr.pontoturistico.repository.PontoTuristicoRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream

class IncluirPontoTuristico : AppCompatActivity() {

    private lateinit var binding: ElementoIncluirBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val REQUEST_LOCATION_PERMISSION = 100
    private var fotoSelecionada: Bitmap? = null
    private lateinit var pontoTuristicoRepository: PontoTuristicoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ElementoIncluirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val latitude  = intent.getDoubleExtra("LATITUDE", 0.0)
        val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)

        println(latitude)
        println(longitude)
        val etLatitude = findViewById<EditText>(R.id.editTextLatitude)
        val etLongitude = findViewById<EditText>(R.id.editTextLongitude)

        etLatitude.setText(latitude.toString())
        etLongitude.setText(longitude.toString())

//        verificarPermissaoLocalizacao()
        pontoTuristicoRepository = PontoTuristicoRepository(this)

        binding.buttonAnexarFoto.setOnClickListener {
            abrirGaleria()
        }
    }

    val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
            image : Bitmap? -> image?.let {
            fotoSelecionada = image
            binding.ivFoto.setImageBitmap(image)
        }
    }

//    private fun verificarPermissaoLocalizacao() {
//        if (ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_LOCATION_PERMISSION
//            )
//        } else {
//            obterLocalizacao()
//        }
//    }
//
//    private fun obterLocalizacao() {
//        if (ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                if (location != null) {
//                    binding.editTextLatitude.setText(location.latitude.toString())
//                    binding.editTextLongitude.setText(location.longitude.toString())
//                } else {
//                    Toast.makeText(this, "Localização indisponível", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }


    private fun abrirGaleria() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).let{
            register.launch(null)
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.isNotEmpty()
//            && grantResults[0] == PackageManager.PERMISSION_GRANTED
//        ) {
//            obterLocalizacao()
//        } else {
//            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
//        }
//    }


    fun btSalvarOnClick(view: View) {
        val nome = binding.editTextNome.text.toString()
        val descricao = binding.editTextDescricao.text.toString()
        val latitude = binding.editTextLatitude.text.toString().toDoubleOrNull() ?: 0.05
        val longitude = binding.editTextLongitude.text.toString().toDoubleOrNull() ?: 0.05

        val imgBT = fotoSelecionada?.let { bitmapToBlob(it) }

        val pontoTuristico = PontoTuristico(0, descricao, nome, latitude, longitude, imgBT)

        pontoTuristicoRepository.salvarPontoTuristico(pontoTuristico)
    }

    fun bitmapToBlob( bitmap: Bitmap) : ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

}