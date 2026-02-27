package com.nityapooja.shared.ui.panchangam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import com.nityapooja.shared.utils.AstronomicalCalculator
import kotlin.math.*

// ═══════════════════════════════════════════════════════════════
// Data classes
// ═══════════════════════════════════════════════════════════════

data class SunTimes(
    val sunrise: String,
    val sunset: String,
    val sunriseDecimal: Double,
    val sunsetDecimal: Double,
)

data class LocationInfo(
    val city: String,
    val lat: Double,
    val lng: Double,
    val timezone: String,
)

data class TithiInfo(
    val index: Int,
    val nameEnglish: String,
    val nameTelugu: String,
    val paksha: String,
    val pakshaTelugu: String,
    val endTime: String,      // when this tithi ends
    val nextNameEnglish: String = "",
    val nextNameTelugu: String = "",
    val nextPaksha: String = "",
    val nextPakshaTelugu: String = "",
)

data class NakshatraInfo(
    val index: Int,
    val nameEnglish: String,
    val nameTelugu: String,
    val endTime: String,      // when this nakshatra ends
    val nextNameEnglish: String = "",
    val nextNameTelugu: String = "",
)

data class YogaInfo(
    val index: Int,
    val nameEnglish: String,
    val nameTelugu: String,
    val endTime: String,
    val nextNameEnglish: String = "",
    val nextNameTelugu: String = "",
)

data class KaranaInfo(
    val firstNameEnglish: String,
    val firstNameTelugu: String,
    val secondNameEnglish: String,
    val secondNameTelugu: String,
)

data class RahuKaalInfo(
    val startTime: String,
    val endTime: String,
    val isActive: Boolean,
)

data class YamagandamInfo(
    val startTime: String,
    val endTime: String,
    val isActive: Boolean,
)

data class GulikaKalamInfo(
    val startTime: String,
    val endTime: String,
    val isActive: Boolean,
)

data class AbhijitMuhurtInfo(
    val startTime: String,
    val endTime: String,
    val isActive: Boolean,
)

data class TimeSlotInfo(
    val nameTelugu: String,
    val nameEnglish: String,
    val startTime: String,
    val endTime: String,
    val isActive: Boolean,
)

data class SelectedDate(
    val year: Int,
    val month: Int,  // 1-based
    val day: Int,
)

data class RashiInfo(
    val index: Int,
    val nameEnglish: String,
    val nameTelugu: String,
)

data class MasaInfo(
    val nameEnglish: String,
    val nameTelugu: String,
)

data class SamvatsaraInfo(
    val nameEnglish: String,
    val nameTelugu: String,
)

data class AyanaInfo(
    val nameEnglish: String,
    val nameTelugu: String,
)

data class RutuInfo(
    val nameEnglish: String,
    val nameTelugu: String,
)

data class PanchangamData(
    val tithi: TithiInfo,
    val nakshatra: NakshatraInfo,
    val yoga: YogaInfo,
    val karana: KaranaInfo,
    val rahuKaal: RahuKaalInfo,
    val yamagandam: YamagandamInfo,
    val gulikaKalam: GulikaKalamInfo,
    val abhijitMuhurt: AbhijitMuhurtInfo,
    val sunTimes: SunTimes,
    val teluguDay: String,
    val englishDay: String,
    val dateDisplay: String,
    // New comprehensive fields
    val samvatsara: SamvatsaraInfo,
    val masa: MasaInfo,
    val ayana: AyanaInfo,
    val rutu: RutuInfo,
    val sunRashi: RashiInfo,
    val moonRashi: RashiInfo,
    // Shubha Muhurtam fields
    val brahmaMuhurta: TimeSlotInfo,
    val shubhHoras: List<TimeSlotInfo>,
    val isToday: Boolean,
)

// ═══════════════════════════════════════════════════════════════
// Name arrays: Telugu and English
// ═══════════════════════════════════════════════════════════════

val TITHI_NAMES_ENGLISH = arrayOf(
    "Prathama", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
    "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
    "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Pournami",
    "Prathama", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
    "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
    "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Amavasya",
)

val TITHI_NAMES_TELUGU = arrayOf(
    "ప్రతిపద", "ద్వితీయ", "తృతీయ", "చతుర్థి", "పంచమి",
    "షష్ఠి", "సప్తమి", "అష్టమి", "నవమి", "దశమి",
    "ఏకాదశి", "ద్వాదశి", "త్రయోదశి", "చతుర్దశి", "పూర్ణిమ",
    "ప్రతిపద", "ద్వితీయ", "తృతీయ", "చతుర్థి", "పంచమి",
    "షష్ఠి", "సప్తమి", "అష్టమి", "నవమి", "దశమి",
    "ఏకాదశి", "ద్వాదశి", "త్రయోదశి", "చతుర్దశి", "అమావాస్య",
)

val NAKSHATRA_NAMES_ENGLISH = arrayOf(
    "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira",
    "Ardra", "Punarvasu", "Pushyami", "Ashlesha", "Magha",
    "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati",
    "Vishakha", "Anuradha", "Jyeshtha", "Moola", "Purvashadha",
    "Uttarashadha", "Shravana", "Dhanishta", "Shatabhisha",
    "Purvabhadra", "Uttarabhadra", "Revati",
)

val NAKSHATRA_NAMES_TELUGU = arrayOf(
    "అశ్విని", "భరణి", "కృత్తిక", "రోహిణి", "మృగశిర",
    "ఆర్ద్ర", "పునర్వసు", "పుష్యమి", "ఆశ్లేష", "మఘ",
    "పూర్వ ఫల్గుణి", "ఉత్తర ఫల్గుణి", "హస్త", "చిత్ర", "స్వాతి",
    "విశాఖ", "అనురాధ", "జ్యేష్ఠ", "మూల", "పూర్వాషాఢ",
    "ఉత్తరాషాఢ", "శ్రవణం", "ధనిష్ఠ", "శతభిషం",
    "పూర్వాభాద్ర", "ఉత్తరాభాద్ర", "రేవతి",
)

