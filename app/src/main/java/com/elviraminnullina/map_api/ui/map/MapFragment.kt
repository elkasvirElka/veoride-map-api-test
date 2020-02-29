package com.elviraminnullina.map_api.ui.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elviraminnullina.map_api.BaseFragment
import com.elviraminnullina.map_api.Constants.Companion.GPSLocationUpdates
import com.elviraminnullina.map_api.Constants.Companion.LOCATION
import com.elviraminnullina.map_api.Constants.Companion.TRAVEL_TIME
import com.elviraminnullina.map_api.MainActivity
import com.elviraminnullina.map_api.MyApplication
import com.elviraminnullina.map_api.R
import com.elviraminnullina.map_api.data.model.StepModel
import com.elviraminnullina.map_api.navigation.NavigationArguments
import com.elviraminnullina.map_api.navigation.NavigationEvent
import com.elviraminnullina.map_api.save_state_factory.InjectingSavedStateViewModelFactory
import com.elviraminnullina.map_api.utils.ChronometerUtils.Companion.getTimeFromChrono
import com.elviraminnullina.map_api.utils.DoubleUtils.Companion.round
import com.elviraminnullina.map_api.utils.PermissionUtils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.PolyUtil
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MapFragment : BaseFragment(),
    OnMapReadyCallback {
    @Inject
    lateinit var abstractFactory: InjectingSavedStateViewModelFactory
    private lateinit var mViewModel: MapViewModel
    private lateinit var navigationPath: TextView
    private var destinationMarker: Marker? = null
    private lateinit var start: Button
    private lateinit var mChronometer: Chronometer
    private var mMap: GoogleMap? = null

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            if (mViewModel.travelProcess.value == false)
                return
            val b = intent?.getBundleExtra(LOCATION)
            val lastKnownLoc = b?.getParcelable(LOCATION) as? Location

            lastKnownLoc?.let { lastLct ->
                if (round(mViewModel.destinationLocation.value?.latitude) == round(lastLct.latitude) && round(
                        mViewModel.destinationLocation.value?.longitude
                    ) == round(
                        lastLct.longitude
                    )
                ) {
                    stopTraveling()
                } else {
                    mViewModel.setCurrentLocation(lastLct)
                }
            }
        }
    }

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
        navigationPath = view.findViewById(R.id.navigation_path)
        start = view.findViewById(R.id.start)
        start.setOnClickListener(onStartClickListener())
        view.findViewById<Button>(R.id.stop).setOnClickListener(stopTravelingListener())

        if (mViewModel.travelProcess.value == true) {
            startChronometer()
            startTravel()
        }

        setViewModelObservers()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        if (!checkLocationPermission()) {
            return
        }
        MapsInitializer.initialize(Objects.requireNonNull(context))
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true

        val location = getLocation()
        location?.apply {
            mViewModel.setCurrentLocation(latitude, longitude)
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude, longitude),
                    13.toFloat()
                )
            )
        }
        mMap = googleMap
        mMap?.setOnMapLongClickListener(onMapLongClickListener())
        mViewModel.destinationMarkerOptions.value?.let {
            destinationMarker = mMap?.addMarker(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::mViewModel.isInitialized && mViewModel.travelProcess.value == true)
            mViewModel.setChronoTime(mChronometer.base.absoluteValue)
    }

    private fun getLocation(): Location? {
        val locationManager =
            ContextCompat.getSystemService<LocationManager>(
                requireContext(),
                LocationManager::class.java
            )

        val criteria = Criteria()
        return if (checkLocationPermission()) {
            locationManager?.getLastKnownLocation(
                locationManager.getBestProvider(criteria, false) ?: ""
            )
        } else null
    }

    private fun setViewModelObservers() {
        mViewModel.apply {
            currentLocation.observe(viewLifecycleOwner, Observer {
                it?.let {
                    getDirection()
                }
            })

            destinationLocation.observe(viewLifecycleOwner, Observer {
                it?.let {
                    getDirection()
                    val latLng = LatLng(it.latitude, it.longitude)
                    setDestinationMarkerOptions(latLng)
                }
            })
            destinationMarkerOptions.observe(viewLifecycleOwner, Observer {
                it?.let {
                    destinationMarker = mMap?.addMarker(it)
                }
            })

            response.observe(viewLifecycleOwner, Observer {
                it?.let {
                    //Draw all roads and give opportunely to choose, but for now we will do first road only
                    it.routes.firstOrNull()?.legs?.firstOrNull()?.let { leg ->
                        displayDirection(leg.steps)
                        start.visibility = View.VISIBLE
                    }
                }
            })

            showSpinner.observe(viewLifecycleOwner, Observer {
                if (it == true) {
                    showSpinner()
                } else {
                    hideSpinner()
                }
            })
        }
    }

    private fun displayDirection(directionsList: ArrayList<StepModel>) {
        updateNavPath(getCurrentPath())
        (mViewModel.polylines.value?.size ?: 0 > 0).run {
            mViewModel.removePolylines()
        }

        mMap?.apply {
            directionsList.forEach {
                val options = PolylineOptions()
                options.color(Color.BLUE)
                options.width(10F)
                options.addAll(PolyUtil.decode(it.polyline.points))
                mViewModel.polylines.value?.add(addPolyline(options))
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
        (activity as? MainActivity)?.startService()
        startChronometer()
        startTravel()
    }

    private fun getCurrentPath(): Spanned? {
        mViewModel.response.value?.routes?.firstOrNull()?.legs?.firstOrNull()
            ?.steps?.forEach { step ->
            if (PolyUtil.decode(step.polyline.points).lastOrNull() == mViewModel.polylines.value?.firstOrNull()?.points?.lastOrNull())
                return HtmlCompat.fromHtml(step.html_instructions, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        return null
    }

    private fun updateNavPath(text: Spanned?) {
        navigationPath.text = text
    }

    private fun stopTravelingListener() = View.OnClickListener {
        stopTraveling()
    }

    private fun stopTraveling() {
        mViewModel.travelProcess.value = false
        mViewModel.setChronoTimeToNull()
        mChronometer.stop()
        mViewModel.setTravelTime(mChronometer.base)
        (activity as? MainActivity)?.stopService()

        val arg = NavigationArguments.create {
            putString(TRAVEL_TIME, getTimeFromChrono(mChronometer))
        }

        getDialog(arg)
    }

    private fun getDialog(arg: NavigationArguments) {
        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.wanna_see_route_info))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                navigate(NavigationEvent(R.id.map_fragment_to_travel_info_fragment, arg))
            }.setNegativeButton(getString(R.string.no)) { _, _ ->
                val db = MyApplication.getInstance()?.getDatabase()
                db?.coordinationDataBase()?.deleteAll()
            }
            .create().show()
    }

    private fun startChronometer() {
        mViewModel.travelProcess.value = true
        mViewModel.setTravelTimeToNull()
        mChronometer.base = mViewModel.chronoTime.value ?: SystemClock.elapsedRealtime()
        mChronometer.start()
    }

    private fun startTravel() {
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            mMessageReceiver, IntentFilter(GPSLocationUpdates)
        )
    }
}
