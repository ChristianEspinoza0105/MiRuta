package com.example.miruta.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://3.147.36.92:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val otpApiService: OtpApiService by lazy {
        retrofit.create(OtpApiService::class.java)
    }
}
