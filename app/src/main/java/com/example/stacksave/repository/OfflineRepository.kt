package com.example.stacksave.repository

import android.content.Context
import com.example.stacksave.model.StoreDeal
import org.json.JSONArray
import org.json.JSONObject

class OfflineRepository(context: Context) {
    private val prefs = context.getSharedPreferences("stacksave_offline", Context.MODE_PRIVATE)

    fun saveDeals(deals: List<StoreDeal>) {
        val array = JSONArray()
        deals.forEach { deal ->
            array.put(JSONObject().apply {
                put("id", deal.id)
                put("store", deal.storeName)
                put("name", deal.productName)
                put("original", deal.originalPrice)
                put("sale", deal.salePrice)
                put("cashback", deal.cashbackRate)
                put("category", deal.category)
                put("description", deal.description)
                put("couponCode", deal.couponCode)
                put("validUntil", deal.validUntil)
                put("isEssential", deal.isEssential)
                put("isSample", deal.isSample)
                put("isFeatured", deal.isFeatured)
            })
        }
        prefs.edit().putString("deals", array.toString()).apply()
    }

    fun loadDeals(): List<StoreDeal> {
        val raw = prefs.getString("deals", null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    add(
                        StoreDeal(
                            id = item.optString("id"),
                            storeName = item.optString("store"),
                            productName = item.optString("name"),
                            originalPrice = item.optDouble("original", 0.0),
                            salePrice = item.optDouble("sale", 0.0),
                            cashbackRate = item.optDouble("cashback", 0.0),
                            category = item.optString("category", "General"),
                            description = item.optString(
                                "description",
                                "Sample deal. Confirm current price and terms with the retailer."
                            ),
                            couponCode = item.optString("couponCode", ""),
                            validUntil = item.optString("validUntil", "2026-10-31"),
                            isEssential = item.optBoolean("isEssential", true),
                            isSample = item.optBoolean("isSample", true),
                            isFeatured = item.optBoolean("isFeatured", false)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun setOfflineEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("offline", enabled).apply()
    }
}
