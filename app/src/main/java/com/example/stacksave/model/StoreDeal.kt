package com.example.stacksave.model

data class StoreDeal(
    val id: String = "",
    val storeName: String = "",
    val productName: String = "",
    val originalPrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val cashbackRate: Double = 0.0,
    val category: String = "General",
    val imageUrl: String = "",
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val estimatedTime: String = "Check store",
    val deliveryFee: String = "Check store",
    val isFeatured: Boolean = false,
    val description: String = "Price supplied by the live retailer catalogue.",
    val couponCode: String = "",
    val validUntil: String = "",
    val isEssential: Boolean = true,
    val isSample: Boolean = false,
    val barcode: String = "",
    val isLive: Boolean = false,
    val isCheapest: Boolean = false,
    val stockKnown: Boolean = false,
    val inStock: Boolean = false,
    val promotion: String = "",
    val lastFetchedAt: String = "",
    val productUrl: String = ""
) {
    val saving: Double
        get() = (originalPrice - salePrice).coerceAtLeast(0.0)

    val discountPercent: Int
        get() = if (originalPrice > 0.0) {
            ((saving / originalPrice) * 100).toInt()
        } else {
            0
        }
}
