package com.elviraminnullina.map_api.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteModel(
    val bounds: BoundsModel,
    val copyrights: String,
    val legs: ArrayList<RoutInformationModel>,
    val overview_polyline: PolylineModel,
    val summary: String
    // "warnings": [],
//"waypoint_order": [],

): Parcelable