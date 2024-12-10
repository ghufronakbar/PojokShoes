package com.example.pojokshoesapk

data class ItemKeranjangs(
    val namaLayanan: String,
    val jumlahSepatu: Int,
    val harga: Int
)

data class KeranjangResponses(
    val success: Boolean,
    val data: List<DataDetailKeranjang>
)

data class DeleteResponse(
    val message: String
)


data class DataDetailKeranjang(
    val detail_id: Int,
    val layanan_nama: String,
    val jumlah_sepatu: Int,
    val detail_harga: Int
)

data class LayananItem(
    val layanan_id: Int,
    val jumlah_sepatu: Int,
    val detail_harga: Int
)

data class PostDetailKeranjang(
    val keranjang_id: Int,
    val layanan: List<LayananItem>,
    val detail_harga: Int
)



