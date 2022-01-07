package com.app.contactsdemo

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.contactsdemo.databinding.ActivityMainBinding
import com.app.contactsdemo.viewModel.ContactsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var contactPermission: ActivityResultLauncher<String>
    private lateinit var contactLauncher: ActivityResultLauncher<Void>
    private val viewModel:ContactsViewModel by viewModels()
    private lateinit var binding:ActivityMainBinding
    val permission=android.Manifest.permission.READ_CONTACTS
    var permssionGranted=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        bindObserver()
        contactPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            permssionGranted = it
        }
        permssionGranted=ContextCompat.checkSelfPermission(this,permission!!)==PackageManager.PERMISSION_GRANTED
        if (!permssionGranted){
            contactPermission.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }

    private fun bindObserver() {
        viewModel.contactsLiveData.observe(this,{
            binding.rvContacts.adapter=ContactsAdapter(this,it)
        })
    }

    override fun onResume() {
        super.onResume()
        if (permssionGranted){
            viewModel.fetchContacts()
        }
    }



}