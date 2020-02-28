package com.elviraminnullina.map_api.save_state_factory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * Base interface for all ViewModel factories
 */
interface AssistedSavedStateViewModelFactory<T : ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}