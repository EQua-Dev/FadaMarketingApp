package com.thefadaap.fadamarketingapp.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object Common {


    val auth = FirebaseAuth.getInstance()

    //lateinit var mAuth: FirebaseAuth// = FirebaseAuth.getInstance()
//    var mAuth = FirebaseAuth.getInstance()
    val marketerCollectionRef = Firebase.firestore.collection("Users")
    val pharmaciesCollectionRef = Firebase.firestore.collection("Pharmacies")
    val ordersCollectionRef = Firebase.firestore.collection("Orders")
    val appointmentsCollectionRef = Firebase.firestore.collection("Appointments")

    const val DATE_MONTH_YEAR_FORMAT = "EEEE, dd MMMM, yyyy"

    const val IS_FIRST_SIGN_IN = "first_sign_in_key"

    var isFirstSignIn = true

}