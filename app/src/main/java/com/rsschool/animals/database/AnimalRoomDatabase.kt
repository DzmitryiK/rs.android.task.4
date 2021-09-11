package com.rsschool.animals.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rsschool.animals.DATABASE_NAME
import com.rsschool.animals.DATABASE_VERSION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

@Database(entities = [Animal::class], version = DATABASE_VERSION)
abstract class AnimalRoomDatabase : RoomDatabase() {

    abstract fun animalDao(): AnimalDao

    companion object {
        @Volatile
        private var INSTANCE: AnimalRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AnimalRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnimalRoomDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AnimalDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class AnimalDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.animalDao())
                    }
                }
            }
        }

        //Populate the database in a new coroutine.
        suspend fun populateDatabase(animalDao: AnimalDao) {
            var animal = Animal(0,"Leo", 5, "Abyssinian")
            animalDao.insert(animal)
            animal = Animal(0,"Porthos", 9, "Beagle")
            animalDao.insert(animal)
        }
    }
}