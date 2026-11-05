package com.example.stacksave.repository

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Retrieves product images from Open Food Facts. Cache is process-wide to avoid repeatedly
 * searching the same product as Compose cards are recomposed or revisited.
 */
object OpenFoodFactsImageRepository {
    // Identifies this app when making Open Food Facts requests.
    private const val OPEN_FOOD_FACTS_USER_AGENT = "StackSave/1.0 (Android)"

    private val api: OpenFoodFactsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsApi::class.java)
    }
    private val cache = ConcurrentHashMap<String, String>() // empty value means no image found
    private val requestSlots = Semaphore(permits = 2)

    suspend fun findImage(productName: String, barcode: String = ""): String? {
        val key = if (barcode.isNotBlank()) "barcode:${barcode.trim()}" else "name:${normalise(productName)}"
        cache[key]?.let { cached -> return cached.takeIf { it.isNotBlank() } }

        return requestSlots.withPermit {
            cache[key]?.let { cached -> return@withPermit cached.takeIf { it.isNotBlank() } }
            val found = runCatching {
                if (barcode.isNotBlank()) imageFromBarcode(barcode.trim()) else null
            }.getOrNull() ?: runCatching {
                imageFromTextSearch(productName)
            }.getOrNull()
            if (!found.isNullOrBlank()) cache[key] = found
            found
        }
    }

    private suspend fun imageFromBarcode(barcode: String): String? {
        val response = api.getProductByBarcode(
            barcode = barcode,
            userAgent = OPEN_FOOD_FACTS_USER_AGENT
        )
        if (!response.isSuccessful) return null
        val root = response.body() ?: return null
        val product = root.objectOrNull("product") ?: return null
        return imageUrl(product)
    }

    private suspend fun imageFromTextSearch(productName: String): String? {
        val query = productName.trim().take(90)
        if (query.length < 3) return null
        val response = api.searchProductsByText(
            searchTerms = query,
            userAgent = OPEN_FOOD_FACTS_USER_AGENT
        )
        if (!response.isSuccessful) return null
        val root = response.body() ?: return null
        val products = root.arrayOrNull("products") ?: return null
        val requestedTokens = meaningfulTokens(query)
        val candidates = products.mapNotNull { element ->
            if (!element.isJsonObject) return@mapNotNull null
            val product = element.asJsonObject
            val name = product.stringOrNull("product_name").orEmpty()
            val candidateTokens = meaningfulTokens(name)
            val overlap = requestedTokens.intersect(candidateTokens).size
            val image = imageUrl(product)
            if (image != null && overlap > 0) Triple(overlap, name.length, image) else null
        }
        return candidates.maxWithOrNull(compareBy<Triple<Int, Int, String>> { it.first }.thenBy { -it.second })?.third
    }

    private fun imageUrl(product: JsonObject): String? {
        val selected = product.objectOrNull("selected_images")
            ?.objectOrNull("front")
        val display = selected?.objectOrNull("display")
        val small = selected?.objectOrNull("small")
        val languageUrl = display?.stringOrNull("en")
            ?: display?.entrySet()?.firstNotNullOfOrNull { it.value.asStringOrNull() }
            ?: small?.stringOrNull("en")
            ?: small?.entrySet()?.firstNotNullOfOrNull { it.value.asStringOrNull() }
        return (languageUrl
            ?: product.stringOrNull("image_front_url")
            ?: product.stringOrNull("image_front_small_url")
            ?: product.stringOrNull("image_front_thumb_url")
            ?: product.stringOrNull("image_url"))
            ?.takeIf { it.startsWith("https://") || it.startsWith("http://") }
    }

    private fun meaningfulTokens(value: String): Set<String> = normalise(value)
        .split(Regex("[^a-z0-9]+"))
        .filter { it.length >= 3 && it !in setOf("the", "and", "with", "pack", "per", "for", "ml", "litre", "litres", "kg") }
        .toSet()

    private fun normalise(value: String) = value.lowercase(Locale.ROOT).trim()

    private fun JsonObject.objectOrNull(key: String): JsonObject? =
        get(key)?.takeIf { it.isJsonObject }?.asJsonObject

    private fun JsonObject.arrayOrNull(key: String) =
        get(key)?.takeIf { it.isJsonArray }?.asJsonArray

    private fun JsonObject.stringOrNull(key: String): String? = get(key).asStringOrNull()

    private fun JsonElement?.asStringOrNull(): String? = try {
        this?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }?.asString
            ?.takeIf { it.isNotBlank() }
    } catch (_: Exception) { null }
}
