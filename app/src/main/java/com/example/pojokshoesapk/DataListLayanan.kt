package com.example.pojokshoesapk

data class DataListLayanan(
    val layanan_id: Int,
    val layanan_nama: String,
    val layanan_harga: Int,
    val layanan_deskripsi: String,
    var quantity: Int = 0, // Tambahkan properti kuantitas
    var detail_harga : Int
)

