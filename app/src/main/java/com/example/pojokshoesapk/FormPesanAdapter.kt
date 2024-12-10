package com.example.pojokshoesapk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class FormPesanAdapter(
    private val context: Context,
    private val layananList: List<DataListLayanan>,
    private val listener: OnQuantityChangeListener
) : BaseAdapter() {

    interface OnQuantityChangeListener {
        fun onQuantityChanged(total: Int)
    }

    override fun getCount(): Int = layananList.size

    override fun getItem(position: Int): Any = layananList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_form_pesan, parent, false)
            holder = ViewHolder()

            holder.imageView = view.findViewById(R.id.image_service)
            holder.nameTextView = view.findViewById(R.id.item_sintetis)
            holder.priceTextView = view.findViewById(R.id.text_price)
            holder.quantityTextView = view.findViewById(R.id.quantity_sintetis)
            holder.increaseButton = view.findViewById(R.id.btn_increase_sintetis)
            holder.decreaseButton = view.findViewById(R.id.btn_decrease_sintetis)

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val layanan = layananList[position]
        holder.nameTextView.text = layanan.layanan_nama
        holder.priceTextView.text = "Rp ${layanan.layanan_harga}"
        holder.quantityTextView.text = layanan.quantity.toString()

        // Perbarui tombol kurangi jika kuantitas 0
        holder.decreaseButton.isEnabled = layanan.quantity > 0

        // Tombol tambah
        holder.increaseButton.setOnClickListener {
            layanan.quantity++
            holder.quantityTextView.text = layanan.quantity.toString()
            holder.decreaseButton.isEnabled = true
            listener.onQuantityChanged(calculateTotalPrice())
        }

        // Tombol kurangi
        holder.decreaseButton.setOnClickListener {
            if (layanan.quantity > 0) {
                layanan.quantity--
                holder.quantityTextView.text = layanan.quantity.toString()
                holder.decreaseButton.isEnabled = layanan.quantity > 0
                listener.onQuantityChanged(calculateTotalPrice())
            }
        }

        return view
    }

    // Menghitung total harga semua layanan berdasarkan kuantitas
    private fun calculateTotalPrice(): Int {
        return layananList.sumOf { it.layanan_harga * it.quantity }
    }

    // Mendapatkan daftar layanan yang dipilih (kuantitas > 0)
    fun getSelectedItems(): List<DataListLayanan> {
        return layananList.filter { it.quantity > 0 }
    }

    private class ViewHolder {
        lateinit var imageView: ImageView
        lateinit var nameTextView: TextView
        lateinit var priceTextView: TextView
        lateinit var quantityTextView: TextView
        lateinit var increaseButton: Button
        lateinit var decreaseButton: Button
    }
}
