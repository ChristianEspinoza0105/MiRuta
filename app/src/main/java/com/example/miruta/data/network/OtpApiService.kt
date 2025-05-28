package com.example.miruta.data.network

import com.example.miruta.data.models.RoutePlanResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OtpApiService {
    @GET("otp/routers/default/plan")
    suspend fun getRoutePlan(
        @Query("fromPlace") fromPlace: String,
        @Query("toPlace") toPlace: String,
        @Query("mode") mode: String = "TRANSIT",
        @Query("date") date: String?,
        @Query("time") time: String?,
        @Query("ignoreRealtimeUpdates") ignoreRealtimeUpdates: Boolean = true
    ): RoutePlanResponse
}
