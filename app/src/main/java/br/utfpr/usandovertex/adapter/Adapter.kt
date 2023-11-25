package br.utfpr.usandovertex.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.utfpr.usandovertex.AnalisarActivity
import br.utfpr.usandovertex.InserirActivity
import br.utfpr.usandovertex.MapsActivity
import br.utfpr.usandovertex.R
import br.utfpr.usandovertex.ZoomImgActivity
import br.utfpr.usandovertex.classes.Local
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class Adapter(private val locais: ArrayList<Local>, private val context: Context) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.lista_locais, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Local = locais[position]

        holder.txtNomeLocal.text = item.getNome()
        holder.txtDescricaoLocal.text = item.getDescricao()
        holder.txtEnderecoLocal.text = item.getEndereco()
//        holder.txtLatitude.text = "Latitude: ${item.getLatitude()}"
//        holder.txtLongitude.text = "Longitude: ${item.getLongitude()}"
        Picasso.get().load(item.getUrlImagem()).resize(300, 300).into(holder.imgLocal)

        holder.linearLayoutLocais.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("intent", "adapter")
            intent.putExtra("nome", item.getNome())
            intent.putExtra("latitude", item.getLatitude())
            intent.putExtra("longitude", item.getLongitude())
            context.startActivity(intent)
        }

        holder.btnEditar.setOnClickListener {
            val intent = Intent(context, InserirActivity::class.java)
            intent.putExtra("intent", "adapter")
            intent.putExtra("nome", item.getNome())
            intent.putExtra("endereco", item.getEndereco())
            intent.putExtra("descricao", item.getDescricao())
            intent.putExtra("urlImagem", item.getUrlImagem())
            intent.putExtra("latitude", item.getLatitude())
            intent.putExtra("longitude", item.getLongitude())
            intent.putExtra("key", item.getId())
            context.startActivity(intent)
        }

        holder.imgLocal.setOnClickListener {
            val intent = Intent(context, ZoomImgActivity::class.java)
            intent.putExtra("url", item.getUrlImagem())
            intent.putExtra("nome", item.getNome())
            intent.putExtra("descricao", item.getDescricao())
            context.startActivity(intent)
        }

        holder.btnAnalisar.setOnClickListener {
            val intent = Intent(context, AnalisarActivity::class.java)
            intent.putExtra("url", item.getUrlImagem())
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return locais.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtNomeLocal: TextView = itemView.findViewById(R.id.txtNomeLocal)
        var txtDescricaoLocal: TextView = itemView.findViewById(R.id.txtDescricaoLocal)
        var txtEnderecoLocal: TextView = itemView.findViewById(R.id.txtEnderecoLocal)
//        var txtLongitude: TextView = itemView.findViewById(R.id.txtLongitudeLocal)
//        var txtLatitude: TextView = itemView.findViewById(R.id.txtLatitudeLocal)
        var imgLocal: ImageView = itemView.findViewById(R.id.imgLocal)
        var linearLayoutLocais: LinearLayout = itemView.findViewById(R.id.linearLayoutLocais)
        var btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        var btnAnalisar: Button = itemView.findViewById(R.id.btnAnalisar)
    }
}
