package com.elviraminnullina.map_api.ui.travel_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.elviraminnullina.map_api.data.repository.MapRepository
import com.elviraminnullina.map_api.save_state_factory.AssistedSavedStateViewModelFactory
import com.elviraminnullina.map_api.ui.map.MapViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class TravelInformationViewModel @AssistedInject constructor(
    @Assisted
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedInject.Factory
    interface Factory :
        AssistedSavedStateViewModelFactory<TravelInformationViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): TravelInformationViewModel
    }

    private val _travelTime =
        savedStateHandle.getLiveData<Long>("travelTime", null)
    val travelTime: LiveData<Long> = _travelTime
}
