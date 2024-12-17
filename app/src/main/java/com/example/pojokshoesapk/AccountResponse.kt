package com.example.pojokshoesapk

data class AccountResponse(
    val pelanggan_id: Int,
    val pelanggan_nama: String,
    val pelanggan_alamat: String,
    val pelanggan_nomor: String,
    val pelanggan_email: String,
    val pelanggan_picture: String? = null,
    var message: String? = null,
    val success: Boolean
) {
}

data class PasswordResponse (
    val message: String
)