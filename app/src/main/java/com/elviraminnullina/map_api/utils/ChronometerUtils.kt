package com.elviraminnullina.map_api.utils

import android.os.SystemClock
import android.widget.Chronometer

class ChronometerUtils {
    companion object {


        private fun addZeroIfLessThanTen(number: Int) =
            (if (number < 10) "0$number" else number.toString())

        fun getTimeFromChrono(mChronometer: Chronometer): String {
            val time = SystemClock.elapsedRealtime() - mChronometer.base
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            return addZeroIfLessThanTen(h).plus(":")
                .plus(addZeroIfLessThanTen(m)).plus(":")
                .plus(addZeroIfLessThanTen(s))
        }
    }
}