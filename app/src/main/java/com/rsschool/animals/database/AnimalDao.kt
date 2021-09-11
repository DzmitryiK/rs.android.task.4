package com.rsschool.animals.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.rsschool.animals.DEFAULT_QUERY
@Dao
interface AnimalDao {

    @Query(DEFAULT_QUERY)
    fun getAnimalsByCreation(): LiveData<List<Animal>>

    @RawQuery(observedEntities = [Animal::class])
    fun getSortedAnimals(query: SupportSQLiteQuery): LiveData<List<Animal>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(animal: Animal)

    @Update
    suspend fun update(animal: Animal)

    @Delete
    suspend fun delete(animal: Animal)

    @Query("DELETE FROM animal_table")
    suspend fun deleteAll()
}