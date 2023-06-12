package com.thefadaap.fadamarketingapp.home.data

data class PharmacyData(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val location: String = "",
    val route: String = "",
    val email: String = "",
    val type: String = "",
    val enteredUserName: String = "",
    val hasCalled: Boolean = false,
    val callerId: String = "",
    val callResponse: String = "",
    val callComment: String = "",
    val dateCalled:  String = "",
    val timeCalled: String = "",
    val hasVisited: Boolean = false,
    val visitorId: String = "",
    val visitResponse: String = "",
    val visitComment: String = "",
    val dateVisited:  String = "",
    val timeVisited: String = "",
    val dateAdded: String = "",
    val isDeleted: Boolean = false
)
