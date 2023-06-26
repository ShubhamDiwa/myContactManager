package com.example.mycontactmanager.Iterfaces

import com.example.mycontactmanager.model.ContactModel

interface ContactDelete {
    fun delete(delete: ContactModel)
   fun share(share:ContactModel)
   fun call(call:ContactModel)
}