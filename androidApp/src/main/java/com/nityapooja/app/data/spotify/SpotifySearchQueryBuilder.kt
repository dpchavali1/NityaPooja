package com.nityapooja.app.data.spotify

object SpotifySearchQueryBuilder {

    // Preferred search queries for specific titles to get the best Spotify match
    private val preferredQueries = mapOf(
        "Hanuman Chalisa" to "MS Rama Rao Hanuman Chalisa",
        "Shiva Suprabhatam" to "Shiva Suprabhatam Vijayaa Shanker",
        "Venkateswara Moola Mantra" to "om namo venkatesaya Ramu",
        "Venkateswara Ashtottara" to "Sri Venkateshwara Ashtottaram Parupalli Ranganath",
        "Kondalalo Nelakonna" to "Kondalalo Nelakonna G Balakrishnaprasad",
        "Shiva Ashtottara" to "Shiva Ashtottara Shatanamavali Ravi Prakash",
        "Shiva Chalisa" to "Shiv Chalisa Religious India",
        "Hanuman Aarti" to "Aarti Keeje Hanuman Lala Ki Hariharan",
        "Hare Krishna Mahamantra" to "Hare Krishna Hare Rama 108 Times Akash Hajgude",
        "Krishna Aarti" to "om jai jagdish hare anuradha paudwal",
        "Lakshmi Ashtottara" to "Sri Lakshmi Ashtothram Veda Pandits",
        "Lalita Sahasranamam" to "Lalita Sahasranamam Ranjani-Gayatri",
        "Lakshmi Chalisa" to "Lakshmi Chalisa Minakshi Majumdar",
        "Jagadanandakaraka" to "Jagadanandakaraka Nattaj Dr. M. Balamuralikrishna",
        "Dudukugala" to "Dudukugala Nanne Gowla Dr. M. Balamuralikrishna",
        "Sadhinchene" to "Sadhinchane Aarabhi Dr. M. Balamuralikrishna",
        "Kanakannaruchira" to "Kanakanaruchira Varali Dr. M. Balamuralikrishna",
        "Endaro Mahanubhavulu" to "Endaro Mahanubhavulu Sri Dr. M. Balamuralikrishna",
        "Aditya Hridayam" to "Aditya Hridaya Stotra Shree Naval Kishori",
        "Vishnu Sahasranamam" to "Vishnu Sahasranamam M.S.Subbulakshmi",
    )

    fun buildQuery(title: String, contentType: String, deityName: String? = null): String {
        // Check for a preferred query override first
        preferredQueries[title]?.let { return it }

        val query = buildString {
            append(title)

            // Only add content type if the title doesn't already contain it
            val typeKeyword = when (contentType.lowercase()) {
                "aarti" -> "aarti"
                "stotram" -> "stotram"
                "mantra" -> "mantra"
                "keertana", "keerthana" -> "keerthana"
                "chalisa" -> "chalisa"
                "suprabhatam" -> "suprabhatam"
                "ashtotharam", "ashtottaram" -> "ashtottara"
                "bhajan" -> "bhajan"
                else -> null
            }
            if (typeKeyword != null && !title.contains(typeKeyword, ignoreCase = true)) {
                append(" ")
                append(typeKeyword)
            }

            // Add deity name if provided and not already in title
            if (!deityName.isNullOrBlank() && !title.contains(deityName, ignoreCase = true)) {
                append(" ")
                append(deityName)
            }

            // Add "telugu" to help find devotional versions
            append(" telugu")
        }

        return query
    }
}