val YOGA_NAMES_ENGLISH = arrayOf(
    "Vishkambha", "Preeti", "Ayushman", "Saubhagya", "Shobhana",
    "Atiganda", "Sukarma", "Dhriti", "Shoola", "Ganda",
    "Vriddhi", "Dhruva", "Vyaghata", "Harshana", "Vajra",
    "Siddhi", "Vyatipata", "Variyan", "Parigha", "Shiva",
    "Siddha", "Sadhya", "Shubha", "Shukla", "Brahma",
    "Aindra", "Vaidhriti",
)

val YOGA_NAMES_TELUGU = arrayOf(
    "విష్కంభ", "ప్రీతి", "ఆయుష్మాన్", "సౌభాగ్య", "శోభన",
    "అతిగండ", "సుకర్మ", "ధృతి", "శూల", "గండ",
    "వృద్ధి", "ధ్రువ", "వ్యాఘాత", "హర్షణ", "వజ్ర",
    "సిద్ధి", "వ్యతీపాత", "వరియాన్", "పరిఘ", "శివ",
    "సిద్ధ", "సాధ్య", "శుభ", "శుక్ల", "బ్రహ్మ",
    "ఐంద్ర", "వైధృతి",
)

// 11 Karanas: 4 fixed + 7 repeating
val KARANA_NAMES_ENGLISH = arrayOf(
    "Bava", "Balava", "Kaulava", "Taitila", "Garaja",
    "Vanija", "Vishti", "Shakuni", "Chatushpada", "Nagava", "Kimstughna",
)

val KARANA_NAMES_TELUGU = arrayOf(
    "బవ", "బాలవ", "కౌలవ", "తైతిల", "గరజ",
    "వణిజ", "విష్టి", "శకుని", "చతుష్పద", "నాగవ", "కింస్తుఘ్న",
)

// 12 Rashi names
val RASHI_NAMES_ENGLISH = arrayOf(
    "Mesha", "Vrishabha", "Mithuna", "Karkataka",
    "Simha", "Kanya", "Tula", "Vrischika",
    "Dhanu", "Makara", "Kumbha", "Meena",
)

val RASHI_NAMES_TELUGU = arrayOf(
    "మేషం", "వృషభం", "మిథునం", "కర్కాటకం",
    "సింహం", "కన్య", "తుల", "వృశ్చికం",
    "ధనుస్సు", "మకరం", "కుంభం", "మీనం",
)

// 60 Samvatsara names (Telugu yearly cycle)
// Index 0 = Prabhava. Cycle: (year - 1987) % 60 for Gregorian year starting from Ugadi
private val SAMVATSARA_NAMES_TELUGU = arrayOf(
    "ప్రభవ", "విభవ", "శుక్ల", "ప్రమోదూత", "ప్రజోత్పత్తి",
    "ఆంగీరస", "శ్రీముఖ", "భావ", "యువ", "ధాత",
    "ఈశ్వర", "బహుధాన్య", "ప్రమాథి", "విక్రమ", "వృష",
    "చిత్రభాను", "స్వభాను", "తారణ", "పార్థివ", "వ్యయ",
    "సర్వజిత్", "సర్వధారి", "విరోధి", "వికృతి", "ఖర",
    "నందన", "విజయ", "జయ", "మన్మథ", "దుర్ముఖి",
    "హేవిళంబి", "విళంబి", "వికారి", "శార్వరి", "ప్లవ",
    "శుభకృత్", "శోభకృత్", "క్రోధి", "విశ్వావసు", "పరాభవ",
    "ప్లవంగ", "కీలక", "సౌమ్య", "సాధారణ", "విరోధికృత్",
    "పరిధావి", "ప్రమాదీచ", "ఆనంద", "రాక్షస", "నల",
    "పింగళ", "కాళయుక్తి", "సిద్ధార్థి", "రౌద్రి", "దుర్మతి",
    "దుందుభి", "రుధిరోద్గారి", "రక్తాక్షి", "క్రోధన", "అక్షయ",
)

private val SAMVATSARA_NAMES_ENGLISH = arrayOf(
    "Prabhava", "Vibhava", "Shukla", "Pramoduta", "Prajotpatti",
    "Angirasa", "Shrimukha", "Bhava", "Yuva", "Dhata",
    "Ishvara", "Bahudhanya", "Pramathi", "Vikrama", "Vrisha",
    "Chitrabhanu", "Svabhanu", "Tarana", "Parthiva", "Vyaya",
    "Sarvajit", "Sarvadhari", "Virodhi", "Vikruti", "Khara",
    "Nandana", "Vijaya", "Jaya", "Manmatha", "Durmukhi",
    "Hevilambi", "Vilambi", "Vikari", "Sharvari", "Plava",
    "Shubhakrut", "Shobhakrut", "Krodhi", "Vishvavasu", "Parabhava",
    "Plavanga", "Keelaka", "Saumya", "Sadharana", "Virodhikrut",
    "Paridhavi", "Pramadicha", "Ananda", "Rakshasa", "Nala",
    "Pingala", "Kalayukti", "Siddhartha", "Raudri", "Durmati",
    "Dundubhi", "Rudhirodgari", "Raktakshi", "Krodhana", "Akshaya",
)

// Telugu Masa names (lunar months)
// Determined by the Sun's sidereal longitude entering each rashi
// Mesha (0-30°) = Chaitra, Vrishabha (30-60°) = Vaishakha, etc.
private val MASA_NAMES_TELUGU = arrayOf(
    "చైత్రము", "వైశాఖము", "జ్యేష్ఠము", "ఆషాఢము",
    "శ్రావణము", "భాద్రపదము", "ఆశ్వయుజము", "కార్తీకము",
    "మార్గశిరము", "పుష్యము", "మాఘము", "ఫాల్గుణము",
)

private val MASA_NAMES_ENGLISH = arrayOf(
    "Chaitra", "Vaishakha", "Jyeshtha", "Ashadha",
    "Shravana", "Bhadrapada", "Ashvayuja", "Kartika",
    "Margashira", "Pushya", "Magha", "Phalguna",
)

// Ayana names
private val AYANA_UTTARAYANA_TELUGU = "ఉత్తరాయణం"
private val AYANA_DAKSHINAYANA_TELUGU = "దక్షిణాయనం"
private val AYANA_UTTARAYANA_ENGLISH = "Uttarayana"
private val AYANA_DAKSHINAYANA_ENGLISH = "Dakshinayana"

