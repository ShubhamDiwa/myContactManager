package com.example.mycontactmanager.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycontactmanager.model.ContactModel
import com.example.mycontactmanager.repo.ContactRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel(private val contactRepo: ContactRepo) : ViewModel() {

    val getCont: LiveData<List<ContactModel>> = contactRepo.getContact


    fun insertC(contactModel: ContactModel) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepo.insertCont(contactModel)
        }


    }

     fun deleteC(contactModel: ContactModel) {
         viewModelScope.launch(Dispatchers.IO) {
             contactRepo.deleteCont(contactModel)
         }

    }

    suspend fun updateC(contactModel: ContactModel) {
        contactRepo.updateCont(contactModel)
    }

    fun deleteAll() {
        contactRepo.allDelete()
    }


}