package com.example.mycontactmanager.Iterfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mycontactmanager.model.ContactModel

@Dao
interface ContactDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insert(contact: ContactModel)

    @Update
    suspend fun update(contact: ContactModel)


    @Delete
    suspend fun delete(contact: ContactModel)

    @Query("select * from contacts")
    fun getContact(): LiveData<List<ContactModel>>

    @Query("delete from contacts")
    fun deleteAll()



}