package com.elviraminnullina.map_api.database

import androidx.room.*
import com.elviraminnullina.map_api.data.model.CoordinationDatabaseModel

@Dao
interface CoordinationDao {
    @Query("SELECT * FROM CoordinationDatabaseModel")
    fun getAll(): List<CoordinationDatabaseModel>

    @Query("SELECT * FROM CoordinationDatabaseModel WHERE id = :id")
    fun getById(id: Int): CoordinationDatabaseModel

    @Insert
    fun insert(song: CoordinationDatabaseModel)

    @Update
    fun update(song: CoordinationDatabaseModel)

    @Delete
    fun delete(song: CoordinationDatabaseModel)

    @Query("DELETE FROM CoordinationDatabaseModel")
    fun deleteAll()
}