package com.nityapooja.shared.data.muhurtam

import com.nityapooja.shared.ui.panchangam.PanchangamData

/**
 * Traditional Telugu muhurtam rules for life events.
 * NOT astrology — these are cultural almanac rules that Telugu families follow.
 *
 * Tithi indices: 0-14 = Shukla Prathama..Pournami, 15-29 = Krishna Prathama..Amavasya
 * Nakshatra indices: 0-26 = Ashwini..Revati
 * Yoga indices: 0-26 = Vishkambha..Vaidhriti
 * Day of week: 1=Sunday..7=Saturday
 */
object MuhurtamRules {

    enum class EventType(val nameTelugu: String, val nameEnglish: String) {
        GRIHA_PRAVESHAM("గృహ ప్రవేశం", "Housewarming"),
        UPANAYANAM("ఉపనయనం", "Thread Ceremony"),
        WEDDING("వివాహం", "Wedding"),
        VEHICLE_PURCHASE("వాహన కొనుగోలు", "Vehicle Purchase"),
        BUSINESS_START("వ్యాపారం ప్రారంభం", "Business Start"),
        TRAVEL("యాత్ర", "Travel"),
        NAMING_CEREMONY("నామకరణం", "Naming Ceremony"),
    }

    enum class MuhurtamScore(val labelTelugu: String, val labelEnglish: String) {
        EXCELLENT("అత్యుత్తమం", "Excellent"),
        GOOD("శుభం", "Good"),
        AVERAGE("సామాన్యం", "Average"),
        AVOID("నివారించండి", "Avoid"),
    }

    data class MuhurtamResult(
        val score: MuhurtamScore,
        val points: Int, // 0-100
        val reasons: List<MuhurtamReason>,
    )

    data class MuhurtamReason(
        val textTelugu: String,
        val textEnglish: String,
        val isPositive: Boolean,
    )

    private data class EventRules(
        val auspiciousTithis: Set<Int>,
        val avoidTithis: Set<Int>,
        val auspiciousNakshatras: Set<Int>,
        val avoidNakshatras: Set<Int>,
        val auspiciousYogas: Set<Int>,
        val avoidYogas: Set<Int>,
        val auspiciousDays: Set<Int>, // 1=Sun..7=Sat
        val avoidDays: Set<Int>,
    )

    // Common avoid sets
    private val RIKTA_TITHIS = setOf(3, 7, 12, 18, 22, 27) // Chaturthi, Ashtami, Trayodashi (both pakshas)
    private val AMAVASYA = setOf(29)
    private val POURNAMI = setOf(14)
    private val COMMON_AVOID_TITHIS = RIKTA_TITHIS + AMAVASYA

    // Auspicious nakshatras for most events
    private val STHIRA_NAKSHATRAS = setOf(3, 11, 20) // Rohini, Uttara Phalguni, Uttarashadha
    private val MRIDU_NAKSHATRAS = setOf(4, 13, 16, 26) // Mrigashira, Chitra, Anuradha, Revati
    private val KSHIPRA_NAKSHATRAS = setOf(0, 7, 12, 21) // Ashwini, Pushyami, Hasta, Shravana
    private val COMMON_GOOD_NAKSHATRAS = STHIRA_NAKSHATRAS + MRIDU_NAKSHATRAS + KSHIPRA_NAKSHATRAS

    // Avoid nakshatras for most events
    private val COMMON_AVOID_NAKSHATRAS = setOf(5, 8, 9, 17, 18) // Ardra, Ashlesha, Magha, Jyeshtha, Moola

    // Avoid yogas
    private val AVOID_YOGAS = setOf(0, 5, 8, 9, 12, 14, 16, 26) // Vishkambha, Atiganda, Shoola, Ganda, Vyaghata, Vajra, Vyatipata, Vaidhriti

    // Auspicious yogas
    private val GOOD_YOGAS = setOf(1, 2, 3, 4, 6, 7, 10, 11, 13, 15, 19, 20, 21, 22, 23) // Preeti, Ayushman, Saubhagya, Shobhana, Sukarma, Dhriti, Vriddhi, Dhruva, Harshana, Siddhi, Shiva, Siddha, Sadhya, Shubha, Shukla

