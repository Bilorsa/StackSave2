package com.example.stacksave.repository

import android.content.Context
import com.example.stacksave.BuildConfig
import com.example.stacksave.model.StoreDeal
import com.example.stacksave.util.FirebaseManager
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Gets AZ Labs live grocery prices through a Firebase-authenticated server proxy. */
class DealFeedService(context: Context) {
    private val appContext = context.applicationContext
    private val baseUrl = BuildConfig.STACKSAVE_BACKEND_BASE_URL.trim().let { if (it.endsWith("/")) it else "$it/" }

    @Volatile
    var dataSourceMessage: String = "Sign in to Firebase to load AZ Labs live prices."
        private set

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val backendApi: StackSaveBackendApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(StackSaveBackendApi::class.java)

    suspend fun getDeals(query: String = "bread"): Result<List<StoreDeal>> = runCatching {
        val auth = FirebaseManager.auth(appContext)
            ?: throw IllegalStateException("Firebase is not configured. Add the correct app/google-services.json.")
        val user = auth.currentUser
            ?: throw IllegalStateException("Sign in with your Firebase email and password before searching live prices.")
        val token = user.getIdToken(false).await().token
            ?: throw IllegalStateException("Firebase did not provide an ID token. Sign out and sign in again.")
        val cleanedQuery = query.trim().ifBlank { "bread" }
        val response = backendApi.getDeals("Bearer $token", cleanedQuery, 20)
        if (!response.isSuccessful) {
            val detail = when (response.code()) {
                401 -> "Firebase token rejected (401). Sign out and sign in again; confirm backend Firebase service account belongs to this Firebase project."
                403 -> "AZ Labs rejected the API key (403). Check AZ_LABS_API_KEY on the backend."
                429 -> "AZ Labs rate limit or quota reached (429). Try again later."
                502, 503 -> "AZ Labs could not be reached by the backend (${response.code()}). Check backend terminal and API key."
                else -> "Live price service returned HTTP ${response.code()}. Check backend logs."
            }
            throw IllegalStateException(detail)
        }
        val body = response.body() ?: throw IllegalStateException("AZ Labs returned an empty response.")
        val parsed = parseApiDeals(body)
        dataSourceMessage = if (parsed.isEmpty()) {
            "AZ Labs responded, but no priced retailer offers matched '$cleanedQuery'. Try a different search term."
        } else {
            "AZ LABS LIVE — ${parsed.size} retailer offers for '$cleanedQuery'. Prices and stock may vary by branch."
        }
        parsed
    }.onFailure { error ->
        dataSourceMessage = error.message ?: "Live AZ Labs lookup failed. Check internet and backend status."
    }

    private fun parseApiDeals(root: JsonElement): List<StoreDeal> {
        val results = linkedMapOf<String, StoreDeal>()

        fun visit(element: JsonElement, inheritedName: String = "", inheritedCategory: String = "General", inheritedImage: String = "") {
            if (!element.isJsonObject) {
                if (element.isJsonArray) element.asJsonArray.forEach { visit(it, inheritedName, inheritedCategory, inheritedImage) }
                return
            }
            val obj = element.asJsonObject
            val name = obj.stringValue("name", "productName", "product_name", "title").ifBlank { inheritedName }
            val category = obj.stringValue("category", "categoryName").ifBlank { inheritedCategory }
            val image = obj.stringValue("imageUrl", "image_url", "image", "thumbnail").ifBlank { inheritedImage }

            val offers = obj.get("offers")
            if (offers != null && offers.isJsonArray) {
                offers.asJsonArray.forEach { offerElement ->
                    if (offerElement.isJsonObject) {
                        parseOffer(offerElement.asJsonObject, name, category, image)?.let { results[it.id] = it }
                    }
                }
            }

            parseOffer(obj, inheritedName, inheritedCategory, inheritedImage)?.let { results[it.id] = it }

            obj.entrySet().forEach { (key, value) ->
                if (key != "offers") visit(value, name, category, image)
            }
        }

        visit(root)
        return results.values.toList()
    }

