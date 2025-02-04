package com.example.skywatcher

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface BirdInterface {
    //These lines of code allows us to pull the geo data from ebird for the nearby hotspots using latitude and longitude
    @GET("data/obs/geo/recent")
    suspend fun getBirds(
        @Header("X-eBirdApiToken") apiKey: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<Birds>
}