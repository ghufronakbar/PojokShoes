package com.example.pojokshoesapk

data class LoginResponse(
    val pelanggan_id: Int,
    val pelanggan_nama: String,
    val pelanggan_alamat: String,
    val pelanggan_nomor: String,
    val token: String,
    val message: String
)