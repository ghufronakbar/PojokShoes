package com.example.pojokshoesapk

data class ProfileData(
    val pelanggan_nama: String,
    val pelanggan_email: String,
    val pelanggan_nomor: String,
    val pelanggan_alamat: String,
)

data class ProfilePassword (
    val new_password: String,
    val old_password: String
)
