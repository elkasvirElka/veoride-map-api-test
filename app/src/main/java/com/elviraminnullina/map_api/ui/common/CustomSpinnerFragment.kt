package com.elviraminnullina.map_api.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.elviraminnullina.map_api.R


class CustomSpinnerFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_spinner, container, false).apply {
            setStyle(
                STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen
            )
        }


    companion object {
        const val UNIQUE_TAG = "Unique CustomProgress"

        fun newInstance() =
            CustomSpinnerFragment()
    }
}
