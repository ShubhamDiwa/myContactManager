package com.example.mycontactmanager.ViewModeFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mycontactmanager.ViewModel.ContactViewModel
import com.example.mycontactmanager.repo.ContactRepo

class ViewModelFactory constructor(private var repo: ContactRepo):ViewModelProvider.Factory {

        override fun<T: ViewModel> create(modeClass: Class<T>):T {
        if (modeClass.isAssignableFrom(ContactViewModel::class.java)) {
            return ContactViewModel(repo) as T
        } else {
            throw IllegalArgumentException(" not get response ")
        }


    }

}