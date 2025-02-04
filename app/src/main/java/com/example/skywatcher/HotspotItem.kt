package com.example.skywatcher

import com.google.gson.annotations.SerializedName
//Semi Junk Code Please Ignore
//Also ebird returns hotspots data as RAW data instead of JSON key/value pairs
//Might send an email to them about this because it was rather annoying
data class HotspotItem(
    val locId: String,
    val countryCode: String,
    val subnational1Code: String,
    val subnational2Code: String?,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val observationDate: String,
    val count: Int
)

