package com.example.skywatcher
import  com.google.gson.annotations.SerializedName
data class BirdItem(
    //These lines of code represent the key/value pairs of the JSON data pulled from eBird with serialization so that they values will match once we pull them
    @SerializedName("speciesCode")
    val speciesCode: String,
    @SerializedName("comName")
    val comName: String,
    @SerializedName("sciName")
    val sciName : String,
    @SerializedName("locId")
    val locId : String,
    @SerializedName("locName")
    val locName : String,
    @SerializedName("obsDt")
    val obsDt : String,
    @SerializedName("howMany")
    val howMany : String,
    @SerializedName("lat")
    val lat : Double,
    @SerializedName("lng")
    val lng : Double,
    @SerializedName("obsValid")
    val obsValid : Boolean,
    @SerializedName("obsReviewed")
    val obsReviewed : Boolean,
    @SerializedName("locationPrivate")
    val locationPrivate : Boolean,
    @SerializedName("subId")
    val subId : String
)