// Rutu (Season) names — paired with masa
// Chaitra-Vaishakha=Vasanta, Jyeshtha-Ashadha=Grishma, etc.
private val RUTU_NAMES_TELUGU = arrayOf(
    "వసంతం", "వసంతం", "గ్రీష్మం", "గ్రీష్మం",
    "వర్షం", "వర్షం", "శరత్తు", "శరత్తు",
    "హేమంతం", "హేమంతం", "శిశిరం", "శిశిరం",
)

private val RUTU_NAMES_ENGLISH = arrayOf(
    "Vasanta", "Vasanta", "Grishma", "Grishma",
    "Varsha", "Varsha", "Sharad", "Sharad",
    "Hemanta", "Hemanta", "Shishira", "Shishira",
)

// Telugu day names (indexed by Calendar convention: Sun=1..Sat=7)
private val TELUGU_DAY_NAMES = arrayOf(
    "", "ఆదివారం", "సోమవారం", "మంగళవారం", "బుధవారం",
    "గురువారం", "శుక్రవారం", "శనివారం",
)

private val ENGLISH_DAY_NAMES = arrayOf(
    "", "Sunday", "Monday", "Tuesday", "Wednesday",
    "Thursday", "Friday", "Saturday",
)

private val ENGLISH_MONTH_NAMES = arrayOf(
    "", "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

// Muhurta slot indices per day-of-week (1-based, Sun=1..Sat=7)
private val RAHU_KAAL_SLOTS = intArrayOf(0, 8, 2, 7, 5, 6, 4, 3)
private val YAMAGANDAM_SLOTS = intArrayOf(0, 5, 4, 3, 7, 2, 1, 6)
private val GULIKA_KALAM_SLOTS = intArrayOf(0, 7, 6, 5, 4, 3, 2, 1)

// Hora planet sequence: Sun→Venus→Mercury→Moon→Saturn→Jupiter→Mars
// Index 0=Sun, 1=Venus, 2=Mercury, 3=Moon, 4=Saturn, 5=Jupiter, 6=Mars
private val HORA_SEQUENCE = intArrayOf(0, 1, 2, 3, 4, 5, 6) // Sun, Venus, Mercury, Moon, Saturn, Jupiter, Mars
private val HORA_PLANET_ENGLISH = arrayOf("Sun", "Venus", "Mercury", "Moon", "Saturn", "Jupiter", "Mars")
private val HORA_PLANET_TELUGU = arrayOf("సూర్యుడు", "శుక్రుడు", "బుధుడు", "చంద్రుడు", "శని", "గురువు", "కుజుడు")
// Auspicious hora planets: Jupiter (5), Venus (1), Mercury (2), Moon (3)
private val AUSPICIOUS_HORA_INDICES = setOf(1, 2, 3, 5) // Venus, Mercury, Moon, Jupiter

// Day lord index in HORA_SEQUENCE: Sun=1→0, Mon=2→3, Tue=3→6, Wed=4→2, Thu=5→5, Fri=6→1, Sat=7→4
// Calendar convention DAY_OF_WEEK: Sun=1, Mon=2, Tue=3, Wed=4, Thu=5, Fri=6, Sat=7
private val DAY_LORD_HORA_INDEX = intArrayOf(0, 0, 3, 6, 2, 5, 1, 4)

// ═══════════════════════════════════════════════════════════════
// Helper: map kotlinx.datetime DayOfWeek (ISO: Monday=1..Sunday=7)
// to Java Calendar convention (Sunday=1..Saturday=7)
// ═══════════════════════════════════════════════════════════════

private fun dayOfWeekToCalendarIndex(dow: DayOfWeek): Int {
    return when (dow) {
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
        else -> 1
    }
}

// ═══════════════════════════════════════════════════════════════
// ViewModel
// ═══════════════════════════════════════════════════════════════

class PanchangamViewModel(
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {
    private data class MasaComputation(
        val info: MasaInfo,
        val masaIndex: Int,
    )

    val locationInfo: StateFlow<LocationInfo> = combine(
        preferencesManager.locationCity,
        preferencesManager.locationLat,
        preferencesManager.locationLng,
        preferencesManager.locationTimezone,
    ) { city, lat, lng, timezone ->
        LocationInfo(city, lat, lng, timezone)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LocationInfo("Hyderabad", 17.385, 78.4867, "Asia/Kolkata"),
    )

    private val _selectedDate = MutableStateFlow<SelectedDate?>(null) // null = today
    val selectedDate: StateFlow<SelectedDate?> = _selectedDate.asStateFlow()

    fun selectDate(year: Int, month: Int, day: Int) {
        val tz = TimeZone.of(locationInfo.value.timezone)
        val now = Clock.System.now()
        val today = now.toLocalDateTime(tz)
        val isToday = year == today.year &&
                month == today.monthNumber &&
                day == today.dayOfMonth
        _selectedDate.value = if (isToday) null else SelectedDate(year, month, day)
    }

    fun selectToday() {
        _selectedDate.value = null
    }

    fun navigateDay(delta: Int) {
        val tz = TimeZone.of(locationInfo.value.timezone)
        val now = Clock.System.now()
        val current = _selectedDate.value
        val baseDate = if (current != null) {
            LocalDate(current.year, current.month, current.day)
        } else {
            now.toLocalDateTime(tz).date
        }
        val newDate = baseDate.plus(delta, DateTimeUnit.DAY)
        val today = now.toLocalDateTime(tz).date
        _selectedDate.value = if (newDate == today) null
        else SelectedDate(newDate.year, newDate.monthNumber, newDate.dayOfMonth)
    }

    fun selectDateFromMillis(epochMillis: Long) {
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val tz = TimeZone.of(locationInfo.value.timezone)
        val localDate = instant.toLocalDateTime(tz).date
        selectDate(localDate.year, localDate.monthNumber, localDate.dayOfMonth)
    }

    fun setLocation(city: String, lat: Double, lng: Double, timezone: String) {
        viewModelScope.launch { preferencesManager.setLocation(city, lat, lng, timezone) }
    }

    // ═══════════════════════════════════════════════════════════
    // Public API: compute full panchangam for a given location
    // ═══════════════════════════════════════════════════════════

    fun calculatePanchangam(lat: Double, lng: Double, timezone: String): PanchangamData {
        return calculatePanchangam(lat, lng, timezone, null)
    }

    fun calculatePanchangam(lat: Double, lng: Double, timezone: String, date: SelectedDate?): PanchangamData {
        val tz = TimeZone.of(timezone)
        val now = Clock.System.now()
        var calculationInstant = if (date == null) {
            now
        } else {
            LocalDateTime(date.year, date.month, date.day, 12, 0).toInstant(tz)
        }
        var calculationLocalDateTime = calculationInstant.toLocalDateTime(tz)
        var utcOffsetHours = calculationInstant.offsetIn(tz).totalSeconds / 3600.0
        val isToday = date == null
        val year = calculationLocalDateTime.year
        val month = calculationLocalDateTime.monthNumber
        val day = calculationLocalDateTime.dayOfMonth
        var hour = calculationLocalDateTime.hour
        var minute = calculationLocalDateTime.minute

        // For selected dates, daily Panchangam should reflect sunrise-based day markers.
        if (date != null) {
            val approxSunTimes = calculateSunTimes(lat, lng, year, month, day, utcOffsetHours)
            val sunriseMinutes = (((approxSunTimes.sunriseDecimal % 24.0 + 24.0) % 24.0) * 60.0).roundToInt()
            val sunriseHour = (sunriseMinutes / 60) % 24
            val sunriseMinute = sunriseMinutes % 60

            calculationInstant = LocalDateTime(year, month, day, sunriseHour, sunriseMinute).toInstant(tz)
            calculationLocalDateTime = calculationInstant.toLocalDateTime(tz)
            utcOffsetHours = calculationInstant.offsetIn(tz).totalSeconds / 3600.0
            hour = calculationLocalDateTime.hour
            minute = calculationLocalDateTime.minute
        }

        val dayOfWeek = dayOfWeekToCalendarIndex(calculationLocalDateTime.dayOfWeek)
        val referenceDate = calculationLocalDateTime.date

        // Julian Day Number at current time (convert local time to UT)
        val utHours = hour + minute / 60.0 - utcOffsetHours
        val jd = julianDay(year, month, day, utHours)

        // ── Lahiri Ayanamsa (Chitrapaksha) ──
        // Government of India standard for sidereal calculations
        val ayanamsa = lahiriAyanamsa(jd)

        // ── Tropical Sun and Moon longitudes ──
        val sunLongTropical = sunLongitude(jd)
        val moonLongTropical = moonLongitude(jd)

        // ── Sidereal (Nirayana) longitudes ──
        val sunLong = normalize360(sunLongTropical - ayanamsa)
        val moonLong = normalize360(moonLongTropical - ayanamsa)

        // ── Core Panchangam Elements (using sidereal longitudes) ──
        val tithi = calculateTithi(sunLong, moonLong, jd, tz, referenceDate)
        val nakshatra = calculateNakshatra(moonLong, jd, tz, referenceDate)
        val yoga = calculateYoga(sunLong, moonLong, jd, tz, referenceDate)
        val karana = calculateKarana(sunLong, moonLong)

        // ── Rashi (zodiac sign) from sidereal longitudes ──
        val sunRashi = calculateRashi(sunLong)
        val moonRashi = calculateRashi(moonLong)

        // ── Calendar elements ──
        val masaComputation = calculateMasa(jd)
        val masa = masaComputation.info
        val samvatsara = calculateSamvatsara(year, masaComputation.masaIndex)
        val ayana = calculateAyana(sunLong)
        val rutu = calculateRutu(sunLong)

        // ── Sun times (timezone-aware, using accurate Meeus method) ──
        val sunTimes = calculateSunTimes(lat, lng, year, month, day, utcOffsetHours)

        // ── Day of week (in user's timezone) ──
        val currentDecimal = hour + minute / 60.0

        // ── Muhurta calculations ──
        // For non-today dates, isActive is always false (currentDecimal set to -1)
        val activeDecimal = if (isToday) currentDecimal else -1.0
        val rahuKaal = calculateMuhurtaSlot(dayOfWeek, sunTimes, activeDecimal, RAHU_KAAL_SLOTS)
        val yamagandamSlot = calculateMuhurtaSlot(dayOfWeek, sunTimes, activeDecimal, YAMAGANDAM_SLOTS)
        val gulikaSlot = calculateMuhurtaSlot(dayOfWeek, sunTimes, activeDecimal, GULIKA_KALAM_SLOTS)
        val abhijitMuhurt = calculateAbhijitMuhurt(sunTimes, activeDecimal)

        // ── Shubha Muhurtam ──
        val brahmaMuhurta = calculateBrahmaMuhurta(sunTimes, activeDecimal)
        val shubhHoras = calculateShubhHoras(dayOfWeek, sunTimes, activeDecimal)

        // ── Date and day strings ──
        val teluguDay = TELUGU_DAY_NAMES.getOrElse(dayOfWeek) { "" }
        val englishDay = ENGLISH_DAY_NAMES.getOrElse(dayOfWeek) { "" }
        val monthName = ENGLISH_MONTH_NAMES.getOrElse(month) { "" }
        val dateDisplay = "$englishDay, $monthName $day, $year"

        return PanchangamData(
            tithi = tithi,
            nakshatra = nakshatra,
            yoga = yoga,
            karana = karana,
            rahuKaal = RahuKaalInfo(rahuKaal.first, rahuKaal.second, rahuKaal.third),
            yamagandam = YamagandamInfo(yamagandamSlot.first, yamagandamSlot.second, yamagandamSlot.third),
            gulikaKalam = GulikaKalamInfo(gulikaSlot.first, gulikaSlot.second, gulikaSlot.third),
            abhijitMuhurt = abhijitMuhurt,
            sunTimes = sunTimes,
            teluguDay = teluguDay,
            englishDay = englishDay,
            dateDisplay = dateDisplay,
            samvatsara = samvatsara,
            masa = masa,
            ayana = ayana,
            rutu = rutu,
            sunRashi = sunRashi,
            moonRashi = moonRashi,
            brahmaMuhurta = brahmaMuhurta,
            shubhHoras = shubhHoras,
            isToday = isToday,
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Lahiri Ayanamsa — delegates to AstronomicalCalculator
    // (IAU 2006 precession + nutation, GoI official base 23.856°)
    // ═══════════════════════════════════════════════════════════

    private fun lahiriAyanamsa(jd: Double): Double = AstronomicalCalculator.lahiriAyanamsa(jd)

    // ═══════════════════════════════════════════════════════════
    // Julian Day calculation
    // ═══════════════════════════════════════════════════════════

    private fun julianDay(year: Int, month: Int, day: Int, utHours: Double): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = y / 100
        val b = 2 - a + a / 4
        return (365.25 * (y + 4716)).toInt() +
                (30.6001 * (m + 1)).toInt() +
                day + utHours / 24.0 + b - 1524.5
    }

    // ═══════════════════════════════════════════════════════════
    // Centuries since J2000.0
    // ═══════════════════════════════════════════════════════════

    private fun centuriesSinceJ2000(jd: Double): Double = (jd - 2451545.0) / 36525.0

    // ═══════════════════════════════════════════════════════════
    // Sun longitude — delegates to AstronomicalCalculator
    // (Full nutation + aberration corrections)
    // ═══════════════════════════════════════════════════════════

    private fun sunLongitude(jd: Double): Double = AstronomicalCalculator.sunLongitude(jd)

    // ═══════════════════════════════════════════════════════════
    // Moon longitude — delegates to AstronomicalCalculator
    // (60-term ELP2000 Meeus series, ~0.003° accuracy)
    // ═══════════════════════════════════════════════════════════

    private fun moonLongitude(jd: Double): Double = AstronomicalCalculator.moonLongitude(jd)

    // ═══════════════════════════════════════════════════════════
    // Sidereal elongation at a given JD (for tithi end time search)
    // Returns moon-sun sidereal elongation in degrees (0..360)
    // ═══════════════════════════════════════════════════════════

    private fun siderealElongation(jd: Double): Double {
        val ayanamsa = lahiriAyanamsa(jd)
        val sunSid = normalize360(sunLongitude(jd) - ayanamsa)
        val moonSid = normalize360(moonLongitude(jd) - ayanamsa)
        return normalize360(moonSid - sunSid)
    }

    // ═══════════════════════════════════════════════════════════
    // Sidereal moon longitude at a given JD (for nakshatra end time)
    // ═══════════════════════════════════════════════════════════

    private fun siderealMoonLong(jd: Double): Double {
        val ayanamsa = lahiriAyanamsa(jd)
        return normalize360(moonLongitude(jd) - ayanamsa)
    }

    // ═══════════════════════════════════════════════════════════
    // Sidereal sun+moon sum at a given JD (for yoga end time)
    // ═══════════════════════════════════════════════════════════

    private fun siderealSunMoonSum(jd: Double): Double {
        val ayanamsa = lahiriAyanamsa(jd)
        val sunSid = normalize360(sunLongitude(jd) - ayanamsa)
        val moonSid = normalize360(moonLongitude(jd) - ayanamsa)
        return normalize360(sunSid + moonSid)
    }

    // ═══════════════════════════════════════════════════════════
    // Binary search: find JD when a cyclical angle crosses a boundary
    // ═══════════════════════════════════════════════════════════

    /**
     * Finds the JD when [angleFunction] crosses [targetDegree].
     * Searches forward from [jdStart] up to [maxDaysAhead].
     *
     * Uses 15-minute coarse scan + 30 binary search iterations.
     * 30 iterations on a 15-min bracket: 15*60 / 2^30 ≈ 0.0008 seconds precision.
     */
    private fun findCrossingJD(
        jdStart: Double,
        targetDegree: Double,
        maxDaysAhead: Double,
        angleFunction: (Double) -> Double,
    ): Double {
        val startAngle = angleFunction(jdStart)
        var targetDelta = normalize360(targetDegree - startAngle)
        if (targetDelta == 0.0) targetDelta = 360.0

        val stepDays = 1.0 / 96.0 // 15 minutes — finer coarse scan
        val maxJd = jdStart + maxDaysAhead
        var lo = jdStart
        var hi = jdStart
        var found = false

        while (hi < maxJd) {
            hi = (hi + stepDays).coerceAtMost(maxJd)
            val delta = normalize360(angleFunction(hi) - startAngle)
            if (delta >= targetDelta) {
                found = true
                break
            }
            lo = hi
        }

        if (!found) return jdStart + 1.0

        // 30 iterations: converges to sub-second precision
        repeat(30) {
            val mid = (lo + hi) / 2.0
            val delta = normalize360(angleFunction(mid) - startAngle)
            if (delta >= targetDelta) {
                hi = mid
            } else {
                lo = mid
            }
        }

        return (lo + hi) / 2.0
    }

    // ═══════════════════════════════════════════════════════════
    // Convert JD to local time string (HH:MM AM/PM)
    // ═══════════════════════════════════════════════════════════

    private fun jdToLocalTime(jd: Double, timezone: TimeZone): Pair<String, LocalDate> {
        val epochMillis = ((jd - 2440587.5) * 86_400_000.0).roundToLong()
        val localDateTime = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(timezone)
        val decimalHours = localDateTime.hour + localDateTime.minute / 60.0 + localDateTime.second / 3600.0
        return Pair(formatTime(decimalHours), localDateTime.date)
    }

    /**
     * Returns local time with a day prefix when event spills outside [referenceDate].
     */
    private fun jdToLocalTimeWithDay(jd: Double, timezone: TimeZone, referenceDate: LocalDate): String {
        val (timeStr, localDate) = jdToLocalTime(jd, timezone)
        return when {
            localDate == referenceDate -> timeStr
            localDate == referenceDate.plus(1, DateTimeUnit.DAY) -> "రేపు $timeStr"
            localDate == referenceDate.plus(-1, DateTimeUnit.DAY) -> "నిన్న $timeStr"
            else -> "${localDate.dayOfMonth} ${localDate.monthNumber} $timeStr"
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Tithi from sidereal moon-sun elongation + end time
    // ═══════════════════════════════════════════════════════════

    private fun calculateTithi(
        sunLong: Double,
        moonLong: Double,
        jd: Double,
        timezone: TimeZone,
        referenceDate: LocalDate,
    ): TithiInfo {
        val elongation = normalize360(moonLong - sunLong)
        val tithiIndex = (elongation / 12.0).toInt().coerceIn(0, 29)

        val paksha = if (tithiIndex < 15) "Shukla Paksha" else "Krishna Paksha"
        val pakshaTelugu = if (tithiIndex < 15) "శుక్ల పక్షం" else "కృష్ణ పక్షం"

        // Find when this tithi ends: next 12° boundary
        val nextBoundary = ((tithiIndex + 1) * 12.0) % 360.0
        val endJd = findCrossingJD(jd, nextBoundary, 2.0) { siderealElongation(it) }
        val endTimeStr = jdToLocalTimeWithDay(endJd, timezone, referenceDate)

        // Next tithi after current one ends
        val nextIndex = (tithiIndex + 1) % 30
        val nextPaksha = if (nextIndex < 15) "Shukla Paksha" else "Krishna Paksha"
        val nextPakshaTelugu = if (nextIndex < 15) "శుక్ల పక్షం" else "కృష్ణ పక్షం"

        return TithiInfo(
            index = tithiIndex,
            nameEnglish = TITHI_NAMES_ENGLISH[tithiIndex],
            nameTelugu = TITHI_NAMES_TELUGU[tithiIndex],
            paksha = paksha,
            pakshaTelugu = pakshaTelugu,
            endTime = endTimeStr,
            nextNameEnglish = TITHI_NAMES_ENGLISH[nextIndex],
            nextNameTelugu = TITHI_NAMES_TELUGU[nextIndex],
            nextPaksha = nextPaksha,
            nextPakshaTelugu = nextPakshaTelugu,
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Nakshatra from sidereal moon longitude + end time
    // ═══════════════════════════════════════════════════════════

    private fun calculateNakshatra(
        moonLong: Double,
        jd: Double,
        timezone: TimeZone,
        referenceDate: LocalDate,
    ): NakshatraInfo {
        val nakshatraDeg = 360.0 / 27.0  // 13.333°
        val index = (moonLong / nakshatraDeg).toInt().coerceIn(0, 26)

        // Find when this nakshatra ends
        val nextBoundary = ((index + 1) * nakshatraDeg) % 360.0
        val endJd = findCrossingJD(jd, nextBoundary, 2.0) { siderealMoonLong(it) }
        val endTimeStr = jdToLocalTimeWithDay(endJd, timezone, referenceDate)

        // Next nakshatra after current one ends
        val nextIndex = (index + 1) % 27

        return NakshatraInfo(
            index = index,
            nameEnglish = NAKSHATRA_NAMES_ENGLISH[index],
            nameTelugu = NAKSHATRA_NAMES_TELUGU[index],
            endTime = endTimeStr,
            nextNameEnglish = NAKSHATRA_NAMES_ENGLISH[nextIndex],
            nextNameTelugu = NAKSHATRA_NAMES_TELUGU[nextIndex],
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Yoga from sum of sidereal sun and moon longitudes + end time
    // ═══════════════════════════════════════════════════════════

    private fun calculateYoga(
        sunLong: Double,
        moonLong: Double,
        jd: Double,
        timezone: TimeZone,
        referenceDate: LocalDate,
    ): YogaInfo {
        val sum = normalize360(sunLong + moonLong)
        val yogaDeg = 360.0 / 27.0  // 13.333°
        val index = (sum / yogaDeg).toInt().coerceIn(0, 26)

        // Find when this yoga ends
        val nextBoundary = ((index + 1) * yogaDeg) % 360.0
        val endJd = findCrossingJD(jd, nextBoundary, 2.0) { siderealSunMoonSum(it) }
        val endTimeStr = jdToLocalTimeWithDay(endJd, timezone, referenceDate)

        // Next yoga after current one ends
        val nextYogaIndex = (index + 1) % 27

        return YogaInfo(
            index = index,
            nameEnglish = YOGA_NAMES_ENGLISH[index],
            nameTelugu = YOGA_NAMES_TELUGU[index],
            endTime = endTimeStr,
            nextNameEnglish = YOGA_NAMES_ENGLISH[nextYogaIndex],
            nextNameTelugu = YOGA_NAMES_TELUGU[nextYogaIndex],
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Karana from sidereal moon-sun elongation
    // ═══════════════════════════════════════════════════════════

    private fun calculateKarana(sunLong: Double, moonLong: Double): KaranaInfo {
        val elongation = normalize360(moonLong - sunLong)
        val karanaIndex = (elongation / 6.0).toInt().coerceIn(0, 59)
        val firstHalf = karanaIndex
        val secondHalf = (karanaIndex + 1) % 60
        val firstName = karanaName(firstHalf)
        val secondName = karanaName(secondHalf)
        return KaranaInfo(firstName.first, firstName.second, secondName.first, secondName.second)
    }

    private fun karanaName(halfIndex: Int): Pair<String, String> {
        return when (halfIndex) {
            0 -> Pair(KARANA_NAMES_ENGLISH[10], KARANA_NAMES_TELUGU[10])
            57 -> Pair(KARANA_NAMES_ENGLISH[7], KARANA_NAMES_TELUGU[7])
            58 -> Pair(KARANA_NAMES_ENGLISH[8], KARANA_NAMES_TELUGU[8])
            59 -> Pair(KARANA_NAMES_ENGLISH[9], KARANA_NAMES_TELUGU[9])
            else -> {
                val cycleIndex = (halfIndex - 1) % 7
                Pair(KARANA_NAMES_ENGLISH[cycleIndex], KARANA_NAMES_TELUGU[cycleIndex])
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Rashi from sidereal longitude
    // ═══════════════════════════════════════════════════════════

    private fun calculateRashi(siderealLong: Double): RashiInfo {
        val index = (siderealLong / 30.0).toInt().coerceIn(0, 11)
        return RashiInfo(
            index = index,
            nameEnglish = RASHI_NAMES_ENGLISH[index],
            nameTelugu = RASHI_NAMES_TELUGU[index],
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Samvatsara (60-year Telugu year cycle)
    // Telugu new year (Ugadi) falls in March/April.
    // For dates before Ugadi (~March/April), use previous year.
    // Reference: 2024-25 = Krodhi = index 37
    // ═══════════════════════════════════════════════════════════

    private fun calculateSamvatsara(year: Int, masaIndex: Int): SamvatsaraInfo {
        // Telugu year rolls over at Chaitra month start (Ugadi period).
        // Magha/Phalguna still belong to previous samvatsara.
        val samvatsaraYear = if (masaIndex >= 10) year - 1 else year
        // 2024 = Krodhi (index 37), so base offset is (samvatsaraYear - 2024 + 37) % 60
        val index = ((samvatsaraYear - 2024 + 37) % 60 + 60) % 60
        return SamvatsaraInfo(
            nameEnglish = SAMVATSARA_NAMES_ENGLISH[index],
            nameTelugu = SAMVATSARA_NAMES_TELUGU[index],
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Masa (Telugu lunar month, Amanta tradition):
    // Determine the month from the Sun's sidereal sign at the PREVIOUS new moon.
    // Meena at Amavasya -> Chaitra, Mesha -> Vaishakha, ... Kumbha -> Phalguna.
    // Also detect Adhika Masa if no Sankranti occurs between consecutive new moons.
    // ═══════════════════════════════════════════════════════════

    private fun calculateMasa(jd: Double): MasaComputation {
        val prevNewMoonJd = findPreviousNewMoonJd(jd)
        val nextNewMoonJd = findNextNewMoonJd(prevNewMoonJd)

        val prevNewMoonSunSidereal = siderealSunLongitude(prevNewMoonJd)
        val nextNewMoonSunSidereal = siderealSunLongitude(nextNewMoonJd)

        val prevSign = (prevNewMoonSunSidereal / 30.0).toInt().coerceIn(0, 11)
        val nextSign = (nextNewMoonSunSidereal / 30.0).toInt().coerceIn(0, 11)

        // Amanta month index is one sign ahead of Sun sign at Amavasya.
        val masaIndex = (prevSign + 1) % 12
        val isAdhikaMasa = prevSign == nextSign

        val englishName = if (isAdhikaMasa) "Adhika ${MASA_NAMES_ENGLISH[masaIndex]}" else MASA_NAMES_ENGLISH[masaIndex]
        val teluguName = if (isAdhikaMasa) "అధిక ${MASA_NAMES_TELUGU[masaIndex]}" else MASA_NAMES_TELUGU[masaIndex]

        return MasaComputation(
            info = MasaInfo(
                nameEnglish = englishName,
                nameTelugu = teluguName,
            ),
            masaIndex = masaIndex,
        )
    }

    private fun siderealSunLongitude(jd: Double): Double {
        val ayanamsa = lahiriAyanamsa(jd)
        return normalize360(sunLongitude(jd) - ayanamsa)
    }

    private fun findPreviousNewMoonJd(jd: Double): Double {
        // One synodic month is ~29.53 days; search window safely spans one cycle.
        return findCrossingJD(jd - 32.0, 0.0, 35.0) { siderealElongation(it) }
    }

    private fun findNextNewMoonJd(jd: Double): Double {
        return findCrossingJD(jd + 0.05, 0.0, 35.0) { siderealElongation(it) }
    }

    // ═══════════════════════════════════════════════════════════
    // Ayana — Uttarayana / Dakshinayana
    // Uttarayana: Sun in Makara to Mithuna (sidereal 270°–90°)
    // Dakshinayana: Sun in Karkataka to Dhanu (sidereal 90°–270°)
    // ═══════════════════════════════════════════════════════════

    private fun calculateAyana(sunSiderealLong: Double): AyanaInfo {
        // Uttarayana: Sun in 270° (Makara) through 0° to 90° (end of Mithuna)
        return if (sunSiderealLong >= 270.0 || sunSiderealLong < 90.0) {
            AyanaInfo(AYANA_UTTARAYANA_ENGLISH, AYANA_UTTARAYANA_TELUGU)
        } else {
            AyanaInfo(AYANA_DAKSHINAYANA_ENGLISH, AYANA_DAKSHINAYANA_TELUGU)
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Rutu (Season) — based on masa index
    // Chaitra-Vaishakha=Vasanta, Jyeshtha-Ashadha=Grishma, etc.
    // ═══════════════════════════════════════════════════════════

    private fun calculateRutu(sunSiderealLong: Double): RutuInfo {
        val masaIndex = (sunSiderealLong / 30.0).toInt().coerceIn(0, 11)
        return RutuInfo(
            nameEnglish = RUTU_NAMES_ENGLISH[masaIndex],
            nameTelugu = RUTU_NAMES_TELUGU[masaIndex],
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Sunrise / Sunset — Accurate Meeus method
    // ═══════════════════════════════════════════════════════════

    fun calculateSunTimes(
        lat: Double,
        lng: Double,
        year: Int,
        month: Int,
        day: Int,
        utcOffsetHours: Double,
    ): SunTimes {
        val jdNoon = julianDay(year, month, day, 12.0 - utcOffsetHours)
        val t = centuriesSinceJ2000(jdNoon)

        val l0 = normalize360(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        val m = normalize360(357.52911 + 35999.05029 * t - 0.0001537 * t * t)
        val mRad = m * PI / 180.0

        val c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mRad) +
                (0.019993 - 0.000101 * t) * sin(2 * mRad) +
                0.000289 * sin(3 * mRad)

        val sunTrueLong = l0 + c
        val omega = normalize360(125.04 - 1934.136 * t)
        val apparentLong = sunTrueLong - 0.00569 - 0.00478 * sin(omega * PI / 180.0)

        val obliquity0 = 23.439291 - 0.0130042 * t
        val obliquity = obliquity0 + 0.00256 * cos(omega * PI / 180.0)
        val oblRad = obliquity * PI / 180.0

        val declination = asin(sin(oblRad) * sin(apparentLong * PI / 180.0)) * 180.0 / PI

        val raRad = atan2(
            cos(oblRad) * sin(apparentLong * PI / 180.0),
            cos(apparentLong * PI / 180.0)
        )
        val ra = normalize360(raRad * 180.0 / PI)

        var eotDeg = l0 - 0.0057183 - ra
        while (eotDeg > 180.0) eotDeg -= 360.0
        while (eotDeg < -180.0) eotDeg += 360.0
        val eotMinutes = eotDeg * 4.0

        val latRad = lat * PI / 180.0
        val declRad = declination * PI / 180.0
        val cosHA = (sin(-0.8333 * PI / 180.0) - sin(latRad) * sin(declRad)) /
                (cos(latRad) * cos(declRad))

        val hourAngle = when {
            cosHA < -1.0 -> 180.0
            cosHA > 1.0 -> 0.0
            else -> acos(cosHA) * 180.0 / PI
        }

        val standardMeridian = utcOffsetHours * 15.0
        val longitudeCorrection = (standardMeridian - lng) * 4.0
        val solarNoonMinutes = 720.0 + longitudeCorrection - eotMinutes
        val solarNoon = solarNoonMinutes / 60.0

        val halfDayHours = hourAngle / 15.0
        val sunriseHours = solarNoon - halfDayHours
        val sunsetHours = solarNoon + halfDayHours

        return SunTimes(
            sunrise = formatTime(sunriseHours),
            sunset = formatTime(sunsetHours),
            sunriseDecimal = sunriseHours,
            sunsetDecimal = sunsetHours,
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Generic muhurta slot calculation (Rahu/Yamagandam/Gulika)
    // ═══════════════════════════════════════════════════════════

    private fun calculateMuhurtaSlot(
        dayOfWeek: Int,
        sunTimes: SunTimes,
        currentDecimal: Double,
        slotTable: IntArray,
    ): Triple<String, String, Boolean> {
        val slot = slotTable[dayOfWeek]
        val dayLength = sunTimes.sunsetDecimal - sunTimes.sunriseDecimal
        val slotDuration = dayLength / 8.0

        val start = sunTimes.sunriseDecimal + (slot - 1) * slotDuration
        val end = start + slotDuration

        val isActive = currentDecimal in start..end

        return Triple(formatTime(start), formatTime(end), isActive)
    }

    // ═══════════════════════════════════════════════════════════
    // Abhijit Muhurt (8th of 15 muhurtas in daytime)
    // ═══════════════════════════════════════════════════════════

    private fun calculateAbhijitMuhurt(
        sunTimes: SunTimes,
        currentDecimal: Double,
    ): AbhijitMuhurtInfo {
        val dayLength = sunTimes.sunsetDecimal - sunTimes.sunriseDecimal
        val muhurtaDuration = dayLength / 15.0
        val start = sunTimes.sunriseDecimal + 7 * muhurtaDuration
        val end = start + muhurtaDuration
        val isActive = currentDecimal in start..end
        return AbhijitMuhurtInfo(formatTime(start), formatTime(end), isActive)
    }

    // ═══════════════════════════════════════════════════════════
    // Brahma Muhurta — 96 minutes (1hr 36min) before sunrise
    // ═══════════════════════════════════════════════════════════

    private fun calculateBrahmaMuhurta(
        sunTimes: SunTimes,
        currentDecimal: Double,
    ): TimeSlotInfo {
        val brahmaDuration = 96.0 / 60.0 // 1.6 hours
        val muhurtaDuration = 48.0 / 60.0 // 48 minutes per muhurta
        val start = sunTimes.sunriseDecimal - brahmaDuration
        val end = start + muhurtaDuration
        val isActive = currentDecimal in start..end
        return TimeSlotInfo(
            nameTelugu = "బ్రహ్మ ముహూర్తం",
            nameEnglish = "Brahma Muhurta",
            startTime = formatTime(if (start < 0) start + 24 else start),
            endTime = formatTime(if (end < 0) end + 24 else end),
            isActive = isActive,
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Shubh Hora — Auspicious hora periods based on day ruler
    // Day divided into 12 daytime hora from sunrise to sunset
    // Each hora ~1 hour. Planet sequence rotates starting from day lord.
    // Sequence: Sun→Venus→Mercury→Moon→Saturn→Jupiter→Mars
    // Auspicious: Jupiter, Venus, Mercury, Moon
    // ═══════════════════════════════════════════════════════════

    private fun calculateShubhHoras(
        dayOfWeek: Int,
        sunTimes: SunTimes,
        currentDecimal: Double,
    ): List<TimeSlotInfo> {
        val dayLength = sunTimes.sunsetDecimal - sunTimes.sunriseDecimal
        val horaDuration = dayLength / 12.0
        val dayLordIndex = DAY_LORD_HORA_INDEX[dayOfWeek]

        val result = mutableListOf<TimeSlotInfo>()

        for (i in 0 until 12) {
            // Rotate through the 7-planet sequence starting from day lord
            val planetIndex = (dayLordIndex + i) % 7
            if (planetIndex in AUSPICIOUS_HORA_INDICES) {
                val start = sunTimes.sunriseDecimal + i * horaDuration
                val end = start + horaDuration
                val isActive = currentDecimal in start..end
                result.add(
                    TimeSlotInfo(
                        nameTelugu = HORA_PLANET_TELUGU[planetIndex],
                        nameEnglish = HORA_PLANET_ENGLISH[planetIndex],
                        startTime = formatTime(start),
                        endTime = formatTime(end),
                        isActive = isActive,
                    )
                )
            }
        }

        return result
    }

    // ═══════════════════════════════════════════════════════════
    // Utility
    // ═══════════════════════════════════════════════════════════

    private fun normalize360(degrees: Double): Double {
        var d = degrees % 360.0
        if (d < 0) d += 360.0
        return d
    }

    private fun formatTime(decimalHours: Double): String {
        var wrapped = decimalHours % 24.0
        if (wrapped < 0.0) wrapped += 24.0

        val totalMinutesRounded = ((wrapped * 60.0) + 0.5).toInt()
        val normalizedMinutes = ((totalMinutesRounded % (24 * 60)) + (24 * 60)) % (24 * 60)
        val hours = normalizedMinutes / 60
        val minutes = normalizedMinutes % 60
        val displayHour: Int
        val amPm: String
        if (hours < 12) {
            displayHour = if (hours == 0) 12 else hours
            amPm = "AM"
        } else {
            displayHour = if (hours == 12) 12 else hours - 12
            amPm = "PM"
        }
        return "$displayHour:${minutes.toString().padStart(2, '0')} $amPm"
    }
}
