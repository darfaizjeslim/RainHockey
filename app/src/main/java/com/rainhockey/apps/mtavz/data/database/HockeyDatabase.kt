package com.rainhockey.apps.mtavz.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rainhockey.apps.mtavz.data.dao.MatchDao
import com.rainhockey.apps.mtavz.data.dao.TeamDao
import com.rainhockey.apps.mtavz.data.models.Match
import com.rainhockey.apps.mtavz.data.models.Team

@Database(entities = [Team::class, Match::class], version = 2, exportSchema = false)
abstract class HockeyDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun matchDao(): MatchDao
    
    companion object {
        @Volatile
        private var INSTANCE: HockeyDatabase? = null
        
        fun getDatabase(context: Context): HockeyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HockeyDatabase::class.java,
                    "hockey_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

