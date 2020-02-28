package com.elviraminnullina.map_api.di.module

import androidx.lifecycle.ViewModel
import com.elviraminnullina.map_api.di.ViewModelKey
import com.elviraminnullina.map_api.save_state_factory.AssistedSavedStateViewModelFactory
import com.elviraminnullina.map_api.ui.map.MapViewModel
import com.elviraminnullina.map_api.ui.travel_info.TravelInformationViewModel
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

//@Module
@AssistedModule
@Module(includes = [AssistedInject_ViewModelModule::class])
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    abstract fun provideMapViewModel(f: MapViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>
    @Binds
    @IntoMap
    @ViewModelKey(TravelInformationViewModel::class)
    abstract fun provideTravelInfoViewModel(f: TravelInformationViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>
}