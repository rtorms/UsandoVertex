package br.utfpr.usandovertex

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.utfpr.usandovertex.databinding.ActivityInserirBinding
import br.utfpr.usandovertex.databinding.ActivityZoomImgBinding
import com.squareup.picasso.Picasso

class ZoomImgActivity : AppCompatActivity() {
    private lateinit var img: ImageView
    private lateinit var tvNome: TextView
    private lateinit var tvDescricao: TextView
    private lateinit var binding: ActivityZoomImgBinding

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZoomImgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        img = binding.fullImageView
        tvNome = binding.tvNome
        tvDescricao = binding.tvDescricao
        val i: Intent = intent
        val url: String? = i.getStringExtra("url")
        tvNome.text = i.getStringExtra("nome")
        tvDescricao.text = i.getStringExtra("descricao")
        Picasso.get().load(url).resize(700, 700).into(img)
    }
}