    private val rules: Map<EventType, EventRules> = mapOf(
        EventType.GRIHA_PRAVESHAM to EventRules(
            auspiciousTithis = setOf(1, 2, 4, 6, 9, 10, 11, 14), // Dwitiya, Tritiya, Panchami, Saptami, Dashami, Ekadashi, Dwadashi, Pournami
            avoidTithis = COMMON_AVOID_TITHIS + setOf(0, 8, 13), // also Prathama, Navami, Chaturdashi
            auspiciousNakshatras = setOf(3, 6, 7, 11, 12, 20, 21, 25, 26), // Rohini, Punarvasu, Pushyami, Uttara Phalguni, Hasta, Uttarashadha, Shravana, Uttarabhadra, Revati
            avoidNakshatras = COMMON_AVOID_NAKSHATRAS + setOf(1, 2), // also Bharani, Krittika
            auspiciousYogas = GOOD_YOGAS,
            avoidYogas = AVOID_YOGAS,
            auspiciousDays = setOf(1, 2, 4, 5), // Sun, Mon, Wed, Thu
            avoidDays = setOf(3, 7), // Tue, Sat
        ),
        EventType.UPANAYANAM to EventRules(
            auspiciousTithis = setOf(1, 2, 4, 6, 9, 10, 11), // Shukla paksha preferred
            avoidTithis = COMMON_AVOID_TITHIS,
            auspiciousNakshatras = setOf(0, 3, 6, 7, 11, 12, 15, 21, 25, 26), // Ashwini, Rohini, Punarvasu, Pushyami, Uttara Phalguni, Hasta, Vishakha, Shravana, Uttarabhadra, Revati
            avoidNakshatras = COMMON_AVOID_NAKSHATRAS,
            auspiciousYogas = GOOD_YOGAS,
            avoidYogas = AVOID_YOGAS,
            auspiciousDays = setOf(1, 2, 4, 5), // Sun, Mon, Wed, Thu
            avoidDays = setOf(3, 7), // Tue, Sat
        ),
        EventType.WEDDING to EventRules(
            auspiciousTithis = setOf(1, 2, 4, 6, 9, 10, 11), // Dwitiya, Tritiya, Panchami, Saptami, Dashami, Ekadashi, Dwadashi
            avoidTithis = COMMON_AVOID_TITHIS + POURNAMI + setOf(0, 8, 13),
            auspiciousNakshatras = setOf(3, 4, 6, 7, 11, 12, 13, 15, 16, 20, 21, 25, 26), // Rohini, Mrigashira, Punarvasu, Pushyami, UPhalguni, Hasta, Chitra, Vishakha, Anuradha, Uttarashadha, Shravana, Uttarabhadra, Revati
            avoidNakshatras = COMMON_AVOID_NAKSHATRAS + setOf(1, 9, 22), // Bharani, Magha, Dhanishta
            auspiciousYogas = GOOD_YOGAS,
            avoidYogas = AVOID_YOGAS,
            auspiciousDays = setOf(1, 2, 4, 5), // Sun, Mon, Wed, Thu
            avoidDays = setOf(3, 7), // Tue, Sat
        ),
        EventType.VEHICLE_PURCHASE to EventRules(
            auspiciousTithis = setOf(1, 2, 4, 6, 9, 10, 11, 14),
            avoidTithis = COMMON_AVOID_TITHIS,
            auspiciousNakshatras = setOf(0, 3, 6, 7, 12, 21, 26), // Ashwini, Rohini, Punarvasu, Pushyami, Hasta, Shravana, Revati
            avoidNakshatras = COMMON_AVOID_NAKSHATRAS,
            auspiciousYogas = GOOD_YOGAS,
            avoidYogas = AVOID_YOGAS,
            auspiciousDays = setOf(1, 2, 4, 5, 6), // Sun, Mon, Wed, Thu, Fri
            avoidDays = setOf(3, 7), // Tue, Sat
        ),
        EventType.BUSINESS_START to EventRules(
            auspiciousTithis = setOf(1, 2, 4, 6, 9, 10, 11, 14),
            avoidTithis = COMMON_AVOID_TITHIS,
            auspiciousNakshatras = setOf(0, 3, 6, 7, 11, 12, 15, 20, 21, 26), // Ashwini, Rohini, Punarvasu, Pushyami, UPhalguni, Hasta, Vishakha, Uttarashadha, Shravana, Revati
            avoidNakshatras = COMMON_AVOID_NAKSHATRAS,
            auspiciousYogas = GOOD_YOGAS,
            avoidYogas = AVOID_YOGAS,
            auspiciousDays = setOf(1, 2, 4, 5, 6), // Sun, Mon, Wed, Thu, Fri
            avoidDays = setOf(3, 7), // Tue, Sat
        ),
        EventType.TRAVEL to EventRules(
            auspiciousTithis = setOf(1, 2, 4, 6, 9, 10, 11, 14),
            avoidTithis = COMMON_AVOID_TITHIS + setOf(8), // also Navami
            auspiciousNakshatras = setOf(0, 4, 6, 7, 12, 13, 14, 21, 26), // Ashwini, Mrigashira, Punarvasu, Pushyami, Hasta, Chitra, Swati, Shravana, Revati
            avoidNakshatras = COMMON_AVOID_NAKSHATRAS + setOf(1), // also Bharani
            auspiciousYogas = GOOD_YOGAS,
            avoidYogas = AVOID_YOGAS,
            auspiciousDays = setOf(1, 2, 4, 5, 6), // Sun, Mon, Wed, Thu, Fri
            avoidDays = setOf(3, 7), // Tue, Sat
        ),
        EventType.NAMING_CEREMONY to EventRules(
            auspiciousTithis = setOf(1, 2, 4, 6, 9, 10, 11, 14),
            avoidTithis = COMMON_AVOID_TITHIS,
            auspiciousNakshatras = setOf(0, 3, 4, 6, 7, 11, 12, 13, 15, 21, 25, 26), // Ashwini, Rohini, Mrigashira, Punarvasu, Pushyami, UPhalguni, Hasta, Chitra, Vishakha, Shravana, Uttarabhadra, Revati
            avoidNakshatras = COMMON_AVOID_NAKSHATRAS,
            auspiciousYogas = GOOD_YOGAS,
            avoidYogas = AVOID_YOGAS,
            auspiciousDays = setOf(1, 2, 4, 5, 6),
            avoidDays = setOf(3, 7),
        ),
    )

