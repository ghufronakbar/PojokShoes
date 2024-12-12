package com.example.pojokshoesapk

data class LayananHomeResponse(
    val fast_clean: LayananHomeInfo,
    val deep_clean: LayananHomeInfo,
    val reglue: LayananHomeInfo,
    val recolor: LayananHomeInfo
)

data class LayananHomeInfo(
    val name: String,
    val picture: String? = null,
    val layanan_deskripsi: String
)