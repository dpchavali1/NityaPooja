package com.nityapooja.shared.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.AartiEntity
import com.nityapooja.shared.data.local.entity.BhajanEntity
import com.nityapooja.shared.data.local.entity.ChalisaEntity
import com.nityapooja.shared.data.local.entity.KeertanaEntity
import com.nityapooja.shared.data.local.entity.MantraEntity
import com.nityapooja.shared.data.local.entity.StotramEntity
import com.nityapooja.shared.data.local.entity.TempleEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class SearchResult(
    val id: Int,
    val titleTelugu: String,
    val titleEnglish: String,
    val type: String, // "aarti", "stotram", "keertana", "mantra", "bhajan", "chalisa", "temple"
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    val searchResults: StateFlow<List<SearchResult>> = _query
        .debounce(300)
        .combine(_selectedFilter) { q, filter -> Pair(q, filter) }
        .flatMapLatest { (q, filter) ->
            if (q.length < 2) {
                flowOf(emptyList())
            } else {
                combine(
                    if (filter == "all" || filter == "aarti") repository.searchAartis(q) else flowOf(emptyList()),
                    if (filter == "all" || filter == "stotram") repository.searchStotrams(q) else flowOf(emptyList()),
                    if (filter == "all" || filter == "keertana") repository.searchKeertanalu(q) else flowOf(emptyList()),
                    if (filter == "all" || filter == "mantra") repository.searchMantras(q) else flowOf(emptyList()),
                    if (filter == "all" || filter == "bhajan") repository.searchBhajans(q) else flowOf(emptyList()),
                    if (filter == "all" || filter == "chalisa") repository.searchChalisas(q) else flowOf(emptyList()),
                    if (filter == "all" || filter == "temple") repository.searchTemples(q) else flowOf(emptyList()),
                ) { results ->
                    val aartis = results[0] as List<*>
                    val stotrams = results[1] as List<*>
                    val keertanalu = results[2] as List<*>
                    val mantras = results[3] as List<*>
                    val bhajans = results[4] as List<*>
                    val chalisas = results[5] as List<*>
                    val temples = results[6] as List<*>
                    val combined = mutableListOf<SearchResult>()
                    aartis.forEach {
                        val a = it as AartiEntity
                        combined.add(SearchResult(a.id, a.titleTelugu, a.title, "aarti"))
                    }
                    stotrams.forEach {
                        val s = it as StotramEntity
                        combined.add(SearchResult(s.id, s.titleTelugu, s.title, "stotram"))
                    }
                    keertanalu.forEach {
                        val k = it as KeertanaEntity
                        combined.add(SearchResult(k.id, k.titleTelugu, k.title, "keertana"))
                    }
                    mantras.forEach {
                        val m = it as MantraEntity
                        combined.add(SearchResult(m.id, m.titleTelugu, m.title, "mantra"))
                    }
                    bhajans.forEach {
                        val b = it as BhajanEntity
                        combined.add(SearchResult(b.id, b.titleTelugu, b.title, "bhajan"))
                    }
                    chalisas.forEach {
                        val c = it as ChalisaEntity
                        combined.add(SearchResult(c.id, c.titleTelugu, c.title, "chalisa"))
                    }
                    temples.forEach {
                        val t = it as TempleEntity
                        combined.add(SearchResult(t.id, t.nameTelugu, t.name, "temple"))
                    }
                    combined
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun updateFilter(filter: String) {
        _selectedFilter.value = filter
    }
}
