package com.example.dogs.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [DogBreed::class])
abstract class DogDatabase: RoomDatabase() {
    abstract fun DogDao(): DogDao

    companion object{
        @Volatile
        private var instance: DogDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room
                .databaseBuilder(context,
                    DogDatabase::class.java,
                    "dog_database")
                .build()
    }
}