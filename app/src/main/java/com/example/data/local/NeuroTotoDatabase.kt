package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DevelopmentalMilestoneEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NeuroTotoDatabase : RoomDatabase() {

    abstract fun developmentalMilestoneDao(): DevelopmentalMilestoneDao

    companion object {
        private const val DATABASE_NAME = "neurototo_milestones.db"

        @Volatile
        private var INSTANCE: NeuroTotoDatabase? = null

        fun getInstance(context: Context): NeuroTotoDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NeuroTotoDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
