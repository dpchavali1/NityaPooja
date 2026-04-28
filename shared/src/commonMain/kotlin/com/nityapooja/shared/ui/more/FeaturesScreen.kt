package com.nityapooja.shared.ui.more

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.ScaledContent
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturesScreen(
    onBack: () -> Unit = {},
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("యాప్ విశేషాలు", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("App Features", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    FontSizeControls(
                        fontSize = fontSize,
                        onDecrease = fontSizeViewModel::decrease,
                        onIncrease = fontSizeViewModel::increase,
                    )
                },
            )
        }
    ) { padding ->
        ScaledContent(fontScale) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Version badge
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = TempleGold.copy(alpha = 0.12f),
                ) {
                    Text(
                        "🙏 NityaPooja · Version 2.3 · 38+ Features",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = TempleGold,
                    )
                }
            }

            // ── Section 1: Japa ──────────────────────────────────────────────
            item { SectionHeader(titleTelugu = "జపం & ఆధ్యాత్మిక సాధన", titleEnglish = "Japa & Spiritual Practice") }
            item {
                FeatureCard(
                    icon = Icons.Default.SelfImprovement,
                    titleTelugu = "జపం (మాల కౌంటర్)",
                    titleEnglish = "Japa Counter",
                    descTelugu = "మాల సంఖ్య లెక్కించండి — హాప్టిక్ ఫీడ్‌బ్యాక్‌తో, సెషన్ చరిత్ర సహా. 1 నుండి 1000+ మాలల వరకు సాధన చేయండి. వరుస రోజుల స్ట్రీక్ ప్రగతి కూడా ట్రాక్ చేస్తుంది.",
                    descEnglish = "Count malas with haptic feedback and full session history. Tracks consecutive-day streaks toward milestone badges.",
                    howToUse = "More → జపం → start counting",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Lock,
                    titleTelugu = "జప లాక్ మోడ్",
                    titleEnglish = "Japa Lock Mode",
                    descTelugu = "జపం చేస్తున్నప్పుడు స్క్రీన్ లాక్ చేయండి — పూర్తి స్క్రీన్ కౌంటర్ మాత్రమే, ఎటువంటి విఘ్నాలు లేవు. పైకి స్వైప్ చేసి బయటకు వెళ్ళండి.",
                    descEnglish = "Lock the screen during japa — full-screen counter only, no distractions. Swipe up to exit.",
                    howToUse = "Japa Counter → 🔒 Lock button",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.EmojiEvents,
                    titleTelugu = "ఆధ్యాత్మిక పురస్కారాలు",
                    titleEnglish = "Spiritual Badges",
                    descTelugu = "హిందూ చిహ్నాలతో 15+ పురస్కారాలు: త్రిపతాక (3 రోజులు), సప్తర్షి (7 రోజులు), సహస్ర దీపం (1000 మాలలు), వైకుంఠ ద్వారం (30 రోజులు). స్ట్రీక్ మైల్‌స్టోన్లు: త్రిమూర్తి వ్రతం (21 రోజులు), మండల సేవ (40 రోజులు), శత దీపం (108 రోజులు) — ప్రతి పురస్కారంపై సంబరం యానిమేషన్.",
                    descEnglish = "15+ badges with Hindu symbols. Streak milestones: Trimurti Vrat (21 days), Mandala Seva (40 days), Shata Deepam (108 days) — each badge earned triggers a celebration animation.",
                    howToUse = "More → పురస్కారాలు",
                )
            }

            // ── Section 2: Puja ──────────────────────────────────────────────
            item { Spacer(Modifier.height(4.dp)); SectionHeader(titleTelugu = "పూజా విధానాలు", titleEnglish = "Puja & Rituals") }
            item {
                FeatureCard(
                    icon = Icons.Default.Spa,
                    titleTelugu = "పూజా విధానం",
                    titleEnglish = "Guided Puja",
                    descTelugu = "దేవతల వారీగా పూజా వినియోగ విధానం — పూజా పదార్థాలు, మంత్రాలు, ప్రతి దశ వివరణతో.",
                    descEnglish = "Step-by-step puja guide per deity — items needed, mantras, and instructions for each stage.",
                    howToUse = "More → పూజా విధానం",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.VolunteerActivism,
                    titleTelugu = "పూజా గది (వర్చువల్)",
                    titleEnglish = "Virtual Pooja Room",
                    descTelugu = "ఇంటరాక్టివ్ పూజా గది — పువ్వులు, అగరుబత్తి, హారతి అర్పించండి. అందమైన యానిమేషన్స్ తో.",
                    descEnglish = "Interactive virtual altar — offer flowers, incense, harathi with beautiful animations.",
                    howToUse = "Bottom nav → Pooja tab",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Timer,
                    titleTelugu = "పూజా టైమర్",
                    titleEnglish = "Pooja Timer",
                    descTelugu = "పూజా సమయాన్ని నిర్ణయించండి — కౌంట్‌డౌన్ అలారం, అవసరమైన ముగింపు ప్రమాదాలు.",
                    descEnglish = "Set a timed puja session with countdown and gentle bell alerts at completion.",
                    howToUse = "More → పూజా టైమర్",
                )
            }

            item { bannerAd?.invoke() }

            // ── Section 3: Panchangam & Time ─────────────────────────────────
            item { Spacer(Modifier.height(4.dp)); SectionHeader(titleTelugu = "పంచాంగం & కాలం", titleEnglish = "Panchangam & Time") }
            item {
                FeatureCard(
                    icon = Icons.Default.CalendarMonth,
                    titleTelugu = "నేటి పంచాంగం",
                    titleEnglish = "Daily Panchangam",
                    descTelugu = "తిథి, నక్షత్రం, యోగం, కరణం, రాహు కాలం, అభిజిత్ ముహూర్తం — మీ ప్రదేశం ఆధారంగా.",
                    descEnglish = "Tithi, Nakshatra, Yoga, Karana, Rahu Kalam, and Abhijit Muhurtam — location-based.",
                    howToUse = "Bottom nav → Panchangam tab",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.EventAvailable,
                    titleTelugu = "శుభ ముహూర్తాలు",
                    titleEnglish = "Muhurtam Finder",
                    descTelugu = "పెళ్ళి, గృహప్రవేశం, వ్యాపారం, ప్రయాణం — ఏదైనా కార్యానికి శుభ ముహూర్తం కనుగొనండి.",
                    descEnglish = "Find auspicious times for marriage, housewarming, travel, or any important event.",
                    howToUse = "More → శుభ ముహూర్తాలు",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Share,
                    titleTelugu = "అందమైన షేర్ కార్డ్",
                    titleEnglish = "Graphic Share Cards",
                    descTelugu = "పంచాంగం మరియు ముహూర్తాన్ని అందమైన చిత్ర కార్డ్‌గా షేర్ చేయండి — WhatsApp, Instagram కి నేరుగా.",
                    descEnglish = "Share Panchangam and Muhurtam as beautiful image cards directly to WhatsApp, Instagram, and more.",
                    howToUse = "Panchangam → Share → Preview → షేర్ చేయండి",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.WatchLater,
                    titleTelugu = "చోఘడియా",
                    titleEnglish = "Choghadiya",
                    descTelugu = "రోజులో ప్రతి గంట యొక్క శుభాశుభ స్థితి — శుభ, లాభ, అమృత, ఉద్వేగ, చల, రోగ, కాల.",
                    descEnglish = "Hourly time-quality chart for the day — know which hours are auspicious for starting activities.",
                    howToUse = "More → చోఘడియా",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Star,
                    titleTelugu = "గ్రహ పరివర్తనలు",
                    titleEnglish = "Planet Transits",
                    descTelugu = "గ్రహాల గమనం, రాశి మార్పు తేదీలు — శని, గురు, రాహు, కేతు పరివర్తన వివరాలు.",
                    descEnglish = "Planetary movement dates — Saturn, Jupiter, Rahu, Ketu transits with their significance.",
                    howToUse = "More → గ్రహ పరివర్తనలు",
                )
            }

            // ── Section 4: Home Screen Widgets ──────────────────────────────
            item { Spacer(Modifier.height(4.dp)); SectionHeader(titleTelugu = "హోమ్ స్క్రీన్ విజెట్స్", titleEnglish = "Home Screen Widgets") }
            item {
                FeatureCard(
                    icon = Icons.Default.Widgets,
                    titleTelugu = "పంచాంగం విజెట్",
                    titleEnglish = "Panchangam Widget",
                    descTelugu = "4×3 పరిమాణంలో పెద్ద అక్షరాలతో నేటి పంచాంగం — తిథి, నక్షత్రం, యోగం, కరణం, సూర్యోదయ/సూర్యాస్తమయ. వయసైన వారికి సులభంగా చదవడానికి రూపొందించారు. అర్ధరాత్రి స్వయంచాలకంగా నవీకరించబడుతుంది.",
                    descEnglish = "Daily Panchangam in a 4×3 tile with large, accessible text — Tithi, Nakshatra, Yoga, Karana, and Sunrise/Sunset. Designed for readability. Auto-updates at midnight.",
                    howToUse = "Home screen long-press → Widgets → Nitya Pooja → Panchangam (4×2 మొదట జోడించండి, తర్వాత సైజు పెంచవచ్చు)",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Stars,
                    titleTelugu = "రాశిఫలం విజెట్",
                    titleEnglish = "Rashifal Widget",
                    descTelugu = "మీ జన్మ రాశి ఫలం నేరుగా హోమ్ స్క్రీన్‌పై — అప్లికేషన్ తెరవకుండా. చంద్రాష్టమ హెచ్చరిక ఎర్ర రంగులో. Settings లో 'Show English' ఆన్ చేస్తే ఆంగ్లంలో చూపిస్తుంది.",
                    descEnglish = "Your janma rashi prediction on your home screen without opening the app. Chandrashtama shown in red. Respects the Show English toggle in Settings.",
                    howToUse = "1. Settings → Sankalpam Details → మీ రాశి ఎంచుకోండి  2. Home screen long-press → Widgets → Nitya Pooja → Rashifal  3. Widget నొక్కితే రాశిఫలం స్క్రీన్‌కు వెళ్తారు",
                )
            }

            // ── Section 5: Jyotish ───────────────────────────────────────────
            item { Spacer(Modifier.height(4.dp)); SectionHeader(titleTelugu = "జ్యోతిష్యం", titleEnglish = "Jyotish & Astrology") }
            item {
                FeatureCard(
                    icon = Icons.Default.Stars,
                    titleTelugu = "రాశిఫలం",
                    titleEnglish = "Rashifal",
                    descTelugu = "12 రాశులకు నిజమైన వైదిక గోచార ఫలం — చంద్రుని సిద్ధాంత స్థానం ఆధారంగా. జన్మ రాశికి చంద్రుడు ఏ స్థానంలో ఉన్నాడో లెక్కించి, చంద్రాష్టమ హెచ్చరిక (8వ స్థానం) సహా చూపిస్తాం. ప్రతి ~2.5 రోజులకు మారుతుంది.",
                    descEnglish = "Authentic Vedic Moon gochara predictions for all 12 rashis. Calculates actual Moon transit house from your janma rashi — Chandrashtama (8th house) shown as red warning. Updates every ~2.5 days as Moon changes sign.",
                    howToUse = "More → రాశిఫలం",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.AutoAwesome,
                    titleTelugu = "జాతక చక్రం",
                    titleEnglish = "Birth Chart",
                    descTelugu = "వైదిక జాతక చక్రం — జన్మ తేదీ, సమయం, స్థలం ఇస్తే లగ్నం, గ్రహ స్థానాలు లెక్కిస్తాం.",
                    descEnglish = "Vedic birth chart — enter date, time, and place of birth for lagna and planetary positions.",
                    howToUse = "More → జాతక చక్రం",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Favorite,
                    titleTelugu = "గుణ మిలనం",
                    titleEnglish = "Compatibility Match",
                    descTelugu = "పెళ్ళికి వధూవరుల నక్షత్ర గుణ మిలనం — 36 గుణాలలో స్కోర్ మరియు వివరణ.",
                    descEnglish = "Marriage compatibility check via nakshatra guna milan — score out of 36 with detailed analysis.",
                    howToUse = "More → గుణ మిలనం",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Person,
                    titleTelugu = "వ్యక్తిగత మార్గదర్శనం",
                    titleEnglish = "Personalized Daily Briefing",
                    descTelugu = "మీ నక్షత్రం ఆధారంగా నేటి ఆధ్యాత్మిక మార్గదర్శనం హోమ్ స్క్రీన్‌లో వస్తుంది.",
                    descEnglish = "Daily spiritual guidance based on your nakshatra shown on the home screen.",
                    howToUse = "Profile → Nakshatra సెట్ చేయండి → Home screen చూడండి",
                )
            }

            // ── Section 6: Family ────────────────────────────────────────────
            item { Spacer(Modifier.height(4.dp)); SectionHeader(titleTelugu = "కుటుంబం", titleEnglish = "Family") }
            item {
                FeatureCard(
                    icon = Icons.Default.People,
                    titleTelugu = "కుటుంబ ప్రొఫైల్స్",
                    titleEnglish = "Family Profiles",
                    descTelugu = "కుటుంబ సభ్యుల జాతక వివరాలు సేవ్ చేయండి — తల్లి, తండ్రి, పిల్లలు. జాతక చక్రం, గుణ మిలనం అన్నింటికీ ఉపయోగపడుతుంది.",
                    descEnglish = "Save birth details for all family members. Reuse for birth chart, compatibility, and muhurtam.",
                    howToUse = "More → కుటుంబ ప్రొఫైల్స్",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Celebration,
                    titleTelugu = "కుటుంబ పర్వదినాలు",
                    titleEnglish = "Family Important Days",
                    descTelugu = "పుట్టినరోజులు, వివాహ వార్షికోత్సవాలు, తిథి శ్రాద్ధ రోజులు నమోదు చేయండి. తిథి తేదీ స్వయంగా లెక్కించి ముందు 3 సంవత్సరాల తేదీలు చూపిస్తాం. ముందు రోజు & ఆ రోజు రిమైండర్ వస్తుంది.",
                    descEnglish = "Add birthdays, anniversaries, and tithi (shraddha) days. Tithi dates shift each year — app calculates next 3 years. Get day-before and same-day reminders.",
                    howToUse = "More → కుటుంబ పర్వదినాలు → + Add",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Star,
                    titleTelugu = "ఈ వారం దేవత",
                    titleEnglish = "Deity of the Week",
                    descTelugu = "ప్రతి వారం ఒక దేవతపై దృష్టి పెట్టండి — మంత్రాలు, భక్తి చిట్కాలు హోమ్ స్క్రీన్‌లో కనిపిస్తాయి.",
                    descEnglish = "Each week focuses on a different deity — mantras and tips shown on your home screen.",
                    howToUse = "Home screen → ఈ వారం దేవత card",
                )
            }

            // ── Section 7: Telugu Culture ────────────────────────────────────
            item { Spacer(Modifier.height(4.dp)); SectionHeader(titleTelugu = "తెలుగు సంస్కృతి", titleEnglish = "Telugu Culture") }
            item {
                FeatureCard(
                    icon = Icons.Default.Brightness5,
                    titleTelugu = "వ్రతాలు & ఉపవాసాలు",
                    titleEnglish = "Vratas & Observances",
                    descTelugu = "ఏకాదశి, ప్రదోష, సత్యనారాయణ వ్రతం, మంగళ గౌరి మరియు 50+ వ్రతాల సంపూర్ణ వివరాలు.",
                    descEnglish = "Complete guide for Ekadashi, Pradosha, Satyanarayana, Mangala Gauri and 50+ more vratas.",
                    howToUse = "More → వ్రతాలు",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.CalendarMonth,
                    titleTelugu = "పవిత్ర మాసాలు",
                    titleEnglish = "Sacred Months",
                    descTelugu = "కార్తీకం, శ్రావణం, ధనుర్మాసం — ప్రతి పవిత్ర మాసం విశేషాలు, చేయవలసిన పూజలు.",
                    descEnglish = "Karthika, Sravana, Dhanurmasam — significance and rituals for each sacred Telugu month.",
                    howToUse = "More → పవిత్ర మాసాలు",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Quiz,
                    titleTelugu = "పురాణాల క్విజ్",
                    titleEnglish = "Puranas Quiz",
                    descTelugu = "12 పురాణాలపై 300+ ద్విభాష ప్రశ్నలు — వేద పురాణ జ్ఞానం పరీక్షించుకోండి. 70%+ స్కోర్ చేస్తే పురాణ పండిత్ పురస్కారం.",
                    descEnglish = "300+ bilingual questions across 12 Puranas. Score 70%+ to earn the Purana Scholar badge.",
                    howToUse = "More → పురాణాల క్విజ్",
                )
            }

            // ── Section 8: Devotional Texts ──────────────────────────────────
            item { Spacer(Modifier.height(4.dp)); SectionHeader(titleTelugu = "భక్తి గ్రంథాలు", titleEnglish = "Devotional Texts") }
            item {
                FeatureCard(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    titleTelugu = "స్తోత్రాలు, అష్టోత్రాలు, చాలీసా",
                    titleEnglish = "Stotrams, Ashtottara & Chalisa",
                    descTelugu = "వినాయక, లక్ష్మీ, శివ, విష్ణు, హనుమాన్ మరియు మరిన్ని దేవతల స్తోత్రాలు. 108 నామాలు (అష్టోత్తరాలు) మరియు హనుమాన్ చాలీసా, దుర్గా చాలీసా.",
                    descEnglish = "Stotrams for all major deities, 108 names (Ashtottara), and popular Chalisas — with font size control.",
                    howToUse = "More → స్తోత్రాలు / అష్టోత్రాలు / చాలీసా",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.SelfImprovement,
                    titleTelugu = "మంత్రాలు (జప మోడ్ తో)",
                    titleEnglish = "Mantras with Chanting Mode",
                    descTelugu = "గాయత్రీ, మహా మృత్యుంజయ, పంచాక్షరి మరియు మరెన్నో మంత్రాలు. జప మోడ్‌లో మాల లెక్కించవచ్చు.",
                    descEnglish = "Gayatri, Maha Mrityunjaya, Panchakshara and many more mantras. Use chanting mode to count malas.",
                    howToUse = "More → మంత్రాలు → Start Chanting",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.LibraryMusic,
                    titleTelugu = "భజనలు, కీర్తనలు, సుప్రభాతం, హారతి",
                    titleEnglish = "Bhajans, Keertanalu, Suprabhatam & Aarti",
                    descTelugu = "భక్తి సంగీత పాఠాలు — భజనలు, కీర్తనలు, సుప్రభాతం పాఠాలు, హారతి పాటలు. ఆడియో ప్లే బ్యాక్ సహా.",
                    descEnglish = "Devotional song lyrics — bhajans, keertanalu, suprabhatam, and aarti songs with audio playback.",
                    howToUse = "More → Bhajans / Keertanalu / Suprabhatam  |  Bottom nav → Aarti",
                )
            }

            // ── Section 9: Personalization ───────────────────────────────────
            item { Spacer(Modifier.height(4.dp)); SectionHeader(titleTelugu = "వ్యక్తిగత సేవలు", titleEnglish = "Personalization") }
            item {
                FeatureCard(
                    icon = Icons.Default.AddPhotoAlternate,
                    titleTelugu = "దేవత ఫోటో మార్చండి",
                    titleEnglish = "Custom Deity Photos",
                    descTelugu = "ఏ దేవత ఫోటోనైనా మీ స్వంత ఫోటోతో మార్చండి — మీ ఇంటి దేవత లేదా ఆలయ ఫోటో. మీ పరికరంలో మాత్రమే సేవ్ అవుతుంది.",
                    descEnglish = "Replace any deity's photo with your own — your home idol or temple photo. Replaces the default image for that deity only. Stored privately on your device.",
                    howToUse = "Any Deity screen → 📷 నా ఫోటో సెట్ చేయండి",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.AccountCircle,
                    titleTelugu = "ప్రొఫైల్ & నక్షత్రం",
                    titleEnglish = "Profile & Nakshatra",
                    descTelugu = "మీ పేరు, నక్షత్రం, గోత్రం సెట్ చేయండి. నక్షత్రం ఆధారంగా హోమ్ స్క్రీన్‌లో వ్యక్తిగత మార్గదర్శనం వస్తుంది.",
                    descEnglish = "Set your name, nakshatra, and gotra. Nakshatra unlocks personalized daily guidance on the home screen.",
                    howToUse = "Home screen → Profile icon (top right)",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Translate,
                    titleTelugu = "ఆంగ్లంలో చూపించు",
                    titleEnglish = "Show English Translations",
                    descTelugu = "మంత్రాల అర్థాలు, ప్రయోజనాలు, రాశిఫలం — అన్నీ ఒక టాగుల్‌తో ఆంగ్లంలో మారుస్తారు. తెలుగు బాగా రాని వారికి లేదా ఆంగ్లంలో చదవాలనుకునే వారికి ఉపయుక్తంగా ఉంటుంది.",
                    descEnglish = "Toggle all mantra meanings, benefits, and Rashifal predictions to English in one tap. Useful for those who prefer reading content in English.",
                    howToUse = "Settings → Appearance → Show English Translations (toggle on/off)",
                )
            }
            item {
                FeatureCard(
                    icon = Icons.Default.Language,
                    titleTelugu = "వెబ్‌సైట్ అందుబాటులో",
                    titleEnglish = "Website: nityapooja.app",
                    descTelugu = "నిత్య పూజ వెబ్‌సైట్ ఇప్పుడు అందుబాటులో ఉంది — నేటి పంచాంగం, ఫీచర్లు, సహాయం.",
                    descEnglish = "NityaPooja now has a full website with live Panchangam, features overview, and support.",
                    howToUse = "Settings → గురించి → Visit nityapooja.app",
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
        } // ScaledContent
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    titleTelugu: String,
    titleEnglish: String,
    descTelugu: String,
    descEnglish: String,
    howToUse: String,
) {
    GlassmorphicCard(accentColor = TempleGold, cornerRadius = 16.dp, contentPadding = 14.dp) {
        Row {
            Icon(icon, null, tint = TempleGold, modifier = Modifier.size(26.dp).padding(top = 2.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(titleTelugu, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(titleEnglish, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(5.dp))
                Text(descTelugu, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(3.dp))
                Text(descEnglish, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Surface(shape = MaterialTheme.shapes.small, color = AuspiciousGreen.copy(alpha = 0.1f)) {
                    Text(
                        howToUse,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = AuspiciousGreen,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
