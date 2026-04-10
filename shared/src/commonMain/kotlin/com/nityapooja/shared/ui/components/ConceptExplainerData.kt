package com.nityapooja.shared.ui.components

/**
 * Static explanations for Panchangam concepts used by InfoBottomSheet.
 * Keyed by the English name of the concept.
 */
object ConceptExplainerData {

    data class ConceptInfo(
        val titleTelugu: String,
        val titleEnglish: String,
        val bodyTelugu: String,
        val bodyEnglish: String,
        val whyItMatters: String? = null,
    )

    // ──────────────────────────────────────────────
    // PANCHANGA ELEMENTS
    // ──────────────────────────────────────────────

    val PANCHANGAM = ConceptInfo(
        titleTelugu = "పంచాంగం అంటే ఏమిటి?",
        titleEnglish = "What is Panchangam?",
        bodyTelugu = "పంచాంగం అనేది హిందూ పంచాంగ విధానం. ఇది ఐదు అంగాలతో రోజు యొక్క స్వభావాన్ని వివరిస్తుంది: తిథి (చంద్ర తేదీ), నక్షత్రం (చంద్రుని స్థానం), యోగం (శుభాశుభ సూచిక), కరణం (అర్థ తిథి), మరియు వారం (వారంలో రోజు).",
        bodyEnglish = "Panchangam is the Hindu almanac. It describes the quality of each day through five elements: Tithi (lunar date), Nakshatra (star of the day), Yoga (planetary combination), Karana (half-lunar day), and Vara (day of the week). Telugu families consult it before weddings, travel, business, and ceremonies.",
        whyItMatters = "రోజువారీ శుభ కార్యాలు ప్రారంభించే ముందు పంచాంగం చూడటం వల్ల అనుకూల సమయాన్ని ఎంచుకోవచ్చు. · Checking Panchangam before important activities helps choose auspicious timing.",
    )

    val TITHI = ConceptInfo(
        titleTelugu = "తిథి అంటే ఏమిటి?",
        titleEnglish = "What is Tithi?",
        bodyTelugu = "తిథి అనేది చంద్ర తేదీ. సూర్యుడు మరియు చంద్రుడు మధ్య కోణీయ దూరం 12 డిగ్రీలు అయినప్పుడు ఒక తిథి అవుతుంది. నెలలో 30 తిథులు ఉంటాయి — శుక్ల పక్షంలో 15, కృష్ణ పక్షంలో 15.",
        bodyEnglish = "Tithi is the lunar date — one unit of the Moon's travel relative to the Sun. Unlike a solar date, a Tithi can span parts of two days or be very short. There are 30 Tithis per lunar month: 15 in Shukla Paksha (waxing) and 15 in Krishna Paksha (waning).",
        whyItMatters = "ఏకాదశి, పూర్ణిమ, అమావాస్య వంటి తిథులు ఉపవాసానికి, పూజకు అత్యంత పవిత్రమైనవి. · Tithis like Ekadashi, Purnima, and Amavasya are sacred for fasting and special worship.",
    )

    val NAKSHATRA = ConceptInfo(
        titleTelugu = "నక్షత్రం అంటే ఏమిటి?",
        titleEnglish = "What is Nakshatra?",
        bodyTelugu = "నక్షత్రం అంటే చంద్రుని స్థానం ఆధారంగా రోజు యొక్క నక్షత్రం. ఆకాశంలో 27 నక్షత్ర గుచ్ఛాలు ఉన్నాయి. చంద్రుడు ప్రతి 27 రోజులలో అన్ని నక్షత్రాలను దాటుతాడు. మీరు పుట్టినప్పుడు చంద్రుడు ఉన్న నక్షత్రం మీ జన్మ నక్షత్రం.",
        bodyEnglish = "A Nakshatra is the star cluster the Moon occupies on a given day. There are 27 Nakshatras dividing the sky into equal segments. The Moon takes about 27 days to pass through all of them. The Nakshatra the Moon was in when you were born is your Janma Nakshatra (birth star).",
        whyItMatters = "మీ జన్మ నక్షత్రం నేటి నక్షత్రంతో అనుకూలంగా ఉంటే — శుభ కార్యాలు ప్రారంభించడానికి మంచి రోజు. · If today's Nakshatra is favorable to your birth star, it's a good day for new endeavors.",
    )

