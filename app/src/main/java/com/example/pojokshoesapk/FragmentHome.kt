package com.example.pojokshoesapk

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentHome : Fragment() {

    // Views
    private lateinit var imgFast: ImageView
    private lateinit var imgDeep: ImageView
    private lateinit var imgReglue: ImageView
    private lateinit var imgRecolor: ImageView

    private lateinit var tvFast: TextView
    private lateinit var tvDeep: TextView
    private lateinit var tvReglue: TextView
    private lateinit var tvRecolor: TextView

    private lateinit var homeName: TextView

    var token: String? = null
    private lateinit var  sessionManager : SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi Views
        imgFast = view.findViewById(R.id.imgfast)
        imgDeep = view.findViewById(R.id.imgdeep)
        imgReglue = view.findViewById(R.id.imgreglue)
        imgRecolor = view.findViewById(R.id.imgrecolor)

        tvFast = view.findViewById(R.id.tv3)
        tvDeep = view.findViewById(R.id.tv4)
        tvReglue = view.findViewById(R.id.tv5)
        tvRecolor = view.findViewById(R.id.tv6)

        // Panggil fetch data layanan
        fetchLayananData()

        // Set click listeners (contoh untuk Fast Clean)
        view.findViewById<View>(R.id.cardfastclean).setOnClickListener {
            val intent = Intent(activity, ActivityFormFastClean::class.java)
            startActivity(intent)
        }

        view.findViewById<View>(R.id.carddeepclean).setOnClickListener {
            val intent = Intent(activity, ActivityFormDeepClean::class.java)
            startActivity(intent)
        }

        view.findViewById<View>(R.id.cardreglue).setOnClickListener {
            val intent = Intent(activity, ActivityFormReglue::class.java)
            startActivity(intent)
        }

        view.findViewById<View>(R.id.cardrecolor).setOnClickListener {
            val intent = Intent(activity, ActivityFormRecolor::class.java)
            startActivity(intent)
        }

        // Keranjang
        val imgKeranjang: View = view.findViewById(R.id.homekeranjang)
        imgKeranjang.setOnClickListener {
            val intent = Intent(activity, ActivityKeranjang::class.java)
            startActivity(intent)
        }

         homeName= view.findViewById(R.id.homeName)
        sessionManager = SessionManager(requireContext())
        token = sessionManager.getToken()

        displayUserData()

        return view
    }

    private fun displayUserData() {
        RetrofitClient.apiService.getProfile("Bearer $token").enqueue(object : Callback<AccountResponse> {
            override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                if (response.isSuccessful) {
                    val accountResponse = response.body()
                    accountResponse?.let {
                        homeName.text = "Halo, ${it.pelanggan_nama}"
                    }
                }
            }

            override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun fetchLayananData() {
        // Memanggil API untuk mendapatkan data layanan
        RetrofitClient.apiService.getLayananHome().enqueue(object : Callback<LayananHomeResponse> {
            override fun onResponse(
                call: Call<LayananHomeResponse>,
                response: Response<LayananHomeResponse>
            ) {
                if (response.isSuccessful) {
                    val layananResponse = response.body()

                    layananResponse?.let { layanan ->
                        // Set data Fast Clean
                        tvFast.text = layanan.fast_clean.name
                        Glide.with(requireContext())
                            .load(layanan.fast_clean.picture) // Gambar URL
                            .apply(RequestOptions()
                                .placeholder(R.drawable.fastdeep)
                                .error(R.drawable.fastdeep))
                            .into(imgFast)

                        // Set data Deep Clean
                        tvDeep.text = layanan.deep_clean.name
                        Glide.with(requireContext())
                            .load(layanan.deep_clean.picture)
                            .apply(RequestOptions()
                                .placeholder(R.drawable.fastdeep)
                                .error(R.drawable.fastdeep))
                            .into(imgDeep)

                        // Set data Reglue
                        tvReglue.text = layanan.reglue.name
                        Glide.with(requireContext())
                            .load(layanan.reglue.picture)
                            .apply(RequestOptions()
                                .placeholder(R.drawable.reglue)
                                .error(R.drawable.reglue))
                            .into(imgReglue)

                        // Set data Recolor
                        tvRecolor.text = layanan.recolor.name
                        Glide.with(requireContext())
                            .load(layanan.recolor.picture)
                            .apply(
                                RequestOptions()
                                .placeholder(R.drawable.recolor)
                                .error(R.drawable.recolor))
                            .into(imgRecolor)
                    }
                } else {
                    showError("Gagal memuat data layanan")
                }
            }

            override fun onFailure(call: Call<LayananHomeResponse>, t: Throwable) {
                showError("Network error: ${t.message ?: "Unknown error"}")
            }
        })
    }

    private fun showError(message: String) {
        // Menampilkan pesan error jika ada masalah dengan request
        // Bisa menggunakan Toast, Snackbar, atau dialog sesuai kebutuhan
    }
}
