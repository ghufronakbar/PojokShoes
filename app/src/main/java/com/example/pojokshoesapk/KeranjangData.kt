package com.example.pojokshoesapk

data class KeranjangData(
    val pelanggan_id: Int?,
    val keranjang_tanggal: String,
    val keranjang_jumlah_harga: Int
)
data class KeranjangItemm(
    val keranjang_id: Int,
    val pelanggan_id: Int,
    val keranjang_tanggal: String,
    val keranjang_jumlah_harga: String
)