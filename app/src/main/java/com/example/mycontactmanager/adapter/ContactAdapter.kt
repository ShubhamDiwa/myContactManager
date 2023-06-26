package com.example.mycontactmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mycontactmanager.Iterfaces.ContactDelete
import com.example.mycontactmanager.R
import com.example.mycontactmanager.databinding.ItemContactListBinding
import com.example.mycontactmanager.model.ContactModel

class ContactAdapter(private val listener:ContactDelete,private var contactList: ArrayList<ContactModel>):RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemContactListBinding):RecyclerView.ViewHolder(binding.root) {
        fun setDataContact(data:ContactModel){
            binding.data=data


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ViewHolder {
        var binding: ItemContactListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_contact_list, parent, false)
        return ViewHolder(binding)
    }

    /*fun filterList(filterlist: String?) {
        contactList = filterlist
        notifyDataSetChanged()

    }*/


        override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
        holder.setDataContact(contactList[position])

        holder.binding.ivDelete.setOnClickListener {
            listener.delete(contactList[position])
        }
         holder.binding.ivShare.setOnClickListener {
             listener.share(contactList[position])
         }
        holder.itemView.setOnClickListener {
            listener.call(contactList[position])
        }

    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}