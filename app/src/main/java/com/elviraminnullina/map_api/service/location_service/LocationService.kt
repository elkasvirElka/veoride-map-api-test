package com.elviraminnullina.map_api.service.location_service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elviraminnullina.map_api.Constants.Companion.GPSLocationUpdates
import com.elviraminnullina.map_api.Constants.Companion.LOCATION
import com.elviraminnullina.map_api.MyApplication
import com.elviraminnullina.map_api.R
import com.elviraminnullina.map_api.data.model.CoordinationDatabaseModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

//TODO rewrite on work managers if you want to keep it work always
class LocationService : Service(), ConnectionCallbacks,
    OnConnectionFailedListener, LocationListener {
    private var currentlyProcessingLocation = false
    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null
    private val db = MyApplication.getInstance()?.getDatabase()
    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int { // if we are currently trying to get a location and the alarm manager has called this again,
// no need to start processing a new location.
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true
            startTracking()
        }
        return START_NOT_STICKY
    }

    private fun startTracking() {
        Log.d(TAG, getString(R.string.start_tracking))
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        if (googleApiClient?.isConnected == false || googleApiClient?.isConnecting == false) {
            googleApiClient?.connect()
        }
    }

    private fun sendLocationDataToDataBase(location: Location) {
        with(CoordinationDatabaseModel()) {
            lat = location.latitude
            lng = location.longitude
            db?.coordinationDataBase()?.insert(this)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        Log.e(
            TAG,
            getString(R.string.position) + location.latitude + ", " + location.longitude + getString(
                            R.string.accuracy) + location.accuracy
        )
        // we have our desired accuracy of 10 meters so lets update database and client
        if (location.accuracy > 10.0f) {
            sendLocationDataToDataBase(location)
            sendMessageToActivity(location)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient?.isConnected == true) {
            googleApiClient?.disconnect()
        }
    }

    private fun sendMessageToActivity(l: Location) {
        val intent = Intent(GPSLocationUpdates)
        // You can also include some extra data.
        val b = Bundle()
        b.putParcelable(LOCATION, l)
        intent.putExtra(LOCATION, b)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, getString(R.string.on_connect))
        locationRequest = LocationRequest.create()
        locationRequest?.apply {
            interval = 3000 // milliseconds 3sec
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                this
            )
        } catch (se: SecurityException) {
            Log.e(
                TAG,
                getString(R.string.go_into_settings)
            )
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "onConnectionFailed")
        stopLocationUpdates()
        stopSelf()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.e(
            TAG,
            getString(R.string.connection_suspended)
        )
    }

    companion object {
        private const val TAG = "LocationService"
        private const val PERMISSION_ACCESS_FINE_LOCATION = 1
    }
}
