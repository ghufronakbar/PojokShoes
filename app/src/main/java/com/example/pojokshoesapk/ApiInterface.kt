package com.example.pojokshoesapk

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("layanan/all")
    fun getLayananList(): Call<List<DataListLayanan>>

    @GET("layanan/check")
    fun getLayananHome(): Call<LayananHomeResponse>

    @GET("history")
    fun getAllHistories(@Header("Authorization") token: String): Call<List<History>>

    @GET("keranjang/keranjang/{pelanggan_id}")
    fun getKeranjangByPelanggan(@Path("pelanggan_id") pelangganId: Int?): Call<List<KeranjangItem>>

    @GET("detailkeranjang/detail/{keranjang_id}")
    fun getDetailKeranjangByKeranjangId(
        @Path("keranjang_id") keranjangId: Int
    ): Call<KeranjangKeranjangResponse>

    @GET("detailkeranjang/detail/{keranjang_id}/detail")
    fun getDetailKeranjang(
        @Path("keranjang_id") keranjangId: Int
    ): Call<KeranjangResponses>

    @GET("checkout/{checkout_id}")
    fun getCheckoutDetail(@Path("checkout_id") checkoutId: Int): Call<CheckoutData>

    @DELETE("detailkeranjang/detail/{id}")
    fun deleteDetailKeranjang(@Path("id") detailId: Int): Call<DeleteResponse>

    @POST("login")
    fun login(@Body loginData: LoginData): Call<LoginResponse>

    @GET("profile")
    fun getProfile(@Header("Authorization") token: String): Call<AccountResponse>

    @PUT("profile")
    fun updateProfile(@Header("Authorization") token: String, @Body updateData: ProfileData): Call<AccountResponse>

    @PATCH("profile")
    fun updatePassword(@Header("Authorization") token: String, @Body updateData: ProfilePassword): Call<PasswordResponse>

    @DELETE("picture")
    fun deletePicture(@Header("Authorization") token: String): Call<AccountResponse>

    @Multipart
    @POST("picture")
    fun postPicture(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<AccountResponse>

    @GET("helper/ongkir")
    fun getShippingCost(@Header("Authorization") token: String): Call<ShippingRespnose>

    @POST("register")
    fun registerUser(@Body registerData: RegisterData): Call<AuthResponse>

    @POST("keranjang/keranjang")
    suspend fun createKeranjang(@Body keranjangData: KeranjangData): Response<TambahKeranjangResponse>

    @POST("detailkeranjang/detail")
    suspend fun addKeranjangDetail(@Body postDetailKeranjang: PostDetailKeranjang): Response<DetailKeranjangResponse>

    @POST("keranjang/keranjang/{keranjang_id}")
    suspend fun updateKeranjangTotalHarga(
        @Path("keranjang_id") keranjangId: Int,
        @Body updateData: UpdateKeranjangTotalHargaData
    ): Response<UpdateKeranjangResponse>

    @POST("checkout/checkout")
    fun createCheckout(
        @Body checkoutRequest: Map<String, Double>
    ): Call<CheckoutResponse>
}
