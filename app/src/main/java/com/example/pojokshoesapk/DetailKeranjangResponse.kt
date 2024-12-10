package com.example.pojokshoesapk

data class DetailKeranjangResponse(
    val message: String,
)

data class KeranjangKeranjangResponse(
    val success: Boolean,
    val data: List<DataDetailKeranjangs>
)

data class DataDetailKeranjangs(
    val detail_id: Int,
    val layanan_nama: String,
    val jumlah_sepatu: Int,
    val detail_harga: Int
)
