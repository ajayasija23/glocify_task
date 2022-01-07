package com.app.contactsdemo.viewModel

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues.TAG
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.contactsdemo.model.Contact
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.math.log


class ContactsViewModel(val mApplication: Application) : AndroidViewModel(mApplication) {

    private val _contactsLiveData = MutableLiveData<ArrayList<Contact>>()
    val contactsLiveData:LiveData<ArrayList<Contact>> = _contactsLiveData

    fun fetchContacts() {
        viewModelScope.launch {
            val contactsListAsync = async { getPhoneContacts() }
            val contactNumbersAsync = async { getContactNumbers() }
            val contactEmailAsync = async { getContactEmails() }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()
            val contactEmails = contactEmailAsync.await()

            contacts.forEach {
                contactNumbers[it.id]?.let { numbers ->
                    it.numbers = numbers
                }
                contactEmails[it.id]?.let { emails ->
                    it.emails = emails
                }

            }
            _contactsLiveData.postValue(contacts)
        }
    }


    private suspend fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val uri= ContactsContract.Contacts.CONTENT_URI
        Log.d(TAG, "getPhoneContacts: $uri")
        val contactsCursor = mApplication.contentResolver?.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")

        Log.d(TAG, "getPhoneContacts: ${contactsCursor.toString()}")

        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.PhoneLookup._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
            val uri = contactsCursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                val inputStream=openPhoto(id.toLong())

                val bitmap=if (inputStream==null) null else BitmapFactory.decodeStream(inputStream)
                if (name != null) {
                    contactsList.add(Contact(id, name,bitmap))
                }
                inputStream?.close()
            }
            contactsCursor.close()
        }
        Log.d(TAG, "getPhoneContacts: $contactsList")
        return contactsList
    }

    private suspend fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = mApplication.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex = phoneCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                //check if the map contains key or not, if not then create a new array list with number
                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    contactsNumberMap[contactId] = arrayListOf(number)
                }
            }
            //contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap
    }


    private suspend fun getContactEmails(): HashMap<String, ArrayList<String>> {
        val contactsEmailMap = HashMap<String, ArrayList<String>>()
        val emailCursor = mApplication.contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                null,
                null,
                null)
        if (emailCursor != null && emailCursor.count > 0) {
            val contactIdIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            while (emailCursor.moveToNext()) {
                val contactId = emailCursor.getString(contactIdIndex)
                val email = emailCursor.getString(emailIndex)
                //check if the map contains key or not, if not then create a new array list with email
                if (contactsEmailMap.containsKey(contactId)) {
                    contactsEmailMap[contactId]?.add(email)
                } else {
                    contactsEmailMap[contactId] = arrayListOf(email)
                }
            }
            //contact contains all the emails of a particular contact
            emailCursor.close()
        }
        return contactsEmailMap
    }

    fun openPhoto(contactId: Long): InputStream? {
        val contactUri: Uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
        val photoUri: Uri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
        val cursor: Cursor = mApplication.contentResolver.query(
            photoUri,
            arrayOf<String>(ContactsContract.Contacts.Photo.PHOTO),
            null,
            null,
            null
        )
            ?: return null
        try {
            if (cursor.moveToFirst()) {
                val data = cursor.getBlob(0)
                if (data != null) {
                    return ByteArrayInputStream(data)
                }
            }
        } finally {
            cursor.close()
        }
        return null
    }


}