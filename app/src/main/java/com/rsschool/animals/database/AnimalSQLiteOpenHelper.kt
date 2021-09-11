package com.rsschool.animals.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.rsschool.animals.DATABASE_NAME
import com.rsschool.animals.DATABASE_TABLE_NAME
import com.rsschool.animals.DATABASE_VERSION
import com.rsschool.animals.DEFAULT_QUERY
import java.sql.SQLException

private const val CREATE_TABLE_SQL =
    "CREATE TABLE IF NOT EXISTS $DATABASE_TABLE_NAME "+
            "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            "name TEXT NOT NULL, "+
            "age INTEGER NOT NULL, "+
            "breed TEXT NOT NULL); "
private const val CREATE_INDEX_SQL =
    "CREATE UNIQUE INDEX IF NOT EXISTS "+
            "`index_animal_table_name_age_breed` ON `animal_table` (`name`, `age`, `breed`)"

private const val LOG_TAG = "AnimalSQLiteOpenHelper"


class AnimalSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION), AnimalDao {

    private val resultLiveData = MutableLiveData<List<Animal>>()

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(CREATE_TABLE_SQL)
            db.execSQL(CREATE_INDEX_SQL)
            //todo: two default values
        } catch (exception: SQLException) {
            Log.e(LOG_TAG, "Exception while trying to create database", exception)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(LOG_TAG, "onUpgrade called")
    }

    private fun getCursor(query: SupportSQLiteQuery): Cursor {
        return readableDatabase.rawQuery(query.sql, null)
    }

    private fun getListOfAnimals(query: SupportSQLiteQuery = SimpleSQLiteQuery(DEFAULT_QUERY)): List<Animal> {
        val listOfAnimals = mutableListOf<Animal>()
        getCursor(query).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    listOfAnimals.add(
                        Animal(cursor.getInt(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getInt(cursor.getColumnIndex("age")),
                            cursor.getString(cursor.getColumnIndex("breed"))
                    ))
                } while (cursor.moveToNext())
            }
        }
        return listOfAnimals.toList()
    }

    override fun getAnimalsByCreation(): LiveData<List<Animal>> {
        resultLiveData.postValue(getListOfAnimals())
        return  resultLiveData
    }

    override fun getSortedAnimals(query: SupportSQLiteQuery): LiveData<List<Animal>> {
        resultLiveData.postValue(getListOfAnimals(query))
        return  resultLiveData
    }

    override suspend fun insert(animal:Animal){
        val values = ContentValues().apply {
            put("name", animal.name)
            put("age", animal.age)
            put("breed", animal.breed)
        }
        readableDatabase.insert(DATABASE_TABLE_NAME,null, values)
    }

    override suspend fun update(animal:Animal){
        val values = ContentValues().apply {
            put("name", animal.name)
            put("age", animal.age)
            put("breed", animal.breed)
        }
        val selection = "id = ?"
        val selectionArgs = arrayOf(animal.id.toString())
        readableDatabase.update(DATABASE_TABLE_NAME, values, selection, selectionArgs)
    }

    override suspend fun delete(animal: Animal) {
        val selection = "id = ?"
        val selectionArgs = arrayOf(animal.id.toString())
        readableDatabase.delete(DATABASE_TABLE_NAME, selection, selectionArgs)
    }

    override suspend fun deleteAll() {
        readableDatabase.delete(DATABASE_TABLE_NAME, null, null)
    }
}