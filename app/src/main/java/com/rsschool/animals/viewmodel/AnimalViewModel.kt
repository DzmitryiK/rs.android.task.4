package com.rsschool.animals.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.rsschool.animals.R
import com.rsschool.animals.database.Animal
import com.rsschool.animals.database.AnimalRepository
import com.rsschool.animals.database.AnimalRoomDatabase
import com.rsschool.animals.database.AnimalSQLiteOpenHelper
import kotlinx.coroutines.launch


class AnimalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AnimalRepository
    var allAnimals: LiveData<List<Animal>>
    private var prefs:SharedPreferences

    init {
        val animalRoomDao = AnimalRoomDatabase.getDatabase(application, viewModelScope).animalDao()
        val animalCursorDao = AnimalSQLiteOpenHelper(application.applicationContext)
        repository = AnimalRepository(animalRoomDao, animalCursorDao)
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        refreshImpl()
        allAnimals = repository.allAnimals
    }

    fun insert(animal: Animal) = viewModelScope.launch {
        refreshImpl()
        repository.insert(animal)
    }

    fun delete(animal: Animal, fieldsSortAfter: String) = viewModelScope.launch {
        refreshImpl()
        repository.delete(animal)
        if (!repository.dbImplRoom) {
            allAnimals = repository.sort(fieldsSortAfter)}
    }

    fun update(animal: Animal) = viewModelScope.launch {
        refreshImpl()
        repository.update(animal)
    }

    fun sort(fields: String) {
        refreshImpl()
        allAnimals = repository.sort(fields)
    }

    private fun refreshImpl(){
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        repository.dbImplRoom = prefs.getString("db_impl_multilist",null) == getApplication<Application>().resources.getString(
            R.string.room_impl_name)
    }


}