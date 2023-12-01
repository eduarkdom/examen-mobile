package com.example.finalmobile.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Lugar::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lugarDao(): LugarDao

    companion object {
        @Volatile
        private var BASE_DATOS: AppDatabase? = null

        fun getInstance(contexto: Context): AppDatabase {
            return BASE_DATOS ?: synchronized(this) {
                Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "lugares.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { BASE_DATOS = it }
            }
        }
    }
}

