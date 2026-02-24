package com.nityapooja.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.theme.TempleGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("గోప్యతా విధానం", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Privacy Policy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
                Text(
                    "Last Updated: February 2026",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "NityaPooja (\"the App\") is a devotional companion app developed for personal spiritual practice. We are committed to protecting your privacy. This Privacy Policy explains what information the App collects and how it is used.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            PolicySection(
                title = "Information We Collect",
                content = "The App may collect the following information, all of which is stored locally on your device:\n\n" +
                        "- Name (optional, for personalization)\n" +
                        "- City/Location (for Panchangam calculations such as sunrise/sunset times)\n" +
                        "- Date and time of birth (optional, for astrological features like Jataka Chakram and Guna Milan)\n" +
                        "- Bookmarks, reading history, and japa session data\n" +
                        "- App preferences (theme, font size, notification settings)"
            )

            PolicySection(
                title = "How We Use Your Information",
                content = "All information is used solely to provide App functionality:\n\n" +
                        "- Location data is used to calculate accurate Panchangam (sunrise, sunset, Rahu Kaal, etc.) for your city\n" +
                        "- Birth information is used for astrological calculations within the App\n" +
                        "- Bookmarks and history are used to personalize your experience\n" +
                        "- Preferences are used to customize the App appearance and notifications"
            )

            PolicySection(
                title = "Data Storage & Security",
                content = "All your personal data is stored locally on your device using Android's secure storage mechanisms (Room Database and DataStore). We do not transmit, upload, or share any personal data to external servers or third parties.\n\n" +
                        "The only network activity is downloading devotional audio content from our content servers. No personal data is sent during these downloads."
            )

            PolicySection(
                title = "Third-Party Services",
                content = "The App integrates the following third-party services:\n\n" +
                        "- Google AdMob: Used to display non-intrusive banner advertisements. AdMob may collect device identifiers (Advertising ID) and usage data to serve relevant ads. This data is processed by Google in accordance with Google's Privacy Policy (https://policies.google.com/privacy).\n\n" +
                        "- Firebase Crashlytics: Used to collect anonymous crash reports and performance data to improve App stability. No personally identifiable information is collected.\n\n" +
                        "- Spotify SDK: Used optionally to play devotional music. Requires user-initiated connection to their Spotify account.\n\n" +
                        "We do not sell, share, or transmit your personal data to any other third parties."
            )

            PolicySection(
                title = "Advertising",
                content = "The App displays banner advertisements provided by Google AdMob. These ads help support continued development and keep the App free.\n\n" +
                        "AdMob may use your device's Advertising ID to show personalized ads. You can opt out of personalized advertising through your device's settings (Settings > Privacy > Ads on Android).\n\n" +
                        "We do not share any of your personal data (name, location, birth details) with advertisers."
            )

            PolicySection(
                title = "Data Deletion",
                content = "You can delete all your personal data at any time by going to Settings > Data & Privacy > Clear My Data. This will remove all bookmarks, reading history, japa sessions, and preferences. You can also uninstall the App to remove all data from your device."
            )

            PolicySection(
                title = "Children's Privacy",
                content = "The App does not knowingly collect personal information from children under 13. The App is a devotional tool suitable for all ages and does not require account creation or personal data submission."
            )

            PolicySection(
                title = "Changes to This Policy",
                content = "We may update this Privacy Policy from time to time. Any changes will be reflected in the App with an updated date. Continued use of the App after changes constitutes acceptance of the updated policy."
            )

            PolicySection(
                title = "Contact Us",
                content = "If you have any questions about this Privacy Policy, please contact us at:\n\nnityapooja.contact@yahoo.com"
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    GlassmorphicCard(cornerRadius = 16.dp, contentPadding = 16.dp) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TempleGold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            content,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
