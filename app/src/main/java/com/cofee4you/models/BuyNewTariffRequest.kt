package com.cofee4you.models

import com.cofee4you.activities.TariffsActivity

import android.os.Parcel
import android.os.Parcelable

data class BuyNewTariffRequest(
    val userPhoneNumber: String,
    val tariffType: TariffType
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        TariffType.values()[parcel.readInt()]
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userPhoneNumber)
        parcel.writeInt(tariffType.ordinal)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BuyNewTariffRequest> {
        override fun createFromParcel(parcel: Parcel): BuyNewTariffRequest {
            return BuyNewTariffRequest(parcel)
        }

        override fun newArray(size: Int): Array<BuyNewTariffRequest?> {
            return arrayOfNulls(size)
        }
    }
}
