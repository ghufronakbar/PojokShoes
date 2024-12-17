package com.example.pojokshoesapk

data class History(
    val keranjang_id: Int,
    val checkout_waktu: String,
    val total: String,
    val checkout_status: String,
    val checkout_items: List<HistoryItem>
)

data class HistoryItem(
    val nama_layanan: String,
    val jumlah_sepatu: Int,
    val status: String
)