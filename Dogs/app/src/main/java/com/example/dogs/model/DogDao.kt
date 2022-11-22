package com.example.dogs.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogDao {
    @Insert
    suspend fun insertDogsList(vararg dog: DogBreed): List<Long>

    @Query("SELECT * FROM dog_breed_table")
    suspend fun getAllDogs(): List<DogBreed>

    @Query("SELECT * FROM dog_breed_table WHERE uuid = :dogId")
    suspend fun getDog(dogId: Int): DogBreed

    @Query("DELETE FROM dog_breed_table")
    suspend fun deleteAll()
}