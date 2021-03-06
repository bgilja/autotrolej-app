package com.example.autotrolejapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.autotrolejapp.entities.Station

@Dao
interface StationDatabaseDao : BaseDao<Station> {

    companion object {
        const val tableName: String = Station.TABLE_NAME
    }

    @Query( "SELECT * from ${Companion.tableName} WHERE identity = :key")
    suspend fun get(key: Long): Station

    @Query("SELECT * FROM ${Companion.tableName} ORDER BY identity DESC")
    fun getAll(): LiveData<List<Station>>

    @Query("SELECT * FROM ${Companion.tableName} ORDER BY identity DESC")
    suspend fun getAllSuspend(): List<Station>

    @Query("SELECT * FROM ${Companion.tableName} WHERE id in (:stations)")
    fun getMultipleById(stations: List<Int>): LiveData<List<Station>>

    @Query("DELETE FROM ${Companion.tableName}")
    suspend fun clear()
}