package com.example.pojokshoesapk

//data class CheckoutData(
//    val checkout_id: Int,
//    val keranjang_id: Int,
//    val checkout_status: String,
//    val checkout_waktu: String
//)
data class CheckoutResponse(
    val message: String,
    val checkout: CheckoutData,
    val snapToken: String
)

data class CheckoutData(
    val checkout_waktu: String,
    val checkout_status: String,
    val checkout_id: Int,
    val keranjang_id: Int
)