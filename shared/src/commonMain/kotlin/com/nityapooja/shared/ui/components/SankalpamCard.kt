package com.nityapooja.shared.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.ui.panchangam.PanchangamData
import com.nityapooja.shared.utils.JyotishConstants
import com.nityapooja.shared.ui.theme.NityaPoojaTextStyles
import com.nityapooja.shared.ui.theme.TempleGold

// Deity names in Telugu for sankalpam
private val deityNameTeluguMap = mapOf(
    "ganesh" to "గణేశ",
    "shiva" to "శివ",
    "lakshmi" to "లక్ష్మీ",
    "vishnu" to "విష్ణు",
    "saraswathi" to "సరస్వతి",
    "durga" to "దుర్గా",
    "hanuman" to "హనుమాన్",
    "satyanarayan" to "సత్యనారాయణ",
    "general" to "పరమేశ్వర",
)

private val deityNameEnglishMap = mapOf(
    "ganesh" to "Ganesha",
    "shiva" to "Shiva",
    "lakshmi" to "Lakshmi",
    "vishnu" to "Vishnu",
    "saraswathi" to "Saraswathi",
    "durga" to "Durga",
    "hanuman" to "Hanuman",
    "satyanarayan" to "Satyanarayan",
    "general" to "Parameshwara",
)

// Country mapping from IANA timezone to Telugu/English country name
private val timezoneCountryMap = mapOf(
    "America/New_York" to ("అమెరికా" to "America"),
    "America/Chicago" to ("అమెరికా" to "America"),
    "America/Denver" to ("అమెరికా" to "America"),
    "America/Los_Angeles" to ("అమెరికా" to "America"),
    "America/Detroit" to ("అమెరికా" to "America"),
    "America/Indiana" to ("అమెరికా" to "America"),
    "America/Phoenix" to ("అమెరికా" to "America"),
    "America/Anchorage" to ("అమెరికా" to "America"),
    "America/Boise" to ("అమెరికా" to "America"),
    "US/Eastern" to ("అమెరికా" to "America"),
    "US/Central" to ("అమెరికా" to "America"),
    "US/Mountain" to ("అమెరికా" to "America"),
    "US/Pacific" to ("అమెరికా" to "America"),
    "America/Toronto" to ("కెనడా" to "Canada"),
    "America/Vancouver" to ("కెనడా" to "Canada"),
    "America/Edmonton" to ("కెనడా" to "Canada"),
    "America/Winnipeg" to ("కెనడా" to "Canada"),
    "America/Halifax" to ("కెనడా" to "Canada"),
    "Canada/Eastern" to ("కెనడా" to "Canada"),
    "Canada/Central" to ("కెనడా" to "Canada"),
    "Canada/Pacific" to ("కెనడా" to "Canada"),
    "Europe/London" to ("ఇంగ్లండ్" to "England"),
    "Europe/Dublin" to ("ఐర్లాండ్" to "Ireland"),
    "Europe/Berlin" to ("జర్మనీ" to "Germany"),
    "Europe/Paris" to ("ఫ్రాన్స్" to "France"),
    "Europe/Amsterdam" to ("నెదర్లాండ్స్" to "Netherlands"),
    "Europe/Zurich" to ("స్విట్జర్లాండ్" to "Switzerland"),
    "Europe/Stockholm" to ("స్వీడన్" to "Sweden"),
    "Europe/Helsinki" to ("ఫిన్లాండ్" to "Finland"),
    "Europe/Rome" to ("ఇటలీ" to "Italy"),
    "Asia/Dubai" to ("యునైటెడ్ అరబ్ ఎమిరేట్స్" to "UAE"),
    "Asia/Qatar" to ("ఖతర్" to "Qatar"),
    "Asia/Riyadh" to ("సౌదీ అరేబియా" to "Saudi Arabia"),
    "Asia/Kuwait" to ("కువైట్" to "Kuwait"),
    "Asia/Bahrain" to ("బహ్రెయిన్" to "Bahrain"),
    "Asia/Muscat" to ("ఓమన్" to "Oman"),
    "Asia/Singapore" to ("సింగపూర్" to "Singapore"),
    "Asia/Kuala_Lumpur" to ("మలేషియా" to "Malaysia"),
    "Asia/Hong_Kong" to ("హాంగ్ కాంగ్" to "Hong Kong"),
    "Asia/Tokyo" to ("జపాన్" to "Japan"),
    "Asia/Seoul" to ("దక్షిణ కొరియా" to "South Korea"),
    "Asia/Shanghai" to ("చైనా" to "China"),
    "Asia/Bangkok" to ("థాయిలాండ్" to "Thailand"),
    "Asia/Jakarta" to ("ఇండోనేషియా" to "Indonesia"),
    "Australia/Sydney" to ("ఆస్ట్రేలియా" to "Australia"),
    "Australia/Melbourne" to ("ఆస్ట్రేలియా" to "Australia"),
    "Australia/Brisbane" to ("ఆస్ట్రేలియా" to "Australia"),
    "Australia/Perth" to ("ఆస్ట్రేలియా" to "Australia"),
    "Australia/Adelaide" to ("ఆస్ట్రేలియా" to "Australia"),
    "Pacific/Auckland" to ("న్యూజిలాండ్" to "New Zealand"),
    "Pacific/Fiji" to ("ఫిజీ" to "Fiji"),
    "Africa/Johannesburg" to ("దక్షిణ ఆఫ్రికా" to "South Africa"),
    "Africa/Nairobi" to ("కెన్యా" to "Kenya"),
    "Africa/Lagos" to ("నైజీరియా" to "Nigeria"),
)

