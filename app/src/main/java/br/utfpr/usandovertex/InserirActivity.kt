package br.utfpr.usandovertex

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.utfpr.usandovertex.classes.Local
import br.utfpr.usandovertex.databinding.ActivityInserirBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.Date

class InserirActivity : AppCompatActivity() , LocationListener  {

        private lateinit var ivFoto: ImageView
        private lateinit var local: Local
        private lateinit var tvLatitude: TextView
        private lateinit var tvLongitude: TextView
        private lateinit var edtCadNome: EditText
        private lateinit var edtCadDescricao: EditText
        private lateinit var edtCadEndereco: EditText
        private lateinit var urlImagem: String
        private lateinit var storage: FirebaseStorage
        private lateinit var storageReference: StorageReference
        private lateinit var databaseReference: DatabaseReference
        private lateinit var database: FirebaseDatabase
        private lateinit var editar: String
        private lateinit var key: String
        private lateinit var progressBar: ProgressBar
        private lateinit var locationManager : LocationManager
        private lateinit var binding : ActivityInserirBinding


        @SuppressLint("MissingInflatedId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityInserirBinding.inflate( layoutInflater)
            setContentView(binding.root)
            progressBar = findViewById(R.id.progressBar)
            storage = FirebaseStorage.getInstance()
            storageReference = FirebaseStorage.getInstance().reference
            database = FirebaseDatabase.getInstance()
            databaseReference = database.getReference("locais")
            val it = intent
            if (it.getStringExtra("intent") == "adapter") {
                editar = "editar"
                binding.edtCadNome.setText(it.getStringExtra("nome"))
                binding.edtCadDescricao.setText(it.getStringExtra("descricao"))
                binding.edtCadEndereco.setText(it.getStringExtra("endereco"))
                binding.tvLatitude.text = it.getStringExtra("latitude")!!
                binding.tvLongitude.text = it.getStringExtra("longitude")!!
                urlImagem = it.getStringExtra("urlImagem")!!
                key = it.getStringExtra("key")!!
                Picasso.get().load(urlImagem).resize(400, 400).into(binding.ivFoto)
            } else {
                val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE)
                startActivityForResult(camera, 11)
                editar = "salvar"

                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0f,
                    this
                )
            }
        }

        //trata imagem recebida da camera
        override fun onActivityResult(requestCode: Int, resultCode: Int,  data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 11) {
                if (data != null) {
                    val imagem = data.extras!!["data"] as Bitmap?
                    binding.ivFoto!!.setImageBitmap(imagem)
                }
            }
        }

        open fun cadastroFoto() {
            val salvaFotoReferencia = storageReference.child(
                "local/" +
                        binding.tvLongitude.text.toString() + binding.tvLatitude.text.toString()
                       + Date().time + ".jpg"
            )
            binding.ivFoto.isDrawingCacheEnabled = true
            binding.ivFoto.buildDrawingCache()
            val bitmap = binding.ivFoto.drawingCache
            val byteArray = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 15, byteArray)
            val data = byteArray.toByteArray()
            val uploadTask = salvaFotoReferencia.putBytes(data)
            val urlTask = uploadTask.continueWithTask<Uri> { task ->
                if (!task.isSuccessful) {
                    throw (task.exception)!!
                }
                salvaFotoReferencia.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    urlImagem = downloadUri.toString()
                    salvar()
                } else {
                    Toast.makeText(this@InserirActivity, "Erro ao salvar local", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        fun btnSalvarOnclick(view: View?) {
            binding.progressBar.visibility = View.VISIBLE
            if (binding.edtCadNome.text.toString() != "") {
                // Assuming databaseReference points to a location where a boolean value is stored
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // verifica se existe conexão
                        val connected = dataSnapshot.value

                        if (connected != null) {
                            cadastroFoto()
                        } else {
                            Toast.makeText(
                                this@InserirActivity,
                                "Sem conexão, dados em cache!",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(
                            this@InserirActivity,
                            "Cancelado pelo usuário!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }
            else {
                Toast.makeText(this@InserirActivity, "Campo nome obrigatório!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        fun salvar() {
            local = Local()
            local.setNome(binding.edtCadNome.text.toString())
            local.setDescricao(binding.edtCadDescricao.text.toString())
            local.setEndereco(binding.edtCadEndereco.text.toString())
            local.setLatitude(binding.tvLatitude.text.toString())
            local.setLongitude(binding.tvLongitude.text.toString())
            local.setUrlImagem(urlImagem)
            if ((editar == "salvar")) {
                try {
                    key = databaseReference!!.push().key.toString()
                    local.setId(key)
                    databaseReference!!.child((key)!!).setValue(local)
                    Toast.makeText(
                        this@InserirActivity,
                        "Local Cadastrado com Sucesso!!",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.progressBar.visibility = View.GONE
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@InserirActivity, "Erro ao salvar local", Toast.LENGTH_LONG)
                        .show()
                    e.printStackTrace()
                }
            } else {
                try {
                    local.setId(key)
                    databaseReference!!.child((key)!!).setValue(local)
                    Toast.makeText(
                        this@InserirActivity,
                        "Local Editado com Sucesso!!",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@InserirActivity, "Erro ao salvar local", Toast.LENGTH_LONG)
                        .show()
                    e.printStackTrace()
                }
            }
        }

        fun btnCancelarOnclick(view: View?) {
            val intent = Intent(this@InserirActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        fun btnNovaFotoOnclick(view: View?) {
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 11)
        }


        // seta dados GPS
        override fun onLocationChanged(location: Location) {
            binding.tvLatitude.text = location.latitude.toString()
            binding.tvLongitude.text = location.longitude.toString()
            if (binding.edtCadEndereco.text.length < 2){
                ConexaoEnderecoThread()
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}


        fun ConexaoEnderecoThread() {
                try {
                    Thread(Runnable {
                        var lat = binding.tvLatitude.text
                        var long = binding.tvLongitude.text
                        val url = ("https://maps.googleapis.com/maps/api/geocode/xml?latlng=" + lat + "," + long
                                     + "&key=AIzaSyBTI7zTnZwKMzCKpCjyODjBCuSanoKp6mQ")
                        val caminho = URL(url)
                        val con = caminho.openConnection()
                        val `in` = con.getInputStream()
                        val msg = StringBuilder()
                        val entrada = BufferedReader(InputStreamReader(`in`))
                        var linha = entrada.readLine()
                        while (linha != null) {
                            msg.append(linha)
                            linha = entrada.readLine()
                        }
                        val enderecoRetornado = msg.toString().substring(
                            msg.toString().indexOf("<formatted_address>") + 19,
                            msg.toString().indexOf("</formatted_address>")
                        )
                        runOnUiThread {

                                binding.edtCadEndereco.setText(enderecoRetornado)

                        }
                    }).start()
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
}