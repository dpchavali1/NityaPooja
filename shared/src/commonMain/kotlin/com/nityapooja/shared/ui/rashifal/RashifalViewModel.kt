package com.nityapooja.shared.ui.rashifal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.RashiEntity
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.repository.DevotionalRepository
import com.nityapooja.shared.utils.AstronomicalCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

// ─────────────────────────────────────────────────────────────────────────────
// Result type returned to the UI
// ─────────────────────────────────────────────────────────────────────────────

data class RashifalPrediction(
    val textTelugu: String,
    val textEnglish: String,
    /** 1–12, Moon's house from this janma rashi */
    val moonHouse: Int,
    val isChandrashtama: Boolean,
    val moonRashiNameTelugu: String,
    val moonRashiNameEnglish: String,
    val sunRashiNameTelugu: String,
    val sunRashiNameEnglish: String,
)

class RashifalViewModel(
    private val repository: DevotionalRepository,
    preferencesManager: UserPreferencesManager,
) : ViewModel() {

    private data class LocationSettings(
        val lat: Double,
        val lng: Double,
        val timezone: String,
    )

    val rashis: StateFlow<List<RashiEntity>> = repository.getAllRashis()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val locationSettings: StateFlow<LocationSettings> = combine(
        preferencesManager.locationLat,
        preferencesManager.locationLng,
        preferencesManager.locationTimezone,
    ) { lat, lng, timezone ->
        LocationSettings(lat = lat, lng = lng, timezone = timezone)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LocationSettings(17.385, 78.4867, "Asia/Kolkata"),
    )

    private val _selectedRashi = MutableStateFlow<RashiEntity?>(null)
    val selectedRashi: StateFlow<RashiEntity?> = _selectedRashi.asStateFlow()

    fun selectRashi(id: Int) {
        viewModelScope.launch {
            _selectedRashi.value = repository.getRashiById(id).first()
        }
    }

    fun clearSelection() {
        _selectedRashi.value = null
    }

    /**
     * Computes today's transit-based prediction for [rashi].
     *
     * Primary driver: Moon's current gochara house (1–12 from janma rashi).
     * The Moon changes sign every ~2.5 days, so the prediction is NOT weekly-static.
     * Secondary context: Sun's current rashi (changes monthly).
     * Chandrashtama (Moon in 8th) is flagged explicitly.
     */
    fun computeGochaRa(rashi: RashiEntity): RashifalPrediction {
        val location = locationSettings.value
        val tz = try { TimeZone.of(location.timezone) } catch (_: Exception) { TimeZone.currentSystemDefault() }
        val now = Clock.System.now()
        val localNow = now.toLocalDateTime(tz)
        val utcOffsetHours = now.offsetIn(tz).totalSeconds / 3600.0

        // Respect panchangam day: if before sunrise, use previous civil date
        val sunTimes = AstronomicalCalculator.calculateSunTimesDecimal(
            lat = location.lat,
            lng = location.lng,
            year = localNow.year,
            month = localNow.monthNumber,
            day = localNow.dayOfMonth,
            utcOffsetHours = utcOffsetHours,
        )
        val sunriseDecimal = normalize24(sunTimes.sunriseDecimal)
        val currentHours = localNow.hour + localNow.minute / 60.0 + localNow.second / 3600.0
        val refDate = if (currentHours < sunriseDecimal) {
            localNow.date.plus(-1, DateTimeUnit.DAY)
        } else {
            localNow.date
        }

        // Julian Day at sunrise of the panchangam reference date (in UT)
        val sunriseUT = sunriseDecimal - utcOffsetHours
        val jd = AstronomicalCalculator.julianDay(
            refDate.year, refDate.monthNumber, refDate.dayOfMonth, sunriseUT,
        )

        val grahas = AstronomicalCalculator.allGrahaPositions(jd)
        val moonRashiIdx = grahas[1].rashiIndex   // 0-11, 0=Mesha
        val sunRashiIdx  = grahas[0].rashiIndex   // 0-11

        val janmaRashiIdx = rashiNameToIndex(rashi.name)   // 0-11
        val moonHouse = ((moonRashiIdx - janmaRashiIdx + 12) % 12) + 1  // 1-12

        val (predTelugu, predEnglish) = MOON_HOUSE_PREDICTIONS[moonHouse - 1]

        return RashifalPrediction(
            textTelugu = predTelugu,
            textEnglish = predEnglish,
            moonHouse = moonHouse,
            isChandrashtama = moonHouse == 8,
            moonRashiNameTelugu = RASHI_NAMES_TELUGU[moonRashiIdx],
            moonRashiNameEnglish = RASHI_NAMES_ENGLISH[moonRashiIdx],
            sunRashiNameTelugu = RASHI_NAMES_TELUGU[sunRashiIdx],
            sunRashiNameEnglish = RASHI_NAMES_ENGLISH[sunRashiIdx],
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Static data
    // ─────────────────────────────────────────────────────────────────────────

    private val RASHI_NAMES_TELUGU = arrayOf(
        "మేషం", "వృషభం", "మిథునం", "కర్కాటకం",
        "సింహం", "కన్య", "తుల", "వృశ్చికం",
        "ధనుస్సు", "మకరం", "కుంభం", "మీనం",
    )

    private val RASHI_NAMES_ENGLISH = arrayOf(
        "Mesha", "Vrishabha", "Mithuna", "Kataka",
        "Simha", "Kanya", "Tula", "Vrischika",
        "Dhanus", "Makara", "Kumbha", "Meena",
    )

    /**
     * Moon gochara predictions for houses 1–12 from janma rashi.
     * Index 0 = house 1 (Janma), index 7 = house 8 (Chandrashtama), etc.
     * Each entry: Pair(Telugu, English).
     */
    private val MOON_HOUSE_PREDICTIONS = arrayOf(
        // House 1 — Janma (Moon in own rashi)
        Pair(
            "చంద్రుడు నేడు మీ జన్మ రాశిలో సంచరిస్తున్నాడు (జన్మ స్థానం). మనస్సు చంచలంగా, భావుకంగా ఉంటుంది. ప్రయాణం, నూతన వ్యాపార ప్రారంభాలు వాయిదా వేయండి. ధ్యానం, పూజలకు ఉత్తమ సమయం. ఆత్మ పరిశీలన చేసుకోండి.",
            "Moon transits your own sign today (Janma). Mind is restless and emotional. Postpone travel and new ventures. Excellent time for prayer, meditation, and self-reflection.",
        ),
        // House 2 — Dhana
        Pair(
            "చంద్రుడు ద్వితీయ స్థానంలో ఉన్నాడు. ధన వ్యయం పట్ల జాగ్రత్త వహించండి. కుటుంబ విషయాలలో శ్రద్ధ అవసరం. వాక్కు నియంత్రించుకోండి — కఠినమైన మాటలు సంబంధాలను దెబ్బతీయవచ్చు. అప్పు ఇవ్వడం, తీసుకోవడం నివారించండి.",
            "Moon in your 2nd house. Guard finances and be mindful of speech. Family matters need attention. Avoid lending or borrowing. Harsh words may strain relationships.",
        ),
        // House 3 — Parakrama (Upachaya — favorable)
        Pair(
            "చంద్రుడు తృతీయ స్థానంలో ఉన్నాడు. సాహస కార్యాలు, చిన్న ప్రయాణాలు అనుకూలంగా ఉంటాయి. సోదర సంబంధాలు మెరుగవుతాయి. రాసిన, మాటల ద్వారా సాధించే పనులు విజయవంతమవుతాయి. మీ శ్రమ తప్పకుండా ఫలిస్తుంది.",
            "Moon in your 3rd house — favorable for courage and action. Good for short travel, communication, and sibling matters. Your effort yields results today. Writing and speaking bring success.",
        ),
        // House 4 — Sukha (Kendra)
        Pair(
            "చంద్రుడు చతుర్థ స్థానంలో ఉన్నాడు. గృహ సుఖం, మాతృ ఆశీర్వాదం ప్రాప్తిస్తుంది. స్థిరాస్తి, వాహన విషయాలకు అనుకూలం. మనస్సు ప్రశాంతంగా ఉంటుంది. ఇంట్లో ఉండడం, కుటుంబంతో సమయం గడపడం శుభప్రదం.",
            "Moon in your 4th house. Domestic comfort and mother's blessings. Good for home, property, and vehicle matters. Mind is calm and content. Spending time with family is auspicious.",
        ),
        // House 5 — Putra (Trikona — highly auspicious)
        Pair(
            "చంద్రుడు పంచమ స్థానంలో ఉన్నాడు — అత్యంత శుభకరం! సంతానానికి శుభం, సృజనాత్మకత వికసిస్తుంది. ఇష్టదేవతను ప్రార్థించండి. మంత్ర జపం, పూజలు విశేష ఫలాన్ని ఇస్తాయి. అదృష్టం మీ వైపు నవ్వుతోంది.",
            "Moon in your 5th house — highly auspicious! Blessings for children and creativity. Pray to your Ishta Devata. Mantra japa and worship yield exceptional results. Fortune smiles on you today.",
        ),
        // House 6 — Shatru/Roga
        Pair(
            "చంద్రుడు షష్ఠ స్థానంలో ఉన్నాడు. ఆరోగ్యం పట్ల జాగ్రత్త వహించండి. శత్రువులు, అడ్డంకులు ఉండవచ్చు. వివాదాలు, కోర్టు విషయాలు నివారించండి. హనుమాన్ చాలీసా పఠించడం రక్షణ కల్పిస్తుంది. సేవా కార్యాలు పుణ్యప్రదం.",
            "Moon in your 6th house. Take care of health. Obstacles and opponents may surface. Avoid disputes and legal matters. Chanting Hanuman Chalisa offers protection. Acts of service bring merit.",
        ),
        // House 7 — Kalatra (Kendra)
        Pair(
            "చంద్రుడు సప్తమ స్థానంలో ఉన్నాడు. దాంపత్య, వ్యాపార భాగస్వామ్యాలకు సమతుల్యత అవసరం. ప్రయాణం సాధ్యమే కానీ జాగ్రత్తగా ప్రణాళిక వేయండి. అహంకారం వదిలి సహకరించడం ద్వారా ఫలితాలు మెరుగవుతాయి.",
            "Moon in your 7th house. Partnerships and relationships need balance. Travel is possible but plan carefully. Cooperation over ego brings better outcomes in business and personal matters.",
        ),
        // House 8 — Chandrashtama (most critical — warn prominently)
        Pair(
            "⚠️ చంద్రాష్టమం: చంద్రుడు అష్టమ స్థానంలో ఉన్నాడు. ఇది అత్యంత సున్నితమైన కాలం. ముఖ్యమైన నిర్ణయాలు, శస్త్రచికిత్స, పెట్టుబడులు వాయిదా వేయండి. ఆరోగ్యం పట్ల అధిక జాగ్రత్త తీసుకోండి. మహామృత్యుంజయ మంత్రం, శివ పంచాక్షరం జపించండి. ఓర్పుగా ఉండండి.",
            "⚠️ Chandrashtama: Moon is in your 8th house — the most sensitive transit. Avoid major decisions, surgery, and new investments. Take extra care of health. Chant Maha Mrityunjaya Mantra and Shiva Panchakshara. Practice patience.",
        ),
        // House 9 — Bhagya (Trikona — highly auspicious)
        Pair(
            "చంద్రుడు భాగ్య స్థానంలో ఉన్నాడు — అత్యంత శుభకరం! గురువు, పెద్దల ఆశీర్వాదాలు లభిస్తాయి. దేవాలయ దర్శనం, తీర్థ యాత్ర, దానధర్మాలు చేయండి. అదృష్టం పుష్కలంగా ఉంది. ఈ రోజు పూజలు చేయడం విశేష ఫలాన్ని ఇస్తుంది.",
            "Moon in your 9th house — highly auspicious! Blessings from Guru and elders. Visit temples, perform charity, and engage in dharmic activities. Fortune is abundant. Worship today yields exceptional blessings.",
        ),
        // House 10 — Karma (Kendra)
        Pair(
            "చంద్రుడు కర్మ స్థానంలో ఉన్నాడు. వృత్తి, పేరు ప్రతిష్ఠలకు అనుకూలమైన రోజు. పై అధికారుల మద్దతు, సహకారం లభిస్తుంది. కష్టపడి పని చేయండి — ఫలితాలు తప్పకుండా వస్తాయి. ప్రజా కార్యక్రమాలలో పాల్గొనడానికి మంచి సమయం.",
            "Moon in your 10th house. Favorable for career, reputation, and public recognition. Support from superiors. Hard work brings tangible results. Good time for professional activities and public engagements.",
        ),
        // House 11 — Labha (best position)
        Pair(
            "చంద్రుడు లాభ స్థానంలో ఉన్నాడు — అత్యుత్తమం! ధనలాభం, కోరికలు నెరవేరే అవకాశం అధికం. వ్యాపార లావాదేవీలు, సాంఘిక కార్యక్రమాలు సఫలమవుతాయి. స్నేహితుల ద్వారా ప్రయోజనం కలుగుతుంది. మీ కోరికలు ఈ రోజు ఫలిస్తాయి.",
            "Moon in your 11th house — excellent! Financial gains and fulfilment of desires are strongly favored. Business dealings and social connections succeed. Friends bring benefit. Your wishes materialise today.",
        ),
        // House 12 — Vyaya
        Pair(
            "చంద్రుడు ద్వాదశ స్థానంలో ఉన్నాడు. అనవసర వ్యయాలు, నష్టాల పట్ల జాగ్రత్త. నిద్ర భంగం అవుతుండవచ్చు. ఆధ్యాత్మిక సాధన, ధ్యానం, దానానికి అనుకూలమైన సమయం. విశ్రాంతి తీసుకోండి. మోక్ష సాధన, భక్తి కార్యాలు పుణ్యప్రదం.",
            "Moon in your 12th house. Guard against unnecessary expenses and losses. Sleep may be disturbed. Excellent for spiritual practice, meditation, and charity. Rest. Devotional and moksha-oriented activities are meritorious.",
        ),
    )

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Maps a rashi English name (as stored in DB) to a 0-based index (0=Mesha…11=Meena). */
    private fun rashiNameToIndex(name: String): Int = when (name.lowercase().trim()) {
        "mesha", "aries"       -> 0
        "vrishabha", "taurus"  -> 1
        "mithuna", "gemini"    -> 2
        "kataka", "cancer"     -> 3
        "simha", "leo"         -> 4
        "kanya", "virgo"       -> 5
        "tula", "libra"        -> 6
        "vrischika", "scorpio" -> 7
        "dhanus", "sagittarius"-> 8
        "makara", "capricorn"  -> 9
        "kumbha", "aquarius"   -> 10
        "meena", "pisces"      -> 11
        else -> {
            println("WARNING: Unknown rashi name '$name' — using hash fallback")
            name.hashCode().and(0x7FFFFFFF) % 12
        }
    }

    private fun normalize24(value: Double): Double {
        var v = value % 24.0
        if (v < 0) v += 24.0
        return v
    }
}
