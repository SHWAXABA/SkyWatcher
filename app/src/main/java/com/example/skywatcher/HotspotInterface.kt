package com.example.skywatcher

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
//Semi Junk Code Please Ignore
interface HotspotInterface {
    @GET("data/obs/geo/recent")
    suspend fun getHotspots(
        @Header("X-eBirdApiToken") apiKey: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<List<HotspotItem>>
}