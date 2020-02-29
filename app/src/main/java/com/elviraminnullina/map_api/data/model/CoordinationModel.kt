package com.elviraminnullina.map_api.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CoordinationModel(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
):Parcelable