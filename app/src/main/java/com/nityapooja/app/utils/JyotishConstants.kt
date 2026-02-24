package com.nityapooja.app.utils

/**
 * Shared Jyotish lookup tables for Navagraha names,
 * Nakshatra/Rashi attributes, and Ashta Koota mappings.
 */
object JyotishConstants {

    // ═══════════════════════════════════════════════════════════
    // Navagraha (9 Grahas) — index order matches AstronomicalCalculator
    // 0=Sun, 1=Moon, 2=Mars, 3=Mercury, 4=Jupiter, 5=Venus, 6=Saturn, 7=Rahu, 8=Ketu
    // ═══════════════════════════════════════════════════════════

    val GRAHA_NAMES_TELUGU = arrayOf(
        "సూర్యుడు", "చంద్రుడు", "కుజుడు", "బుధుడు",
        "గురువు", "శుక్రుడు", "శని", "రాహువు", "కేతువు"
    )

    val GRAHA_NAMES_ENGLISH = arrayOf(
        "Sun", "Moon", "Mars", "Mercury",
        "Jupiter", "Venus", "Saturn", "Rahu", "Ketu"
    )

    val GRAHA_ABBREVIATIONS_TELUGU = arrayOf(
        "సూ", "చం", "కు", "బు", "గు", "శు", "శ", "రా", "కే"
    )

    // ═══════════════════════════════════════════════════════════
    // 12 Rashis (Zodiac Signs) — index 0=Mesha(Aries)..11=Meena(Pisces)
    // ═══════════════════════════════════════════════════════════

    val RASHI_NAMES_TELUGU = arrayOf(
        "మేషం", "వృషభం", "మిథునం", "కర్కాటకం",
        "సింహం", "కన్య", "తుల", "వృశ్చికం",
        "ధనుస్సు", "మకరం", "కుంభం", "మీనం"
    )

    val RASHI_NAMES_ENGLISH = arrayOf(
        "Mesha", "Vrishabha", "Mithuna", "Karkataka",
        "Simha", "Kanya", "Tula", "Vrischika",
        "Dhanus", "Makara", "Kumbha", "Meena"
    )

    val RASHI_SYMBOLS = arrayOf(
        "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓"
    )

    // Rashi lord (graha index): 0=Sun..8=Ketu
    // Mars=Mesha/Vrischika, Venus=Vrishabha/Tula, Mercury=Mithuna/Kanya,
    // Moon=Karkataka, Sun=Simha, Jupiter=Dhanus/Meena, Saturn=Makara/Kumbha
    val RASHI_LORD = intArrayOf(
        2, 5, 3, 1, 0, 3, 5, 2, 4, 6, 6, 4
    )

    // ═══════════════════════════════════════════════════════════
    // 27 Nakshatras — index 0=Ashwini..26=Revati
    // ═══════════════════════════════════════════════════════════

    val NAKSHATRA_NAMES_TELUGU = arrayOf(
        "అశ్విని", "భరణి", "కృత్తిక", "రోహిణి", "మృగశిర",
        "ఆర్ద్ర", "పునర్వసు", "పుష్యమి", "ఆశ్లేష", "మఘ",
        "పూర్వ ఫల్గుణి", "ఉత్తర ఫల్గుణి", "హస్త", "చిత్త", "స్వాతి",
        "విశాఖ", "అనురాధ", "జ్యేష్ఠ", "మూల", "పూర్వాషాఢ",
        "ఉత్తరాషాఢ", "శ్రవణం", "ధనిష్ఠ", "శతభిషం", "పూర్వాభాద్ర",
        "ఉత్తరాభాద్ర", "రేవతి"
    )

    val NAKSHATRA_NAMES_ENGLISH = arrayOf(
        "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira",
        "Ardra", "Punarvasu", "Pushyami", "Ashlesha", "Magha",
        "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitta", "Swati",
        "Vishakha", "Anuradha", "Jyeshtha", "Moola", "Purvashadha",
        "Uttarashadha", "Shravana", "Dhanishtha", "Shatabhisha", "Purvabhadra",
        "Uttarabhadra", "Revati"
    )

    // ═══════════════════════════════════════════════════════════
    // Ashta Koota Lookup Tables
    // ═══════════════════════════════════════════════════════════

    // --- 1. Varna (1 point) ---
    // 4 varnas: 0=Brahmin, 1=Kshatriya, 2=Vaishya, 3=Shudra
    // Mapped by nakshatra index (0-26)
    val NAKSHATRA_VARNA = intArrayOf(
        1, 3, 2, 3, 2, 0, 0, 1, 2, 3,  // Ashwini..Magha
        1, 1, 2, 2, 3, 0, 3, 1, 2, 0,  // Purva Phalguni..Purvashadha
        1, 2, 3, 3, 0, 1, 3             // Uttarashadha..Revati
    )

    val VARNA_NAMES_TELUGU = arrayOf("బ్రాహ్మణ", "క్షత్రియ", "వైశ్య", "శూద్ర")

