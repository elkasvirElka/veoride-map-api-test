package com.elviraminnullina.map_api.component

import com.elviraminnullina.map_api.MainActivity
import com.elviraminnullina.map_api.ui.map.MapFragment
import com.elviraminnullina.map_api.ui.travel_info.TravelInformationFragment
import dagger.Subcomponent

@Subcomponent(modules = [])
interface MainComponent {

    fun injectMainActivity(activity: MainActivity)

    fun injectMapFragment(fragment: MapFragment)

    fun injectTravelInfoFragment(fragment: TravelInformationFragment)
}