package com.elviraminnullina.map_api.navigation

import android.os.Bundle
import androidx.annotation.IdRes

data class NavigationEvent(@IdRes val navId: Int, val navigationArguments: NavigationArguments? = null) {
    /**
     * Function to convert from NavigationArguments back to an actual Bundle object.
     */
    fun argumentsBundle(): Bundle? = navigationArguments?.asBundle()
}
