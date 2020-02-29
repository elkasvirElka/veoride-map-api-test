package com.elviraminnullina.map_api.ui.travel_info

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.elviraminnullina.map_api.BaseFragment
import com.elviraminnullina.map_api.MyApplication
import com.elviraminnullina.map_api.R
import com.elviraminnullina.map_api.data.model.CoordinationDatabaseModel
import com.elviraminnullina.map_api.save_state_factory.InjectingSavedStateViewModelFactory
import com.elviraminnullina.map_api.ui.map.TRAVEL_TIME
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import java.util.*
import javax.inject.Inject


class TravelInformationFragment : BaseFragment(),
    OnMapReadyCallback {

    @Inject
    lateinit var abstractFactory: InjectingSavedStateViewModelFactory
    lateinit var viewModel: TravelInformationViewModel
    private var mMap: GoogleMap? = null

    override val layoutResourceId: Int = R.layout.fragment_travel_indormation
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.getApp(activity).getAppComponent().createMainComponent()
            .injectTravelInfoFragment(this)

        val factory = abstractFactory.create(this, null)
        // get the ViewModel with the factory and scope you want
        viewModel = ViewModelProvider(this, factory)[TravelInformationViewModel::class.java]

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.travel_info_googleMap) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        //pass thru ViewModel by Activity
        arguments?.getString(TRAVEL_TIME)?.let {
            view.findViewById<TextView>(R.id.travel_info_time).apply {
                text = it
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        val db = MyApplication.getInstance()?.getDatabase()
        val allCoordinates = db?.coordinationDataBase()?.getAll()
        MapsInitializer.initialize(Objects.requireNonNull(context));
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true


        mMap = googleMap

        allCoordinates?.firstOrNull()?.apply {
            mMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(lat, lng),
                    13.toFloat()
                )
            )
        }
        allCoordinates?.let {
            displayDirection(it)
        }
    }

    private fun displayDirection(directionsList: List<CoordinationDatabaseModel>) {
        mMap?.apply {
            val options = PolylineOptions()
            options.color(Color.BLUE)
            options.width(10F)

            directionsList.forEach { it ->
                options.add(LatLng(it.lat, it.lng))
            }
            addPolyline(options)
        }
    }

}