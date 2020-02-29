package com.elviraminnullina.map_api.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoundsModel(
    val northeast: CoordinationModel,
    val southwest: CoordinationModel
) : Parcelable