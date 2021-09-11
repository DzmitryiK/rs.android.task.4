package com.rsschool.animals.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "animal_table",
    indices = [Index(value = ["name", "age", "breed"], unique = true)]
    )
data class Animal(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name", ) val name: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "breed") val breed: String)