private val timezonePrefixCountryMap = mapOf(
    "America/" to ("అమెరికా" to "America"),
    "US/" to ("అమెరికా" to "America"),
    "Canada/" to ("కెనడా" to "Canada"),
    "Europe/" to ("యూరోప్" to "Europe"),
    "Australia/" to ("ఆస్ట్రేలియా" to "Australia"),
    "Pacific/" to ("పసిఫిక్" to "Pacific"),
    "Africa/" to ("ఆఫ్రికా" to "Africa"),
    "Asia/" to ("ఆసియా" to "Asia"),
)

private data class AbroadLocationInfo(
    val countryTelugu: String,
    val countryEnglish: String,
)

private fun getAbroadLocationFromTimezone(timezone: String): AbroadLocationInfo {
    val country = timezoneCountryMap[timezone]
    if (country != null) {
        return AbroadLocationInfo(country.first, country.second)
    }
    for ((key, value) in timezoneCountryMap) {
        if (timezone.startsWith(key)) {
            return AbroadLocationInfo(value.first, value.second)
        }
    }
    for ((prefix, value) in timezonePrefixCountryMap) {
        if (timezone.startsWith(prefix)) {
            return AbroadLocationInfo(value.first, value.second)
        }
    }
    return AbroadLocationInfo("విదేశ", "Abroad")
}

private fun isIndiaTimezone(timezone: String): Boolean {
    return timezone == "Asia/Kolkata" || timezone == "Asia/Calcutta"
}