    // Telugu nakshatra names for reasons
    private val NAKSHATRA_TELUGU = arrayOf(
        "అశ్విని", "భరణి", "కృత్తిక", "రోహిణి", "మృగశిర",
        "ఆర్ద్ర", "పునర్వసు", "పుష్యమి", "ఆశ్లేష", "మఘ",
        "పూర్వ ఫల్గుణి", "ఉత్తర ఫల్గుణి", "హస్త", "చిత్ర", "స్వాతి",
        "విశాఖ", "అనురాధ", "జ్యేష్ఠ", "మూల", "పూర్వాషాఢ",
        "ఉత్తరాషాఢ", "శ్రవణం", "ధనిష్ఠ", "శతభిషం",
        "పూర్వాభాద్ర", "ఉత్తరాభాద్ర", "రేవతి",
    )

    private val NAKSHATRA_ENGLISH = arrayOf(
        "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira",
        "Ardra", "Punarvasu", "Pushyami", "Ashlesha", "Magha",
        "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati",
        "Vishakha", "Anuradha", "Jyeshtha", "Moola", "Purvashadha",
        "Uttarashadha", "Shravana", "Dhanishta", "Shatabhisha",
        "Purvabhadra", "Uttarabhadra", "Revati",
    )

    // Tara Balam — 9 groups counted from birth nakshatra
    enum class TaraBalam(val nameTelugu: String, val nameEnglish: String, val isGood: Boolean) {
        JANMA("జన్మ", "Janma", false),
        SAMPAT("సంపత్", "Sampat", true),
        VIPAT("విపత్", "Vipat", false),
        KSHEMA("క్షేమ", "Kshema", true),
        PRATYARI("ప్రత్యరి", "Pratyari", false),
        SADHAKA("సాధక", "Sadhaka", true),
        VADHA("వధ", "Vadha", false),
        MITRA("మిత్ర", "Mitra", true),
        PARAMA_MITRA("పరమ మిత్ర", "Parama Mitra", true),
    }

    /**
     * Calculate Tara Balam from birth nakshatra index to today's nakshatra index.
     * Returns null if birth nakshatra is not set.
     */
    fun calculateTaraBalam(birthNakshatraIndex: Int, todayNakshatraIndex: Int): TaraBalam {
        val distance = ((todayNakshatraIndex - birthNakshatraIndex + 27) % 27)
        val taraGroup = distance % 9
        return TaraBalam.entries[taraGroup]
    }

