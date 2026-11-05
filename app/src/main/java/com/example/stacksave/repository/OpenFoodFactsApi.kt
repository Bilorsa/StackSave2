package com.example.stacksave.repository

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {
    @GET("api/v3/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,
        @Query("fields") fields: String = "code,product_name,selected_images,image_front_url,image_url",
        @Header("User-Agent") userAgent: String
    ): Response<JsonObject>

    /**
     * Open Food Facts currently documents this text-search route as legacy.
     * It is used only for demo records that have no barcode; new/live records should
     * provide barcodes so the v3 product endpoint can be used instead.
     */
    @GET("api/v2/search")
    suspend fun searchProductsByText(
        @Query("search_terms") searchTerms: String,
        
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String = "code,product_name,selected_images,image_front_url,image_front_small_url,image_front_thumb_url,image_url",
        @Header("User-Agent") userAgent: String
    ): Response<JsonObject>
}
