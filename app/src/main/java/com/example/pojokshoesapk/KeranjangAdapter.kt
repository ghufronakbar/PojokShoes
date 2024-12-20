package com.example.pojokshoesapk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.util.Locale

class KeranjangAdapter(
    private val itemList: MutableList<ItemKeranjangs>,
    private val deleteListener: OnItemDeleteListener
) : RecyclerView.Adapter<KeranjangAdapter.KeranjangViewHolder>() {

    // Define the ViewHolder
    class KeranjangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaLayanan: TextView = itemView.findViewById(R.id.keranjang_nama_layanan)
        val jumlahSepatu: TextView = itemView.findViewById(R.id.JumlahSepatuKeranjang)
        val harga: TextView = itemView.findViewById(R.id.harga_layanan)
        val deleteIcon: ImageView = itemView.findViewById(R.id.delete_icon)
        val imgKeranjang: ImageView = itemView.findViewById(R.id.imgKeranjang)
        val statusText : TextView = itemView.findViewById(R.id.text_status_keranjang)
    }

    // Define the interface for delete listener
    interface OnItemDeleteListener {
        fun onItemDelete(position: Int)
    }

    // Implement the onCreateViewHolder method
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeranjangViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
        return KeranjangViewHolder(itemView)
    }

    // Implement the onBindViewHolder method
    override fun onBindViewHolder(holder: KeranjangViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.namaLayanan.text = currentItem.namaLayanan
        holder.jumlahSepatu.text = "x${currentItem.jumlahSepatu}"
        holder.harga.text = "Rp ${currentItem.harga}"

        if(currentItem.layanan_status == 0){
            holder.statusText.text = "Layanan sedang tidak tersedia"
            holder.statusText.visibility = View.VISIBLE
        }

        Glide.with(holder.itemView.context)
            .load(currentItem.layanan_picture) // URL gambar
            .apply(
                RequestOptions()
                .placeholder(R.drawable.recolor)  // Gambar placeholder saat loading
                .error(R.drawable.recolor)) // Gambar error jika gagal load
            .into(holder.imgKeranjang)


        // Set click listener on delete icon
        holder.deleteIcon.setOnClickListener {
            deleteListener.onItemDelete(position)
        }
    }

    // Return the size of the list
    override fun getItemCount(): Int = itemList.size

}
