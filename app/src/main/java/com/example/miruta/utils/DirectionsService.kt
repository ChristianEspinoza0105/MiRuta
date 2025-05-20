package com.example.miruta.utils

import com.example.miruta.data.models.DirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsService {
    @GET("api/directions")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String
    ): Response<DirectionsResponse>
}
