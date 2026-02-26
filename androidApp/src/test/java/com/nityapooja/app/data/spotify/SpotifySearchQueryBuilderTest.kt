package com.nityapooja.app.data.spotify

import org.junit.Assert.assertEquals
import org.junit.Test

class SpotifySearchQueryBuilderTest {

    @Test
    fun `preferred query returns exact override`() {
        val result = SpotifySearchQueryBuilder.buildQuery("Hanuman Chalisa", "chalisa")
        assertEquals("MS Rama Rao Hanuman Chalisa", result)
    }

    @Test
    fun `preferred query for Vishnu Sahasranamam`() {
        val result = SpotifySearchQueryBuilder.buildQuery("Vishnu Sahasranamam", "stotram")
        assertEquals("Vishnu Sahasranamam M.S.Subbulakshmi", result)
    }

    @Test
    fun `preferred query for Aditya Hridayam`() {
        val result = SpotifySearchQueryBuilder.buildQuery("Aditya Hridayam", "stotram")
        assertEquals("Aditya Hridaya Stotra Shree Naval Kishori", result)
    }

    @Test
    fun `generic query appends content type and telugu`() {
        val result = SpotifySearchQueryBuilder.buildQuery("Some Title", "stotram")
        assertEquals("Some Title stotram telugu", result)
    }

    @Test
    fun `generic query does not duplicate content type if already in title`() {
        val result = SpotifySearchQueryBuilder.buildQuery("Shiva Stotram", "stotram")
        assertEquals("Shiva Stotram telugu", result)
    }

    @Test
    fun `generic query adds deity name if not in title`() {
        val result = SpotifySearchQueryBuilder.buildQuery("Some Mantra", "mantra", "Shiva")
        assertEquals("Some Mantra Shiva telugu", result)
    }

    @Test
    fun `generic query does not duplicate deity name if in title`() {
        val result = SpotifySearchQueryBuilder.buildQuery("Shiva Tandava", "stotram", "Shiva")
        assertEquals("Shiva Tandava stotram telugu", result)
    }

    @Test
    fun `unknown content type does not add type keyword`() {
        val result = SpotifySearchQueryBuilder.buildQuery("Some Song", "unknown")
        assertEquals("Some Song telugu", result)
    }
}