    val YOGA = ConceptInfo(
        titleTelugu = "యోగం అంటే ఏమిటి?",
        titleEnglish = "What is Yoga?",
        bodyTelugu = "ఇక్కడ 'యోగం' అంటే వ్యాయామం కాదు — సూర్యుడు మరియు చంద్రుని రాశి స్థానాల మొత్తం ఆధారంగా లెక్కించే జ్యోతిష్య సూచిక. 27 యోగాలు ఉంటాయి, ఒక్కొక్కటి ఒక రోజు నాణ్యతను నిర్ణయిస్తుంది.",
        bodyEnglish = "In this context, Yoga is not the exercise — it's an astrological quality indicator. It's calculated by adding the longitudes of the Sun and Moon and dividing into 27 equal parts. Each Yoga carries a different quality: some auspicious (Siddhi, Amrit), some neutral, and some to be avoided (Vyatipata, Vaidhriti).",
        whyItMatters = "సిద్ధి, అమృత, శివ యోగాలు అత్యంత శుభప్రదమైనవి. వ్యతీపాత, వైధృతి యోగాలలో ముఖ్యమైన కార్యాలు నివారించాలి. · Siddhi, Amrit, and Shiva Yogas are highly auspicious; avoid important work on Vyatipata and Vaidhriti.",
    )

    val KARANA = ConceptInfo(
        titleTelugu = "కరణం అంటే ఏమిటి?",
        titleEnglish = "What is Karana?",
        bodyTelugu = "కరణం అనేది అర్థ తిథి. ప్రతి తిథిలో రెండు కరణాలు ఉంటాయి — ఒకటి పగటిపూట, మరొకటి రాత్రిపూట. 11 కరణాలు ఉంటాయి. బవ, బాలవ వంటివి శుభప్రదమైనవి.",
        bodyEnglish = "Karana is half a Tithi. Each lunar day contains two Karanas — one for the first half and one for the second. There are 11 Karanas in total (4 fixed and 7 recurring). Bava, Balava, and Taitila are auspicious; Vishti (Bhadra) is considered inauspicious.",
        whyItMatters = "విష్టి (భద్ర) కరణంలో ముఖ్యమైన కార్యాలు మానుకోవడం మంచిది. · Avoid important activities during Vishti (Bhadra) Karana.",
    )

    val RAHU_KALAM = ConceptInfo(
        titleTelugu = "రాహు కాలం అంటే ఏమిటి?",
        titleEnglish = "What is Rahu Kalam?",
        bodyTelugu = "రాహు కాలం అనేది ప్రతి రోజు 90 నిమిషాలు ఉండే అశుభ సమయం. రాహువు అనే ఛాయా గ్రహం యొక్క ప్రభావం వల్ల ఈ సమయంలో కొత్త పనులు ప్రారంభించడం మానుకోవాలి. వారానికి ఒక్కో రోజు వేరే వేరే సమయంలో వస్తుంది.",
        bodyEnglish = "Rahu Kalam is a 90-minute inauspicious window each day, associated with Rahu — the ascending lunar node (a shadow planet in Vedic astrology). Each day of the week has a fixed Rahu Kalam slot, calculated from sunrise. Telugu families avoid starting new activities, journeys, or ceremonies during this time.",
        whyItMatters = "ఆదివారం 4:30–6 PM, సోమవారం 7:30–9 AM, మంగళవారం 3–4:30 PM, బుధవారం 12–1:30 PM, గురువారం 1:30–3 PM, శుక్రవారం 10:30 AM–12 PM, శనివారం 9–10:30 AM (సూర్యోదయ సమయం బట్టి మారుతుంది). · Approximate times: Sun 4:30–6 PM, Mon 7:30–9 AM, Tue 3–4:30 PM, Wed 12–1:30 PM, Thu 1:30–3 PM, Fri 10:30 AM–12 PM, Sat 9–10:30 AM.",
    )

