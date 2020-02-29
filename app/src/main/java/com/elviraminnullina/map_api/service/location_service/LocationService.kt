package com.elviraminnullina.map_api.service.location_service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elviraminnullina.map_api.MyApplication
import com.elviraminnullina.map_api.data.model.CoordinationDatabaseModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

//TODO rewrite on work managers if you want to keep it wotk always
//import com.google.android.gms.common.GooglePlayServicesUtil;
class LocationService : Service(), ConnectionCallbacks,
    OnConnectionFailedListener, LocationListener {
    // use the websmithing defaultUploadWebsite for testing and then check your
// location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private var defaultUploadWebsite: String? = null
    private var currentlyProcessingLocation = false
    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null
    val db = MyApplication.getInstance()?.getDatabase()
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
        Log.d(TAG, "startTracking")
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
            "position: " + location.latitude + ", " + location.longitude + " accuracy: " + location.accuracy
        )
        // we have our desired accuracy of 10 meters so lets quit this service,
// onDestroy will be called and stop our location updates
        if (location.accuracy < 10.0f) {
            // stopLocationUpdates()
        } else {
            sendLocationDataToDataBase(location)
            sendMessageToActivity(location)
        }
    }

    private fun stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient?.isConnected == true) {
            googleApiClient!!.disconnect()
        }
    }

    private fun sendMessageToActivity(l: Location) {
        val intent = Intent("GPSLocationUpdates")
        // You can also include some extra data.
        val b = Bundle()
        b.putParcelable("Location", l)
        intent.putExtra("Location", b)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, "onConnected")
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
                "Go into settings and find Gps Tracker app and enable Location."
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
            "GoogleApiClient connection has been suspended."
        )
    }

    companion object {
        private const val TAG = "LocationService"
        private const val PERMISSION_ACCESS_FINE_LOCATION = 1
    }
}
