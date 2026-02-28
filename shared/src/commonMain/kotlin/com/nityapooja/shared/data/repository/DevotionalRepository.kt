package com.nityapooja.shared.data.repository

import com.nityapooja.shared.data.local.dao.*
import com.nityapooja.shared.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class DevotionalRepository(
    private val deityDao: DeityDao,
    private val aartiDao: AartiDao,
    private val stotramDao: StotramDao,
    private val keertanaDao: KeertanaDao,
    private val mantraDao: MantraDao,
    private val bhajanDao: BhajanDao,
    private val suprabhatamDao: SuprabhatamDao,
    private val ashtotraDao: AshtotraDao,
    private val templeDao: TempleDao,
    private val festivalDao: FestivalDao,
    private val bookmarkDao: BookmarkDao,
    private val shlokaDao: ShlokaDao,
    private val chalisaDao: ChalisaDao,
    private val rashiDao: RashiDao,
    private val pujaStepDao: PujaStepDao,
    private val readingHistoryDao: ReadingHistoryDao,
    private val savedProfileDao: SavedProfileDao,
    private val puranaQuizDao: PuranaQuizDao,
) {
    // Deities
    fun getAllDeities(): Flow<List<DeityEntity>> = deityDao.getAllDeities()
    fun getDeityById(id: Int): Flow<DeityEntity?> = deityDao.getDeityById(id)
    fun getDeityByDay(day: String): Flow<List<DeityEntity>> = deityDao.getDeityByDay(day)

    // Aartis
    fun getAllAartis(): Flow<List<AartiEntity>> = aartiDao.getAllAartis()
    fun getAartiById(id: Int): Flow<AartiEntity?> = aartiDao.getAartiById(id)
    fun getAartisByDeity(deityId: Int): Flow<List<AartiEntity>> = aartiDao.getAartisByDeity(deityId)
    fun searchAartis(query: String): Flow<List<AartiEntity>> = aartiDao.searchAartis(query)

    // Stotrams
    fun getAllStotrams(): Flow<List<StotramEntity>> = stotramDao.getAllStotrams()
    fun getStotramById(id: Int): Flow<StotramEntity?> = stotramDao.getStotramById(id)
    fun getStotramsByDeity(deityId: Int): Flow<List<StotramEntity>> = stotramDao.getStotramsByDeity(deityId)
    fun searchStotrams(query: String): Flow<List<StotramEntity>> = stotramDao.searchStotrams(query)

    // Keertanalu
    fun getAllKeertanalu(): Flow<List<KeertanaEntity>> = keertanaDao.getAllKeertanalu()
    fun getKeertanaById(id: Int): Flow<KeertanaEntity?> = keertanaDao.getKeertanaById(id)
    fun getKeertanaluByDeity(deityId: Int): Flow<List<KeertanaEntity>> = keertanaDao.getKeertanaluByDeity(deityId)
    fun getKeertanaluByComposer(composer: String): Flow<List<KeertanaEntity>> = keertanaDao.getKeertanaluByComposer(composer)
    fun getAllComposers(): Flow<List<String>> = keertanaDao.getAllComposers()
    fun searchKeertanalu(query: String): Flow<List<KeertanaEntity>> = keertanaDao.searchKeertanalu(query)

    // Mantras
    fun getAllMantras(): Flow<List<MantraEntity>> = mantraDao.getAllMantras()
    fun getMantraById(id: Int): Flow<MantraEntity?> = mantraDao.getMantraById(id)
    fun getMantrasByDeity(deityId: Int): Flow<List<MantraEntity>> = mantraDao.getMantrasByDeity(deityId)
    fun getMantrasByCategory(category: String): Flow<List<MantraEntity>> = mantraDao.getMantrasByCategory(category)
    fun searchMantras(query: String): Flow<List<MantraEntity>> = mantraDao.searchMantras(query)

    // Bhajans
    fun getAllBhajans(): Flow<List<BhajanEntity>> = bhajanDao.getAllBhajans()
    fun getBhajanById(id: Int): Flow<BhajanEntity?> = bhajanDao.getBhajanById(id)
    fun getBhajansByDeity(deityId: Int): Flow<List<BhajanEntity>> = bhajanDao.getBhajansByDeity(deityId)
    fun searchBhajans(query: String): Flow<List<BhajanEntity>> = bhajanDao.searchBhajans(query)

    // Suprabhatam
    fun getAllSuprabhatam(): Flow<List<SuprabhatamEntity>> = suprabhatamDao.getAll()
    fun getSuprabhatamById(id: Int): Flow<SuprabhatamEntity?> = suprabhatamDao.getById(id)
    fun getSuprabhatamByDeity(deityId: Int): Flow<List<SuprabhatamEntity>> = suprabhatamDao.getByDeity(deityId)

    // Ashtotra
    fun getAllAshtotra(): Flow<List<AshtotraEntity>> = ashtotraDao.getAll()
    fun getAshtotraById(id: Int): Flow<AshtotraEntity?> = ashtotraDao.getById(id)
    fun getAshtotraByDeity(deityId: Int): Flow<List<AshtotraEntity>> = ashtotraDao.getByDeity(deityId)

    // Temples
    fun getAllTemples(): Flow<List<TempleEntity>> = templeDao.getAllTemples()
    fun getTempleById(id: Int): Flow<TempleEntity?> = templeDao.getTempleById(id)
    fun getLiveDarshanTemples(): Flow<List<TempleEntity>> = templeDao.getLiveDarshanTemples()
    fun searchTemples(query: String): Flow<List<TempleEntity>> = templeDao.searchTemples(query)

    // Festivals
    fun getAllFestivals(): Flow<List<FestivalEntity>> = festivalDao.getAllFestivals()

    // Bookmarks
    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
    fun isBookmarked(type: String, contentId: Int): Flow<Boolean> = bookmarkDao.isBookmarked(type, contentId)
    suspend fun addBookmark(type: String, contentId: Int) {
        bookmarkDao.insert(BookmarkEntity(contentType = type, contentId = contentId))
    }
    suspend fun removeBookmark(type: String, contentId: Int) {
        bookmarkDao.removeBookmark(type, contentId)
    }

    // Shlokas
    fun getShlokaForDay(dayOfYear: Int): Flow<ShlokaEntity?> = shlokaDao.getShlokaForDay(dayOfYear)
    fun getRandomShloka(): Flow<ShlokaEntity?> = shlokaDao.getRandomShloka()

    // Chalisas
    fun getAllChalisas(): Flow<List<ChalisaEntity>> = chalisaDao.getAll()
    fun getChalisaById(id: Int): Flow<ChalisaEntity?> = chalisaDao.getById(id)
    fun getChalisasByDeity(deityId: Int): Flow<List<ChalisaEntity>> = chalisaDao.getByDeityId(deityId)
    fun searchChalisas(query: String): Flow<List<ChalisaEntity>> = chalisaDao.searchChalisas(query)

    // Rashis (Horoscope)
    fun getAllRashis(): Flow<List<RashiEntity>> = rashiDao.getAll()
    fun getRashiById(id: Int): Flow<RashiEntity?> = rashiDao.getById(id)

    // Puja Steps
    fun getPujaSteps(pujaType: String, tier: String): Flow<List<PujaStepEntity>> =
        pujaStepDao.getSteps(pujaType, tier)
    fun getAllPujaTypes(): Flow<List<String>> = pujaStepDao.getAllPujaTypes()

    // Reading History
    fun getRecentHistory(limit: Int = 20): Flow<List<ReadingHistoryEntity>> =
        readingHistoryDao.getRecentHistory(limit)
    suspend fun addToHistory(
        contentType: String,
        contentId: Int,
        title: String,
        titleTelugu: String,
    ) {
        readingHistoryDao.deleteByContent(contentType, contentId)
        readingHistoryDao.insert(
            ReadingHistoryEntity(
                contentType = contentType,
                contentId = contentId,
                title = title,
                titleTelugu = titleTelugu,
            )
        )
        val count = readingHistoryDao.getCount()
        if (count > 50) {
            readingHistoryDao.clearAll()
        }
    }
    suspend fun clearHistory() = readingHistoryDao.clearAll()

    // Saved Profiles (for Jataka Chakram / Guna Milan)
    fun getSavedProfiles(): Flow<List<SavedProfileEntity>> = savedProfileDao.getAllProfiles()
    suspend fun getSavedProfileById(id: Long): SavedProfileEntity? = savedProfileDao.getProfileById(id)
    suspend fun insertProfile(profile: SavedProfileEntity): Long = savedProfileDao.insertProfile(profile)
    suspend fun updateProfile(profile: SavedProfileEntity) = savedProfileDao.updateProfile(profile)
    suspend fun deleteProfile(profile: SavedProfileEntity) = savedProfileDao.deleteProfile(profile)
    suspend fun findProfileByNameAndBirth(name: String, year: Int, month: Int, day: Int, hour: Int, minute: Int): SavedProfileEntity? =
        savedProfileDao.findByNameAndBirth(name, year, month, day, hour, minute)

    // Purana Quizzes
    fun getRandomQuizzes(limit: Int = 5): Flow<List<PuranaQuizEntity>> = puranaQuizDao.getRandomQuizzes(limit)
}