    val CHOGHADIYA = ConceptInfo(
        titleTelugu = "చోఘడియా అంటే ఏమిటి?",
        titleEnglish = "What is Choghadiya?",
        bodyTelugu = "చోఘడియా అంటే 'నాలుగు ఘడియాలు' — పగలు మరియు రాత్రి ఒక్కొక్కటి 8 సమాన భాగాలుగా విభజించబడతాయి. ప్రతి భాగం 'అమృత, శుభ, లాభ, చర' (శుభ) లేదా 'ఉద్వేగ, కాల, రోగ' (అశుభ) అని వర్గీకరించబడుతుంది.",
        bodyEnglish = "Choghadiya literally means 'four ghadiyas.' The day and night are each split into 8 equal time slots. Each slot is ruled by a planet and classified as auspicious, inauspicious, or neutral. Telugu families check this before travel, business deals, medical procedures, and ceremonies.",
        whyItMatters = "అమృత = అన్నింటికీ అత్యుత్తమం. శుభ = కార్యక్రమాలకు మంచిది. లాభ = వ్యాపారానికి మంచిది. చర = ప్రయాణానికి మంచిది. కాల, రోగ, ఉద్వేగ = ముఖ్యమైన కార్యాలు నివారించాలి. · Amrit = best for everything; Shubh = good for ceremonies; Labh = good for business; Char = good for travel; Kaal/Rog/Udveg = avoid important activities.",
    )

    val MUHURTAM = ConceptInfo(
        titleTelugu = "ముహూర్తం అంటే ఏమిటి?",
        titleEnglish = "What is Muhurtam?",
        bodyTelugu = "ముహూర్తం అంటే శుభ సమయం. పెళ్ళి, గృహప్రవేశం, నామకరణం వంటి ముఖ్యమైన కార్యక్రమాలకు తిథి, నక్షత్రం, యోగం మరియు వారాన్ని బట్టి శుభ సమయాన్ని నిర్ణయిస్తారు.",
        bodyEnglish = "Muhurtam is an auspicious window of time selected for an important life event — a wedding, housewarming, naming ceremony, or business launch. It is calculated by evaluating whether the Tithi, Nakshatra, Yoga, and weekday are traditionally favorable for that specific event type.",
        whyItMatters = "సరైన ముహూర్తంలో కార్యక్రమాలు మొదలుపెట్టడం వల్ల శుభ ఫలితాలు కలుగుతాయని నమ్మకం. · Starting important events at an auspicious Muhurtam is believed to bring favorable outcomes.",
    )

    val PLANET_TRANSITS = ConceptInfo(
        titleTelugu = "గ్రహ పరివర్తన అంటే ఏమిటి?",
        titleEnglish = "What is a Planet Transit?",
        bodyTelugu = "వేద జ్యోతిష్యంలో గురుడు, శని, రాహు, కేతు వంటి మంద గతి గ్రహాలు ఒక రాశి నుండి మరొక రాశికి మారడాన్ని గ్రహ పరివర్తన అంటారు. ఇవి సాధారణంగా 1–2.5 సంవత్సరాలకు ఒకసారి జరుగుతాయి.",
        bodyEnglish = "In Vedic astrology, slow-moving planets — Jupiter (Guru), Saturn (Sani), Rahu, and Ketu — change zodiac signs (Rashis) every 1–2.5 years. Each transit is significant because it shifts the planetary influence over all 12 signs. This screen shows upcoming transits so you can prepare for major life shifts.",
        whyItMatters = "గురు పరివర్తన జ్ఞానం, సంతానం, సంపదలను ప్రభావితం చేస్తుంది. శని పరివర్తన కర్మ మరియు క్రమశిక్షణను ప్రభావితం చేస్తుంది. · Jupiter transits affect wisdom, children, and wealth. Saturn transits affect karma and discipline.",
    )

    val GUNA_MILAN = ConceptInfo(
        titleTelugu = "గుణ మిలనం అంటే ఏమిటి?",
        titleEnglish = "What is Guna Milan?",
        bodyTelugu = "గుణ మిలనం అంటే వివాహ అనుకూలత లెక్కింపు. వరుడు మరియు వధువు జన్మ నక్షత్రాల ఆధారంగా అష్ట కూటం (8 విభాగాలు) లో మొత్తం 36 గుణాలు పరిశీలిస్తారు.",
        bodyEnglish = "Guna Milan is Vedic compatibility matching for marriage. Based on the birth Nakshatras of the bride and groom, 8 dimensions (Koota) are scored for a total of 36 points. A score above 18 is generally considered acceptable; above 24 is good; above 32 is excellent.",
        whyItMatters = "36లో 18 కంటే ఎక్కువ స్కోరు వివాహానికి అనుకూలం. కొన్ని దోషాలు పరిష్కార పూజలతో తగ్గించవచ్చు. · A score above 18/36 is considered compatible. Some doshas can be mitigated with specific remedies.",
    )

