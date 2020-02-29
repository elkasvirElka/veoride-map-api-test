package com.elviraminnullina.map_api.utils

class DoubleUtils {
    companion object {
        fun round(number: Double?): Double =
            if (number != null) Math.round(number * 1000.0) / 1000.0 else 0.0
    }
}