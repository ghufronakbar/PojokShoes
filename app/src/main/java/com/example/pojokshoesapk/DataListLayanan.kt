package com.example.pojokshoesapk

data class DataListLayanan(
    val layanan_id: Int,
    val layanan_nama: String,
    val layanan_harga: Int,
    val layanan_deskripsi: String,
    var quantity: Int = 0,
    var detail_harga : Int,
    var layanan_picture: String? = null,
    val status: Int
)

