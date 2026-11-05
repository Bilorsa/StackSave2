package com.example.stacksave.repository

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

/** AZ Labs Grocery Data API endpoints. All responses are parsed defensively because
 * optional product fields can vary by retailer and endpoint. */
interface GroceryApi {
    @GET("api/v1/grocery/compare")
    suspend fun compareProducts(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10
    ): JsonObject

    @GET("api/v1/grocery/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("store") store: String = "all",
        @Query("limit") limit: Int = 20
    ): JsonObject
}
