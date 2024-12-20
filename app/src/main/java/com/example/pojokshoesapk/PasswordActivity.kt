package com.example.pojokshoesapk

import SessionManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Callback
import retrofit2.Response

class PasswordActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private var token: String? = null

    private lateinit var et_passwordlama: EditText
    private lateinit var et_passwordbaru: EditText
    private lateinit var et_konfirmasipassword: EditText
    private lateinit var btn_simpanpassword: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sessionManager = SessionManager(this)
        token = sessionManager.getToken()
        et_passwordlama = findViewById(R.id.et_passwordlama)
        et_passwordbaru = findViewById(R.id.et_passwordbaru)
        et_konfirmasipassword = findViewById(R.id.et_konfirmasipassword)
        btn_simpanpassword = findViewById(R.id.btn_simpanpassword)
        btn_simpanpassword.setOnClickListener {
            handleEditPassword()
        }
    }

    private fun handleEditPassword() {
        val passwordlama = et_passwordlama.text.toString().trim()
        val passwordbaru = et_passwordbaru.text.toString().trim()
        val konfirmasipassword = et_konfirmasipassword.text.toString().trim()
        if(passwordlama.isEmpty() || passwordbaru.isEmpty() || konfirmasipassword.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }
        if(passwordbaru.length < 6) {
            Toast.makeText(this, "Password baru minimal 6 karakter!", Toast.LENGTH_SHORT).show()
            return
        }
        if (passwordbaru != konfirmasipassword) {
            Toast.makeText(this, "Password baru dan konfirmasi password tidak cocok!", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = ProfilePassword(
            passwordbaru,
            passwordlama,
        )

        RetrofitClient.apiService.updatePassword(
            "Bearer $token",
            requestBody
        ).enqueue(object : Callback<PasswordResponse> {
            override fun onResponse(call: retrofit2.Call<PasswordResponse>, response: Response<PasswordResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PasswordActivity, "${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    et_passwordlama.text.clear()
                    et_passwordbaru.text.clear()
                    et_konfirmasipassword.text.clear()
                } else {
//                    Log.e("FragmentSettings", "Reserrorupdateprofile error: ${response}")
                    Toast.makeText(this@PasswordActivity, "Password lama salah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<PasswordResponse>, t: Throwable) {
                Log.e("FragmentSettings", "Login error: ${t.message}", t)
                Toast.makeText(this@PasswordActivity, "Terjadi kesalahan saat mengubah password", Toast.LENGTH_SHORT).show()
            }
        })
    }
}