package com.example.autotrolejapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.autotrolejapp.entities.Line

@Dao
interface LineDatabaseDao : BaseDao<Line> {

    companion object {
        const val tableName: String = Line.TABLE_NAME
    }

    @Query( "SELECT * from ${Companion.tableName} WHERE identity = :key")
    suspend fun get(key: String): Line

    @Query("SELECT * FROM ${Companion.tableName} ORDER BY identity DESC")
    fun getAll(): LiveData<List<Line>>

    @Query("DELETE FROM ${Companion.tableName}")
    suspend fun clear()
}