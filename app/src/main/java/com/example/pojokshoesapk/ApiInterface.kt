package com.example.pojokshoesapk

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ApiInterface {

    @GET("layanan/layanan")
    fun getLayananList(): Call<List<DataListLayanan>>

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

//    @POST("checkout/checkout")
//    suspend fun createCheckout(@Body keranjangId: Int): Response<CheckoutData>

    @POST("checkout/checkout")
    fun createCheckout(@Body checkoutRequest: Map<String, Int>): Call<CheckoutResponse>


}
