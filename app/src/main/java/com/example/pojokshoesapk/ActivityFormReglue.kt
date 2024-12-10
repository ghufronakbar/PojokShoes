package com.example.pojokshoesapk

import android.os.Bundle
import SessionManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pojokshoesapk.databinding.ActivityFormReglueBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ActivityFormReglue : AppCompatActivity(), FormPesanAdapter.OnQuantityChangeListener {
    private lateinit var binding: ActivityFormReglueBinding
    private val TAG = "ActivityFormRglue"
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormReglueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupViews()
        fetchLayananData()
    }

    private fun setupViews() {
        binding.icbackformreglue.setOnClickListener {
            finish()
        }

        binding.btnAddToCart.setOnClickListener {
            val adapter = binding.listViewPemesanan.adapter as? FormPesanAdapter
            val selectedServices = adapter?.getSelectedItems() ?: emptyList()

            when {
                selectedServices.isEmpty() -> showError("Pilih layanan terlebih dahulu!")
                !validateServices(selectedServices) -> showError("Jumlah sepatu tidak valid!")
                else -> postKeranjang(selectedServices)
            }
        }
    }

    private fun validateServices(services: List<DataListLayanan>): Boolean {
        return services.all { it.quantity >= 0 } && services.any { it.quantity > 0 }
    }

    private fun fetchLayananData() {
        RetrofitClient.apiService.getLayananList().enqueue(object : Callback<List<DataListLayanan>> {
            override fun onResponse(call: Call<List<DataListLayanan>>, response: Response<List<DataListLayanan>>) {
                if (response.isSuccessful) {
                    val layananList = response.body() ?: listOf()
                    val reglueList = layananList.filter { it.layanan_nama.contains("Reglue", ignoreCase = true) }
                    reglueList.forEach { it.quantity = 0 }
                    if (reglueList.isNotEmpty()) {
                        setupListView(reglueList)
                    } else {
                        showError("Tidak ada layanan Reglue tersedia")
                    }
                } else {
                    showError("Gagal memuat data layanan")
                }
            }

            override fun onFailure(call: Call<List<DataListLayanan>>, t: Throwable) {
                showError("Network error: ${t.message ?: "Unknown error"}")
            }
        })
    }

    private fun setupListView(layananList: List<DataListLayanan>) {
        val adapter = FormPesanAdapter(this, layananList, this)
        binding.listViewPemesanan.adapter = adapter
    }

    override fun onQuantityChanged(total: Int) {
        binding.textPrice.text = "Rp ${formatPrice(total)}"
    }

    private fun formatPrice(price: Int): String {
        return String.format("%,d", price).replace(",", ".")
    }

    private fun updateKeranjangTotalHarga(keranjangId: Int) {
        RetrofitClient.apiService.getDetailKeranjangByKeranjangId(keranjangId)
            .enqueue(object : Callback<KeranjangKeranjangResponse> {
                override fun onResponse(
                    call: Call<KeranjangKeranjangResponse>,
                    response: Response<KeranjangKeranjangResponse>
                ) {
                    if (response.isSuccessful) {
                        val detailResponse = response.body()
                        if (detailResponse?.success == true) {
                            val detailList = detailResponse.data
                            val totalHarga = detailList.sumOf { it.detail_harga }
                            updateKeranjangTotalHargaAPI(
                                keranjangId,
                                UpdateKeranjangTotalHargaData(keranjang_jumlah_harga = totalHarga)
                            )
                        } else {
                            showError("Gagal mendapatkan detail keranjang: Respons tidak valid")
                        }
                    } else {
                        showError("Gagal mendapatkan detail keranjang: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<KeranjangKeranjangResponse>, t: Throwable) {
                    showError("Error mendapatkan detail keranjang: ${t.message}")
                }
            })
    }

    private fun updateKeranjangTotalHargaAPI(keranjangId: Int, updateData: UpdateKeranjangTotalHargaData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updateResponse = RetrofitClient.apiService.updateKeranjangTotalHarga(keranjangId, updateData)
                withContext(Dispatchers.Main) {
                    if (updateResponse.isSuccessful) {
                        showSuccess("Total harga keranjang berhasil diperbarui!")
                    } else {
                        showError("Gagal memperbarui total harga: ${updateResponse.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Error saat memperbarui total harga: ${e.message}")
                }
            }
        }
    }

    private fun validateOrCreateKeranjang(
        pelangganId: Int,
        totalHarga: Int,
        onResult: (keranjangId: Int) -> Unit,
        onError: (error: String) -> Unit
    ) {
        RetrofitClient.apiService.getKeranjangByPelanggan(pelangganId)
            .enqueue(object : Callback<List<KeranjangItem>> {
                override fun onResponse(call: Call<List<KeranjangItem>>, response: Response<List<KeranjangItem>>) {
                    if (response.isSuccessful) {
                        val keranjangList = response.body() ?: emptyList()
                        val activeKeranjang = keranjangList.find { it.keranjang_status == "1" }
                        if (activeKeranjang != null) {
                            onResult(activeKeranjang.keranjang_id)
                        } else {
                            createKeranjang(pelangganId, totalHarga, onResult, onError)
                        }
                    } else {
                        createKeranjang(pelangganId, totalHarga, onResult, onError)
                    }
                }

                override fun onFailure(call: Call<List<KeranjangItem>>, t: Throwable) {
                    onError("Error: ${t.message}")
                }
            })
    }

    private fun createKeranjang(
        pelangganId: Int,
        totalHarga: Int,
        onResult: (keranjangId: Int) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val currentDate = getCurrentDate()
        val newKeranjang = KeranjangData(
            pelanggan_id = pelangganId,
            keranjang_tanggal = currentDate,
            keranjang_jumlah_harga = totalHarga
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.createKeranjang(newKeranjang)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val createdKeranjang = response.body()
                        if (createdKeranjang != null) {
                            onResult(createdKeranjang.keranjang_id)
                        } else {
                            onError("Gagal membuat keranjang baru: Response kosong")
                        }
                    } else {
                        onError("Gagal membuat keranjang baru: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error: ${e.message}")
                }
            }
        }
    }

    private fun postKeranjang(layananList: List<DataListLayanan>) {
        val totalHarga = calculateTotalHarga(layananList)

        if (totalHarga <= 0) {
            showError("Total harga tidak valid")
            return
        }

        val pelangganId = sessionManager.getUserId()
        if (pelangganId != null) {
            validateOrCreateKeranjang(
                pelangganId,
                totalHarga,
                onResult = { keranjangId ->
                    postDetailKeranjang(keranjangId, layananList)
                },
                onError = { error ->
                    showError(error)
                }
            )
        }
    }

    private fun calculateTotalHarga(layananList: List<DataListLayanan>): Int {
        return layananList.sumOf { it.quantity * it.layanan_harga }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun postDetailKeranjang(keranjangId: Int, layananList: List<DataListLayanan>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val layananItems = layananList.map { layanan ->
                    LayananItem(
                        layanan_id = layanan.layanan_id,
                        jumlah_sepatu = layanan.quantity,
                        detail_harga = layanan.quantity * layanan.layanan_harga
                    )
                }

                val postDetailKeranjangData = PostDetailKeranjang(
                    keranjang_id = keranjangId,
                    layanan = layananItems,
                    detail_harga = layananItems.sumOf { it.detail_harga }
                )

                val response = RetrofitClient.apiService.addKeranjangDetail(postDetailKeranjangData)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showSuccess("Berhasil menambahkan semua detail ke keranjang")
                        updateKeranjangTotalHarga(keranjangId)
                        finish()
                    } else {
                        showError("Gagal menambahkan detail keranjang: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Error: ${e.message}")
                }
            }
        }
    }

    private fun handleErrorResponse(response: Response<*>, baseErrorMessage: String) {
        val errorBody = response.errorBody()?.string()
        showError("$baseErrorMessage (${response.code()}): ${errorBody ?: "Unknown error"}")
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e(TAG, message)
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.d(TAG, message)
    }
}
