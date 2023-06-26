package com.example.mycontactmanager.repo

import androidx.lifecycle.LiveData
import com.example.mycontactmanager.Iterfaces.ContactDao
import com.example.mycontactmanager.model.ContactModel

class ContactRepo(private val contactDao: ContactDao) {

    val getContact: LiveData<List<ContactModel>> = contactDao.getContact()


    suspend fun insertCont(contactModel: ContactModel) {
        contactDao.insert(contactModel)
    }


    suspend fun deleteCont(contactModel: ContactModel) {
        contactDao.delete(contactModel)
    }

    suspend fun updateCont(contactModel: ContactModel) {
        contactDao.update(contactModel)
    }

    fun allDelete(){
        contactDao.deleteAll()
    }


}