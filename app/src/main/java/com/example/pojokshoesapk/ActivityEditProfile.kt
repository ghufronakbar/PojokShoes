package com.example.pojokshoesapk

import SessionManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ActivityEditProfile : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private var token: String? = null

    private lateinit var et_nama: EditText
    private lateinit var et_email: EditText
    private lateinit var et_nomor: EditText
    private lateinit var et_alamat: EditText
    private lateinit var btn_simpanprofile: Button
    private lateinit var btn_gantipassword: Button
    private lateinit var editPicture: ImageView
    var isImageFromResponse:Boolean = false

    private var currentImageUri: Uri? = null

    // Izin yang diperlukan
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val pickImageFromGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                handleImagePicked(it)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && currentImageUri != null) {
                handleImagePicked(currentImageUri!!)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        sessionManager = SessionManager(this)
        token = sessionManager.getToken()

        et_alamat = findViewById(R.id.et_alamat)
        et_email = findViewById(R.id.et_email)
        et_nomor = findViewById(R.id.et_nomor)
        et_nama = findViewById(R.id.et_nama)

        btn_simpanprofile = findViewById(R.id.btn_simpanprofile)
        btn_simpanprofile.setOnClickListener { handleUpdateProfile() }

        btn_gantipassword = findViewById(R.id.btn_gantipassword)
        btn_gantipassword.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            startActivity(intent)
        }

        editPicture = findViewById(R.id.editPicture)
        editPicture.setOnClickListener {
            onClickImage()
        }

        displayUserData()
    }

    private fun compressImage(uri: Uri): File {
        val originalBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))

        // Mengambil ukuran baru yang lebih kecil
        val maxSize = 1024 // Ukuran maksimal dalam pixel
        val ratio = maxSize.toFloat() / Math.max(originalBitmap.width, originalBitmap.height)

        // Menghitung ukuran baru dan melakukan kompresi
        val newWidth = (originalBitmap.width * ratio).toInt()
        val newHeight = (originalBitmap.height * ratio).toInt()

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false)

        // Crop gambar menjadi rasio 1:1
        val croppedBitmap = cropToSquare(resizedBitmap)

        // Simpan gambar hasil kompresi dan crop
        val file = File(cacheDir, "profile_picture_compressed.jpg")
        val outputStream = FileOutputStream(file)
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.flush()
        outputStream.close()

        return file
    }


    // Fungsi untuk crop gambar menjadi 1:1
    private fun cropToSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Tentukan ukuran sisi terkecil
        val size = Math.min(width, height)
        val xOffset = (width - size) / 2
        val yOffset = (height - size) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }



    private fun onClickImage() {
        // Cek apakah gambar sudah ada, jika ada, tampilkan opsi untuk menghapus atau mengubah gambar
        val options = if (isImageFromResponse) {
            arrayOf("Ambil Gambar", "Pilih dari Galeri", "Hapus Gambar")
        } else {
            arrayOf("Ambil Gambar", "Pilih dari Galeri")
        }

        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> takePhoto() // Ambil gambar dari kamera
                1 -> pickImageFromGallery() // Pilih gambar dari galeri
                2 -> deleteImage() // Hapus gambar jika sudah ada
            }
        }
        builder.show()
    }

    private fun takePhoto() {
        if (checkPermissions()) {
            val imageFile = File(cacheDir, "temp_image.jpg")
            currentImageUri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
            takePictureLauncher.launch(currentImageUri)
        } else {
            requestPermissions()
        }
    }

    private fun pickImageFromGallery() {
        pickImageFromGalleryLauncher.launch("image/*")
    }

    private fun deleteImage() {
        // Tampilkan konfirmasi untuk menghapus gambar
        AlertDialog.Builder(this)
            .setTitle("Hapus Gambar")
            .setMessage("Apakah Anda yakin ingin menghapus gambar profil?")
            .setPositiveButton("Ya") { _, _ ->
                // Hapus gambar dari server
                if (token != null) {
                    RetrofitClient.apiService.deletePicture("Bearer $token").enqueue(object : Callback<AccountResponse> {
                        override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                            if (response.isSuccessful) {
                                // Reset gambar di UI
                                Glide.with(this@ActivityEditProfile)
                                    .load(R.drawable.profile) // Gambar default
                                    .apply(RequestOptions().placeholder(R.drawable.profile).error(R.drawable.profile))
                                    .into(editPicture)
                                currentImageUri = null
                                Toast.makeText(this@ActivityEditProfile, "Berhasil menghapus gambar", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@ActivityEditProfile, "Gagal menghapus gambar", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                            Log.e("ActivityEditProfile", "Error deleting picture: ${t.message}", t)
                            Toast.makeText(this@ActivityEditProfile, "Terjadi kesalahan saat menghapus gambar", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun handleImagePicked(uri: Uri) {
        val compressedFile = compressImage(uri) // Kompresi dan crop gambar
        currentImageUri = compressedFile.toUri()
        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val requestBody: RequestBody = RequestBody.create(mediaType, compressedFile)
        val part = MultipartBody.Part.createFormData("picture", compressedFile.name, requestBody)

        // Upload gambar ke server
        postPicture(part)
    }

    private fun postPicture(file: MultipartBody.Part) {
        if (token != null) {
            RetrofitClient.apiService.postPicture("Bearer $token", file).enqueue(object : Callback<AccountResponse> {
                override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ActivityEditProfile, "Berhasil mengunggah gambar", Toast.LENGTH_SHORT).show()
                        Glide.with(this@ActivityEditProfile)
                            .load(currentImageUri)
                            .apply(RequestOptions().placeholder(R.drawable.profile).error(R.drawable.profile))
                            .into(editPicture)
                    } else {
                        Toast.makeText(this@ActivityEditProfile, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                    Log.e("ActivityEditProfile", "Error uploading picture: ${t.message}", t)
                    Toast.makeText(this@ActivityEditProfile, "Terjadi kesalahan saat mengunggah gambar", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun checkPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions, 100)
    }

    private fun displayUserData() {
        if (token != null) {
            RetrofitClient.apiService.getProfile("Bearer $token").enqueue(object : Callback<AccountResponse> {
                override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                    if (response.isSuccessful) {
                        val accountResponse = response.body()
                        accountResponse?.let {
                            et_nama.setText(it.pelanggan_nama)
                            et_email.setText(it.pelanggan_email)
                            et_nomor.setText(it.pelanggan_nomor)
                            et_alamat.setText(it.pelanggan_alamat)

                            if(it.pelanggan_picture != null) {
                                isImageFromResponse = true
                            }

                            Glide.with(this@ActivityEditProfile)
                                .load(it.pelanggan_picture)
                                .apply(RequestOptions().placeholder(R.drawable.profile).error(R.drawable.profile))
                                .into(editPicture)
                        }
                    }
                }

                override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                    Log.e("ActivityEditProfile", "Error fetching profile: ${t.message}", t)
                    Toast.makeText(this@ActivityEditProfile, "Terjadi kesalahan saat mengambil data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun handleUpdateProfile() {
        val updatedNama = et_nama.text.toString()
        val updatedEmail = et_email.text.toString()
        val updatedNomor = et_nomor.text.toString()
        val updatedAlamat = et_alamat.text.toString()

        if (updatedNama.isEmpty() || updatedEmail.isEmpty() || updatedNomor.isEmpty() || updatedAlamat.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = ProfileData(updatedNama, updatedEmail, updatedNomor, updatedAlamat)

        RetrofitClient.apiService.updateProfile("Bearer $token", requestBody).enqueue(object : Callback<AccountResponse> {
            override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                val accountResponse = response.body()
                if (response.isSuccessful && accountResponse?.success == true) {
                    Toast.makeText(this@ActivityEditProfile, "Berhasil memperbarui profile", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ActivityEditProfile, accountResponse?.message ?: "Gagal memperbarui profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                Toast.makeText(this@ActivityEditProfile, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
