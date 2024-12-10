package com.example.pojokshoesapk

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants

class ActivityKeranjang : AppCompatActivity() {
    private lateinit var launcher:ActivityResultLauncher<Intent>
    private lateinit var recyclerView: RecyclerView
    private lateinit var keranjangAdapter: KeranjangAdapter
    private lateinit var backButton: ImageView
    private lateinit var checkoutButton: Button
    private lateinit var totalPembayaranTextView: TextView
    private var keranjangList: MutableList<ItemKeranjangs> = mutableListOf()
    private var detailKeranjangList: MutableList<DataDetailKeranjang> = mutableListOf()
    private lateinit var sessionManager: SessionManager
    private var currentKeranjangId: Int? = null // ID keranjang aktif

    companion object {
        private const val TAG = "ActivityKeranjang"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)


        setupMidtrans()

        // Inisialisasi View
        initializeViews()

        setupRecyclerView()
        sessionManager = SessionManager(this)
        val idPelanggans = sessionManager.getUserId()

        Log.d(TAG, "User ID: $idPelanggans")

        // Validasi keranjang untuk mendapatkan ID keranjang aktif
        if (idPelanggans != null) {
            validateOnlineKeranjang(
                pelangganId = idPelanggans,
                onResult = { keranjangId ->
                    Log.d(TAG, "Active Cart ID: $keranjangId")
                    currentKeranjangId = keranjangId
                    // Jika keranjang aktif ditemukan, muat detail keranjang
                    fetchKeranjangDetails(keranjangId)
                },
                onError = { error ->
                    // Tampilkan pesan error jika validasi gagal
                    Log.e(TAG, "Cart Validation Error: $error")
                    showError(error)
                }
            )
        } else {
            showError("Pengguna tidak teridentifikasi")
        }
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        backButton = findViewById(R.id.icbackkeranjang)
        checkoutButton = findViewById(R.id.btn_checkout)
        totalPembayaranTextView = findViewById(R.id.text_harga)

        // Tambahkan listener untuk tombol back
        backButton.setOnClickListener { onBackPressed() }

        // Tambahkan listener untuk tombol checkout
        checkoutButton.setOnClickListener {
            Log.d(TAG, "Checkout Button Clicked")
            Log.d(TAG, "Keranjang List Size: ${keranjangList.size}")
            Log.d(TAG, "Current Keranjang ID: $currentKeranjangId")
            Log.d(TAG, "Detail Keranjang List Size: ${detailKeranjangList.size}")

            // Pastikan keranjang tidak kosong sebelum checkout
            if (detailKeranjangList.isNotEmpty() && currentKeranjangId != null) {
                performCheckout(currentKeranjangId!!)
            } else {
                Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        keranjangAdapter = KeranjangAdapter(keranjangList, object : KeranjangAdapter.OnItemDeleteListener {
            override fun onItemDelete(position: Int) {
                // Tampilkan dialog konfirmasi sebelum menghapus
                showDeleteConfirmationDialog(position)
            }
        })
        recyclerView.adapter = keranjangAdapter
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Item")
            .setMessage("Apakah Anda yakin ingin menghapus item ini dari keranjang?")
            .setPositiveButton("Ya") { _, _ ->
                deleteKeranjangItemFromServer(position)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun updateAdapterWithDetails() {
        // Gabungkan data detail keranjang dengan keranjang utama
        val combinedList = detailKeranjangList.map { detail ->
            ItemKeranjangs(
                namaLayanan = detail.layanan_nama,
                jumlahSepatu = detail.jumlah_sepatu,
                harga = detail.detail_harga
            )
        }

        // Perbarui data adapter
        keranjangList.clear()
        keranjangList.addAll(combinedList)
        keranjangAdapter.notifyDataSetChanged()

        Log.d(TAG, "Updating Adapter: Combined List Size = ${combinedList.size}")

        // Perbarui tampilan total pembayaran
        updateTotalPembayaran()
    }

    private fun fetchKeranjangDetails(keranjangId: Int) {
        Log.d(TAG, "Fetching Cart Details for Keranjang ID: $keranjangId")
        RetrofitClient.apiService.getDetailKeranjang(keranjangId)
            .enqueue(object : Callback<KeranjangResponses> {
                override fun onResponse(call: Call<KeranjangResponses>, response: Response<KeranjangResponses>) {
                    Log.d(TAG, "Fetch Cart Details Response: ${response.isSuccessful}")
                    Log.d(TAG, "Response Body: ${response.body()}")

                    if (response.isSuccessful) {
                        val keranjangResponse = response.body()
                        if (keranjangResponse?.success == true) {
                            detailKeranjangList.clear() // Kosongkan data sebelumnya
                            detailKeranjangList.addAll(keranjangResponse.data) // Tambahkan data baru

                            Log.d(TAG, "Detail Keranjang List Size: ${detailKeranjangList.size}")

                            updateAdapterWithDetails() // Perbarui data adapter
                        } else {
                            showError("Gagal memuat detail keranjang.")
                        }
                    }
                }

                override fun onFailure(call: Call<KeranjangResponses>, t: Throwable) {
                    Log.e(TAG, "Fetch Cart Details Error: ${t.message}")
                    showError("Error: ${t.message}")
                }
            })
    }

    private fun validateOnlineKeranjang(
        pelangganId: Int,
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
                            Log.d("Keranjang", "Keranjang aktif ditemukan: ${activeKeranjang.keranjang_id}")
                            onResult(activeKeranjang.keranjang_id)
                        } else {
                            showError("Tidak ada keranjang aktif yang ditemukan.")
                        }
                    } else {
                        showError("Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<KeranjangItem>>, t: Throwable) {
                    onError("Error: ${t.message}")
                }
            })
    }

