package com.example.stacksave.repository

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/** Firebase-authenticated StackSave proxy for AZ Labs grocery data. */
interface StackSaveBackendApi {
    @GET("api/deals")
    suspend fun getDeals(
        @Header("Authorization") authorization: String,
        @Query("search") search: String,
        @Query("limit") limit: Int = 20
    ): Response<JsonElement>
}
