package br.utfpr.usandovertex

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import br.utfpr.usandovertex.classes.Local
import br.utfpr.usandovertex.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var locais: ArrayList<Local>
    private lateinit var referenciaFirebase: DatabaseReference
    private lateinit var todosLocais: Local
    private lateinit var pato: LatLng
    private var nomeLocal = ""
    private lateinit var binding: ActivityMapsBinding

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment: SupportMapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val it: Intent = intent
        if (it.getStringExtra("intent") == "adapter") {
            pato = LatLng(
                it.getDoubleExtra("latitude", 0.0),
                it.getDoubleExtra("longitude", 0.0)
            )
            nomeLocal = it.getStringExtra("nome")!!
        } else {
            pato = LatLng(-26.2271, -52.6718)
        }
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pato, 10f))
        if (nomeLocal != "") {
            mMap.addMarker(MarkerOptions().title(nomeLocal).snippet("").position(pato))
        } else {
            locais = ArrayList<Local>()
            referenciaFirebase = FirebaseDatabase.getInstance().reference
            referenciaFirebase.child("locais").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {
                        todosLocais = postSnapshot.getValue(Local::class.java)!!
                        locais!!.add(todosLocais)
                        mMap.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    todosLocais.getLatitude().toString().toDouble(),
                                    todosLocais.getLongitude().toString().toDouble()
                                )
                            )
                                .title(todosLocais.getNome())
                        )
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}