@Composable
fun SankalpamCard(
    panchangamData: PanchangamData,
    userName: String,
    gotra: String,
    userNakshatra: String,
    city: String,
    fontScale: Float,
    timezone: String = "Asia/Kolkata",
    pujaType: String? = null,
    onNavigateToSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val missingInfo = gotra.isBlank() || userNakshatra.isBlank()

    GlassmorphicCard(
        modifier = modifier,
        cornerRadius = 16.dp,
        contentPadding = 16.dp,
        accentColor = TempleGold,
    ) {
        Text(
            "సంకల్పం · SANKALPAM",
            style = NityaPoojaTextStyles.GoldLabel,
            color = TempleGold,
        )
        Spacer(Modifier.height(12.dp))

        Text(
            buildSankalpamTelugu(panchangamData, userName, gotra, userNakshatra, city, timezone, pujaType),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = (14 * fontScale).sp,
                lineHeight = (22 * fontScale).sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        Spacer(Modifier.height(12.dp))

        Text(
            buildSankalpamEnglish(panchangamData, userName, gotra, userNakshatra, city, timezone, pujaType),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = (12 * fontScale).sp,
                lineHeight = (18 * fontScale).sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (missingInfo && onNavigateToSettings != null) {
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TempleGold,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "గోత్ర/నక్షత్రం సెట్ చేయండి · Set in Settings",
                    style = MaterialTheme.typography.labelSmall,
                    color = TempleGold,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

private fun buildSankalpamTelugu(
    data: PanchangamData,
    userName: String,
    gotra: String,
    userNakshatra: String,
    city: String,
    timezone: String,
    pujaType: String?,
): String {
    val name = userName.ifBlank { "___" }
    val gotraText = gotra.ifBlank { "___" }
    val nakshatraNames = JyotishConstants.NAKSHATRA_NAMES_ENGLISH
    val nakshatraNamesT = JyotishConstants.NAKSHATRA_NAMES_TELUGU
    val userNakshatraTelugu = if (userNakshatra.isNotBlank()) {
        val idx = nakshatraNames.indexOf(userNakshatra)
        if (idx >= 0) nakshatraNamesT[idx] else userNakshatra
    } else "___"

    val deityTelugu = if (pujaType != null) {
        deityNameTeluguMap[pujaType] ?: "పరమేశ్వర"
    } else "పరమేశ్వర"

    val locationLine = if (isIndiaTimezone(timezone)) {
        "జంబూద్వీపే, భరతవర్షే, భరతఖండే, ${city} క్షేత్రే,"
    } else {
        val abroad = getAbroadLocationFromTimezone(timezone)
        "జంబూద్వీపే, ${abroad.countryTelugu} దేశే, ${city} నగరే,"
    }

    return buildString {
        append("మమ ఉపాత్త సమస్త దురితక్షయ ద్వారా\n")
        append("శ్రీ ${deityTelugu} ప్రీత్యర్థం,\n")
        append("శుభే శోభనే ముహూర్తే,\n")
        append("శ్రీ మహావిష్ణోః ఆజ్ఞయా ప్రవర్తమానస్య,\n")
        append("అద్య బ్రహ్మణః ద్వితీయపరార్ధే, శ్వేతవరాహ కల్పే,\n")
        append("వైవస్వత మన్వంతరే, కలియుగే, ప్రథమపాదే,\n")
        append("శ్రీమాన్ ${data.samvatsara.nameTelugu} నామ సంవత్సరే,\n")
        append("${data.masa.nameTelugu} మాసే, ${data.tithi.pakshaTelugu},\n")
        append("${data.tithi.nameTelugu} తిథౌ, ${data.teluguDay} వాసరే,\n")
        append("${data.nakshatra.nameTelugu} నక్షత్రే, ${data.yoga.nameTelugu} యోగే, ${data.karana.firstNameTelugu} కరణే,\n")
        append("${locationLine}\n")
        append("${gotraText} గోత్రస్య, ${userNakshatraTelugu} నక్షత్రే జాతస్య,\n")
        append("${name} నామధేయస్య,\n")
        append("సహ కుటుంబానాం క్షేమ, స్థైర్య, విజయ,\n")
        append("ఆయురారోగ్య ఐశ్వర్యాభివృద్ధ్యర్థం,\n")
        append("సర్వాభీష్ట సిద్ధ్యర్థం,\n")
        append("శ్రీ ${deityTelugu} ప్రీత్యర్థం,\n")
        append("ధ్యాన ఆవాహనాది షోడశోపచార పూజాం కరిష్యే ॥")
    }
}

private fun buildSankalpamEnglish(
    data: PanchangamData,
    userName: String,
    gotra: String,
    userNakshatra: String,
    city: String,
    timezone: String,
    pujaType: String?,
): String {
    val name = userName.ifBlank { "___" }
    val gotraText = gotra.ifBlank { "___" }
    val nakshatraText = userNakshatra.ifBlank { "___" }

    val deityEnglish = if (pujaType != null) {
        deityNameEnglishMap[pujaType] ?: "Parameshwara"
    } else "Parameshwara"

    val locationLine = if (isIndiaTimezone(timezone)) {
        "Jambudweepe, Bharatavarshe, Bharatakhande, $city kshetre,"
    } else {
        val abroad = getAbroadLocationFromTimezone(timezone)
        "Jambudweepe, ${abroad.countryEnglish} deshe, $city nagare,"
    }

    return buildString {
        append("Mama upatta samasta duritakshaya dwara\n")
        append("Sri $deityEnglish prityartham,\n")
        append("Shubhe shobhane muhurte,\n")
        append("Sri Mahavishnoh ajnaya pravartamanasya,\n")
        append("Adya Brahmanah dwitiya parardhe, Shwetavaraha kalpe,\n")
        append("Vaivaswata manvantare, Kaliyuge, prathama pade,\n")
        append("Shriman ${data.samvatsara.nameEnglish} nama samvatsare,\n")
        append("${data.masa.nameEnglish} mase, ${data.tithi.paksha},\n")
        append("${data.tithi.nameEnglish} tithau, ${data.englishDay} vasare,\n")
        append("${data.nakshatra.nameEnglish} nakshtre, ${data.yoga.nameEnglish} yoge, ${data.karana.firstNameEnglish} karane,\n")
        append("$locationLine\n")
        append("$gotraText gotrasya, $nakshatraText nakshtre jatasya,\n")
        append("$name namadheyasya,\n")
        append("Saha kutumbanam kshema, sthairya, vijaya,\n")
        append("Ayurarogya aishwaryabhivriddhyartham,\n")
        append("Sarvabhishta siddhyartham,\n")
        append("Sri $deityEnglish prityartham,\n")
        append("Dhyana avahanadi shodashopachara pujam karishye.")
    }
}