    val JATAKA_CHAKRAM = ConceptInfo(
        titleTelugu = "జాతక చక్రం అంటే ఏమిటి?",
        titleEnglish = "What is a Birth Chart (Jataka)?",
        bodyTelugu = "జాతక చక్రం అంటే జన్మ సమయంలో గ్రహాల స్థానాన్ని చూపే పటం. ఇది దక్షిణ భారత శైలిలో 12 భవనాలుగా చూపబడుతుంది. లగ్నం (ఉదయ రాశి), జన్మ రాశి (చంద్ర రాశి), నవాంశ చక్రం ముఖ్యమైనవి.",
        bodyEnglish = "A Jataka Chakram (birth chart) is a map of planetary positions at the exact moment of birth. It's displayed in South Indian style as a 12-house grid. The Lagna (ascendant) is the rising sign at birth; Janma Rashi is the Moon sign. Together these form the foundation of Vedic astrology readings.",
        whyItMatters = "జాతక చక్రం ద్వారా వ్యక్తిత్వం, వృత్తి, వివాహ అనుకూలత మరియు జీవిత దశలు అర్థం చేసుకోవచ్చు. · The birth chart reveals personality traits, career tendencies, compatibility, and life phases through dashas.",
    )

    val VRATA = ConceptInfo(
        titleTelugu = "వ్రతం అంటే ఏమిటి?",
        titleEnglish = "What is a Vrata?",
        bodyTelugu = "వ్రతం అంటే నిర్దిష్ట దేవత ప్రీత్యర్థం ఒక నిర్దిష్ట తిథి లేదా నక్షత్రంలో ఆచరించే ఉపవాస/పూజా విధానం. ఉపవాసం ఉంటూ నిర్దిష్ట పూజలు, పారాయణలు చేయడం వ్రత ఆచరణ.",
        bodyEnglish = "A Vrata is a Hindu observance — typically a fast combined with specific worship — performed on particular Tithis or Nakshatras to honor a deity. Common Vratas include Ekadashi (for Vishnu), Pradosh (for Shiva), and Varalakshmi Vratam. They are considered acts of devotion that bring blessings and fulfill wishes.",
        whyItMatters = "వ్రత ఆచరణ వల్ల మనోబలం, ఆరోగ్యం మరియు ఆధ్యాత్మిక శక్తి పెరుగుతుందని నమ్మకం. · Regular Vrata observance is believed to strengthen willpower, improve health, and deepen spiritual connection.",
    )

    // ──────────────────────────────────────────────
    // SCREEN-LEVEL LOOKUPS
    // ──────────────────────────────────────────────

    /** Returns the ConceptInfo for a given Tithi English name, or a generic Tithi description. */
    fun forTithi(nameEnglish: String): ConceptInfo = TITHI.copy(
        whyItMatters = tithiSignificance(nameEnglish) ?: TITHI.whyItMatters,
    )

    fun forNakshatra(nameEnglish: String): ConceptInfo = NAKSHATRA.copy(
        whyItMatters = nakshatraQuality(nameEnglish) ?: NAKSHATRA.whyItMatters,
    )

    fun forYoga(nameEnglish: String): ConceptInfo = YOGA.copy(
        whyItMatters = yogaQuality(nameEnglish) ?: YOGA.whyItMatters,
    )

    private fun tithiSignificance(name: String): String? = when (name.lowercase()) {
        "ekadashi" -> "ఏకాదశి: విష్ణు దేవుని పూజకు అత్యంత పవిత్రమైన తిథి. ఉపవాసం ఉండటం అనేక పాపాలను నశింపజేస్తుందని నమ్మకం. · Ekadashi: Most sacred for Vishnu worship. Fasting on this day is believed to cleanse sins."
        "purnima", "poornima" -> "పూర్ణిమ: చంద్రుడు పూర్తిగా ఉదయించే తిథి. సత్యనారాయణ వ్రతానికి, పితృ తర్పణానికి ప్రత్యేకమైన రోజు. · Full Moon: Special for Satyanarayana Vrat, ancestral offerings, and goddess worship."
        "amavasya", "amavasai" -> "అమావాస్య: పితరులకు తర్పణం ఇవ్వడానికి అత్యంత శుభప్రదమైన తిథి. కొత్త కార్యాలు మానుకోవడం మంచిది. · New Moon: Best day for ancestral remembrance (pitru tarpan). Avoid new ventures."
        "chaturthi" -> "చతుర్థి: గణపతి దేవుని విశేష తిథి. మంగళ కార్యాలకు అనుకూలం. · Chaturthi: Sacred to Ganesha. Especially auspicious for starting new endeavors with his blessings."
        "panchami" -> "పంచమి: నాగుల పంచమికి ప్రత్యేకమైన తిథి. సర్పదేవతలకు పూజ చేయడానికి మంచి రోజు. · Panchami: Sacred to Nagaraja (serpent deity). Auspicious for prayers seeking protection and fertility."
        "saptami" -> "సప్తమి: సూర్యభగవానుని విశేష తిథి. ఆరోగ్యానికి, కళ్ళ సమస్యలకు సూర్య పూజ మంచిది. · Saptami: Sacred to Surya (Sun God). Special prayers for health and eye-related issues."
        else -> null
    }

