package com.elviraminnullina.map_api.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DirectionResponse(
    val geocoded_waypoints: ArrayList<GeocodedWaypointsModel> = ArrayList(),
    val routes: ArrayList<RouteModel> = ArrayList(),
    val status: String = ""
) : Parcelable