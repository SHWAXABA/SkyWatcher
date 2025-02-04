package com.example.skywatcher

data class BirdObservationData(
    //This is the data that will be sent to the firebase database
    var birdName: String = "",
    var addDetails: String = "",
    var amount: String = "",
    var date: String = "",
    var location: String = "",
    var time: String = ""
)
