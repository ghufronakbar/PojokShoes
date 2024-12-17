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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import retrofit2.Callback
import retrofit2.Response

class FragmentSettings : Fragment() {

    private lateinit var sessionManager: SessionManager
    private var token: String? = null
    private lateinit var displayName : TextView
    private lateinit var displayEmail : TextView
    private  lateinit var  btnEdProf: LinearLayout
    private  lateinit var  btnTtgKami: LinearLayout
    private lateinit var  btnKeluar: LinearLayout
    private lateinit var displayPicture: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sessionManager = SessionManager(requireContext())

        displayName = view.findViewById(R.id.displayName)
        displayEmail = view.findViewById(R.id.displayEmail)

        token = sessionManager.getToken()

        displayUserData()


        btnKeluar = view.findViewById(R.id.lnLogout)
        btnKeluar.setOnClickListener {
            logout()
        }

        btnEdProf = view.findViewById(R.id.lnEdit)
        btnEdProf.setOnClickListener {
            val intent = Intent(activity, ActivityEditProfile::class.java)
            startActivity(intent)
        }

        btnTtgKami = view.findViewById(R.id.lnTentangKami)
        btnTtgKami.setOnClickListener {
            val intent = Intent(activity, ActivityTentangKami::class.java)
            startActivity(intent)
        }

        displayPicture = view.findViewById(R.id.displayPicture)


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
                            displayEmail.text = accountResponse.pelanggan_email
                            Glide.with(requireContext())
                                .load(accountResponse.pelanggan_picture)
                                .apply(
                                    RequestOptions()
                                    .placeholder(R.drawable.profile)
                                    .error(R.drawable.profile))
                                .into(displayPicture)
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
