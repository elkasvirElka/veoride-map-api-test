package com.elviraminnullina.map_api.service.location_service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.elviraminnullina.map_api.MyApplication
import com.elviraminnullina.map_api.data.model.CoordinationDatabaseModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


//import com.google.android.gms.common.GooglePlayServicesUtil;
class LocationService : Service(), ConnectionCallbacks,
    OnConnectionFailedListener, LocationListener {
    // use the websmithing defaultUploadWebsite for testing and then check your
// location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private var defaultUploadWebsite: String? = null
    private var currentlyProcessingLocation = false
    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null
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
        val db = MyApplication.getInstance()?.getDatabase()
        with(CoordinationDatabaseModel()){
            lat = location.latitude
            lng = location.longitude
            db?.coordinationDataBase()?.insert(this)
        }
        // formatted for mysql datetime format
        /* val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
         dateFormat.timeZone = TimeZone.getDefault()
         val date = Date(location.time)
         val sharedPreferences = getSharedPreferences(
             "com.websmithing.gpstracker.prefs",
             Context.MODE_PRIVATE
         )
         val editor = sharedPreferences.edit()
         var totalDistanceInMeters =
             sharedPreferences.getFloat("totalDistanceInMeters", 0f)
         val firstTimeGettingPosition =
             sharedPreferences.getBoolean("firstTimeGettingPosition", true)
         if (firstTimeGettingPosition) {
             editor.putBoolean("firstTimeGettingPosition", false)
         } else {
             val previousLocation = Location("")
             previousLocation.latitude = sharedPreferences.getFloat(
                 "previousLatitude",
                 0f
             ).toDouble()
             previousLocation.longitude = sharedPreferences.getFloat(
                 "previousLongitude",
                 0f
             ).toDouble()
             val distance = location.distanceTo(previousLocation)
             totalDistanceInMeters += distance
             editor.putFloat("totalDistanceInMeters", totalDistanceInMeters)
         }
         editor.putFloat("previousLatitude", location.latitude.toFloat())
         editor.putFloat("previousLongitude", location.longitude.toFloat())
         editor.apply()
         val requestParams = RequestParams()
         requestParams.put("latitude", java.lang.Double.toString(location.latitude))
         requestParams.put("longitude", java.lang.Double.toString(location.longitude))
         val speedInMilesPerHour = location.speed * 2.2369
         requestParams.put("speed", Integer.toString(speedInMilesPerHour.toInt()))
         try {
             requestParams.put("date", URLEncoder.encode(dateFormat.format(date), "UTF-8"))
         } catch (e: UnsupportedEncodingException) {
         }
         requestParams.put("locationmethod", location.provider)
         if (totalDistanceInMeters > 0) {
             requestParams.put(
                 "distance",
                 String.format("%.1f", totalDistanceInMeters / 1609)
             ) // in miles,
         } else {
             requestParams.put("distance", "0.0") // in miles
         }
         requestParams.put("username", sharedPreferences.getString("userName", ""))
         requestParams.put("phonenumber", sharedPreferences.getString("appID", "")) // uuid
         requestParams.put("sessionid", sharedPreferences.getString("sessionID", "")) // uuid
         val accuracyInFeet = location.accuracy * 3.28
         requestParams.put("accuracy", Integer.toString(accuracyInFeet.toInt()))
         val altitudeInFeet = location.altitude * 3.28
         requestParams.put("extrainfo", Integer.toString(altitudeInFeet.toInt()))
         requestParams.put("eventtype", "android")
         val direction = location.bearing
         requestParams.put("direction", Integer.toString(direction.toInt()))
         val uploadWebsite =
             sharedPreferences.getString("defaultUploadWebsite", defaultUploadWebsite)
         LoopjHttpClient.get(uploadWebsite, requestParams, object : AsyncHttpResponseHandler() {
             fun onSuccess(
                 statusCode: Int,
                 headers: Array<cz.msebera.android.httpclient.Header?>?,
                 responseBody: ByteArray?
             ) {
                 LoopjHttpClient.debugLoopJ(
                     TAG,
                     "sendLocationDataToWebsite - success",
                     uploadWebsite,
                     requestParams,
                     responseBody,
                     headers,
                     statusCode,
                     null
                 )
                 stopSelf()
             }

             fun onFailure(
                 statusCode: Int,
                 headers: Array<cz.msebera.android.httpclient.Header?>?,
                 errorResponse: ByteArray?,
                 e: Throwable?
             ) {
                 LoopjHttpClient.debugLoopJ(
                     TAG,
                     "sendLocationDataToWebsite - failure",
                     uploadWebsite,
                     requestParams,
                     errorResponse,
                     headers,
                     statusCode,
                     e
                 )
                 stopSelf()
             }
         })*/
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
            sendLocationDataToDataBase(location)
        }
    }

    private fun stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient?.isConnected == true) {
            googleApiClient!!.disconnect()
        }
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
            interval = 1000 // milliseconds
            fastestInterval = 1000
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

/*
class LocationService : Service() {

    val BROADCAST_ACTION = "LocationService"
    private val TWO_SECONDS = 1000 * 2// 60 *
    var locationManager: LocationManager? = null
    var listener: MyLocationListener? = null
    var previousBestLocation: Location? = null

    var intent: Intent? = null
    var counter = 0

    override fun onCreate() {
        super.onCreate()
        intent = Intent(BROADCAST_ACTION)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = MyLocationListener()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            4000,
            0f,
            listener as LocationListener
        )
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0f, listener)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    protected fun isBetterLocation(
        location: Location,
        currentBestLocation: Location?
    ): Boolean {
        if (currentBestLocation == null) { // A new location is always better than no location
            return true
        }
        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > TWO_SECONDS
        val isSignificantlyOlder = timeDelta < -TWO_SECONDS
        val isNewer = timeDelta > 0
        // If it's been more than two minutes since the current location, use the new location
// because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }
        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200
        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(
            location.provider,
            currentBestLocation.provider
        )
        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }


    */
/**
 * Checks whether two providers are the same
 *//*

    private fun isSameProvider(
        provider1: String?,
        provider2: String?
    ): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }


    override fun onDestroy() { // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy()
        Log.v("STOP_SERVICE", "DONE")
        locationManager!!.removeUpdates(listener)
    }

    fun performOnBackgroundThread(runnable: Runnable): Thread? {
        val t: Thread = object : Thread() {
            override fun run() {
                try {
                    runnable.run()
                } finally {
                }
            }
        }
        t.start()
        return t
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(loc: Location) {
            Log.i("*****", "Location changed")
            if (isBetterLocation(loc, previousBestLocation)) {
                loc.latitude
                loc.longitude
                intent?.apply {
                    putExtra("Latitude", loc.latitude)
                    putExtra("Longitude", loc.longitude)
                    putExtra("Provider", loc.provider)
                    sendBroadcast(intent)
                }
            }
        }

        override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle
        ) {
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show()
        }
    }
}*/
