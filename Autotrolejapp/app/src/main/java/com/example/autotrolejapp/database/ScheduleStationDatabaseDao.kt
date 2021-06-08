package com.example.autotrolejapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.autotrolejapp.entities.ScheduleStation

@Dao
interface ScheduleStationDatabaseDao: BaseDao<ScheduleStation> {

    companion object {
        const val tableName: String = ScheduleStation.TABLE_NAME
    }

    @Query( "SELECT * from $tableName WHERE identity = :key")
    suspend fun get(key: String): ScheduleStation

    @Query( "SELECT * from $tableName WHERE line_variant_id = :lineVariantId")
    fun getByLineVariantId(lineVariantId: String): LiveData<List<ScheduleStation>>

    @Query( "SELECT * from $tableName WHERE station_id = :stationId")
    fun getByStationId(stationId: Int): LiveData<List<ScheduleStation>>

    @Query( "SELECT * from $tableName WHERE line_variant_id = :lineVariantId AND station_id = :stationId LIMIT 1")
    suspend fun getScheduleStationId(lineVariantId: String, stationId: Int): ScheduleStation

    @Query("SELECT * FROM $tableName ORDER BY identity DESC")
    fun getAll(): LiveData<List<ScheduleStation>>

    @Query("DELETE FROM $tableName")
    suspend fun clear()
}