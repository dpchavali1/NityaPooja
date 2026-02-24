package com.nityapooja.app.utils

/**
 * Ashta Koota (8-factor) compatibility calculator for Guna Milan.
 * Computes scores based on Nakshatra and Rashi of bride and groom.
 * Maximum total score: 36 points.
 */
object AshtaKootaCalculator {

    data class KootaScore(
        val nameTelugu: String,
        val nameEnglish: String,
        val maxPoints: Double,
        val obtainedPoints: Double,
        val description: String,  // Brief Telugu description
    )

    data class GunaMilanResult(
        val kootaScores: List<KootaScore>,
        val totalScore: Double,
        val maxScore: Double,
        val recommendation: JyotishConstants.Recommendation,
    )

    /**
     * Calculate all 8 Ashta Koota scores.
     * @param brideNakshatra 0-26 nakshatra index of bride
     * @param brideRashi 0-11 rashi index of bride
     * @param groomNakshatra 0-26 nakshatra index of groom
     * @param groomRashi 0-11 rashi index of groom
     */
    fun calculate(
        brideNakshatra: Int,
        brideRashi: Int,
        groomNakshatra: Int,
        groomRashi: Int,
    ): GunaMilanResult {
        val c = JyotishConstants

        // 1. Varna (1 point)
        val varnaB = c.NAKSHATRA_VARNA[brideNakshatra]
        val varnaG = c.NAKSHATRA_VARNA[groomNakshatra]
        val varnaScore = if (varnaG >= varnaB) 1.0 else 0.0

        // 2. Vasya (2 points)
        val vasyaB = c.RASHI_VASYA[brideRashi]
        val vasyaG = c.RASHI_VASYA[groomRashi]
        val vasyaScore = c.VASYA_SCORE[vasyaB][vasyaG].toDouble()

        // 3. Tara (3 points)
        val taraScore = c.taraScore(brideNakshatra, groomNakshatra)

        // 4. Yoni (4 points)
        val yoniScore = c.yoniScore(brideNakshatra, groomNakshatra).toDouble()

        // 5. Graha Maitri (5 points)
        val grahaMaitriScore = c.grahaMaitriScore(brideRashi, groomRashi)

        // 6. Gana (6 points)
        val ganaB = c.NAKSHATRA_GANA[brideNakshatra]
        val ganaG = c.NAKSHATRA_GANA[groomNakshatra]
        val ganaScore = c.GANA_SCORE[ganaB][ganaG].toDouble()

        // 7. Bhakoot (7 points)
        val bhakootScore = c.bhakootScore(brideRashi, groomRashi).toDouble()

        // 8. Nadi (8 points)
        val nadiScore = c.nadiScore(brideNakshatra, groomNakshatra).toDouble()

        val scores = listOf(
            KootaScore("వర్ణ", "Varna", 1.0, varnaScore, "${c.VARNA_NAMES_TELUGU[varnaB]} — ${c.VARNA_NAMES_TELUGU[varnaG]}"),
            KootaScore("వశ్య", "Vasya", 2.0, vasyaScore, "${c.VASYA_NAMES_TELUGU[vasyaB]} — ${c.VASYA_NAMES_TELUGU[vasyaG]}"),
            KootaScore("తార", "Tara", 3.0, taraScore, "నక్షత్ర దూరం ఆధారంగా"),
            KootaScore("యోని", "Yoni", 4.0, yoniScore, "${c.YONI_NAMES_TELUGU[c.NAKSHATRA_YONI[brideNakshatra]]} — ${c.YONI_NAMES_TELUGU[c.NAKSHATRA_YONI[groomNakshatra]]}"),
            KootaScore("గ్రహ మైత్రి", "Graha Maitri", 5.0, grahaMaitriScore, "రాశి అధిపతుల మైత్రి"),
            KootaScore("గణ", "Gana", 6.0, ganaScore, "${c.GANA_NAMES_TELUGU[ganaB]} — ${c.GANA_NAMES_TELUGU[ganaG]}"),
            KootaScore("భకూట్", "Bhakoot", 7.0, bhakootScore, "రాశి స్థానాల ఆధారంగా"),
            KootaScore("నాడి", "Nadi", 8.0, nadiScore, "${c.NADI_NAMES_TELUGU[c.NAKSHATRA_NADI[brideNakshatra]]} — ${c.NADI_NAMES_TELUGU[c.NAKSHATRA_NADI[groomNakshatra]]}"),
        )

        val total = scores.sumOf { it.obtainedPoints }

        return GunaMilanResult(
            kootaScores = scores,
            totalScore = total,
            maxScore = 36.0,
            recommendation = c.getRecommendation(total),
        )
    }
}
