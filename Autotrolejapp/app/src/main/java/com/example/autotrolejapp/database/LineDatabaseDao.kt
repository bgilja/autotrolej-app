package com.example.autotrolejapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.entities.LineStation
import com.example.autotrolejapp.entities.Station

@Dao
interface LineDatabaseDao : BaseDao<Line> {

    companion object {
        const val tableName: String = Line.TABLE_NAME
    }

    @Query( "SELECT * from ${Companion.tableName} WHERE identity = :key")
    suspend fun get(key: String): Line

    @Query( "SELECT * from ${Companion.tableName} WHERE variant_id = :line_variant_id LIMIT 1")
    suspend fun getByVariantId(line_variant_id: String): Line

    @Query("SELECT * FROM ${Companion.tableName} ORDER BY identity DESC")
    fun getAll(): LiveData<List<Line>>

    @Query("DELETE FROM ${Companion.tableName}")
    suspend fun clear()

    @Query("SELECT DISTINCT B.* FROM (SELECT * FROM ${LineStation.TABLE_NAME} WHERE line_variant_id = :lineVariantId) AS A INNER JOIN ${Station.TABLE_NAME} AS B ON A.station_id = B.id")
    suspend fun getStations(lineVariantId: String): List<Station>
}