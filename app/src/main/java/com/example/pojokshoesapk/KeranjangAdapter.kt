package com.example.pojokshoesapk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

        // Set click listener on delete icon
        holder.deleteIcon.setOnClickListener {
            deleteListener.onItemDelete(position)
        }
    }

    // Return the size of the list
    override fun getItemCount(): Int = itemList.size

    // Method to remove an item from the list and update the adapter
    fun removeItem(position: Int) {
        if (position in 0 until itemList.size) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    // Updated method to safely update data
    fun updateData(newList: List<ItemKeranjangs>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}
