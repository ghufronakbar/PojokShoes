package com.example.pojokshoesapk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityRegister : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister: Button = findViewById(R.id.btnRegister)
        val etNama: EditText = findViewById(R.id.etNamaRegister)
        val etAlamat: EditText = findViewById(R.id.etAlamatRegister)
        val etNomor: EditText = findViewById(R.id.etNoRegister)
        val etEmail: EditText = findViewById(R.id.etEmailRegister)
        val etPassword: EditText = findViewById(R.id.etPasswordRegister)
        val keLogin: TextView = findViewById(R.id.ke_login)

        keLogin.setOnClickListener {
            startActivity(Intent(this, ActivityLogin::class.java))
        }


        btnRegister.setOnClickListener {
            val nama = etNama.text.toString()
            val alamat = etAlamat.text.toString()
            val nomor = etNomor.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (nama.isEmpty() || alamat.isEmpty() || nomor.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerData = RegisterData(
                pelanggan_nama = nama,
                pelanggan_alamat = alamat,
                pelanggan_nomor = nomor,
                pelanggan_email = email,
                pelanggan_password = password
            )

            // Panggil API register melalui RetrofitClient
            RetrofitClient.apiService.registerUser(registerData)
                .enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                        if (response.isSuccessful) {
                            val authResponse = response.body()
                            Toast.makeText(this@ActivityRegister, authResponse?.message ?: "Berhasil!", Toast.LENGTH_SHORT).show()
                            finish() // Kembali ke layar sebelumnya
                        } else {
                            Toast.makeText(this@ActivityRegister, "Registrasi gagal, coba lagi!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        Log.e("Register", "onFailure: ${t.message}")
                        Toast.makeText(this@ActivityRegister, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
