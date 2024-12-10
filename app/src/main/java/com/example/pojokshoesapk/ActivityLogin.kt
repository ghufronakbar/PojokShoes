package com.example.pojokshoesapk

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLogin : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi elemen UI
        val usernameEditText = findViewById<EditText>(R.id.etNamaLogin)
        val passwordEditText = findViewById<EditText>(R.id.etPasswordLogin)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerTextView = findViewById<TextView>(R.id.daftar_register)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Navigasi ke ActivityRegister
        registerTextView.setOnClickListener {
            startActivity(Intent(this, ActivityRegister::class.java))
        }

        // Fungsi login
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validasi input
            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Nama dan Password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        val loginRequest = LoginData(pelanggan_nama = username, pelanggan_password = password)
        Log.d("ActivityLogin", "Data login dikirim: ${loginRequest.toString()}")

        RetrofitClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val pelangganId = loginResponse.pelanggan_id
                        val pelangganNama = loginResponse.pelanggan_nama
                        val token = loginResponse.token

                        // Bersihkan data keranjang sebelumnya
                        sessionManager.clearKeranjangData()

                        // Simpan data sesi baru
                        sessionManager.saveToken(token)
                        sessionManager.saveUserId(pelangganId)
                        sessionManager.saveKeranjangOwnerId(pelangganId)

                        // Log untuk debugging
                        Log.d("ActivityLogin", "Login sukses: ${loginResponse.message}")
                        Log.d("USER_ID", "pelanggan_id: $pelangganId")

                        Toast.makeText(
                            this@ActivityLogin,
                            "Login sukses: ${loginResponse.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigasi ke ActivityMain
                        val homeIntent = Intent(this@ActivityLogin, ActivityMain::class.java)
                        homeIntent.putExtra("pelanggan_nama", pelangganNama)
                        startActivity(homeIntent)
                        finish()
                    }
                } else {
                    // Log error untuk debugging
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityLogin", "Login gagal: $errorBody")

                    try {
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        Toast.makeText(
                            this@ActivityLogin,
                            "Login gagal: ${errorResponse.details}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Log.e("ActivityLogin", "Error parsing: ${e.message}")
                        Toast.makeText(
                            this@ActivityLogin,
                            "Login gagal: Tidak dapat memproses error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("ActivityLogin", "Login error: ${t.message}", t)
                Toast.makeText(
                    this@ActivityLogin,
                    "Terjadi kesalahan: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Data class untuk response error
    data class ErrorResponse(
        val error: String,
        val details: String
    )
}
