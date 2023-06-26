package com.example.mycontactmanager.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mycontactmanager.Iterfaces.ContactDao
import com.example.mycontactmanager.model.ContactModel


@Database(entities = [ContactModel::class], version = 2, autoMigrations = [
    AutoMigration(from = 1, to = 2),
//    AutoMigration(from = 2, to = 3)
], exportSchema = true)
abstract class ContactDB : RoomDatabase() {
    abstract fun getContactDao(): ContactDao
    companion object {
        @Volatile
        private var INSTANCE: ContactDB? = null
        fun getDatabase(context: Context): ContactDB {
            return INSTANCE ?: kotlin.synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDB::class.java,
                    "noteDB"

                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}