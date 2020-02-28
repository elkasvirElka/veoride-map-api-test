package com.elviraminnullina.map_api.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.elviraminnullina.map_api.data.model.CoordinationDatabaseModel

@Database(entities = [CoordinationDatabaseModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coordinationDataBase(): CoordinationDao
}