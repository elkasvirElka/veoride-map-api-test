package com.elviraminnullina.map_api.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class CoordinationDatabaseModel {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var lat: Double = 0.0
    var lng: Double = 0.0
  //  var polylineModel :PolylineModel = PolylineModel("")
}