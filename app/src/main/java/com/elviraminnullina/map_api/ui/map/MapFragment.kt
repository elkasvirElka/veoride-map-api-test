package com.elviraminnullina.map_api.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.elviraminnullina.map_api.BaseFragment
import com.elviraminnullina.map_api.MainActivity
import com.elviraminnullina.map_api.MyApplication
import com.elviraminnullina.map_api.R
import com.elviraminnullina.map_api.data.model.StepModel
import com.elviraminnullina.map_api.navigation.NavigationArguments
import com.elviraminnullina.map_api.navigation.NavigationEvent
import com.elviraminnullina.map_api.save_state_factory.InjectingSavedStateViewModelFactory
import com.elviraminnullina.map_api.utils.PermissionUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.PolyUtil
import java.util.*
import javax.inject.Inject

const val TRAVEL_TIME = "TRAVEL_TIME"

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MapFragment : BaseFragment(),
    OnMapReadyCallback {
    @Inject
    lateinit var abstractFactory: InjectingSavedStateViewModelFactory
    lateinit var mViewModel: MapViewModel
    lateinit var start: Button
    lateinit var mChronometer: Chronometer
    private var mMap: GoogleMap? = null
    private var mCurrLocationMarker: Marker? = null
    private var destinationMarker: Marker? = null

    //TODO move to VM
    private var mLastLocation: Location = Location("")


    //TODO move to viewModel
    @Volatile
    var polylines: ArrayList<Polyline> = ArrayList()

    override val layoutResourceId: Int = R.layout.fragment_map

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLocationPermission()
        MyApplication.getApp(activity).getAppComponent().createMainComponent()
            .injectMapFragment(this)

        val factory = abstractFactory.create(this, null)
        // get the ViewModel with the factory and scope you want
        mViewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.googleMap) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        mChronometer = view.findViewById(R.id.chronometer)
        start = view.findViewById(R.id.start)
        start.setOnClickListener(onStartClickListener())
        view.findViewById<Button>(R.id.stop).setOnClickListener(stopTraveling())



        if (mViewModel.chronoProgress.value == true) {
            startChronometer()
        }

        mViewModel.currentLocation.observe(viewLifecycleOwner, Observer {
            it?.let {
                mViewModel.getDirection()
            }
        })
        mViewModel.destinationLocation.observe(viewLifecycleOwner, Observer {
            it?.let {
                mViewModel.getDirection()
                val latLng = LatLng(it.lat, it.lng)
                mViewModel.setDestinationMarkerOptions(latLng)
            }
        })
        mViewModel.destinationMarkerOptions.observe(viewLifecycleOwner, Observer {
            it?.let {
                destinationMarker = mMap?.addMarker(it)
            }
        })
        mViewModel.response.observe(viewLifecycleOwner, Observer {
            it?.let {
                //Draw all roads and give opportunely to choose
                it.routes.firstOrNull()?.legs?.firstOrNull()?.let { leg ->
                    displayDirection(leg.steps)
                    start.visibility = View.VISIBLE
                }
            }
        })
        mViewModel.showSpinner.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                showSpinner()
            } else {
                hideSpinner()
            }
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        MapsInitializer.initialize(Objects.requireNonNull(context));
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true

        val locationManager =
            ContextCompat.getSystemService<LocationManager>(
                requireContext(),
                LocationManager::class.java
            )

        val criteria = Criteria()
        val location =
            locationManager?.getLastKnownLocation(
                locationManager.getBestProvider(criteria, false) ?: ""
            )
        location?.apply {
            mViewModel.setCurrentLocation(latitude, longitude)
        }
        /*
         location?.let {
             CameraUpdateFactory.newLatLngZoom(
                 LatLng(location.latitude, location.longitude),
                 13.toFloat()
             )
         }*/
        mMap = googleMap
        mMap?.setOnMapLongClickListener(onMapLongClickListener())
        mViewModel.destinationMarkerOptions.value?.let {
            destinationMarker = mMap?.addMarker(it)
        }
    }

    private fun displayDirection(directionsList: ArrayList<StepModel>) {

        for (item in polylines) {
            item.remove()
        }
        polylines.clear()

        val count = directionsList.size
        mMap?.apply {
            directionsList.forEach { it ->
                val options = PolylineOptions()
                options.color(Color.BLUE)
                options.width(10F)
                options.addAll(PolyUtil.decode(it.polyline.points))
                polylines.add(addPolyline(options))
            }
        }
    }


    private fun checkLocationPermission(): Boolean =
        PermissionUtils.checkLocationPermission(requireActivity(), requireContext())

    private fun onMapLongClickListener() =
        GoogleMap.OnMapLongClickListener { item ->
            destinationMarker?.apply {
                remove()
            }
            mViewModel.setDestinationLocation(item.latitude, item.longitude)
        }

    private fun onStartClickListener() = View.OnClickListener {
        startChronometer()
    }

    private fun stopTraveling() = View.OnClickListener {
        mViewModel.chronoProgress.value = false
        mViewModel.setTravelTime(mChronometer.base)
        mViewModel.setChronoTimeToNull()
        mChronometer.stop()


        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.wanna_see_route_info))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val arg = NavigationArguments.create {
                    (activity as? MainActivity)?.stopService()
                    mViewModel.travelTime.value?.let {
                        putLong(TRAVEL_TIME, it)
                    }
                }
                navigate(NavigationEvent(R.id.map_fragment_to_travel_info_fragment, arg))
            }.setNegativeButton(getString(R.string.no)) { _, _ ->
                val db = MyApplication.getInstance()?.getDatabase()
                db?.coordinationDataBase()?.deleteAll()
            }
            .create().show()
    }

    private fun startChronometer() {
        mViewModel.chronoProgress.value = true
        mViewModel.setTravelTimeToNull()
        mChronometer.base = mViewModel.chronoTime.value ?: SystemClock.elapsedRealtime()
        mChronometer.start()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mViewModel.setChronoTime(mChronometer.base)
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}