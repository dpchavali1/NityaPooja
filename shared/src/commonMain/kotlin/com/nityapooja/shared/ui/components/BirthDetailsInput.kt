package com.nityapooja.shared.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.ui.theme.TempleGold
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round

/**
 * Shared birth details input form used by Jataka Chakram and Guna Milan.
 */

data class BirthDetails(
    val name: String = "",
    val year: Int = 1990,
    val month: Int = 1,
    val day: Int = 1,
    val hour: Int = 6,
    val minute: Int = 0,
    val city: String = "Hyderabad",
    val latitude: Double = 17.385,
    val longitude: Double = 78.4867,
    val timezoneOffsetHours: Double = 5.5,
)

/**
 * Platform-specific date picker. Shows a native date picker dialog.
 * Calls onDateSelected(year, month, day) when user picks a date.
 */
@Composable
expect fun PlatformDatePicker(
    year: Int,
    month: Int,
    day: Int,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit,
)

/**
 * Platform-specific time picker. Shows a native time picker dialog.
 * Calls onTimeSelected(hour, minute) when user picks a time.
 */
@Composable
expect fun PlatformTimePicker(
    hour: Int,
    minute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
)

/**
 * Platform-specific place search result.
 */
data class PlaceResult(
    val displayName: String,
    val subtitle: String,
    val lat: Double,
    val lng: Double,
    val tzOffsetHours: Double,
)

/**
 * Platform-specific place search function.
 * Returns list of matching places for the given query.
 */
