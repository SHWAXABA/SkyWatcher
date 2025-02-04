package com.example.skywatcher

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface RetrofitInstance {
    //This is one half of our API call and is what is used for out to access our API URL
    companion object{
        val mainURL = "https://api.ebird.org/v2/"
        fun getRetrofitInstance(): Retrofit{
            return Retrofit.Builder()
                .baseUrl(mainURL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }
}