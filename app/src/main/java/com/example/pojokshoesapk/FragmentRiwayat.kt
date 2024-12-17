package com.example.pojokshoesapk

import SessionManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentRiwayat : Fragment() {
    private var token: String? = null
    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerViewRiwayat: RecyclerView
    private lateinit var lnNoHistory: LinearLayout
    private lateinit var historyAdapter: HistoryAdapter
    private var historyList: List<History> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        sessionManager = SessionManager(requireContext())
        token = sessionManager.getToken()

        recyclerViewRiwayat = view.findViewById(R.id.recyclerViewRiwayat)
        lnNoHistory = view.findViewById(R.id.lnNoHistory)
        recyclerViewRiwayat.layoutManager = LinearLayoutManager(requireContext())

        fetchLayananData()

        return view
    }

    private fun fetchLayananData() {
        if (token != null) {
            RetrofitClient.apiService.getAllHistories("Bearer $token").enqueue(object : Callback<List<History>> {
                override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                    if (response.isSuccessful) {
                        val historyResponse = response.body()

                        historyResponse?.let { history ->
                            // Check if history is empty and update the UI
                            historyList = history
                            updateUI()
                        }
                    } else {
                        showError("Gagal memuat data riwayat")
                    }
                }

                override fun onFailure(call: Call<List<History>>, t: Throwable) {
                    showError("Network error: ${t.message ?: "Unknown error"}")
                }
            })
        }
    }

    private fun updateUI() {
        // If there are no history records, show the "No History" message
        if (historyList.isEmpty()) {
            lnNoHistory.visibility = View.VISIBLE
            recyclerViewRiwayat.visibility = View.GONE
        } else {
            // Show RecyclerView with data
            lnNoHistory.visibility = View.GONE
            recyclerViewRiwayat.visibility = View.VISIBLE
            // Set the adapter with the fetched data
            historyAdapter = HistoryAdapter(historyList)
            recyclerViewRiwayat.adapter = historyAdapter
        }
    }

    private fun showError(message: String) {
        // Menampilkan pesan error jika ada masalah dengan request
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
