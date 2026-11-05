
package com.example.stacksave.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stacksave.model.StoreDeal
import com.example.stacksave.ui.StackSaveViewModel
import java.text.NumberFormat
import java.util.Locale

private val StackNavy = Color(0xFF101828)
private val StackGreen = Color(0xFFB8F34A)
private val StackSurface = Color(0xFF1C2638)
private val StackMuted = Color(0xFF98A2B3)
private val StackBackground = Color(0xFF0B1220)

private val zarFormat: NumberFormat =
    NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackSaveHomeScreen(
    viewModel: StackSaveViewModel,
    onOpenDeal: (StoreDeal) -> Unit = {},
    onOpenBasket: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenFavourites: () -> Unit = {}
) {
    val deals by viewModel.deals.collectAsStateWithLifecycle()
    val savedDeals by viewModel.savedDeals.collectAsStateWithLifecycle()
    val favoriteDeals by viewModel.favoriteDeals.collectAsStateWithLifecycle()
    val quantities by viewModel.basketQuantities.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val liveResultsQuery by viewModel.liveResultsQuery.collectAsStateWithLifecycle()
    val location by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val search by viewModel.searchQuery.collectAsStateWithLifecycle()
    var category by remember { mutableStateOf("All") }
    var sort by remember { mutableStateOf("Recommended") }

    val categories = remember(deals) {
        listOf("All") + deals
            .map { it.category }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    val filteredDeals = remember(deals, search, category, sort, liveResultsQuery) {
        var result = deals.filter { deal ->
            val matchesSearch =
                (deal.isLive && liveResultsQuery.equals(search.trim(), ignoreCase = true) && search.isNotBlank()) ||
                        deal.productName.contains(search, ignoreCase = true) ||
                        deal.storeName.contains(search, ignoreCase = true) ||
                        deal.category.contains(search, ignoreCase = true)

            val matchesCategory =
                category == "All" || deal.category == category

            matchesSearch && matchesCategory
        }

        result = when (sort) {
            "Biggest discount" ->
                result.sortedByDescending { it.discountPercent }

            "Lowest price" ->
                result.sortedBy { it.salePrice }

            "Highest rated" ->
                result.sortedByDescending { it.rating }

            "Featured" ->
                result.sortedByDescending { it.isFeatured }

            else -> result
        }

        result
    }

    val basketCount = savedDeals.sumOf { deal ->
        quantities[deal.id] ?: 1
    }

    val estimatedSavings = savedDeals.sumOf { deal ->
        deal.saving * (quantities[deal.id] ?: 1)
    }

    Scaffold(
        containerColor = StackBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "StackSave",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Smarter shopping starts here",
                            style = MaterialTheme.typography.labelSmall,
                            color = StackMuted
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenFavourites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Open favourites",
                            tint = Color(0xFFFF6B81)
                        )
                    }

                    IconButton(onClick = onOpenProfile) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Open profile",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onOpenBasket) {
                        BadgedBox(
                            badge = {
                                if (basketCount > 0) {
                                    Badge {
                                        Text(
                                            if (basketCount > 99) "99+"
                                            else basketCount.toString()
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Open basket",
                                tint = StackGreen
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StackBackground
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 8.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {
                LocationDropdown(
                    location = location,
                    onLocationSelected = viewModel::setSelectedLocation
                )
            }

            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = viewModel::setSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Search products, stores...",
                            color = StackMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = StackGreen
                        )
                    },
                    trailingIcon = {
                        if (search.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.setSearchQuery("") }
                            ) {
                                Text("Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StackGreen,
                        unfocusedBorderColor = StackSurface,
                        cursorColor = StackGreen,
                        focusedContainerColor = StackSurface,
                        unfocusedContainerColor = StackSurface
                    )
                )
            }

            item {
                Button(
                    onClick = { viewModel.searchLivePrices(search) },
                    enabled = !refreshing && search.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StackGreen,
                        contentColor = StackNavy,
                        disabledContainerColor = StackSurface,
                        disabledContentColor = StackMuted
                    )
                ) {
                    if (refreshing) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = StackNavy)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Compare live prices", fontWeight = FontWeight.Bold)
                }
                if (message.isNotBlank()) {
                    Text(message, color = StackMuted, style = MaterialTheme.typography.bodySmall)
                }
            }

            item {
                SavingsSummary(
                    savedCount = favoriteDeals.size,
                    basketCount = basketCount,
                    savings = estimatedSavings
                )
            }

            item {
                SectionHeading(
                    title = "Explore deals",
                    subtitle = "Find more value in every purchase"
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DropdownSelector(
                        label = "Category",
                        selected = category,
                        options = categories,
                        onSelected = { category = it }
                    )

                    DropdownSelector(
                        label = "Sort deals",
                        selected = sort,
                        options = listOf(
                            "Recommended",
                            "Biggest discount",
                            "Lowest price",
                            "Highest rated",
                            "Featured"
                        ),
                        onSelected = { sort = it }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredDeals.size} deals found",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = viewModel::refreshDeals,
                        enabled = !refreshing
                    ) {
                        if (refreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = StackGreen
                            )
                        } else {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = null,
                                tint = StackGreen
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Refresh", color = StackGreen)
                        }
                    }
                }
            }

            if (filteredDeals.isEmpty()) {
                item {
                    EmptyDealsState(
                        searching = search.isNotBlank()
                    )
                }
            } else {
                items(
                    items = filteredDeals,
                    key = { it.id }
                ) { deal ->
                    DealCard(
                        deal = deal,
                        isSaved = favoriteDeals.any { it.id == deal.id },
                        onToggleSaved = { viewModel.toggleFavoriteDeal(deal) },
                        onClick = {
                            viewModel.openDealDetail(deal)
                            onOpenDeal(deal)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationDropdown(
    location: String,
    onLocationSelected: (String) -> Unit
) {
    val locations = listOf(
        "Sandton, Johannesburg",
        "Cape Town",
        "Pretoria",
        "Durban"
    )

    DropdownSelector(
        label = "Shopping location",
        selected = location,
        options = locations,
        onSelected = onLocationSelected,
        leadingIcon = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    leadingIcon: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = {
                if (leadingIcon) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = StackGreen
                    )
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = StackGreen,
                unfocusedLabelColor = StackMuted,
                focusedBorderColor = StackGreen,
                unfocusedBorderColor = StackSurface,
                focusedContainerColor = StackSurface,
                unfocusedContainerColor = StackSurface
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SavingsSummary(
    savedCount: Int,
    basketCount: Int,
    savings: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = StackGreen
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "YOUR SAVINGS SNAPSHOT",
                style = MaterialTheme.typography.labelMedium,
                color = StackNavy,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = zarFormat.format(savings),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = StackNavy
            )

            Text(
                text = "Potential savings from your basket",
                style = MaterialTheme.typography.bodyMedium,
                color = StackNavy
            )

            HorizontalDivider(
                color = StackNavy.copy(alpha = 0.15f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryMetric(
                    label = "Favourites",
                    value = savedCount.toString()
                )

                SummaryMetric(
                    label = "Basket items",
                    value = basketCount.toString()
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String
) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = StackNavy
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = StackNavy.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun SectionHeading(
    title: String,
    subtitle: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = subtitle,
            color = StackMuted,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DealCard(
    deal: StoreDeal,
    isSaved: Boolean,
    onToggleSaved: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = StackSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProductPhoto(
                deal = deal,
                modifier = Modifier.fillMaxWidth().height(168.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = deal.storeName,
                        color = StackGreen,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = deal.productName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = deal.category + if (deal.isLive) " • API PRICE" else " • SAMPLE",
                        color = if (deal.isLive) StackGreen else StackMuted,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (deal.isLive) FontWeight.Bold else FontWeight.Normal
                    )
                }

                IconButton(
                    onClick = onToggleSaved
                ) {
                    Icon(
                        imageVector = if (isSaved) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = if (isSaved) {
                            "Remove saved deal"
                        } else {
                            "Save deal"
                        },
                        tint = if (isSaved) {
                            Color(0xFFFF6B81)
                        } else {
                            Color.White
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = zarFormat.format(deal.salePrice),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    if (deal.originalPrice > deal.salePrice) {
                        Text(
                            text = zarFormat.format(deal.originalPrice),
                            color = StackMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (deal.discountPercent > 0) {
                    Surface(
                        color = StackGreen,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${deal.discountPercent}% OFF",
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            color = StackNavy,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            if (deal.cashbackRate > 0) {
                Text(
                    text = "Cashback available: ${deal.cashbackRate}%",
                    color = StackGreen,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (deal.isCheapest) {
                Surface(color = StackGreen.copy(alpha = 0.16f), shape = RoundedCornerShape(10.dp)) {
                    Text("✓ LOWEST AVAILABLE PRICE", color = StackGreen, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }

            if (deal.isFeatured) {
                Text(
                    text = "★ FEATURED DEAL",
                    color = StackGreen,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = deal.description,
                color = StackMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (deal.couponCode.isNotBlank()) {
                Surface(
                    color = StackNavy,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Coupon: ${deal.couponCode}",
                        color = StackGreen,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StackGreen,
                    contentColor = StackNavy
                )
            ) {
                Text(
                    text = "View deal",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyDealsState(
    searching: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = StackSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = StackGreen,
                modifier = Modifier.size(42.dp)
            )

            Text(
                text = if (searching) {
                    "No matching deals"
                } else {
                    "No deals available"
                },
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (searching) {
                    "Try another product, store, or category."
                } else {
                    "Refresh the feed to look for available deals."
                },
                color = StackMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
