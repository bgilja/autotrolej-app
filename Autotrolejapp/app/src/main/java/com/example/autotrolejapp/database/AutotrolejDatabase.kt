package com.example.autotrolejapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.autotrolejapp.entities.*

@Database(entities = [Line::class, Station::class, LineStation::class, ScheduleLine::class, ScheduleStation::class], version = 5, exportSchema = false)
abstract class AutotrolejDatabase: RoomDatabase() {

    abstract val lineDatabaseDao: LineDatabaseDao
    abstract val stationDatabaseDao: StationDatabaseDao
    abstract val lineStationDatabaseDao: LineStationDatabaseDao
    abstract val scheduleLineDatabaseDao: ScheduleLineDatabaseDao
    abstract val scheduleStationDatabaseDao: ScheduleStationDatabaseDao

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