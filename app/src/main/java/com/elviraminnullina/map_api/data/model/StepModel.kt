package com.elviraminnullina.map_api.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StepModel(
    val distance: TextValueModel,
    val duration: TextValueModel,
    val end_location: CoordinationModel,
    val html_instructions: String,
    val polyline: PolylineModel,
    val start_location: CoordinationModel,
    val travel_mode: String
): Parcelable