    private fun parseOffer(
        obj: JsonObject,
        inheritedName: String,
        inheritedCategory: String,
        inheritedImage: String
    ): StoreDeal? {
        val name = obj.stringValue("name", "productName", "product_name", "title").ifBlank { inheritedName }
        val rawStore = obj.stringValue("storeName", "store_name", "retailerName", "retailer", "store")
        val store = when (rawStore.lowercase().trim()) {
            "pnp", "pick n pay", "picknpay" -> "Pick n Pay"
            "checkers", "checkers sixtysixty", "checkers sixty60" -> "Checkers"
            "shoprite" -> "Shoprite"
            "woolworths" -> "Woolworths"
            "makro" -> "Makro"
            else -> rawStore
        }
        val price = obj.doubleValue("price", "salePrice", "sale_price", "currentPrice", "current_price", "finalPrice")
        if (name.isBlank() || store.isBlank() || price <= 0.0) return null

        val wasPrice = obj.doubleValue("wasPrice", "was_price", "originalPrice", "original_price", "regularPrice", "regular_price")
        val categoryCandidate = obj.stringValue("category", "categoryName").ifBlank { inheritedCategory }
        val category = if (categoryCandidate.isBlank() || categoryCandidate == "General") inferCategory(name) else categoryCandidate
        val image = obj.stringValue("imageUrl", "image_url", "image", "thumbnail").ifBlank { inheritedImage }
        val productId = obj.stringValue("id", "productId", "product_id")
        val stableId = "api_${(store.lowercase() + "|" + name.lowercase() + "|" + productId + "|" + price).hashCode().toUInt().toString(16)}"
        val promo = obj.stringValue("promotion", "promotionLabel", "promotion_label", "deal", "description")
        val stock = obj.stringValue("stockState", "stock_state", "availability")
        val stockText = when {
            obj.get("inStock")?.takeIf { it.isJsonPrimitive }?.asBoolean == true -> "In stock where confirmed. "
            obj.get("inStock")?.takeIf { it.isJsonPrimitive }?.asBoolean == false -> "Reported out of stock. "
            stock.isNotBlank() -> "Availability: $stock. "
            else -> "Stock not confirmed. "
        }

        return StoreDeal(
            id = stableId,
            storeName = store,
            productName = name,
            originalPrice = if (wasPrice >= price) wasPrice else price,
            salePrice = price,
            cashbackRate = 0.0,
            category = category,
            imageUrl = image,
            rating = 0.0,
            ratingCount = 0,
            estimatedTime = "Check store",
            deliveryFee = "Check store",
            isFeatured = false,
            description = listOf(stockText.trim(), promo).filter { it.isNotBlank() }.joinToString(" ") + " Price returned by the live catalogue API; local branch prices may differ.",
            couponCode = "",
            validUntil = "",
            isEssential = category in setOf("Groceries", "Fresh Food", "Household", "Personal Care", "Baby & Family", "Everyday Essentials"),
            isSample = false,
            barcode = obj.stringValue("barcode", "ean", "gtin"),
            isLive = true,
            stockKnown = obj.get("inStock")?.isJsonPrimitive == true || stock.isNotBlank(),
            inStock = obj.get("inStock")?.takeIf { it.isJsonPrimitive }?.asBoolean ?: stock.contains("in stock", ignoreCase = true),
            promotion = promo,
            lastFetchedAt = obj.stringValue("lastFetchedAt", "last_fetched_at", "updatedAt", "updated_at"),
            productUrl = obj.stringValue("productUrl", "product_url", "url")
        )
    }

    private fun inferCategory(name: String): String {
        val n = name.lowercase()
        return when {
            listOf("milk", "bread", "rice", "maize", "sugar", "oil", "oats", "pasta", "beans", "coffee", "cereal").any(n::contains) -> "Groceries"
            listOf("chicken", "beef", "egg", "apple", "banana", "potato", "vegetable", "yoghurt").any(n::contains) -> "Fresh Food"
            listOf("detergent", "cleaner", "dishwashing", "toilet paper", "bleach").any(n::contains) -> "Household"
            listOf("nappy", "diaper", "baby", "wipes").any(n::contains) -> "Baby & Family"
            listOf("toothpaste", "toothbrush", "soap", "sanitary", "shampoo").any(n::contains) -> "Personal Care"
            else -> "Everyday Essentials"
        }
    }


}

private fun JsonObject.stringValue(vararg keys: String): String {
    for (key in keys) {
        val value = get(key) ?: continue
        if (value.isJsonNull) continue
        if (value.isJsonPrimitive) {
            val text = runCatching { value.asString }.getOrDefault("")
            if (text.isNotBlank() && text != "null") return text
        } else if (value.isJsonObject) {
            val nested = value.asJsonObject.stringValue("name", "label", "value")
            if (nested.isNotBlank()) return nested
        }
    }
    return ""
}

private fun JsonObject.doubleValue(vararg keys: String): Double {
    for (key in keys) {
        val value = get(key) ?: continue
        if (value.isJsonNull) continue
        val number = when {
            value.isJsonPrimitive -> runCatching { value.asDouble }.getOrDefault(0.0)
            value.isJsonObject -> value.asJsonObject.doubleValue("amount", "value", "price")
            else -> 0.0
        }
        if (number > 0.0) return number
    }
    return 0.0
}

private val Int.absoluteValue: Int
    get() = if (this == Int.MIN_VALUE) 0 else kotlin.math.abs(this)
