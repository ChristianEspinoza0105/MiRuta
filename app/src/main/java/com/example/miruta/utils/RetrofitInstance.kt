package com.example.miruta.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: DirectionsService by lazy {
        Retrofit.Builder()
            .baseUrl("https://backend-miruta.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsService::class.java)
    }
}