    // --- 2. Vasya (2 points) ---
    // 5 types: 0=Chatushpada, 1=Manava, 2=Jalachara, 3=Vanachara, 4=Keeta
    // Mapped by rashi index (0-11)
    val RASHI_VASYA = intArrayOf(
        0, 0, 1, 2, 3, 1, 1, 4, 0, 2, 1, 2
    )

    val VASYA_NAMES_TELUGU = arrayOf("చతుష్పాద", "మానవ", "జలచర", "వనచర", "కీట")

    // Vasya compatibility matrix: [bride_vasya][groom_vasya] → points (0 or 1 or 2)
    val VASYA_SCORE = arrayOf(
        intArrayOf(2, 1, 0, 1, 0), // Chatushpada vs all
        intArrayOf(1, 2, 1, 0, 0), // Manava vs all
        intArrayOf(0, 1, 2, 0, 1), // Jalachara vs all
        intArrayOf(1, 0, 0, 2, 1), // Vanachara vs all
        intArrayOf(0, 0, 1, 1, 2), // Keeta vs all
    )

    // --- 3. Tara (3 points) ---
    // Based on nakshatra distance modulo 9. Groups 3,5,7 are inauspicious.
    // Score: auspicious → 3, inauspicious → 0, mixed → 1.5
    fun taraScore(brideNakshatra: Int, groomNakshatra: Int): Double {
        val dist1 = ((groomNakshatra - brideNakshatra + 27) % 27) % 9
        val dist2 = ((brideNakshatra - groomNakshatra + 27) % 27) % 9
        val inauspicious1 = dist1 == 3 || dist1 == 5 || dist1 == 7
        val inauspicious2 = dist2 == 3 || dist2 == 5 || dist2 == 7
        return when {
            !inauspicious1 && !inauspicious2 -> 3.0
            inauspicious1 && inauspicious2 -> 0.0
            else -> 1.5
        }
    }

    // --- 4. Yoni (4 points) ---
    // 14 animals: each nakshatra maps to one
    // 0=Horse, 1=Elephant, 2=Sheep, 3=Serpent, 4=Dog, 5=Cat, 6=Rat,
    // 7=Cow, 8=Buffalo, 9=Tiger, 10=Deer, 11=Monkey, 12=Mongoose, 13=Lion
    val NAKSHATRA_YONI = intArrayOf(
        0, 1, 2, 3, 3, 4, 5, 2, 5, 6,   // Ashwini..Magha
        6, 7, 8, 9, 8, 9, 10, 10, 4, 11, // Purva Phalguni..Purvashadha
        12, 11, 13, 0, 13, 7, 1          // Uttarashadha..Revati
    )

    val YONI_NAMES_TELUGU = arrayOf(
        "అశ్వం", "గజం", "మేషం", "సర్పం", "శ్వానం", "మార్జాలం", "మూషికం",
        "గోవు", "మహిషం", "వ్యాఘ్రం", "మృగం", "వానరం", "నకులం", "సింహం"
    )

    // Yoni enemy pairs (score 0). If same yoni = 4, friendly = 3, neutral = 2, enemy = 0
    private val YONI_ENEMIES = setOf(
        0 to 8, 8 to 0,   // Horse-Buffalo
        1 to 13, 13 to 1, // Elephant-Lion
        2 to 11, 11 to 2, // Sheep-Monkey
        3 to 12, 12 to 3, // Serpent-Mongoose
        4 to 10, 10 to 4, // Dog-Deer
        5 to 6, 6 to 5,   // Cat-Rat
        7 to 9, 9 to 7,   // Cow-Tiger
    )

    fun yoniScore(brideNakshatra: Int, groomNakshatra: Int): Int {
        val y1 = NAKSHATRA_YONI[brideNakshatra]
        val y2 = NAKSHATRA_YONI[groomNakshatra]
        return when {
            y1 == y2 -> 4
            (y1 to y2) in YONI_ENEMIES -> 0
            else -> 2 // neutral/friendly
        }
    }

    // --- 5. Graha Maitri (5 points) ---
    // Based on friendship between rashi lords.
    // Friendship levels: -2=Bitter Enemy, -1=Enemy, 0=Neutral, 1=Friend, 2=Best Friend
    // Graha indices: 0=Sun, 1=Moon, 2=Mars, 3=Mercury, 4=Jupiter, 5=Venus, 6=Saturn
    // (Rahu/Ketu not rashi lords)
    // Matrix: FRIENDSHIP[lord1][lord2]
    val GRAHA_FRIENDSHIP = arrayOf(
        //         Sun  Moon  Mars  Merc  Jup   Ven   Sat
        intArrayOf( 0,   1,    1,   0,    1,   -1,   -1), // Sun
        intArrayOf( 1,   0,    0,   -1,   1,    0,   -1), // Moon
        intArrayOf( 1,   1,    0,   -1,   1,   -1,    0), // Mars
        intArrayOf( 1,  -1,    0,    0,    0,    1,   -1), // Mercury
        intArrayOf( 1,   1,    1,    0,    0,   -1,    0), // Jupiter
        intArrayOf(-1,   0,   -1,    1,   -1,    0,    1), // Venus
        intArrayOf(-1,  -1,    0,    0,    0,    1,    0), // Saturn
    )

