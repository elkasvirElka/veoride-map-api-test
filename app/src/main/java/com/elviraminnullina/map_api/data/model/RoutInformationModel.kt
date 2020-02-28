package com.elviraminnullina.map_api.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoutInformationModel(
    val distance: TextValueModel,
    val duration: TextValueModel,
    val end_address: String,
    val end_location: CoordinationModel,
    val start_address: String,
    val start_location: CoordinationModel,
    val steps: ArrayList<StepModel>
//traffic_speed_entry
//via_waypoint
) : Parcelable