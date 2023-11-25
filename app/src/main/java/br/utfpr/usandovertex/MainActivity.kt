package br.utfpr.usandovertex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import br.utfpr.usandovertex.databinding.ActivityInserirBinding
import br.utfpr.usandovertex.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

        private lateinit var btnInserir: Button
        private lateinit var btnListar: Button
        private lateinit var btnMaps: Button
        private lateinit var binding : ActivityMainBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate( layoutInflater)
            setContentView(binding.root)

            binding.btnInserir
            binding.btnListar
            binding.btnMaps

            val it = intent
            if (it == null) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            }
        }

        fun btnInserirOnclick(view: View?) {
            val i = Intent(this, InserirActivity::class.java)
            i.putExtra("intent", "main")
            startActivity(i)
        }

        fun btnListarOnclick(view: View?) {
            val i = Intent(this, ListarActivity::class.java)
            i.putExtra("intent", "main")
            startActivity(i)
        }

        fun btnMapsOnclick(view: View?) {
            val i = Intent(this, MapsActivity::class.java)
            i.putExtra("intent", "main")
            startActivity(i)
        }
}