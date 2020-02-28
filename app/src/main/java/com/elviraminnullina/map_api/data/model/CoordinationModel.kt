package com.elviraminnullina.map_api.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CoordinationModel(
    val lat: Double = 0.0,
    val lng: Double = 0.0
):Parcelable