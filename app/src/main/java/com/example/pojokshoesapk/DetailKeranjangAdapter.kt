package com.example.pojokshoesapk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DetailKeranjangAdapter(
    private val detailList: List<DataDetailKeranjang>
) : RecyclerView.Adapter<DetailKeranjangAdapter.DetailViewHolder>() {

    class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaLayanan: TextView = itemView.findViewById(R.id.keranjang_nama_layanan)
        val jumlahSepatu: TextView = itemView.findViewById(R.id.JumlahSepatuKeranjang)
        val hargaLayanan: TextView = itemView.findViewById(R.id.harga_layanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
        return DetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val detail = detailList[position]
        holder.namaLayanan.text = detail.layanan_nama
        holder.jumlahSepatu.text = "x${detail.jumlah_sepatu}"
        holder.hargaLayanan.text = "Rp ${detail.detail_harga}"
    }

    override fun getItemCount(): Int = detailList.size
}
