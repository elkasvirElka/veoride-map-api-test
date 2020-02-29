package com.elviraminnullina.map_api.component

import com.elviraminnullina.map_api.MainActivity
import com.elviraminnullina.map_api.ui.map.MapFragment
import dagger.Subcomponent

@Subcomponent(modules = [])
interface MainComponent {

    fun injectMainActivity(activity: MainActivity)

    fun injectMapFragment(fragment: MapFragment)
}