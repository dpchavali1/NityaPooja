package com.nityapooja.shared.ui.store

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.ui.settings.SettingsViewModel
import com.nityapooja.shared.ui.theme.TempleGold

// Data Model

data class StoreProduct(
    val name: String,
    val imageUrl: String,
    val price: String,
    val originalPrice: String,
    val rating: Float,
    val reviewCount: Int,
    val amazonAsin: String,
    val category: String,
)

// Affiliate URL Builder

private fun buildAmazonUrl(asin: String, isUS: Boolean): String {
    return if (isUS) {
        "https://www.amazon.com/dp/$asin?tag=syncflow-20&utm_source=nityapooja"
    } else {
        "https://www.amazon.in/dp/$asin?tag=syncflowin-21&utm_source=nityapooja"
    }
}

private fun isUSTimezone(timezone: String): Boolean {
    return timezone.startsWith("America/") || timezone.startsWith("US/") || timezone == "Pacific/Honolulu"
}

// Product Data

private val categories = listOf("All", "Incense", "Diyas", "Idols", "Puja Thali", "Books", "Rudraksha", "Camphor")

// US Amazon Products (amazon.com, USD prices)
private val usProducts = listOf(
    // Incense
    StoreProduct("Satya Nag Champa Incense 100g Pack of 2", "https://m.media-amazon.com/images/I/71aZe02ZZAL._SL1000_.jpg", "$7.99", "$12.99", 4.7f, 45000, "B004C20O8A", "Incense"),
    StoreProduct("HEM Sandalwood Incense Sticks Pack of 6", "https://m.media-amazon.com/images/I/91UJYow0hHL._AC_SL1500_.jpg", "$7.80", "$12.99", 4.5f, 18000, "B001G3ZPAA", "Incense"),
    StoreProduct("Satya Nag Champa Agarbatti 12 Pack", "https://m.media-amazon.com/images/I/91lKAkPiRsL._AC_SL1500_.jpg", "$7.57", "$14.99", 4.7f, 32000, "B0118PZQWM", "Incense"),
    StoreProduct("HEM Dragon's Blood Incense 12 Pack", "https://m.media-amazon.com/images/I/616b+RjHOmL._AC_SL1280_.jpg", "$6.99", "$10.99", 4.5f, 12000, "B07YDCH5FG", "Incense"),
    // Diyas
    StoreProduct("Rastogi Handicrafts Pure Brass Diya Deepak", "https://m.media-amazon.com/images/I/61RKvVnX5rL._AC_SL1500_.jpg", "$22.50", "$34.99", 4.4f, 3200, "B01LWM1SB7", "Diyas"),
    StoreProduct("Hashcart Brass Kuber Diya Pack of 8", "https://m.media-amazon.com/images/I/71TCS1aRtlL._AC_SL1500_.jpg", "$24.99", "$34.99", 4.3f, 2800, "B01M63BMS0", "Diyas"),
    StoreProduct("SATVIK Large Brass Aarti Diya with Handle", "https://m.media-amazon.com/images/I/612W8e6aBeL._AC_SL1500_.jpg", "$64.99", "$89.99", 4.5f, 1500, "B09MTPCHKS", "Diyas"),
    StoreProduct("Collectible India Brass Lotus Diya Set of 2", "https://m.media-amazon.com/images/I/61dSvb6f3fL._AC_SL1500_.jpg", "$16.99", "$24.99", 4.3f, 1800, "B07HLSKDR6", "Diyas"),
    // Idols
    StoreProduct("ASHIRWAD Brass Lord Ganesha Idol 4.9 Inch", "https://m.media-amazon.com/images/I/71mcbyFr0oL._AC_SL1500_.jpg", "$24.99", "$39.99", 4.6f, 3500, "B08CBHF5VS", "Idols"),
    StoreProduct("CraftVatika Large Goddess Lakshmi Statue 9\"", "https://m.media-amazon.com/images/I/81QDANtCO7L._AC_SL1500_.jpg", "$119.00", "$159.99", 4.7f, 1200, "B00RMZ323I", "Idols"),
    StoreProduct("Mini Ganesha Brass Idol for Car Dashboard", "https://m.media-amazon.com/images/I/71ZBHnGHLgL._AC_SL1500_.jpg", "$19.33", "$29.99", 4.4f, 4500, "B0CH8NWSL8", "Idols"),
    StoreProduct("Brass Blessing Hanuman Idol 9.5 Inch", "https://m.media-amazon.com/images/I/81ugO2iR3lL._SL1500_.jpg", "$49.99", "$79.99", 4.6f, 2100, "B07MJ32XGG", "Idols"),
    // Puja Thali
    StoreProduct("Hashcart Brass Puja Thali Set 8.75 Inch", "https://m.media-amazon.com/images/I/618pDXMlhTL._AC_SL1024_.jpg", "$64.99", "$89.99", 4.5f, 2200, "B08H8JSLCN", "Puja Thali"),
    StoreProduct("BENGALEN Silver Plated Pooja Thali Set 8\"", "https://m.media-amazon.com/images/I/61h5+2AiG9L._AC_SL1148_.jpg", "$29.00", "$44.99", 4.4f, 1800, "B086ZHCB4D", "Puja Thali"),
    StoreProduct("Stainless Steel Pooja Thali Set 12\" Gold", "https://m.media-amazon.com/images/I/91cPDP4GiJL._AC_SL1500_.jpg", "$19.99", "$34.99", 4.3f, 3500, "B0CNNV1DTT", "Puja Thali"),
    StoreProduct("RoyaltyRoute Copper Puja Thali Set", "https://m.media-amazon.com/images/I/81EjscNc8bL._AC_SL1500_.jpg", "$24.99", "$39.99", 4.4f, 1500, "B00I6GPW9Y", "Puja Thali"),
    // Books
    StoreProduct("Bhagavad Gita As It Is - Hardcover", "https://m.media-amazon.com/images/I/71ypDjKjDeL._SL1400_.jpg", "$8.29", "$16.00", 4.8f, 28000, "089213268X", "Books"),
    StoreProduct("The Bhagavad Gita - Eknath Easwaran", "https://m.media-amazon.com/images/I/71nDBHiOzIL._SL1500_.jpg", "$6.99", "$15.95", 4.7f, 18000, "1586380192", "Books"),
    StoreProduct("Hanuman Chalisa - Shubha Vilas", "https://m.media-amazon.com/images/I/81Q8+LQd4VL._SL1500_.jpg", "$9.99", "$14.99", 4.6f, 5200, "9354405584", "Books"),
    StoreProduct("Hanuman Chalisa Pocket Book Hindi + English", "https://m.media-amazon.com/images/I/81SFULSkOLL._SL1500_.jpg", "$7.49", "$12.99", 4.5f, 3800, "B0C38S1T4Y", "Books"),
    // Rudraksha
    StoreProduct("Rudraksha Mala 5 Mukhi 108 Beads Certified", "https://m.media-amazon.com/images/I/71vXBn744rL._AC_SL1500_.jpg", "$11.80", "$24.99", 4.3f, 4800, "B0BCZ62FC4", "Rudraksha"),
    StoreProduct("Nepali Rudraksha Mala 5 Mukhi 108 Beads", "https://m.media-amazon.com/images/I/815Ynn0E9wL._AC_SL1500_.jpg", "$16.75", "$29.99", 4.4f, 3500, "B00I6Z67AS", "Rudraksha"),
    StoreProduct("Storite Rudraksha Japa Mala 108+1 Beads", "https://m.media-amazon.com/images/I/61bIV3UZ6mL._AC_SL1200_.jpg", "$8.99", "$14.99", 4.2f, 6200, "B00NYA3N98", "Rudraksha"),
    StoreProduct("IS4A Rudraksha Japa Mala 108+1 Prayer Beads", "https://m.media-amazon.com/images/I/71iTwr+wbfL._AC_SL1050_.jpg", "$8.99", "$16.99", 4.3f, 5100, "B014FOZD4Y", "Rudraksha"),
    // Camphor
    StoreProduct("333 Pure Camphor Tablets 250g for Puja", "https://m.media-amazon.com/images/I/41m0TPazoHL._AC_.jpg", "$24.99", "$34.99", 4.5f, 8500, "B01JB5U9JG", "Camphor"),
    StoreProduct("Pure Camphor Tablets 50g Pack of 2 Puja", "https://m.media-amazon.com/images/I/41N+Ch4CFAL._AC_.jpg", "$16.40", "$24.99", 4.3f, 6200, "B0795NH37L", "Camphor"),
    StoreProduct("Pure Camphor Blocks 140g 18 Tablets", "https://m.media-amazon.com/images/I/711zINIHucL._AC_SL1500_.jpg", "$15.88", "$22.99", 4.4f, 3800, "B0D31BRZ91", "Camphor"),
    StoreProduct("Pure Camphor Blocks 300g 64 Tablets Natural", "https://m.media-amazon.com/images/I/71fF9ev61eL._AC_SL1500_.jpg", "$19.99", "$29.99", 4.5f, 2900, "B0D53ZFF4H", "Camphor"),
)

