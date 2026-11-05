package com.example.stacksave.ui.screens

import android.content.Intent
import android.net.Uri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stacksave.model.StoreDeal
import com.example.stacksave.ui.StackSaveViewModel
import java.text.NumberFormat
import java.util.Locale

private val AccountBackground = Color(0xFF0B1220)
private val AccountSurface = Color(0xFF1C2638)
private val AccountGreen = Color(0xFFB8F34A)
private val AccountMuted = Color(0xFF98A2B3)
private val AccountCurrency = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

@Composable
fun StackSaveAuthScreen(viewModel: StackSaveViewModel) {
    val message by viewModel.message.collectAsStateWithLifecycle()
    var isRegistering by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var validation by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AccountSurface)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("StackSave", color = AccountGreen, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                Text(
                    if (isRegistering) "Create your account to start saving." else "Sign in to find smarter deals.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (isRegistering) {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it }, label = { Text("Full name") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                }
                OutlinedTextField(
                    value = email, onValueChange = { email = it.trim() }, label = { Text("Email address") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                if (validation.isNotBlank()) Text(validation, color = Color(0xFFFF8B8B), style = MaterialTheme.typography.bodySmall)
                if (message.isNotBlank()) Text(message, color = AccountMuted, style = MaterialTheme.typography.bodySmall)
                Button(
                    onClick = {
                        validation = when {
                            !email.contains("@") -> "Enter a valid email address."
                            password.length < 6 -> "Password must contain at least 6 characters."
                            isRegistering && name.isBlank() -> "Enter your name."
                            else -> ""
                        }
                        if (validation.isBlank()) {
                            if (isRegistering) viewModel.register(email, password, name.trim())
                            else viewModel.login(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccountGreen, contentColor = Color(0xFF101828))
                ) {
                    Text(if (isRegistering) "Create account" else "Sign in", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { isRegistering = !isRegistering; validation = "" }) {
                    Text(if (isRegistering) "Already have an account? Sign in" else "New to StackSave? Create an account", color = AccountGreen)
                }
                Text(
                    "Sample deal prices are illustrative. Check the retailer for current prices, stock and terms.",
                    color = AccountMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackSaveBasketScreen(viewModel: StackSaveViewModel, onBackHome: () -> Unit) {
    val savedDeals by viewModel.savedDeals.collectAsStateWithLifecycle()
    val quantities by viewModel.basketQuantities.collectAsStateWithLifecycle()
    val savings = savedDeals.sumOf { it.saving * (quantities[it.id] ?: 1) }

    Scaffold(
        containerColor = AccountBackground,
        topBar = {
            TopAppBar(
                title = { Text("Your basket", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackHome) { Icon(Icons.Default.ArrowBack, "Back to deals", tint = AccountGreen) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AccountBackground)
            )
        }
    ) { padding ->
        if (savedDeals.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.ShoppingBasket, contentDescription = null, tint = AccountGreen, modifier = Modifier.size(48.dp))
                    Text("Your basket is empty", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Open a deal and choose “Add to basket” to start your shopping list.", color = AccountMuted)
                    Button(onClick = onBackHome, colors = ButtonDefaults.buttonColors(containerColor = AccountGreen, contentColor = Color(0xFF101828))) { Text("Explore deals") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = AccountGreen), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.fillMaxWidth().padding(18.dp)) {
                            Text("Potential savings", color = Color(0xFF101828))
                            Text(AccountCurrency.format(savings), color = Color(0xFF101828), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                            Text("Savings are estimates based on listed prices.", color = Color(0xFF101828))
                        }
                    }
                }
                items(savedDeals, key = { it.id }) { deal ->
                    BasketDealRow(
                        deal = deal,
                        quantity = quantities[deal.id] ?: 1,
                        onQuantityChange = { viewModel.setBasketQuantity(deal.id, it) },
                        onRemove = { viewModel.toggleSavedDeal(deal) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BasketDealRow(deal: StoreDeal, quantity: Int, onQuantityChange: (Int) -> Unit, onRemove: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = AccountSurface), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ProductPhoto(deal = deal, modifier = Modifier.fillMaxWidth().height(112.dp))
            Text(deal.storeName, color = AccountGreen, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(deal.productName, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(AccountCurrency.format(deal.salePrice), color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Save ${AccountCurrency.format(deal.saving)} each", color = AccountMuted, style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { onQuantityChange(quantity - 1) }) { Text("−", color = AccountGreen) }
                    Text(quantity.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { onQuantityChange(quantity + 1) }) { Text("+", color = AccountGreen) }
                    IconButton(onClick = onRemove) { Icon(Icons.Default.DeleteOutline, contentDescription = "Remove deal", tint = Color(0xFFFF8B8B)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackSaveProfileScreen(viewModel: StackSaveViewModel, onBackHome: () -> Unit, onSignOut: () -> Unit) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    var language by remember(profile.language) { mutableStateOf(profile.language) }
    var notifications by remember(profile.notificationsEnabled) { mutableStateOf(profile.notificationsEnabled) }
    var offlineSync by remember(profile.offlineSyncEnabled) { mutableStateOf(profile.offlineSyncEnabled) }

    Scaffold(
        containerColor = AccountBackground,
        topBar = {
            TopAppBar(
                title = { Text("Profile & settings", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackHome) { Icon(Icons.Default.ArrowBack, "Back to deals", tint = AccountGreen) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AccountBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = AccountSurface), shape = RoundedCornerShape(22.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(profile.displayName.ifBlank { "StackSave shopper" }, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(profile.email.ifBlank { "No email available" }, color = AccountMuted)
                    }
                }
            }
            item { Text("Preferences", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Language", color = AccountMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("en" to "English", "zu" to "isiZulu").forEach { (code, label) ->
                            Button(
                                onClick = { language = code },
                                colors = ButtonDefaults.buttonColors(containerColor = if (language == code) AccountGreen else AccountSurface, contentColor = if (language == code) Color(0xFF101828) else Color.White)
                            ) { Text(label) }
                        }
                    }
                }
            }
            item { SwitchRow("Deal notifications", notifications) { notifications = it } }
            item { SwitchRow("Offline deal cache", offlineSync) { offlineSync = it } }
            item {
                Button(
                    onClick = { viewModel.updateSettings(language, notifications, offlineSync) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccountGreen, contentColor = Color(0xFF101828))
                ) { Text("Save preferences", fontWeight = FontWeight.Bold) }
            }
            if (message.isNotBlank()) item { Text(message, color = AccountMuted, style = MaterialTheme.typography.bodySmall) }
            item {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF44232C), contentColor = Color.White)
                ) { Text("Sign out") }
            }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White)
        androidx.compose.material3.Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackSaveFavouritesScreen(
    viewModel: StackSaveViewModel,
    onBackHome: () -> Unit,
    onOpenDeal: (StoreDeal) -> Unit
) {
    val favorites by viewModel.favoriteDeals.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = AccountBackground,
        topBar = {
            TopAppBar(
                title = { Text("Your favourites", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackHome) { Icon(Icons.Default.ArrowBack, "Back to deals", tint = AccountGreen) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AccountBackground)
            )
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("No favourites yet", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Tap the heart on a deal to keep it here. Your favourites sync to your account when Firebase is configured.", color = AccountMuted)
                    Button(onClick = onBackHome, colors = ButtonDefaults.buttonColors(containerColor = AccountGreen, contentColor = Color(0xFF101828))) { Text("Explore deals") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Text("${favorites.size} saved favourites", color = AccountMuted, style = MaterialTheme.typography.bodyMedium) }
                items(favorites, key = { it.id }) { deal ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = AccountSurface)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProductPhoto(deal = deal, modifier = Modifier.fillMaxWidth().height(150.dp))
                            Text(deal.storeName, color = AccountGreen, fontWeight = FontWeight.Bold)
                            Text(deal.productName, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(AccountCurrency.format(deal.salePrice), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                                if (deal.discountPercent > 0) Text("${deal.discountPercent}% OFF", color = AccountGreen, fontWeight = FontWeight.Bold)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onOpenDeal(deal) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccountGreen, contentColor = Color(0xFF101828))
                                ) { Text("View deal") }
                                TextButton(onClick = { viewModel.toggleFavoriteDeal(deal) }) { Text("Remove", color = Color(0xFFFF8B8B)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackSaveDealDetailScreen(viewModel: StackSaveViewModel, onBackHome: () -> Unit) {
    val deal by viewModel.selectedDealForDetail.collectAsStateWithLifecycle()
    val savedDeals by viewModel.savedDeals.collectAsStateWithLifecycle()
    val favoriteDeals by viewModel.favoriteDeals.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentDeal = deal

    Scaffold(
        containerColor = AccountBackground,
        topBar = {
            TopAppBar(
                title = { Text("Deal details", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackHome) { Icon(Icons.Default.ArrowBack, "Back to deals", tint = AccountGreen) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AccountBackground)
            )
        }
    ) { padding ->
        if (currentDeal == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Select a deal from the home screen.", color = AccountMuted)
            }
        } else {
            val isSaved = savedDeals.any { it.id == currentDeal.id }
            val isFavorite = favoriteDeals.any { it.id == currentDeal.id }
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = AccountSurface), shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProductPhoto(deal = currentDeal, modifier = Modifier.fillMaxWidth().height(230.dp))
                        Text(currentDeal.storeName, color = AccountGreen, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(currentDeal.productName, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                        Text(currentDeal.category + if (currentDeal.isLive) " • API PRICE" else " • SAMPLE", color = if (currentDeal.isLive) AccountGreen else AccountMuted)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(AccountCurrency.format(currentDeal.salePrice), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                            if (currentDeal.discountPercent > 0) {
                                Surface(color = AccountGreen, shape = RoundedCornerShape(10.dp)) {
                                    Text("${currentDeal.discountPercent}% OFF", color = Color(0xFF101828), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (currentDeal.originalPrice > currentDeal.salePrice) {
                            Text("Usual price: ${AccountCurrency.format(currentDeal.originalPrice)}", color = AccountMuted)
                            Text("You could save: ${AccountCurrency.format(currentDeal.saving)}", color = AccountGreen, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                        Text(currentDeal.description, color = Color.White)
                        if (currentDeal.cashbackRate > 0) Text("Cashback: ${currentDeal.cashbackRate}%", color = AccountGreen)
                        if (currentDeal.couponCode.isNotBlank()) Text("Coupon code: ${currentDeal.couponCode}", color = AccountGreen, fontWeight = FontWeight.Bold)
                        if (currentDeal.stockKnown) {
                            Text(if (currentDeal.inStock) "Stock: In stock (according to retailer feed)" else "Stock: Unavailable (according to retailer feed)", color = if (currentDeal.inStock) AccountGreen else Color(0xFFFF8B8B), fontWeight = FontWeight.SemiBold)
                        } else if (currentDeal.isLive) {
                            Text("Stock status: not confirmed by the feed", color = AccountMuted)
                        }
                        if (currentDeal.promotion.isNotBlank()) Text("Promotion: ${currentDeal.promotion}", color = AccountGreen)
                        if (currentDeal.lastFetchedAt.isNotBlank()) Text("Price checked: ${currentDeal.lastFetchedAt}", color = AccountMuted, style = MaterialTheme.typography.bodySmall)
                        if (currentDeal.validUntil.isNotBlank()) Text("Offer validity / feed timestamp: ${currentDeal.validUntil}", color = AccountMuted, style = MaterialTheme.typography.bodySmall)
                        Text(
                            if (currentDeal.isLive) "Live catalogue data can change and may vary by branch, delivery area or membership. Confirm the final price with the retailer."
                            else "Illustrative sample offer. Confirm current price, availability and terms with the retailer.",
                            color = AccountMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Button(
                    onClick = { viewModel.toggleSavedDeal(currentDeal) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSaved) AccountSurface else AccountGreen, contentColor = if (isSaved) Color.White else Color(0xFF101828)),
                    shape = RoundedCornerShape(16.dp)
                ) { Text(if (isSaved) "Remove from basket" else "Add to basket", fontWeight = FontWeight.Bold) }
                Button(
                    onClick = { viewModel.toggleFavoriteDeal(currentDeal) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccountSurface, contentColor = if (isFavorite) Color(0xFFFF6B81) else Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) { Text(if (isFavorite) "Remove from favourites" else "Add to favourites", fontWeight = FontWeight.Bold) }
                if (currentDeal.productUrl.isNotBlank()) {
                    TextButton(
                        onClick = {
                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(currentDeal.productUrl))) }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Open retailer listing", color = AccountGreen) }
                }
                TextButton(onClick = onBackHome, modifier = Modifier.fillMaxWidth()) { Text("Continue exploring", color = AccountGreen) }
            }
        }
    }
}
