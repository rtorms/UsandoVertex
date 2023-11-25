package br.utfpr.usandovertex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.utfpr.usandovertex.adapter.Adapter
import br.utfpr.usandovertex.classes.Local
import br.utfpr.usandovertex.databinding.ActivityInserirBinding
import br.utfpr.usandovertex.databinding.ActivityListarBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListarActivity : AppCompatActivity()  {

    private lateinit var adapter: Adapter
    private lateinit var locais: MutableList<Local>
    private lateinit var referenciaFirebase: DatabaseReference
    private lateinit var todosLocais: Local
    private lateinit var mLayoutManagerLocais: LinearLayoutManager
    private lateinit var binding : ActivityListarBinding


    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListarBinding.inflate( layoutInflater)
        setContentView(binding.root)

        carregaLocais()
    }

    private fun carregaLocais() {
        binding.recycleViewLocais.setHasFixedSize(true)
        mLayoutManagerLocais = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycleViewLocais.setLayoutManager(mLayoutManagerLocais)
        locais = ArrayList<Local>()
        referenciaFirebase = FirebaseDatabase.getInstance().getReference()
        referenciaFirebase.child("locais").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                locais!!.clear()
                for (postSnapshot in dataSnapshot.getChildren()) {
                    todosLocais = postSnapshot.getValue(Local::class.java)!!
                    locais!!.add(todosLocais)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        adapter = Adapter(locais as ArrayList<Local>, this)
        binding.recycleViewLocais.setAdapter(adapter)
    }


}