// India Amazon Products (amazon.in, INR prices)
private val indiaProducts = listOf(
    // Incense
    StoreProduct("Cycle Pure Agarbatti Three in One", "https://m.media-amazon.com/images/I/71wJ6jMxkeL._SL1500_.jpg", "\u20B9199", "\u20B9299", 4.4f, 12500, "B00Q8KMAGI", "Incense"),
    StoreProduct("Zed Black Manthan Dhoop Sticks", "https://m.media-amazon.com/images/I/61A+9VPq1CL._SL1500_.jpg", "\u20B9249", "\u20B9399", 4.3f, 8900, "B0926ZZ2HG", "Incense"),
    StoreProduct("Hem Precious Assorted Incense Sticks", "https://m.media-amazon.com/images/I/61uIccGIpHL._SL1000_.jpg", "\u20B9150", "\u20B9220", 4.2f, 5600, "B00MGNRAWK", "Incense"),
    StoreProduct("Cycle Pure Woods Natural Agarbatti", "https://m.media-amazon.com/images/I/815+8GVQw3L._SL1500_.jpg", "\u20B9175", "\u20B9250", 4.3f, 3200, "B074KCWMFB", "Incense"),
    // Diyas
    StoreProduct("Borosil Akhand Diya Medium Brass", "https://m.media-amazon.com/images/I/61tFDSCoNGL._SL1000_.jpg", "\u20B9699", "\u20B91,299", 4.5f, 7800, "B00EZMNH9A", "Diyas"),
    StoreProduct("LED Flameless Tealight Diya Set of 12", "https://m.media-amazon.com/images/I/61GWvbaakVL._SL1500_.jpg", "\u20B9199", "\u20B9399", 4.3f, 4500, "B09KGQBXPF", "Diyas"),
    StoreProduct("Borosil Akhand Diya Large Brass", "https://m.media-amazon.com/images/I/61AG6SgUWtL._SL1000_.jpg", "\u20B9899", "\u20B91,499", 4.6f, 2300, "B00EZMNHHW", "Diyas"),
    StoreProduct("Terracotta Decorative Akhand Diya", "https://m.media-amazon.com/images/I/810LveIQuqL._SL1500_.jpg", "\u20B9349", "\u20B9599", 4.0f, 6100, "B01M7SYMTC", "Diyas"),
    // Idols
    StoreProduct("Brass Large Ganesh Idol Murti", "https://m.media-amazon.com/images/I/71r+46zauRL._SL1121_.jpg", "\u20B9799", "\u20B91,299", 4.7f, 9200, "B013LTDRPG", "Idols"),
    StoreProduct("Brass Goddess Lakshmi Idol 6 Inch", "https://m.media-amazon.com/images/I/811Nf8Dko6L._SL1500_.jpg", "\u20B9699", "\u20B91,199", 4.4f, 3800, "B08G1R85NF", "Idols"),
    StoreProduct("Brass Hanuman Idol 9.5 Inch", "https://m.media-amazon.com/images/I/81ugO2iR3lL._SL1500_.jpg", "\u20B91,299", "\u20B91,999", 4.6f, 2100, "B07MJ32XGG", "Idols"),
    StoreProduct("Brass Radha Krishna Statue Pair", "https://m.media-amazon.com/images/I/91UMJZzWpHL._SL1500_.jpg", "\u20B92,999", "\u20B94,999", 4.8f, 1500, "B01CNMQG0K", "Idols"),
    // Puja Thali
    StoreProduct("German Silver Pooja Thali Set 11 Items", "https://m.media-amazon.com/images/I/61jEjukjGZL._SL1080_.jpg", "\u20B9699", "\u20B91,199", 4.5f, 5400, "B0BW4T4JV9", "Puja Thali"),
    StoreProduct("Brass Puja Thali Set Complete", "https://m.media-amazon.com/images/I/61dGEqKWm7L._SL1000_.jpg", "\u20B91,499", "\u20B92,199", 4.6f, 3200, "B00EZMNMJK", "Puja Thali"),
    StoreProduct("German Silver Pooja Thali 10 Items", "https://m.media-amazon.com/images/I/71YzFFhmoDL._SL1080_.jpg", "\u20B9599", "\u20B9999", 4.2f, 7600, "B07J37KQZW", "Puja Thali"),
    StoreProduct("Pure Copper Kalash for Puja", "https://m.media-amazon.com/images/I/61yrISNCEuL._SL1500_.jpg", "\u20B9549", "\u20B9899", 4.4f, 2800, "B0GDFM234H", "Puja Thali"),
    // Books
    StoreProduct("Shrimad Bhagwat Gita Gita Press", "https://m.media-amazon.com/images/I/310OuT3+zRL._SL500_.jpg", "\u20B935", "\u20B975", 4.8f, 25000, "B06XXTZHRN", "Books"),
    StoreProduct("Hanuman Chalisa Gita Press", "https://m.media-amazon.com/images/I/81Yokrz107L._SL1136_.jpg", "\u20B925", "\u20B950", 4.5f, 11000, "B07TJXMP4B", "Books"),
    StoreProduct("Sunderkand with Hanuman Chalisa", "https://m.media-amazon.com/images/I/A17ObNxMRJL._SL1500_.jpg", "\u20B949", "\u20B999", 4.6f, 8500, "B092RG8BPZ", "Books"),
    StoreProduct("Ramcharitmanas Gita Press", "https://m.media-amazon.com/images/I/81R404zE3lL._SL1500_.jpg", "\u20B9495", "\u20B9700", 4.7f, 6200, "B09BD6P7M3", "Books"),
    // Rudraksha
    StoreProduct("5 Mukhi Rudraksha Mala 108 Beads", "https://m.media-amazon.com/images/I/61QzUMGN0+S._SL1142_.jpg", "\u20B9399", "\u20B9799", 4.3f, 4100, "B078HT76H7", "Rudraksha"),
    StoreProduct("Original Nepali 5 Mukhi Rudraksha", "https://m.media-amazon.com/images/I/81yZYYCRObL._SL1500_.jpg", "\u20B9415", "\u20B9799", 4.2f, 3500, "B00I6Z66DG", "Rudraksha"),
    StoreProduct("5 Mukhi Rudraksha Bracelet Certified", "https://m.media-amazon.com/images/I/710OrnYuk-L._SL1500_.jpg", "\u20B9449", "\u20B9799", 4.4f, 5800, "B0C3M5L1X3", "Rudraksha"),
    StoreProduct("Rudraksha Mala Original Certified", "https://m.media-amazon.com/images/I/51Dfskp3IUL._SL500_.jpg", "\u20B9599", "\u20B91,199", 4.3f, 890, "B0CJTTPJWW", "Rudraksha"),
    // Camphor
    StoreProduct("Mangalam Camphor Tablet 100g Pure", "https://m.media-amazon.com/images/I/61ny0s3+UfL._SL1000_.jpg", "\u20B9195", "\u20B9299", 4.5f, 15000, "B0725N7D7D", "Camphor"),
    StoreProduct("Mangalam Bhimseni Camphor 100g", "https://m.media-amazon.com/images/I/71GnKrgPmeL._SL1500_.jpg", "\u20B9280", "\u20B9399", 4.4f, 8700, "B07ZJSTTHM", "Camphor"),
    StoreProduct("Cycle Om Shanthi Pure Camphor", "https://m.media-amazon.com/images/I/51xrX+Lu-SL._SL1000_.jpg", "\u20B9199", "\u20B9349", 4.3f, 6300, "B09ZPNG422", "Camphor"),
    StoreProduct("Mangalam Camphor Diffuser Wooden", "https://m.media-amazon.com/images/I/61ELjjfMMdL._SL1080_.jpg", "\u20B9499", "\u20B9799", 4.6f, 2100, "B0C71BMZ82", "Camphor"),
)

// Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevotionalStoreScreen(
    onBack: () -> Unit = {},
    settingsViewModel: SettingsViewModel = koinViewModel(),
) {
    val uriHandler = LocalUriHandler.current
    var selectedCategory by remember { mutableStateOf("All") }

    val timezone by settingsViewModel.locationTimezone.collectAsState()
    val isUS = remember(timezone) { isUSTimezone(timezone) }
    val products = if (isUS) usProducts else indiaProducts

    val filteredProducts = remember(selectedCategory, isUS) {
        if (selectedCategory == "All") products
        else products.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("\u0C2D\u0C15\u0C4D\u0C24\u0C3F \u0C38\u0C4D\u0C1F\u0C4B\u0C30\u0C4D", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            if (isUS) "Devotional Store (Amazon.com)" else "Devotional Store (Amazon.in)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Category filter chips
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleGold.copy(alpha = 0.2f),
                            selectedLabelColor = TempleGold,
                        ),
                    )
                }
            }

            // Product grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filteredProducts, key = { it.amazonAsin }) { product ->
                    ProductCard(
                        product = product,
                        onClick = {
                            val url = buildAmazonUrl(product.amazonAsin, isUS)
                            uriHandler.openUri(url)
                        },
                    )
                }
            }

            // Affiliate disclosure
            Text(
                "As an Amazon Associate, we earn from qualifying purchases. Prices shown are approximate and may vary.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: StoreProduct,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            )

            Column(modifier = Modifier.padding(10.dp)) {
                // Product name
                Text(
                    product.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(4.dp))

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = TempleGold,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        "${product.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TempleGold,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "(${formatCount(product.reviewCount)})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Price
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        product.price,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        product.originalPrice,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Buy button
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Buy on Amazon", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return if (count >= 1000) "${count / 1000}.${(count % 1000) / 100}k"
    else count.toString()
}
