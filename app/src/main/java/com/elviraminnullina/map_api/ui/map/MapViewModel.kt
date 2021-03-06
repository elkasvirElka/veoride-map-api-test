package com.elviraminnullina.map_api.ui.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.elviraminnullina.map_api.Constants.Companion.ERROR
import com.elviraminnullina.map_api.data.model.CoordinationModel
import com.elviraminnullina.map_api.data.model.DirectionResponse
import com.elviraminnullina.map_api.data.repository.MapRepository
import com.elviraminnullina.map_api.save_state_factory.AssistedSavedStateViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.util.*

class MapViewModel @AssistedInject constructor(
    private val repository: MapRepository,
    @Assisted
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedInject.Factory
    interface Factory :
        AssistedSavedStateViewModelFactory<MapViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): MapViewModel
    }

    private val _showSpinner = MutableLiveData(false)
    val showSpinner: LiveData<Boolean> = _showSpinner

    private val responseStateHandle = savedStateHandle.getLiveData<DirectionResponse>(
        "response", null
    )
    val response: LiveData<DirectionResponse> = responseStateHandle

    private val _currentLocation =
        savedStateHandle.getLiveData<CoordinationModel>("current_location", null)

    fun setCurrentLocationAndUpdateDirection(latitude: Double, longitude: Double) {
        _currentLocation.value = CoordinationModel(latitude, longitude)
        getDirection()
    }

    fun setCurrentLocationAndUpdateDirection(location: Location) {
        _currentLocation.value = CoordinationModel(location.latitude, location.longitude)
        getDirection()
    }

    fun setDestinationAndUpdateDirection(latitude: Double = 0.0, longitude: Double = 0.0) {
        _destinationLocation.value = CoordinationModel(latitude, longitude)
        setDestinationMarkerOptions(LatLng(latitude, longitude))
        getDirection()
    }

    private val _destinationLocation =
        savedStateHandle.getLiveData<CoordinationModel>("destination", null)
    val destinationLocation: LiveData<CoordinationModel> = _destinationLocation

    private val _destinationMarkerOptions =
        savedStateHandle.getLiveData<MarkerOptions>("markerOptions", null)
    val destinationMarkerOptions: LiveData<MarkerOptions> = _destinationMarkerOptions

    private fun setDestinationMarkerOptions(latLng: LatLng) {
        _destinationMarkerOptions.value = MarkerOptions().position(latLng).draggable(true)
    }

    var travelProcess: Boolean?
        get() = savedStateHandle.get<Boolean>("travelProcess")
        set(value) {
            savedStateHandle.set("travelProcess", value)
        }

    var chronoTime: Long?
        get() = savedStateHandle.get<Long>("chronoTime")
        set(time) {
            savedStateHandle.set("chronoTime", time)
        }

    var polylines: ArrayList<Polyline>
            get()= savedStateHandle.get<ArrayList<Polyline>>("polylines") ?: ArrayList()
            set(value) = savedStateHandle.set("polylines", value)

    fun removePolylines() {
        polylines.forEach { x -> x.remove() }
        polylines.clear()
    }

    private fun getDirection(mode: String = "bicycling") {
        if (_currentLocation.value == null || _destinationLocation.value == null) {
            return
        }

        _showSpinner.value = true
        viewModelScope.launch {
            val response = repository.direction(
                _currentLocation.value ?: CoordinationModel(),
                _destinationLocation.value ?: CoordinationModel(), mode
            )
            if (response.isSuccessful) {
                responseStateHandle.value = response.body()
            } else {
                Log.d(ERROR, response.message())
            }
            _showSpinner.value = false
        }
    }
}