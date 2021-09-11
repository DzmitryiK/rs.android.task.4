package com.rsschool.animals.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.rsschool.animals.DATABASE_TABLE_NAME

class AnimalRepository(private val animalRoomDao: AnimalDao, private val animalCursorDao: AnimalSQLiteOpenHelper) {

    var dbImplRoom = true //Room = true, Cursor = false
    val allAnimals: LiveData<List<Animal>> = if(dbImplRoom){animalRoomDao.getAnimalsByCreation()}else{animalCursorDao.getAnimalsByCreation()}

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(animal: Animal) {
        if(dbImplRoom){animalRoomDao.insert(animal)}
        else{animalCursorDao.insert(animal)}
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(animal: Animal){
        if(dbImplRoom){animalRoomDao.delete(animal)}
        else{animalCursorDao.delete(animal)}
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(animal: Animal){
        if(dbImplRoom){animalRoomDao.update(animal)}
        else{animalCursorDao.update(animal)}
    }

    fun sort(orderFields: String): LiveData<List<Animal>> {
        val query = SimpleSQLiteQuery(
            "SELECT * FROM $DATABASE_TABLE_NAME ORDER BY $orderFields")
        return if(dbImplRoom){animalRoomDao.getSortedAnimals(query)
        }else {animalCursorDao.getSortedAnimals(query)}

    }

}