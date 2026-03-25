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

    /**
     * Score a given day's panchangam for a specific event type.
     * Returns a MuhurtamResult with score, points (0-100), and bilingual reasons.
     */
    fun scoreMuhurtam(panchangam: PanchangamData, eventType: EventType): MuhurtamResult {
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
}
