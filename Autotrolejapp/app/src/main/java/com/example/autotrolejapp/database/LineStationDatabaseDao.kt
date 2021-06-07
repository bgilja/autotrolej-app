package com.example.autotrolejapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.autotrolejapp.entities.LineStation

@Dao
interface LineStationDatabaseDao: BaseDao<LineStation> {

    companion object {
        const val tableName: String = LineStation.TABLE_NAME
    }

    @Query( "SELECT * from ${Companion.tableName} WHERE identity = :key")
    suspend fun get(key: String): LineStation

    @Query("SELECT * FROM ${Companion.tableName} ORDER BY identity DESC")
    fun getAll(): LiveData<List<LineStation>>

    @Query("SELECT * FROM ${Companion.tableName} ORDER BY station_order ASC")
    fun getAllOrdered(): LiveData<List<LineStation>>

    @Query("DELETE FROM ${Companion.tableName}")
    suspend fun clear()
}