    private fun deleteKeranjangItemFromServer(position: Int) {
        if (position < 0 || position >= detailKeranjangList.size) {
            showError("Posisi item tidak valid")
            return
        }

        val itemToDelete = detailKeranjangList[position]
        val detailIdToDelete = itemToDelete.detail_id

        RetrofitClient.apiService.deleteDetailKeranjang(detailIdToDelete)
            .enqueue(object : Callback<DeleteResponse> {
                override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                    when {
                        response.isSuccessful -> {
                            // Hapus item dari list
                            detailKeranjangList.removeAt(position)
                            keranjangList.removeAt(position)

                            // Perbarui adapter
                           // keranjangAdapter.removeItem(position)
                            keranjangAdapter.notifyItemRemoved(position)

                            // Perbarui total pembayaran
                            updateTotalPembayaran()

                            Toast.makeText(
                                this@ActivityKeranjang,
                                "Item berhasil dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Log.e("DeleteItem", "Failed to delete: ${response.message()}")
                            Toast.makeText(
                                this@ActivityKeranjang,
                                "Gagal menghapus item",
                                Toast.LENGTH_SHORT
                            ).show()
                            refreshCart()
                        }
                    }
                }

                override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                    Log.e("DeleteItem", "Connection error: ${t.message}")
                    Toast.makeText(
                        this@ActivityKeranjang,
                        "Gagal menghapus item",
                        Toast.LENGTH_SHORT
                    ).show()
                    refreshCart()
                }
            })
    }


    private fun calculateTotalPembayaran(): Int {
        return detailKeranjangList.sumOf { it.detail_harga }
    }

    private fun updateTotalPembayaran() {
        val total = calculateTotalPembayaran()
        // Gunakan NumberFormat untuk memformat harga
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        totalPembayaranTextView.text = currencyFormatter.format(total)

        // Nonaktifkan tombol checkout jika keranjang kosong
        checkoutButton.isEnabled = keranjangList.isNotEmpty()
    }

    private fun refreshCart() {
        val idPelanggans = sessionManager.getUserId()
        idPelanggans?.let { pelangganId ->
            validateOnlineKeranjang(
                pelangganId = pelangganId,
                onResult = { keranjangId ->
                    fetchKeranjangDetails(keranjangId)
                },
                onError = { error ->
                    showError(error)
                }
            )
        }
    }

    private fun performCheckout(keranjangId: Int) {
        val checkoutRequest = mapOf("keranjang_id" to keranjangId)

        RetrofitClient.apiService.createCheckout(checkoutRequest)
            .enqueue(object : Callback<CheckoutResponse> {
                override fun onResponse(call: Call<CheckoutResponse>, response: Response<CheckoutResponse>) {
                    if (response.isSuccessful) {
                        val checkoutData = response.body()
                        checkoutData?.let {
                            Toast.makeText(
                                this@ActivityKeranjang,
                                "Checkout berhasil: ID ${it.checkout.checkout_id}, Status ${it.checkout.checkout_status}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("SNAP", "Snap token: ${it.snapToken}")

                            startPayment(it.snapToken)
                            // Reset keranjang setelah checkout
                            keranjangList.clear()
                            detailKeranjangList.clear()
                            keranjangAdapter.notifyDataSetChanged()
                            updateTotalPembayaran()
                        }
                    } else {
                        null
                    }
                }

                override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                    showError("Terjadi error saat checkout: ${t.message}")
                }
            })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    fun setupMidtrans() {
        UiKitApi.Builder()
            .withMerchantClientKey("SB-Mid-client-rTMPU8glYQjPkdDQ") // Client key dari Midtrans
            .withContext(this) // Konteks aktivitas
            .withMerchantUrl("https://45e4-2001-448a-4040-9ce4-3029-1922-5aa6-9303.ngrok-free.app/api/midtrans/callback/") // Ganti dengan URL server untuk menangani callback
            .enableLog(true) // Aktifkan log SDK
            .withColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // Tema warna
            .build()

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result?.resultCode == RESULT_OK) {
                    result.data?.let {
                        val transactionResult =
                            it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                        when (transactionResult?.status) {
                            UiKitConstants.STATUS_SUCCESS -> {
                                Toast.makeText(this, "Transaksi Berhasil", Toast.LENGTH_SHORT)
                                    .show()
                                navigateToHome()
                            }

                            UiKitConstants.STATUS_PENDING -> {
                                Toast.makeText(this, "Transaksi Pending", Toast.LENGTH_SHORT).show()
                                navigateToHome()
                            }

                            UiKitConstants.STATUS_FAILED -> {
                                Toast.makeText(this, "Transaksi Gagal", Toast.LENGTH_SHORT).show()
                                navigateToHome()
                            }

                            UiKitConstants.STATUS_CANCELED -> {
                                Toast.makeText(this, "Transaksi Dibatalkan", Toast.LENGTH_SHORT)
                                    .show()
                                navigateToHome()
                            }

                            else -> {
                                Toast.makeText(
                                    this,
                                    "Status Transaksi Tidak Dikenal",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navigateToHome()
                            }
                        }
                    }
                } else {
                    // Handle cases where result is not OK
                    Toast.makeText(this, "Transaksi Tidak Selesai", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
            }

    }

    private fun navigateToHome() {
        // Start ActivityMain and pass the open_fragment key to open the Home fragment
        val intent = Intent(this, ActivityMain::class.java).apply {
            putExtra("open_fragment", "home")
        }
        startActivity(intent)
        finish() // Optionally close this activity if you want to go back to the home screen
    }


    private fun startPayment(snapToken: String) {
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            this@ActivityKeranjang, // Aktivitas
            launcher, // ActivityResultLauncher
            snapToken // Snap Token dari Midtrans
        )
    }
}