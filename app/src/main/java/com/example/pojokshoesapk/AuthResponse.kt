package com.example.pojokshoesapk

data class AuthResponse(
    val message: String,
    val pelanggan: Pelanggan,
    val success: Boolean,
    val error: String?
)

data class Pelanggan(
    val pelanggan_id: Int,
    val pelanggan_nama: String,
    val pelanggan_alamat: String,
    val pelanggan_nomor: String,
    val pelanggan_email: String
)