    fun grahaMaitriScore(brideRashi: Int, groomRashi: Int): Double {
        val lord1 = RASHI_LORD[brideRashi]
        val lord2 = RASHI_LORD[groomRashi]
        if (lord1 > 6 || lord2 > 6) return 2.5 // Rahu/Ketu — neutral fallback
        val f1 = GRAHA_FRIENDSHIP[lord1][lord2]
        val f2 = GRAHA_FRIENDSHIP[lord2][lord1]
        val combined = f1 + f2
        return when {
            combined >= 2 -> 5.0
            combined == 1 -> 4.0
            combined == 0 -> 3.0
            combined == -1 -> 1.0
            else -> 0.0
        }
    }

    // --- 6. Gana (6 points) ---
    // 3 ganas: 0=Deva, 1=Manushya, 2=Rakshasa
    val NAKSHATRA_GANA = intArrayOf(
        0, 1, 2, 1, 0, 1, 0, 0, 2, 2,   // Ashwini..Magha
        1, 1, 0, 2, 0, 2, 0, 2, 2, 1,   // Purva Phalguni..Purvashadha
        1, 0, 2, 2, 1, 1, 0             // Uttarashadha..Revati
    )

    val GANA_NAMES_TELUGU = arrayOf("దేవ", "మానుష", "రాక్షస")

    // Gana compatibility: [bride_gana][groom_gana] → score (0 or 6)
    val GANA_SCORE = arrayOf(
        intArrayOf(6, 6, 0), // Deva-Deva, Deva-Manushya, Deva-Rakshasa
        intArrayOf(6, 6, 0), // Manushya-...
        intArrayOf(0, 0, 6), // Rakshasa-...(only same is good)
    )

    // --- 7. Bhakoot (7 points) ---
    // Based on rashi distance. Inauspicious pairs: 2/12, 5/9, 6/8 → 0 points
    fun bhakootScore(brideRashi: Int, groomRashi: Int): Int {
        val dist = ((groomRashi - brideRashi + 12) % 12) + 1 // 1-based distance
        val inauspicious = setOf(2, 12, 5, 9, 6, 8)
        return if (dist in inauspicious) 0 else 7
    }

    // --- 8. Nadi (8 points) ---
    // 3 nadis: 0=Aadi, 1=Madhya, 2=Antya
    val NAKSHATRA_NADI = intArrayOf(
        0, 1, 2, 2, 1, 0, 0, 1, 2, 0,   // Ashwini..Magha
        1, 2, 2, 1, 0, 0, 1, 2, 0, 1,   // Purva Phalguni..Purvashadha
        2, 2, 1, 0, 0, 1, 2             // Uttarashadha..Revati
    )

    val NADI_NAMES_TELUGU = arrayOf("ఆది", "మధ్య", "అంత్య")

    fun nadiScore(brideNakshatra: Int, groomNakshatra: Int): Int {
        val n1 = NAKSHATRA_NADI[brideNakshatra]
        val n2 = NAKSHATRA_NADI[groomNakshatra]
        return if (n1 == n2) 0 else 8 // Same nadi = dosha (0 points)
    }

    // ═══════════════════════════════════════════════════════════
    // South Indian Chart — Fixed rashi positions in 4×4 grid
    // (row, col) for each rashi index 0-11
    // ═══════════════════════════════════════════════════════════

    val SOUTH_INDIAN_POSITIONS = arrayOf(
        intArrayOf(0, 1), // 0  Mesha
        intArrayOf(0, 2), // 1  Vrishabha
        intArrayOf(0, 3), // 2  Mithuna
        intArrayOf(1, 3), // 3  Karkataka
        intArrayOf(2, 3), // 4  Simha
        intArrayOf(3, 3), // 5  Kanya
        intArrayOf(3, 2), // 6  Tula
        intArrayOf(3, 1), // 7  Vrischika
        intArrayOf(3, 0), // 8  Dhanus
        intArrayOf(2, 0), // 9  Makara
        intArrayOf(1, 0), // 10 Kumbha
        intArrayOf(0, 0), // 11 Meena
    )

    // ═══════════════════════════════════════════════════════════
    // Recommendation thresholds
    // ═══════════════════════════════════════════════════════════

    data class Recommendation(val telugu: String, val english: String, val level: Int)

    fun getRecommendation(totalScore: Double): Recommendation {
        return when {
            totalScore >= 33 -> Recommendation("ఉత్తమం", "Excellent", 4)
            totalScore >= 25 -> Recommendation("అనుకూలం", "Good", 3)
            totalScore >= 18 -> Recommendation("మధ్యమం", "Average", 2)
            else -> Recommendation("అననుకూలం", "Not Recommended", 1)
        }
    }
}
