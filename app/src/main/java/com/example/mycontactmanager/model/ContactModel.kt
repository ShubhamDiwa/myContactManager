package com.example.mycontactmanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "contacts")
data class ContactModel(
   @ColumnInfo(name = "name") val name:String?,
    @ColumnInfo(name="phone")val phone:String?,
   @ColumnInfo(name = "contactId") val contactId:String?,
   @PrimaryKey(autoGenerate = true)val id :Int?
)
