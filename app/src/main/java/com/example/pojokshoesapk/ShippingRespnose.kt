package com.example.pojokshoesapk

data class ShippingRespnose(
    val distance : Double,
    val normalized_distance : Int,
    val shipping_cost : Int,
    val currency : String
)
