package com.example.pojokshoesapk

data class KeranjangResponse(
    val success: Boolean,
    val message: String,
    val keranjang_id: Int
)
//ini tak ganti
data class TambahKeranjangResponse(
    val keranjang_status: String,
    val keranjang_id: Int,
    val pelanggan_id: Int,
    val keranjang_jumlah_harga: Int,
    val keranjang_tanggal: String
)

