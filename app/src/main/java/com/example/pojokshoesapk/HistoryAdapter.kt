package com.example.pojokshoesapk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(private val historyList: List<History>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    // ViewHolder untuk setiap item dalam RecyclerView
    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkoutWaktu: TextView = itemView.findViewById(R.id.text_checkout_waktu)
        val totalHarga: TextView = itemView.findViewById(R.id.text_total_harga)
        val recyclerViewItems: RecyclerView = itemView.findViewById(R.id.recyclerViewItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]

        holder.checkoutWaktu.text = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(SimpleDateFormat("yyyy-MM-dd").parse(history.checkout_waktu))
        holder.totalHarga.text = "Rp. " + history.total
//        holder.checkoutStatus.text = history.checkout_status

        // Set up adapter untuk items di dalam riwayat (HistoryItem)
        val itemsAdapter = HistoryItemAdapter(history.checkout_items)
        holder.recyclerViewItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerViewItems.adapter = itemsAdapter

    }

    override fun getItemCount(): Int = historyList.size
}

class HistoryItemAdapter(private val items: List<HistoryItem>) : RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder>() {

    inner class HistoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaLayanan: TextView = itemView.findViewById(R.id.text_nama_layanan)
        val jumlahSepatu: TextView = itemView.findViewById(R.id.text_jumlah_sepatu)
        val status: TextView = itemView.findViewById(R.id.text_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_detail, parent, false)
        return HistoryItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        val item = items[position]

        holder.namaLayanan.text = item.nama_layanan
        holder.jumlahSepatu.text = "x${item.jumlah_sepatu}"
        holder.status.text = item.status.replaceFirstChar { it.uppercase() }
    }

    override fun getItemCount(): Int = items.size
}
