package com.app.contactsdemo.model

import android.graphics.Bitmap

data class Contact(val id: String, val name:String,val bitmap: Bitmap?) {
    var numbers = ArrayList<String>()
    var emails = ArrayList<String>()
}