    /**
     * Chandrabalam — Moon strength based on moon's current rashi relative to birth rashi.
     * Good positions (from birth rashi): 1, 3, 6, 7, 10, 11
     * Bad positions: 2, 4, 5, 8, 9, 12
     */
    data class ChandraBalam(val position: Int, val isGood: Boolean, val nameTelugu: String, val nameEnglish: String)

    private val GOOD_CHANDRA_POSITIONS = setOf(1, 3, 6, 7, 10, 11)

    fun calculateChandraBalam(birthRashiIndex: Int, moonRashiIndex: Int): ChandraBalam {
        val position = ((moonRashiIndex - birthRashiIndex + 12) % 12) + 1 // 1-based
        val isGood = position in GOOD_CHANDRA_POSITIONS
        val nameTelugu = if (isGood) "చంద్ర బలం ఉంది" else "చంద్ర బలం లేదు"
        val nameEnglish = if (isGood) "Chandrabalam present" else "Chandrabalam absent"
        return ChandraBalam(position, isGood, nameTelugu, nameEnglish)
    }

    // Rashi names for lookup
    private val RASHI_TELUGU = arrayOf(
        "మేషం", "వృషభం", "మిథునం", "కర్కాటకం",
        "సింహం", "కన్య", "తుల", "వృశ్చికం",
        "ధనుస్సు", "మకరం", "కుంభం", "మీనం",
    )

    fun rashiIndexFromTelugu(name: String): Int {
        return RASHI_TELUGU.indexOfFirst { name.contains(it) || it.contains(name) }
    }

    /**
     * Resolve a nakshatra name (Telugu) to its index (0-26). Returns -1 if not found.
     */
    fun nakshatraIndexFromTelugu(name: String): Int {
        return NAKSHATRA_TELUGU.indexOfFirst { name.contains(it) || it.contains(name) }
    }

