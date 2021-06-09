package com.example.autotrolejapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.entities.ScheduleLine

@Dao
interface ScheduleLineDatabaseDao: BaseDao<ScheduleLine> {

    companion object {
        const val tableName: String = ScheduleLine.TABLE_NAME
    }

    @Query( "SELECT * from $tableName WHERE identity = :key")
    suspend fun get(key: String): ScheduleLine

    @Query( "SELECT * from $tableName WHERE start_id = :startId")
    fun getByStartId(startId: Int): LiveData<List<ScheduleLine>>

    @Query( "SELECT * from $tableName WHERE line_variant_id = :lineVariantId")
    fun getByVariantId(lineVariantId: String): LiveData<List<ScheduleLine>>

    @Query("SELECT * FROM $tableName ORDER BY identity DESC")
    fun getAll(): LiveData<List<ScheduleLine>>

    @Query("DELETE FROM $tableName")
    suspend fun clear()

    @Query("SELECT * FROM ${Line.TABLE_NAME} WHERE variant_id IN (SELECT DISTINCT line_variant_id FROM $tableName WHERE start_id = :startId)")
    fun getLineByStart(startId: Int): LiveData<List<Line>>
}