package com.example.pojokshoesapk

data class AuthResponse(
    val message: String, // Pesan dari server, misalnya "Pelanggan berhasil terdaftar"
    val pelanggan: Pelanggan // Objek pelanggan yang berisi detail pengguna
)

data class Pelanggan(
    val pelanggan_id: Int, // ID pelanggan dari database
    val pelanggan_nama: String, // Nama pelanggan
    val pelanggan_alamat: String, // Alamat pelanggan
    val pelanggan_nomor: String, // Nomor telepon pelanggan
    val pelanggan_email: String // Email pelanggan
)