    /**
     * Score a given day's panchangam for a specific event type.
     * Returns a MuhurtamResult with score, points (0-100), and bilingual reasons.
     * If birthNakshatraIndex >= 0, Tara Balam is factored into the score.
     */
    fun scoreMuhurtam(panchangam: PanchangamData, eventType: EventType, birthNakshatraIndex: Int = -1, birthRashiIndex: Int = -1): MuhurtamResult {
        val eventRules = rules[eventType] ?: return MuhurtamResult(MuhurtamScore.AVERAGE, 50, emptyList())

        var points = 50 // start neutral
        val reasons = mutableListOf<MuhurtamReason>()

        val tithiIndex = panchangam.tithi.index
        val nakshatraIndex = panchangam.nakshatra.index
        val yogaIndex = panchangam.yoga.index
        val dayOfWeek = dayOfWeekFromTelugu(panchangam.teluguDay)

        // Tithi scoring
        when {
            tithiIndex in eventRules.auspiciousTithis -> {
                points += 15
                reasons.add(MuhurtamReason(
                    "${panchangam.tithi.nameTelugu} - శుభ తిథి",
                    "${panchangam.tithi.nameEnglish} - Auspicious tithi",
                    true,
                ))
            }
            tithiIndex in eventRules.avoidTithis -> {
                points -= 20
                reasons.add(MuhurtamReason(
                    "${panchangam.tithi.nameTelugu} - అశుభ తిథి",
                    "${panchangam.tithi.nameEnglish} - Inauspicious tithi",
                    false,
                ))
            }
        }

        // Nakshatra scoring
        when {
            nakshatraIndex in eventRules.auspiciousNakshatras -> {
                points += 20
                reasons.add(MuhurtamReason(
                    "${NAKSHATRA_TELUGU[nakshatraIndex]} - ${eventType.nameTelugu}కు శుభ నక్షత్రం",
                    "${NAKSHATRA_ENGLISH[nakshatraIndex]} - Auspicious for ${eventType.nameEnglish}",
                    true,
                ))
            }
            nakshatraIndex in eventRules.avoidNakshatras -> {
                points -= 25
                reasons.add(MuhurtamReason(
                    "${NAKSHATRA_TELUGU[nakshatraIndex]} - నివారించాల్సిన నక్షత్రం",
                    "${NAKSHATRA_ENGLISH[nakshatraIndex]} - Avoid this nakshatra",
                    false,
                ))
            }
        }

        // Yoga scoring
        when {
            yogaIndex in eventRules.auspiciousYogas -> {
                points += 10
                reasons.add(MuhurtamReason(
                    "${panchangam.yoga.nameTelugu} - శుభ యోగం",
                    "${panchangam.yoga.nameEnglish} - Auspicious yoga",
                    true,
                ))
            }
            yogaIndex in eventRules.avoidYogas -> {
                points -= 15
                reasons.add(MuhurtamReason(
                    "${panchangam.yoga.nameTelugu} - అశుభ యోగం",
                    "${panchangam.yoga.nameEnglish} - Inauspicious yoga",
                    false,
                ))
            }
        }

        // Day of week scoring
        when {
            dayOfWeek in eventRules.auspiciousDays -> {
                points += 10
                reasons.add(MuhurtamReason(
                    "${panchangam.teluguDay} - శుభ వారం",
                    "${panchangam.englishDay} - Auspicious day",
                    true,
                ))
            }
            dayOfWeek in eventRules.avoidDays -> {
                points -= 15
                reasons.add(MuhurtamReason(
                    "${panchangam.teluguDay} - నివారించాల్సిన వారం",
                    "${panchangam.englishDay} - Avoid this day",
                    false,
                ))
            }
        }

        // Krishna paksha penalty for most events (Shukla preferred)
        if (tithiIndex >= 15 && eventType != EventType.TRAVEL) {
            points -= 10
            reasons.add(MuhurtamReason(
                "కృష్ణ పక్షం - శుక్ల పక్షం మెరుగు",
                "Krishna Paksha - Shukla Paksha preferred",
                false,
            ))
        }

        // Tara Balam (personalized based on birth nakshatra)
        if (birthNakshatraIndex in 0..26) {
            val tara = calculateTaraBalam(birthNakshatraIndex, nakshatraIndex)
            if (tara.isGood) {
                val bonus = if (tara == TaraBalam.PARAMA_MITRA) 15 else 10
                points += bonus
                reasons.add(MuhurtamReason(
                    "తార బలం: ${tara.nameTelugu} - మీ జన్మ నక్షత్రానికి శుభం",
                    "Tara Balam: ${tara.nameEnglish} - Auspicious for your birth star",
                    true,
                ))
            } else {
                points -= 15
                reasons.add(MuhurtamReason(
                    "తార బలం: ${tara.nameTelugu} - మీ జన్మ నక్షత్రానికి అశుభం",
                    "Tara Balam: ${tara.nameEnglish} - Inauspicious for your birth star",
                    false,
                ))
            }
        }

        // Chandrabalam (personalized based on birth rashi)
        if (birthRashiIndex in 0..11) {
            val chandra = calculateChandraBalam(birthRashiIndex, panchangam.moonRashi.index)
            if (chandra.isGood) {
                points += 10
                reasons.add(MuhurtamReason(
                    "చంద్ర బలం ఉంది (${chandra.position}వ స్థానం) - శుభం",
                    "Chandrabalam present (${chandra.position}th position) - Auspicious",
                    true,
                ))
            } else {
                points -= 15
                reasons.add(MuhurtamReason(
                    "చంద్ర బలం లేదు (${chandra.position}వ స్థానం) - అశుభం",
                    "Chandrabalam absent (${chandra.position}th position) - Inauspicious",
                    false,
                ))
            }
        }

        // Rahu Kalam active is always a concern
        if (panchangam.rahuKaal.isActive) {
            reasons.add(MuhurtamReason(
                "రాహు కాలం: ${panchangam.rahuKaal.startTime} - ${panchangam.rahuKaal.endTime} నివారించండి",
                "Avoid Rahu Kalam: ${panchangam.rahuKaal.startTime} - ${panchangam.rahuKaal.endTime}",
                false,
            ))
        }

        // Clamp points
        val clampedPoints = points.coerceIn(0, 100)

        val score = when {
            clampedPoints >= 75 -> MuhurtamScore.EXCELLENT
            clampedPoints >= 55 -> MuhurtamScore.GOOD
            clampedPoints >= 35 -> MuhurtamScore.AVERAGE
            else -> MuhurtamScore.AVOID
        }

        return MuhurtamResult(score, clampedPoints, reasons)
    }

