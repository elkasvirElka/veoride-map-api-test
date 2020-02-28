package com.elviraminnullina.map_api.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeocodedWaypointsModel(
    val geocoder_status: String,
    val place_id: String,
    val types: ArrayList<String>
): Parcelable