expect suspend fun searchPlaces(query: String): List<PlaceResult>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDetailsInput(
    label: String,
    details: BirthDetails,
    onDetailsChange: (BirthDetails) -> Unit,
    showNameField: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        PlatformDatePicker(
            year = details.year,
            month = details.month,
            day = details.day,
            onDateSelected = { y, m, d ->
                onDetailsChange(details.copy(year = y, month = m, day = d))
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }

    if (showTimePicker) {
        PlatformTimePicker(
            hour = details.hour,
            minute = details.minute,
            onTimeSelected = { h, m ->
                onDetailsChange(details.copy(hour = h, minute = m))
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TempleGold,
            )

            if (showNameField) {
                OutlinedTextField(
                    value = details.name,
                    onValueChange = { onDetailsChange(details.copy(name = it)) },
                    label = { Text("పేరు / Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TempleGold,
                        focusedLabelColor = TempleGold,
                        cursorColor = TempleGold,
                    ),
                )
            }

            // Date picker row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.CalendarMonth, "Date", tint = TempleGold, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("జన్మ తేదీ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${details.day.toString().padStart(2, '0')}/${details.month.toString().padStart(2, '0')}/${details.year.toString().padStart(4, '0')}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }

                // Time picker
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePicker = true },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.AccessTime, "Time", tint = TempleGold, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("జన్మ సమయం", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val amPm = if (details.hour < 12) "AM" else "PM"
                            val displayHour = when {
                                details.hour == 0 -> 12
                                details.hour > 12 -> details.hour - 12
                                else -> details.hour
                            }
                            Text(
                                "$displayHour:${details.minute.toString().padStart(2, '0')} $amPm",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            // City / Location — searchable
            PlaceSearchField(
                currentCity = details.city,
                currentLat = details.latitude,
                currentLng = details.longitude,
                onPlaceSelected = { city, lat, lng, tzOffset ->
                    onDetailsChange(
                        details.copy(
                            city = city,
                            latitude = lat,
                            longitude = lng,
                            timezoneOffsetHours = tzOffset,
                        )
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceSearchField(
    currentCity: String,
    currentLat: Double,
    currentLng: Double,
    onPlaceSelected: (city: String, lat: Double, lng: Double, tzOffset: Double) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val coordsText = remember(currentLat, currentLng) {
        val latDir = if (currentLat >= 0) "N" else "S"
        val lngDir = if (currentLng >= 0) "E" else "W"
        val latAbs = abs(currentLat)
        val lngAbs = abs(currentLng)
        val latInt = latAbs.toInt()
        val latFrac = ((latAbs - latInt) * 10000).toInt()
        val lngInt = lngAbs.toInt()
        val lngFrac = ((lngAbs - lngInt) * 10000).toInt()
        "$latInt.${latFrac.toString().padStart(4, '0')}°$latDir, $lngInt.${lngFrac.toString().padStart(4, '0')}°$lngDir"
    }

    Column {
        if (!isSearching) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isSearching = true },
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.LocationOn, "Location", tint = TempleGold, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("జన్మ ప్రదేశం / Birthplace", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            currentCity,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            coordsText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(Icons.Default.Search, "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        } else {
            OutlinedTextField(
                value = query,
                onValueChange = { newQuery ->
                    query = newQuery
                    if (newQuery.length >= 2) {
                        scope.launch {
                            isLoading = true
                            suggestions = searchPlaces(newQuery)
                            showSuggestions = suggestions.isNotEmpty()
                            isLoading = false
                        }
                    } else {
                        suggestions = emptyList()
                        showSuggestions = false
                    }
                },
                label = { Text("నగరం / City or Place") },
                placeholder = { Text("Type city name...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Search, "Search", tint = TempleGold)
                },
                trailingIcon = {
                    IconButton(onClick = {
                        query = ""
                        suggestions = emptyList()
                        showSuggestions = false
                        isSearching = false
                    }) {
                        Icon(Icons.Default.Close, "Close")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TempleGold,
                    focusedLabelColor = TempleGold,
                    cursorColor = TempleGold,
                ),
            )

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    color = TempleGold,
                )
            }

            if (showSuggestions) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column {
                        suggestions.forEach { place ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onPlaceSelected(place.displayName, place.lat, place.lng, place.tzOffsetHours)
                                        query = ""
                                        suggestions = emptyList()
                                        showSuggestions = false
                                        isSearching = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    "Place",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        place.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Text(
                                        place.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                val sign = if (place.tzOffsetHours >= 0) "+" else ""
                                Text(
                                    "UTC$sign${place.tzOffsetHours}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TempleGold,
                                )
                            }
                            if (place != suggestions.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Fallback city list — used on all platforms when geocoder unavailable
// ═══════════════════════════════════════════════════════════

internal data class FallbackCity(
    val name: String,
    val country: String,
    val lat: Double,
    val lng: Double,
    val tzOffset: Double,
)

internal val FALLBACK_CITIES = listOf(
    // India
    FallbackCity("Hyderabad", "India", 17.385, 78.4867, 5.5),
    FallbackCity("Delhi", "India", 28.6139, 77.2090, 5.5),
    FallbackCity("Mumbai", "India", 19.0760, 72.8777, 5.5),
    FallbackCity("Chennai", "India", 13.0827, 80.2707, 5.5),
    FallbackCity("Bangalore", "India", 12.9716, 77.5946, 5.5),
    FallbackCity("Kolkata", "India", 22.5726, 88.3639, 5.5),
    FallbackCity("Pune", "India", 18.5204, 73.8567, 5.5),
    FallbackCity("Ahmedabad", "India", 23.0225, 72.5714, 5.5),
    FallbackCity("Jaipur", "India", 26.9124, 75.7873, 5.5),
    FallbackCity("Visakhapatnam", "India", 17.6868, 83.2185, 5.5),
    FallbackCity("Vijayawada", "India", 16.5062, 80.6480, 5.5),
    FallbackCity("Tirupati", "India", 13.6288, 79.4192, 5.5),
    FallbackCity("Varanasi", "India", 25.3176, 82.9739, 5.5),
    FallbackCity("Lucknow", "India", 26.8467, 80.9462, 5.5),
    FallbackCity("Coimbatore", "India", 11.0168, 76.9558, 5.5),
    FallbackCity("Indore", "India", 22.7196, 75.8577, 5.5),
    FallbackCity("Bhopal", "India", 23.2599, 77.4126, 5.5),
    FallbackCity("Nagpur", "India", 21.1458, 79.0882, 5.5),
    FallbackCity("Thiruvananthapuram", "India", 8.5241, 76.9366, 5.5),
    FallbackCity("Kochi", "India", 9.9312, 76.2673, 5.5),
    FallbackCity("Surat", "India", 21.1702, 72.8311, 5.5),
    FallbackCity("Patna", "India", 25.5941, 85.1376, 5.5),
    FallbackCity("Ranchi", "India", 23.3441, 85.3096, 5.5),
    FallbackCity("Chandigarh", "India", 30.7333, 76.7794, 5.5),
    FallbackCity("Guwahati", "India", 26.1445, 91.7362, 5.5),
    FallbackCity("Mysore", "India", 12.2958, 76.6394, 5.5),
    FallbackCity("Madurai", "India", 9.9252, 78.1198, 5.5),
    FallbackCity("Amritsar", "India", 31.6340, 74.8723, 5.5),
    FallbackCity("Warangal", "India", 17.9784, 79.5941, 5.5),
    FallbackCity("Guntur", "India", 16.3067, 80.4365, 5.5),
    FallbackCity("Nellore", "India", 14.4426, 79.9865, 5.5),
    FallbackCity("Rajahmundry", "India", 17.0005, 81.8040, 5.5),
    FallbackCity("Kakinada", "India", 16.9891, 82.2475, 5.5),
    FallbackCity("Kurnool", "India", 15.8281, 78.0373, 5.5),
    FallbackCity("Anantapur", "India", 14.6819, 77.6006, 5.5),
    FallbackCity("Kadapa", "India", 14.4674, 78.8241, 5.5),
    FallbackCity("Ongole", "India", 15.5057, 80.0499, 5.5),
    FallbackCity("Eluru", "India", 16.7107, 81.0952, 5.5),
    // USA
    FallbackCity("New York", "USA", 40.7128, -74.0060, -5.0),
    FallbackCity("Los Angeles", "USA", 34.0522, -118.2437, -8.0),
    FallbackCity("Chicago", "USA", 41.8781, -87.6298, -6.0),
    FallbackCity("Houston", "USA", 29.7604, -95.3698, -6.0),
    FallbackCity("San Francisco", "USA", 37.7749, -122.4194, -8.0),
    FallbackCity("Dallas", "USA", 32.7767, -96.7970, -6.0),
    FallbackCity("Seattle", "USA", 47.6062, -122.3321, -8.0),
    FallbackCity("Atlanta", "USA", 33.7490, -84.3880, -5.0),
    FallbackCity("Boston", "USA", 42.3601, -71.0589, -5.0),
    FallbackCity("Phoenix", "USA", 33.4484, -112.0740, -7.0),
    FallbackCity("Denver", "USA", 39.7392, -104.9903, -7.0),
    FallbackCity("Washington DC", "USA", 38.9072, -77.0369, -5.0),
    FallbackCity("Edison", "USA", 40.5187, -74.4121, -5.0),
    FallbackCity("Jersey City", "USA", 40.7178, -74.0431, -5.0),
    FallbackCity("Fremont", "USA", 37.5485, -121.9886, -8.0),
    FallbackCity("Sunnyvale", "USA", 37.3688, -122.0363, -8.0),
    FallbackCity("San Jose", "USA", 37.3382, -121.8863, -8.0),
    FallbackCity("Irving", "USA", 32.8140, -96.9489, -6.0),
    FallbackCity("Plano", "USA", 33.0198, -96.6989, -6.0),
    // UK / Europe
    FallbackCity("London", "UK", 51.5074, -0.1278, 0.0),
    FallbackCity("Paris", "France", 48.8566, 2.3522, 1.0),
    FallbackCity("Berlin", "Germany", 52.5200, 13.4050, 1.0),
    FallbackCity("Amsterdam", "Netherlands", 52.3676, 4.9041, 1.0),
    FallbackCity("Rome", "Italy", 41.9028, 12.4964, 1.0),
    FallbackCity("Madrid", "Spain", 40.4168, -3.7038, 1.0),
    FallbackCity("Moscow", "Russia", 55.7558, 37.6173, 3.0),
    // Middle East
    FallbackCity("Dubai", "UAE", 25.2048, 55.2708, 4.0),
    FallbackCity("Abu Dhabi", "UAE", 24.4539, 54.3773, 4.0),
    FallbackCity("Riyadh", "Saudi Arabia", 24.7136, 46.6753, 3.0),
    FallbackCity("Doha", "Qatar", 25.2854, 51.5310, 3.0),
    FallbackCity("Kuwait City", "Kuwait", 29.3759, 47.9774, 3.0),
    FallbackCity("Muscat", "Oman", 23.5880, 58.3829, 4.0),
    // Asia Pacific
    FallbackCity("Singapore", "Singapore", 1.3521, 103.8198, 8.0),
    FallbackCity("Tokyo", "Japan", 35.6762, 139.6503, 9.0),
    FallbackCity("Beijing", "China", 39.9042, 116.4074, 8.0),
    FallbackCity("Shanghai", "China", 31.2304, 121.4737, 8.0),
    FallbackCity("Hong Kong", "China", 22.3193, 114.1694, 8.0),
    FallbackCity("Bangkok", "Thailand", 13.7563, 100.5018, 7.0),
    FallbackCity("Sydney", "Australia", -33.8688, 151.2093, 11.0),
    FallbackCity("Melbourne", "Australia", -37.8136, 144.9631, 11.0),
    FallbackCity("Auckland", "New Zealand", -36.8485, 174.7633, 13.0),
    FallbackCity("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, 8.0),
    FallbackCity("Jakarta", "Indonesia", -6.2088, 106.8456, 7.0),
    FallbackCity("Seoul", "South Korea", 37.5665, 126.9780, 9.0),
    FallbackCity("Manila", "Philippines", 14.5995, 120.9842, 8.0),
    // South Asia
    FallbackCity("Karachi", "Pakistan", 24.8607, 67.0011, 5.0),
    FallbackCity("Lahore", "Pakistan", 31.5204, 74.3587, 5.0),
    FallbackCity("Islamabad", "Pakistan", 33.6844, 73.0479, 5.0),
    FallbackCity("Dhaka", "Bangladesh", 23.8103, 90.4125, 6.0),
    FallbackCity("Colombo", "Sri Lanka", 6.9271, 79.8612, 5.5),
    FallbackCity("Kathmandu", "Nepal", 27.7172, 85.3240, 5.75),
    // Africa
    FallbackCity("Johannesburg", "South Africa", -26.2041, 28.0473, 2.0),
    FallbackCity("Cape Town", "South Africa", -33.9249, 18.4241, 2.0),
    FallbackCity("Nairobi", "Kenya", -1.2921, 36.8219, 3.0),
    FallbackCity("Lagos", "Nigeria", 6.5244, 3.3792, 1.0),
    FallbackCity("Cairo", "Egypt", 30.0444, 31.2357, 2.0),
    // Americas
    FallbackCity("Toronto", "Canada", 43.6532, -79.3832, -5.0),
    FallbackCity("Vancouver", "Canada", 49.2827, -123.1207, -8.0),
    FallbackCity("São Paulo", "Brazil", -23.5505, -46.6333, -3.0),
    FallbackCity("Mexico City", "Mexico", 19.4326, -99.1332, -6.0),
)

/**
 * Helper to search fallback cities by name. Used by platform actual implementations
 * when geocoder is unavailable.
 */
fun searchFallbackCities(query: String): List<PlaceResult> {
    val lowerQuery = query.lowercase()
    return FALLBACK_CITIES
        .filter { it.name.lowercase().contains(lowerQuery) }
        .take(8)
        .map { city ->
            val latStr = "${city.lat.toInt()}.${((kotlin.math.abs(city.lat) - kotlin.math.abs(city.lat).toInt()) * 100).toInt().toString().padStart(2, '0')}"
            val lngStr = "${city.lng.toInt()}.${((kotlin.math.abs(city.lng) - kotlin.math.abs(city.lng).toInt()) * 100).toInt().toString().padStart(2, '0')}"
            PlaceResult(
                displayName = city.name,
                subtitle = "${city.country} ($latStr°, $lngStr°)",
                lat = city.lat,
                lng = city.lng,
                tzOffsetHours = city.tzOffset,
            )
        }
}

/**
 * Estimates UTC timezone offset from latitude/longitude.
 * Uses heuristic timezone region detection.
 */
fun getTimezoneOffsetFromCoords(lat: Double, lng: Double): Double {
    // India (roughly 68-97° E) is always +5.5
    if (lat in 6.0..37.0 && lng in 68.0..98.0) return 5.5

    // Rough longitude-based offset
    val roughOffset = lng / 15.0

    // Common timezone region overrides for better accuracy
    val knownOffset: Double? = when {
        lat in 24.0..50.0 && lng in -130.0..-60.0 -> when {
            lng < -115.0 -> -8.0  // Pacific
            lng < -100.0 -> -7.0  // Mountain
            lng < -85.0 -> -6.0   // Central
            else -> -5.0          // Eastern
        }
        lat in 49.0..61.0 && lng in -11.0..2.0 -> 0.0     // UK
        lat in 43.0..56.0 && lng in 2.0..25.0 -> 1.0       // Central Europe
        lat in 43.0..60.0 && lng in 25.0..45.0 -> 3.0      // Eastern Europe
        lat in 18.0..54.0 && lng in 98.0..135.0 -> 8.0     // China
        lat in 30.0..46.0 && lng in 125.0..146.0 -> 9.0    // Japan/Korea
        lat in -10.0..24.0 && lng in 95.0..120.0 -> 7.0    // SE Asia
        lat in -44.0..-10.0 && lng in 113.0..154.0 -> 10.0 // Australia
        lat in 12.0..42.0 && lng in 34.0..60.0 -> 4.0      // Middle East
        lat in 23.0..37.0 && lng in 60.0..78.0 -> 5.0      // Pakistan
        lat in 20.0..27.0 && lng in 88.0..93.0 -> 6.0      // Bangladesh
        lat in 5.0..30.0 && lng in 79.0..89.0 -> 5.5       // Sri Lanka/Nepal
        lat in -34.0..6.0 && lng in -74.0..-34.0 -> -3.0   // Brazil
        lat in -35.0..-22.0 && lng in 16.0..33.0 -> 2.0    // South Africa
        lat in -48.0..-34.0 && lng in 166.0..179.0 -> 12.0 // New Zealand
        else -> null
    }

    return knownOffset ?: round(roughOffset * 2.0) / 2.0
}
