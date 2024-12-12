package com.example.pojokshoesapk

import android.content.Intent
import SessionManager
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Callback
import retrofit2.Response

class FragmentSettings : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var displayName : TextView
    private lateinit var displayPhone : TextView
    private lateinit var displayAddress : TextView
    private var token: String? = null  // Perubahan: Membuat token nullable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sessionManager = SessionManager(requireContext())

        displayName = view.findViewById(R.id.displayName)
        displayPhone = view.findViewById(R.id.displayPhone)
        displayAddress = view.findViewById(R.id.displayAddress)

        token = sessionManager.getToken()

        displayUserData()


        val btnKeluar: Button = view.findViewById(R.id.btnKeluar)

        btnKeluar.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {
        sessionManager.clearSessionData()
        val intent = Intent(activity, ActivityLogin::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun displayUserData() {
        // Pastikan token sudah tersedia
        if (token != null) {
            RetrofitClient.apiService.getProfile("Bearer $token").enqueue(object : Callback<AccountResponse> {
                override fun onResponse(
                    call: retrofit2.Call<AccountResponse>,
                    response: Response<AccountResponse>
                ) {
                    if (response.isSuccessful) {
                        val accountResponse = response.body()
                        if (accountResponse != null) {
                            displayName.text = accountResponse.pelanggan_nama
                            displayPhone.text = accountResponse.pelanggan_nomor
                            displayAddress.text = accountResponse.pelanggan_alamat
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<AccountResponse>, t: Throwable) {
                    Log.e("FragmentSettings", "Login error: ${t.message}", t)
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan saat mengambil data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Log.e("FragmentSettings", "Token tidak ditemukan")
            Toast.makeText(requireContext(), "Token tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }
}
