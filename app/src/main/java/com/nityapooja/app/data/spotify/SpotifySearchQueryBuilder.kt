package com.nityapooja.app.data.spotify

object SpotifySearchQueryBuilder {

    // Preferred search queries for specific titles to get the best Spotify match
    private val preferredQueries = mapOf(
        "Hanuman Chalisa" to "MS Rama Rao Hanuman Chalisa",
        "Shiva Suprabhatam" to "Shiva Suprabhatam Vijayaa Shanker",
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