    private fun nakshatraQuality(name: String): String? = when (name.lowercase()) {
        "rohini" -> "రోహిణి: అత్యంత శుభప్రదమైన నక్షత్రం. గృహప్రవేశం, వివాహం, వ్యాపార ప్రారంభానికి అనుకూలం. · Rohini: One of the most auspicious Nakshatras. Excellent for housewarming, weddings, and business starts."
        "pushyami", "pushya" -> "పుష్యమి: అన్ని శుభ కార్యాలకు అత్యుత్తమైన నక్షత్రం. 'సర్వ కార్య సాధకం' అని పేరు పొందింది. · Pushyami: The most universally auspicious Nakshatra — called 'the giver of all wishes.'"
        "hasta" -> "హస్త: శిల్పకళ, వ్యాపారం, వైద్యానికి అనుకూలమైన నక్షత్రం. · Hasta: Favorable for craftsmanship, trade, and healing activities."
        "moola", "mula" -> "మూల: కొత్త పనులు ప్రారంభించడానికి జాగ్రత్తగా ఉండాలి. వైద్య చికిత్సకు అనుకూలం. · Moola: Exercise caution for new beginnings; favorable for medical treatments and rooting out problems."
        "ardra" -> "ఆర్ద్ర: శుభ కార్యాలు మానుకోవాలి. రుద్ర నక్షత్రం — శివుని ప్రార్థనకు మంచిది. · Ardra: Avoid auspicious ceremonies; sacred to Rudra (Shiva) — good for Shiva worship."
        "ashlesha" -> "ఆశ్లేష: జాగ్రత్తగా ఉండాలి. నాగ దేవతల నక్షత్రం — పితృ కార్యాలకు అనుకూలం. · Ashlesha: Exercise caution; it's the Naga (serpent) Nakshatra — good for ancestral rites."
        "jyeshtha" -> "జ్యేష్ఠ: ముఖ్యమైన కార్యాలకు జాగ్రత్తగా ఉండాలి. ఇంద్ర దేవుని నక్షత్రం. · Jyeshtha: Be cautious for major events. Ruled by Indra — good for leadership-related matters."
        else -> null
    }

    private fun yogaQuality(name: String): String? = when (name.lowercase()) {
        "siddhi" -> "సిద్ధి యోగం: అత్యంత శుభప్రదమైన యోగం. సిద్ధి = సాఫల్యం. ఈ రోజు ప్రారంభించిన కార్యాలు విజయవంతమవుతాయని నమ్మకం. · Siddhi Yoga: Highly auspicious. Activities started today are believed to succeed."
        "amrita", "amrit" -> "అమృత యోగం: అమృత = జీవజలం. ఈ రోజు అన్ని కార్యాలకు అత్యంత అనుకూలం. · Amrita Yoga: Most auspicious. Excellent for all activities — health, wealth, and ceremonies."
        "vyatipata" -> "వ్యతీపాత యోగం: ముఖ్యమైన కార్యాలు నివారించాలి. · Vyatipata Yoga: Inauspicious — avoid important activities, especially new starts."
        "vaidhriti", "vaidhrti" -> "వైధృతి యోగం: శుభ కార్యాలకు అనుకూలం కాదు. · Vaidhriti Yoga: Inauspicious for auspicious ceremonies and important decisions."
        "shiva", "siva" -> "శివ యోగం: శివుని ఆరాధనకు అత్యంత అనుకూలం. శుభ కార్యాలకు మంచిది. · Shiva Yoga: Excellent for Shiva worship and auspicious activities."
        "brahma" -> "బ్రహ్మ యోగం: జ్ఞానం, విద్యకు సంబంధించిన కార్యాలకు అనుకూలం. · Brahma Yoga: Favorable for education, knowledge, and creative pursuits."
        else -> null
    }
}
