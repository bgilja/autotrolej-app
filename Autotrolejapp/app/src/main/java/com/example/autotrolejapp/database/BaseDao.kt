package com.example.autotrolejapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update

@Dao
interface BaseDao<T> {

    @Insert
    suspend fun insert(item: T) : Long

    @Insert
    suspend fun insertMultiple(items: List<T>) : List<Long>

    @Update
    suspend fun update(item: T)

}