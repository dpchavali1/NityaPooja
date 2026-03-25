package com.nityapooja.shared.data.sacredmonth

data class SacredMonthInfo(
    val masaNameEnglish: String,
    val masaNameTelugu: String,
    val significanceTelugu: String,
    val significanceEnglish: String,
    val dailyPractices: List<DailyPractice>,
    val specialDays: List<SpecialDay>,
)

data class SacredMonthDateRange(
    val info: SacredMonthInfo,
    val startDate: String, // "MMM dd, yyyy"
    val endDate: String,
    val daysUntilStart: Int, // -1 if already started
    val daysRemaining: Int, // -1 if not started yet
    val isActive: Boolean,
)

data class DailyPractice(
    val nameTelugu: String,
    val nameEnglish: String,
    val descriptionTelugu: String,
)

data class SpecialDay(
    val nameTelugu: String,
    val nameEnglish: String,
    val descriptionTelugu: String,
)

object SacredMonthData {

    // Masa names from PanchangamViewModel: Chaitra(0)..Phalguna(11)
    private val sacredMonths: Map<String, SacredMonthInfo> = mapOf(
        "Karthika" to SacredMonthInfo(
            masaNameEnglish = "Karthika",
            masaNameTelugu = "కార్తీక మాసం",
            significanceTelugu = "కార్తీక మాసం శివ-విష్ణువులకు అత్యంత ప్రీతికరమైన మాసం. దీపదానం, నదీ స్నానం, శివ పూజ ఈ నెల ప్రత్యేకతలు.",
            significanceEnglish = "Karthika month is most dear to Lord Shiva and Vishnu. Lighting lamps, river bathing, and Shiva puja are the hallmarks of this sacred month.",
            dailyPractices = listOf(
                DailyPractice("దీపారాధన", "Lighting Lamps", "సాయంత్రం తులసి మొక్క వద్ద మరియు శివ ఆలయంలో దీపం వెలిగించాలి"),
                DailyPractice("ప్రాతఃకాల స్నానం", "Early Morning Bath", "సూర్యోదయానికి ముందు నదీ స్నానం లేదా చన్నీటి స్నానం చేయాలి"),
                DailyPractice("శివ పంచాక్షరి జపం", "Shiva Panchakshari Japa", "ఓం నమః శివాయ 108 సార్లు జపించాలి"),
                DailyPractice("సాయం సంధ్య దీపం", "Evening Sandhya Deepam", "ఇంట్లో అన్ని గదులలో దీపాలు వెలిగించాలి"),
            ),
            specialDays = listOf(
                SpecialDay("కార్తీక సోమవారం", "Karthika Monday", "ప్రతి సోమవారం శివ అభిషేకం, రుద్రాభిషేకం చేయాలి"),
                SpecialDay("కార్తీక పౌర్ణమి", "Karthika Pournami", "లక్షల దీపాలు వెలిగించే దీపోత్సవం. వనభోజనం చేస్తారు"),
                SpecialDay("నాగుల చవితి", "Nagula Chavithi", "పుట్టల వద్ద పాలు పోసి సర్ప పూజ చేస్తారు"),
            ),
        ),

        "Shravana" to SacredMonthInfo(
            masaNameEnglish = "Shravana",
            masaNameTelugu = "శ్రావణ మాసం",
            significanceTelugu = "శ్రావణ మాసం మంగళ గౌరీ వ్రతం, వరలక్ష్మీ వ్రతం, రక్షాబంధన్‌కు ప్రసిద్ధి. మహిళలకు అత్యంత ముఖ్యమైన మాసం.",
            significanceEnglish = "Shravana month is famous for Mangala Gowri Vratam, Varalakshmi Vratam, and Raksha Bandhan. Most important month for women's observances.",
            dailyPractices = listOf(
                DailyPractice("శివ పూజ", "Shiva Puja", "ప్రతి సోమవారం శ్రావణ సోమవార వ్రతం ఆచరించాలి"),
                DailyPractice("లక్ష్మీ పూజ", "Lakshmi Puja", "ప్రతి శుక్రవారం లక్ష్మీ దేవిని పూజించాలి"),
                DailyPractice("హరిదాసు సేవ", "Haridasu Seva", "విష్ణు నామ సంకీర్తన చేయాలి"),
            ),
            specialDays = listOf(
                SpecialDay("మంగళ గౌరీ వ్రతం", "Mangala Gowri Vratam", "ప్రతి మంగళవారం కొత్తగా పెళ్ళైన మహిళలు ఆచరించే వ్రతం"),
                SpecialDay("వరలక్ష్మీ వ్రతం", "Varalakshmi Vratam", "శ్రావణ పౌర్ణమికి ముందు శుక్రవారం లక్ష్మీ పూజ"),
                SpecialDay("రక్షాబంధన్", "Raksha Bandhan", "సోదరీ సోదరుల బంధాన్ని జరుపుకునే పండుగ"),
                SpecialDay("శ్రావణ పౌర్ణమి", "Shravana Pournami", "ఉపాకర్మ / హయగ్రీవ జయంతి"),
            ),
        ),

        "Margashira" to SacredMonthInfo(
            masaNameEnglish = "Margashira",
            masaNameTelugu = "మార్గశిర మాసం",
            significanceTelugu = "భగవద్గీతలో కృష్ణుడు 'మాసాలలో నేను మార్గశిరం' అని చెప్పాడు. ధనుర్ మాసం పేరుతో విష్ణు ఆలయాలలో ప్రత్యేక పూజలు జరుగుతాయి.",
            significanceEnglish = "In Bhagavad Gita, Krishna says 'Among months, I am Margashira'. Known as Dhanur Masam with special Vishnu temple worship.",
            dailyPractices = listOf(
                DailyPractice("తిరుప్పావై పఠనం", "Tiruppavai Recitation", "సూర్యోదయానికి ముందు ఆండాళ్ తిరుప్పావై 30 పాశురాలు పఠించాలి"),
                DailyPractice("సుప్రభాతం", "Suprabhatam", "వేకువన విష్ణు సుప్రభాతం పఠించాలి"),
                DailyPractice("ధనుర్ మాస పూజ", "Dhanur Masa Puja", "విష్ణు ఆలయాలలో ప్రత్యేక అభిషేకం"),
            ),
            specialDays = listOf(
                SpecialDay("వైకుంఠ ఏకాదశి", "Vaikunta Ekadashi", "వైకుంఠ ద్వారాలు తెరుచుకునే అత్యంత పవిత్ర ఏకాదశి"),
                SpecialDay("ఆరుద్ర దర్శనం", "Arudra Darshanam", "శివుని నటరాజ రూపంలో విశ్వ నృత్య దర్శనం"),
                SpecialDay("ధనుర్ మాస సమాప్తి", "Dhanur Masa Completion", "భోగి పండుగతో ధనుర్ మాసం ముగుస్తుంది"),
            ),
        ),

        "Chaitra" to SacredMonthInfo(
            masaNameEnglish = "Chaitra",
            masaNameTelugu = "చైత్ర మాసం",
            significanceTelugu = "చైత్ర మాసం తెలుగు నూతన సంవత్సరం ఉగాదితో ప్రారంభమయ్యే పవిత్ర మాసం. వసంత నవరాత్రులు, శ్రీరామ నవమి ఈ మాసంలో వస్తాయి.",
            significanceEnglish = "Chaitra month begins with Ugadi, the Telugu New Year. Vasanta Navaratri and Sri Rama Navami fall in this sacred month.",
            dailyPractices = listOf(
                DailyPractice("ఉగాది పంచాంగ శ్రవణం", "Ugadi Panchangam", "నూతన సంవత్సర ఫలాలు వినాలి"),
                DailyPractice("వసంత పూజ", "Vasanta Puja", "వసంత ఋతువు ప్రారంభంలో దేవి పూజ"),
                DailyPractice("రామాయణ పారాయణం", "Ramayana Parayanam", "శ్రీరామ నవమి వరకు రామాయణ పఠనం"),
            ),
            specialDays = listOf(
                SpecialDay("ఉగాది", "Ugadi", "తెలుగు నూతన సంవత్సర వేడుక. ఉగాది పచ్చడి తయారీ"),
                SpecialDay("వసంత నవరాత్రులు", "Vasanta Navaratri", "9 రోజులు దేవి పూజ"),
                SpecialDay("శ్రీరామ నవమి", "Sri Rama Navami", "శ్రీరాముని జన్మదినం, కళ్యాణోత్సవం"),
                SpecialDay("హనుమాన్ జయంతి", "Hanuman Jayanti", "చైత్ర పౌర్ణమి — హనుమంతుని జన్మదినం"),
            ),
        ),
    )

    fun getCurrentSacredMonth(masaNameEnglish: String): SacredMonthInfo? {
        return sacredMonths[masaNameEnglish]
    }

    fun getAllSacredMonths(): List<SacredMonthInfo> {
        return sacredMonths.values.toList()
    }
}
