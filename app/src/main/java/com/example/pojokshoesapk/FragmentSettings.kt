package com.example.pojokshoesapk

import android.content.Intent
import SessionManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class FragmentSettings : Fragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sessionManager = SessionManager(requireContext())

        val btnKeluar: Button = view.findViewById(R.id.btnKeluar)

        // Set listener untuk tombol keluar
        btnKeluar.setOnClickListener {
            logout()
        }

        return view
    }

    // Fungsi logout untuk membersihkan sesi dan mengarahkan pengguna ke halaman login
    private fun logout() {
        // Bersihkan data sesi
        sessionManager.clearSessionData()

        // Pindah ke Activity Login
        val intent = Intent(activity, ActivityLogin::class.java)
        startActivity(intent)
        activity?.finish()  // Menutup FragmentSettings agar pengguna tidak bisa kembali ke halaman ini
    }
}