    private fun dayOfWeekFromTelugu(teluguDay: String): Int {
        return when {
            teluguDay.contains("ఆదివారం") -> 1
            teluguDay.contains("సోమవారం") -> 2
            teluguDay.contains("మంగళవారం") -> 3
            teluguDay.contains("బుధవారం") -> 4
            teluguDay.contains("గురువారం") -> 5
            teluguDay.contains("శుక్రవారం") -> 6
            teluguDay.contains("శనివారం") -> 7
            else -> 0
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Choghadiya — 8 day + 8 night time slots
    // ═══════════════════════════════════════════════════════════════

    enum class ChoghadiyaType(
        val nameTelugu: String,
        val nameEnglish: String,
        val isAuspicious: Boolean,
        val descriptionTelugu: String,
        val descriptionEnglish: String,
    ) {
        AMRIT("అమృత", "Amrit", true, "అన్ని పనులకు అత్యుత్తమ కాలం", "Best time for all activities"),
        SHUBH("శుభ", "Shubh", true, "శుభ కార్యాలకు మంచి కాలం", "Good for auspicious activities"),
        LABH("లాభ", "Labh", true, "వ్యాపారం మరియు కొత్త పనులకు మంచిది", "Good for business and new ventures"),
        CHAL("చర", "Chal", true, "యాత్రలకు అనువైన కాలం", "Suitable for travel"),
        UDVEG("ఉద్వేగ", "Udveg", false, "ఈ కాలంలో ముఖ్యమైన పనులు నివారించండి", "Avoid important tasks in this period"),
        KAAL("కాల", "Kaal", false, "ముఖ్యమైన పనులు నివారించండి", "Avoid important activities"),
        ROG("రోగ", "Rog", false, "ముఖ్యమైన పనులు నివారించండి", "Avoid important activities"),
    }

    data class ChoghadiyaSlot(
        val type: ChoghadiyaType,
        val startTime: String,
        val endTime: String,
        val isActive: Boolean,
        val isDay: Boolean,
    )

    data class ChoghadiyaResult(
        val daySlots: List<ChoghadiyaSlot>,
        val nightSlots: List<ChoghadiyaSlot>,
    )

    // Enum ordinals: AMRIT=0, SHUBH=1, LABH=2, CHAL=3, UDVEG=4, KAAL=5, ROG=6
    // Day Choghadiya sequences indexed [dayOfWeek 0=Sunday..6=Saturday][slot 0..7]
    private val DAY_SEQUENCES = arrayOf(
        // Sunday:    Udveg, Chal, Labh, Amrit, Kaal, Shubh, Rog, Udveg
        intArrayOf(4, 3, 2, 0, 5, 1, 6, 4),
        // Monday:    Amrit, Kaal, Shubh, Rog, Udveg, Chal, Labh, Amrit
        intArrayOf(0, 5, 1, 6, 4, 3, 2, 0),
        // Tuesday:   Rog, Udveg, Chal, Labh, Amrit, Kaal, Shubh, Rog
        intArrayOf(6, 4, 3, 2, 0, 5, 1, 6),
        // Wednesday: Labh, Amrit, Kaal, Shubh, Rog, Udveg, Chal, Labh
        intArrayOf(2, 0, 5, 1, 6, 4, 3, 2),
        // Thursday:  Shubh, Rog, Udveg, Chal, Labh, Amrit, Kaal, Shubh
        intArrayOf(1, 6, 4, 3, 2, 0, 5, 1),
        // Friday:    Chal, Labh, Amrit, Kaal, Shubh, Rog, Udveg, Chal
        intArrayOf(3, 2, 0, 5, 1, 6, 4, 3),
        // Saturday:  Kaal, Shubh, Rog, Udveg, Chal, Labh, Amrit, Kaal
        intArrayOf(5, 1, 6, 4, 3, 2, 0, 5),
    )

    // Night Choghadiya sequences indexed [dayOfWeek 0=Sunday..6=Saturday][slot 0..7]
    private val NIGHT_SEQUENCES = arrayOf(
        // Sunday night:    Shubh, Amrit, Chal, Rog, Kaal, Labh, Udveg, Shubh
        intArrayOf(1, 0, 3, 6, 5, 2, 4, 1),
        // Monday night:    Chal, Rog, Kaal, Labh, Udveg, Shubh, Amrit, Chal
        intArrayOf(3, 6, 5, 2, 4, 1, 0, 3),
        // Tuesday night:   Kaal, Labh, Udveg, Shubh, Amrit, Chal, Rog, Kaal
        intArrayOf(5, 2, 4, 1, 0, 3, 6, 5),
        // Wednesday night: Udveg, Shubh, Amrit, Chal, Rog, Kaal, Labh, Udveg
        intArrayOf(4, 1, 0, 3, 6, 5, 2, 4),
        // Thursday night:  Amrit, Chal, Rog, Kaal, Labh, Udveg, Shubh, Amrit
        intArrayOf(0, 3, 6, 5, 2, 4, 1, 0),
        // Friday night:    Rog, Kaal, Labh, Udveg, Shubh, Amrit, Chal, Rog
        intArrayOf(6, 5, 2, 4, 1, 0, 3, 6),
        // Saturday night:  Labh, Udveg, Shubh, Amrit, Chal, Rog, Kaal, Labh
        intArrayOf(2, 4, 1, 0, 3, 6, 5, 2),
    )

    private val CHOGHADIYA_TYPES = ChoghadiyaType.entries.toTypedArray()

    /**
     * Calculate Choghadiya slots for a given day.
     *
     * @param sunriseDecimal Sunrise in local decimal hours (e.g. 6.25 = 06:15)
     * @param sunsetDecimal  Sunset in local decimal hours (e.g. 18.75 = 18:45)
     * @param dayOfWeek      0=Sunday, 1=Monday ... 6=Saturday
     * @param currentDecimal Current time in local decimal hours
     */
    fun calculateChoghadiya(
        sunriseDecimal: Double,
        sunsetDecimal: Double,
        dayOfWeek: Int,
        currentDecimal: Double,
    ): ChoghadiyaResult {
        val dow = dayOfWeek.coerceIn(0, 6)
        val dayLength = sunsetDecimal - sunriseDecimal
        val slotDuration = dayLength / 8.0
        // Night from sunset to next sunrise (approx sunrise + 24h)
        val nightLength = 24.0 - dayLength
        val nightSlotDuration = nightLength / 8.0

        val daySlots = DAY_SEQUENCES[dow].mapIndexed { i, typeIndex ->
            val start = sunriseDecimal + i * slotDuration
            val end = start + slotDuration
            ChoghadiyaSlot(
                type = CHOGHADIYA_TYPES[typeIndex],
                startTime = formatDecimalTime(start),
                endTime = formatDecimalTime(end),
                isActive = currentDecimal >= start && currentDecimal < end,
                isDay = true,
            )
        }

        val nightSlots = NIGHT_SEQUENCES[dow].mapIndexed { i, typeIndex ->
            val start = sunsetDecimal + i * nightSlotDuration
            val end = start + nightSlotDuration
            // Wrap times past midnight
            val activeStart = start % 24.0
            val activeEnd = end % 24.0
            val isActive = if (activeEnd > activeStart) {
                currentDecimal >= activeStart && currentDecimal < activeEnd
            } else {
                // Wraps midnight: active if after start OR before end
                currentDecimal >= activeStart || currentDecimal < activeEnd
            }
            ChoghadiyaSlot(
                type = CHOGHADIYA_TYPES[typeIndex],
                startTime = formatDecimalTime(start),
                endTime = formatDecimalTime(end),
                isActive = isActive,
                isDay = false,
            )
        }

        return ChoghadiyaResult(daySlots, nightSlots)
    }

    /** Format decimal hours to "HH:MM" local time string */
    private fun formatDecimalTime(decimal: Double): String {
        val normalized = ((decimal % 24.0 + 24.0) % 24.0)
        val hours = normalized.toInt()
        val minutes = ((normalized - hours) * 60.0).toInt()
        val h = if (hours > 12) hours - 12 else if (hours == 0) 12 else hours
        val ampm = if (((decimal % 24.0 + 24.0) % 24.0) < 12.0) "AM" else "PM"
        return "$h:${minutes.toString().padStart(2, '0')} $ampm"
    }

    /** Convert teluguDay string to 0=Sunday..6=Saturday index */
    fun dayOfWeekIndexFromTelugu(teluguDay: String): Int {
        return when {
            teluguDay.contains("ఆదివారం") -> 0
            teluguDay.contains("సోమవారం") -> 1
            teluguDay.contains("మంగళవారం") -> 2
            teluguDay.contains("బుధవారం") -> 3
            teluguDay.contains("గురువారం") -> 4
            teluguDay.contains("శుక్రవారం") -> 5
            teluguDay.contains("శనివారం") -> 6
            else -> 0
        }
    }
}
