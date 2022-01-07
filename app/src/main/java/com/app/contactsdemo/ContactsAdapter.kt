package com.app.contactsdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.contactsdemo.databinding.ItemContactBinding
import com.app.contactsdemo.model.Contact

class ContactsAdapter(val mContext:Context,val list: ArrayList<Contact>):RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {
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
        var email=""
        var phone=""
        holder.binding.tvName.text=list[position].name
        list[position].numbers.forEach {
            phone+=it
        }
        list[position].emails.forEach {
            email+=it+","
        }
        holder.binding.tvPhone.text=phone
        holder.binding.tvEmail.text=email
    }

    override fun getItemCount()=list.size
}