package com.elviraminnullina.map_api.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TextValueModel(
    val text: String,
    val value: Int
) : Parcelable