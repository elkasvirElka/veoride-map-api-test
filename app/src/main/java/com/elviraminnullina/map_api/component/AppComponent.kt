package com.elviraminnullina.map_api.component

import com.elviraminnullina.map_api.di.module.DataModule
import com.elviraminnullina.map_api.di.module.FrameworkModule
import com.elviraminnullina.map_api.di.module.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, FrameworkModule::class, ViewModelModule::class])
interface AppComponent {

    fun createMainComponent(): MainComponent
}
