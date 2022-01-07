package com.app.contactsdemo

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.contactsdemo.databinding.ItemContactBinding
import com.app.contactsdemo.model.Contact
import contacts.core.util.emailList
import contacts.core.util.phoneList

class ContactsAdapter(val mContext:Context, val list: List<Contact>):RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ItemContactBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactsAdapter.MyViewHolder {
       return MyViewHolder(
           ItemContactBinding.inflate(LayoutInflater.from(mContext),parent,false)
       )
    }

    override fun onBindViewHolder(holder: ContactsAdapter.MyViewHolder, position: Int) {

        holder.binding.tvName.text=list[position].name


        holder.binding.tvPhone.text=list[position].numbers.joinToString(",")
        holder.binding.tvEmail.text=list[position].emails.joinToString(",")
        if (list[position].bitmap!=null){
            Log.d(TAG, "onBindViewHolder: ${list[position]}")
            holder.binding.ivContact.setImageBitmap(list[position].bitmap)
        }else{
            holder.binding.ivContact.setImageResource(R.drawable.img_avatar)
        }
    }

    override fun getItemCount()=list.size
}