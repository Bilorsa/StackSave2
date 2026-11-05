package com.example.stacksave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.stacksave.model.StoreDeal
import com.example.stacksave.repository.OpenFoodFactsImageRepository
import kotlinx.coroutines.launch

private val PhotoSurface = Color(0xFF263449)
private val PhotoAccent = Color(0xFFB8F34A)

@Composable
fun ProductPhoto(
    deal: StoreDeal,
    modifier: Modifier = Modifier
) {
    var imageUrl by remember(deal.id, deal.imageUrl, deal.barcode) {
        mutableStateOf(deal.imageUrl.takeIf { it.startsWith("http") })
    }
    var imageFailed by remember(deal.id, deal.imageUrl, deal.barcode) { mutableStateOf(false) }
    var usingOpenFoodFacts by remember(deal.id, deal.imageUrl, deal.barcode) {
        mutableStateOf(deal.imageUrl.isBlank())
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(deal.id, deal.imageUrl, deal.barcode, deal.productName) {
        if (imageUrl.isNullOrBlank()) {
            usingOpenFoodFacts = true
            val result = OpenFoodFactsImageRepository.findImage(deal.productName, deal.barcode)
            if (result.isNullOrBlank()) {
                imageUrl = null
                imageFailed = true
            } else {
                imageUrl = result
            }
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PhotoSurface),
        contentAlignment = Alignment.Center
    ) {
        if (!imageFailed || imageUrl != null) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = PhotoAccent.copy(alpha = 0.85f),
                modifier = Modifier.fillMaxSize(0.22f)
            )
        }

        val url = imageUrl
        if (!url.isNullOrBlank() && !imageFailed) {
            AsyncImage(
                model = url,
                contentDescription = "${deal.productName} product photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = {
                    if (!usingOpenFoodFacts) {
                        usingOpenFoodFacts = true
                        scope.launch {
                            val fallback = OpenFoodFactsImageRepository.findImage(deal.productName, deal.barcode)
                            if (!fallback.isNullOrBlank() && fallback != url) {
                                imageUrl = fallback
                                imageFailed = false
                            } else {
                                imageUrl = null
                                imageFailed = true
                            }
                        }
                    } else {
                        imageUrl = null
                        imageFailed = true
                    }
                }
            )
        }

        if (imageFailed && imageUrl.isNullOrBlank()) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = PhotoAccent
                )
                Text(
                    text = "Photo unavailable",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
