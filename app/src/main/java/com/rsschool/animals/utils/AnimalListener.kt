package com.rsschool.animals.utils

import com.rsschool.animals.database.Animal

interface AnimalListener {
    fun deleteAnimal(animal: Animal)
    fun updateAnimal(animal: Animal)
}