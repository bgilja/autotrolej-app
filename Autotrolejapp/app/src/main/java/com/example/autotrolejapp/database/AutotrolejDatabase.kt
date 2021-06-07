package com.example.autotrolejapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.entities.LineStation
import com.example.autotrolejapp.entities.Station

@Database(entities = [Line::class, Station::class, LineStation::class], version = 2, exportSchema = false)
abstract class AutotrolejDatabase: RoomDatabase() {

    abstract val lineDatabaseDao: LineDatabaseDao
    abstract val stationDatabaseDao: StationDatabaseDao
    abstract val lineStationDatabaseDao: LineStationDatabaseDao

    companion object {

        const val DATABASE_NAME = "autotrolej_database"

        @Volatile
        private var INSTANCE: AutotrolejDatabase? = null

        fun getInstance(context: Context): AutotrolejDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AutotrolejDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}