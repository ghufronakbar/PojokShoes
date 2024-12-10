package com.example.pojokshoesapk

import android.os.Parcel
import android.os.Parcelable

data class KeranjangItem(
    val keranjang_id: Int,
    val namaLayanan: String,  // Service name
    val jumlahSepatu: Int,    // Quantity
    val harga: Int,           // Price
    val keranjang_status: String,          // Image resource
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",  // Read String for service name
        parcel.readInt(),           // Read Int for quantity
        parcel.readInt(),           // Read Int for price
        parcel.readString() ?: ""            // Read Int for image resource
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(keranjang_id)
        parcel.writeString(namaLayanan)   // Write service name to Parcel
        parcel.writeInt(jumlahSepatu)     // Write quantity to Parcel
        parcel.writeInt(harga)            // Write price to Parcel
        parcel.writeString(keranjang_status)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<KeranjangItem> {
        override fun createFromParcel(parcel: Parcel): KeranjangItem = KeranjangItem(parcel)
        override fun newArray(size: Int): Array<KeranjangItem?> = arrayOfNulls(size)
    }
}
data class UpdateKeranjangTotalHargaData(
    val keranjang_jumlah_harga: Int
)

data class UpdateKeranjangResponse(
    val success: Boolean,
    val message: String
)

