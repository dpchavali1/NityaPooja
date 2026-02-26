package com.nityapooja.shared.utils

/**
 * Shared Jyotish lookup tables for Navagraha names,
 * Nakshatra/Rashi attributes, and Ashta Koota mappings.
 */
object JyotishConstants {

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

    val RASHI_LORD = intArrayOf(
        2, 5, 3, 1, 0, 3, 5, 2, 4, 6, 6, 4
    )

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

    // Ashta Koota Lookup Tables

    val NAKSHATRA_VARNA = intArrayOf(
        1, 3, 2, 3, 2, 0, 0, 1, 2, 3,
        1, 1, 2, 2, 3, 0, 3, 1, 2, 0,
        1, 2, 3, 3, 0, 1, 3
    )

    val VARNA_NAMES_TELUGU = arrayOf("బ్రాహ్మణ", "క్షత్రియ", "వైశ్య", "శూద్ర")

    val RASHI_VASYA = intArrayOf(
        0, 0, 1, 2, 3, 1, 1, 4, 0, 2, 1, 2
    )

    val VASYA_NAMES_TELUGU = arrayOf("చతుష్పాద", "మానవ", "జలచర", "వనచర", "కీట")

    val VASYA_SCORE = arrayOf(
        intArrayOf(2, 1, 0, 1, 0),
        intArrayOf(1, 2, 1, 0, 0),
        intArrayOf(0, 1, 2, 0, 1),
        intArrayOf(1, 0, 0, 2, 1),
        intArrayOf(0, 0, 1, 1, 2),
    )

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

    val NAKSHATRA_YONI = intArrayOf(
        0, 1, 2, 3, 3, 4, 5, 2, 5, 6,
        6, 7, 8, 9, 8, 9, 10, 10, 4, 11,
        12, 11, 13, 0, 13, 7, 1
    )

    val YONI_NAMES_TELUGU = arrayOf(
        "అశ్వం", "గజం", "మేషం", "సర్పం", "శ్వానం", "మార్జాలం", "మూషికం",
        "గోవు", "మహిషం", "వ్యాఘ్రం", "మృగం", "వానరం", "నకులం", "సింహం"
    )

    private val YONI_ENEMIES = setOf(
        0 to 8, 8 to 0,
        1 to 13, 13 to 1,
        2 to 11, 11 to 2,
        3 to 12, 12 to 3,
        4 to 10, 10 to 4,
        5 to 6, 6 to 5,
        7 to 9, 9 to 7,
    )

    fun yoniScore(brideNakshatra: Int, groomNakshatra: Int): Int {
        val y1 = NAKSHATRA_YONI[brideNakshatra]
        val y2 = NAKSHATRA_YONI[groomNakshatra]
        return when {
            y1 == y2 -> 4
            (y1 to y2) in YONI_ENEMIES -> 0
            else -> 2
        }
    }

    val GRAHA_FRIENDSHIP = arrayOf(
        intArrayOf( 0,  1,  1,  0,  1, -1, -1),
        intArrayOf( 1,  0,  0, -1,  1,  0, -1),
        intArrayOf( 1,  1,  0, -1,  1, -1,  0),
        intArrayOf( 1, -1,  0,  0,  0,  1, -1),
        intArrayOf( 1,  1,  1,  0,  0, -1,  0),
        intArrayOf(-1,  0, -1,  1, -1,  0,  1),
        intArrayOf(-1, -1,  0,  0,  0,  1,  0),
    )

    fun grahaMaitriScore(brideRashi: Int, groomRashi: Int): Double {
        val lord1 = RASHI_LORD[brideRashi]
        val lord2 = RASHI_LORD[groomRashi]
        if (lord1 > 6 || lord2 > 6) return 2.5
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

    val NAKSHATRA_GANA = intArrayOf(
        0, 1, 2, 1, 0, 1, 0, 0, 2, 2,
        1, 1, 0, 2, 0, 2, 0, 2, 2, 1,
        1, 0, 2, 2, 1, 1, 0
    )

    val GANA_NAMES_TELUGU = arrayOf("దేవ", "మానుష", "రాక్షస")

    val GANA_SCORE = arrayOf(
        intArrayOf(6, 6, 0),
        intArrayOf(6, 6, 0),
        intArrayOf(0, 0, 6),
    )

    fun bhakootScore(brideRashi: Int, groomRashi: Int): Int {
        val dist = ((groomRashi - brideRashi + 12) % 12) + 1
        val inauspicious = setOf(2, 12, 5, 9, 6, 8)
        return if (dist in inauspicious) 0 else 7
    }

    val NAKSHATRA_NADI = intArrayOf(
        0, 1, 2, 2, 1, 0, 0, 1, 2, 0,
        1, 2, 2, 1, 0, 0, 1, 2, 0, 1,
        2, 2, 1, 0, 0, 1, 2
    )

    val NADI_NAMES_TELUGU = arrayOf("ఆది", "మధ్య", "అంత్య")

    fun nadiScore(brideNakshatra: Int, groomNakshatra: Int): Int {
        val n1 = NAKSHATRA_NADI[brideNakshatra]
        val n2 = NAKSHATRA_NADI[groomNakshatra]
        return if (n1 == n2) 0 else 8
    }

    // South Indian Chart positions

    val SOUTH_INDIAN_POSITIONS = arrayOf(
        intArrayOf(0, 1), intArrayOf(0, 2), intArrayOf(0, 3),
        intArrayOf(1, 3), intArrayOf(2, 3), intArrayOf(3, 3),
        intArrayOf(3, 2), intArrayOf(3, 1), intArrayOf(3, 0),
        intArrayOf(2, 0), intArrayOf(1, 0), intArrayOf(0, 0),
    )

    // Recommendation thresholds

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
