package com.nityapooja.shared.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.theme.NityaPoojaTextStyles
import com.nityapooja.shared.ui.theme.TempleGold

// Nakshatra guidance: key = English name (matches JyotishConstants.NAKSHATRA_NAMES_ENGLISH)
// Value = Pair(teluguGuidance, englishGuidance)
private val NAKSHATRA_GUIDANCE: Map<String, Pair<String, String>> = mapOf(
    "Ashwini" to Pair(
        "అశ్విని నక్షత్రం — నేడు ఆరోగ్య కార్యాలకు శుభం. గణేశ స్తోత్రం పఠించండి.",
        "Ashwini Nakshatra — Auspicious for health matters. Recite Ganesha Stotra."
    ),
    "Bharani" to Pair(
        "భరణి నక్షత్రం — నేడు పితృ తర్పణం చేయడం మంచిది. యమ స్తుతి చదవండి.",
        "Bharani Nakshatra — Good day for ancestral offerings. Recite Yama Stuti."
    ),
    "Krittika" to Pair(
        "కృత్తిక నక్షత్రం — నేడు అగ్ని పూజ అత్యంత ఫలదాయకం. సూర్యనమస్కారాలు చేయండి.",
        "Krittika Nakshatra — Agni puja is highly beneficial. Practice Surya Namaskar."
    ),
    "Rohini" to Pair(
        "రోహిణి నక్షత్రం — నేడు లక్ష్మీ పూజ మంచిది. వ్యాపారం, సంపద కోసం శ్రీ సూక్తం పఠించండి.",
        "Rohini Nakshatra — Auspicious for Lakshmi worship. Recite Sri Suktam for prosperity."
    ),
    "Mrigashira" to Pair(
        "మృగశిర నక్షత్రం — నేడు విద్య, కళలకు శుభం. సరస్వతీ నమస్కారం చేయండి.",
        "Mrigashira Nakshatra — Auspicious for learning and arts. Offer prayers to Saraswati."
    ),
    "Ardra" to Pair(
        "ఆర్ద్ర నక్షత్రం — నేడు శివ పూజ విశేషం. రుద్రం పఠించి శివుని ప్రసన్నం చేసుకోండి.",
        "Ardra Nakshatra — Shiva puja is specially powerful. Recite Rudram for Lord Shiva's blessings."
    ),
    "Punarvasu" to Pair(
        "పునర్వసు నక్షత్రం — నేడు విష్ణు భక్తికి అనుకూలం. విష్ణు సహస్రనామం పఠించండి.",
        "Punarvasu Nakshatra — Favorable for Vishnu devotion. Recite Vishnu Sahasranama."
    ),
    "Pushyami" to Pair(
        "పుష్యమి నక్షత్రం — అత్యంత శుభ నక్షత్రం. నేడు బృహస్పతి పూజ చేయండి, గురు స్తోత్రం పఠించండి.",
        "Pushyami Nakshatra — Most auspicious nakshatra. Worship Brihaspati and recite Guru Stotra."
    ),
    "Ashlesha" to Pair(
        "ఆశ్లేష నక్షత్రం — నేడు నాగ దేవత పూజ మంచిది. నాగ పంచమి స్తోత్రం చదవండి.",
        "Ashlesha Nakshatra — Good for Naga devata worship. Recite Naga Panchami Stotra."
    ),
    "Magha" to Pair(
        "మఘ నక్షత్రం — నేడు పితృ కార్యాలకు శుభం. పితృ సూక్తం పఠించండి.",
        "Magha Nakshatra — Auspicious for ancestral rites. Recite Pitru Sukta."
    ),
    "Purva Phalguni" to Pair(
        "పూర్వ ఫల్గుణి నక్షత్రం — నేడు భగ దేవతా పూజ శుభం. సంతోష, సమృద్ధి కోసం ప్రార్థించండి.",
        "Purva Phalguni Nakshatra — Worship Bhaga for joy and abundance."
    ),
    "Uttara Phalguni" to Pair(
        "ఉత్తర ఫల్గుణి నక్షత్రం — నేడు వివాహ, ఒప్పందాలకు శుభం. సూర్య నమస్కారాలు చేయండి.",
        "Uttara Phalguni Nakshatra — Auspicious for marriage and contracts. Practice Surya Namaskar."
    ),
    "Hasta" to Pair(
        "హస్త నక్షత్రం — నేడు చేతి నైపుణ్యాలకు, కళలకు శుభం. సరస్వతీ పూజ చేయండి.",
        "Hasta Nakshatra — Favorable for crafts and arts. Offer prayers to Saraswati."
    ),
    "Chitta" to Pair(
        "చిత్త నక్షత్రం — నేడు విశ్వకర్మ పూజ మంచిది. నిర్మాణ పనులు ప్రారంభించవచ్చు.",
        "Chitta Nakshatra — Good for Vishwakarma puja. Auspicious to begin construction work."
    ),
    "Swati" to Pair(
        "స్వాతి నక్షత్రం — నేడు వాయు దేవతా ప్రార్థన శుభం. వ్యాపార వ్యవహారాలు ప్రారంభించవచ్చు.",
        "Swati Nakshatra — Vayu devata prayers are auspicious. Good for starting business ventures."
    ),
    "Vishakha" to Pair(
        "విశాఖ నక్షత్రం — నేడు ఇంద్ర పూజ మంచిది. విజయం కోసం ఇంద్ర స్తోత్రం పఠించండి.",
        "Vishakha Nakshatra — Indra puja is auspicious. Recite Indra Stotra for victory."
    ),
    "Anuradha" to Pair(
        "అనురాధ నక్షత్రం — నేడు మిత్రత్వం, సంబంధాలకు శుభం. విష్ణు పూజ చేయండి.",
        "Anuradha Nakshatra — Favorable for friendships and relationships. Worship Vishnu."
    ),
    "Jyeshtha" to Pair(
        "జ్యేష్ఠ నక్షత్రం — నేడు ఇంద్ర పూజ, నాయకత్వ కార్యాలకు శుభం.",
        "Jyeshtha Nakshatra — Auspicious for Indra worship and leadership activities."
    ),
    "Moola" to Pair(
        "మూల నక్షత్రం — నేడు నిర్రుతి పూజ చేయండి. పాత విషయాలు వదిలి కొత్తవి ప్రారంభించవచ్చు.",
        "Moola Nakshatra — Worship Nirrutti. Ideal for releasing the old and starting anew."
    ),
    "Purvashadha" to Pair(
        "పూర్వాషాఢ నక్షత్రం — నేడు అపాం నపాత్ పూజ శుభం. విజయం కోసం దేవీ ప్రార్థన చేయండి.",
        "Purvashadha Nakshatra — Auspicious for Apam Napat worship. Pray to Devi for victory."
    ),
    "Uttarashadha" to Pair(
        "ఉత్తరాషాఢ నక్షత్రం — నేడు విశ్వదేవ పూజ శుభం. ధర్మ మార్గంలో నడవండి.",
        "Uttarashadha Nakshatra — Vishvadeva puja is auspicious. Walk the path of dharma."
    ),
    "Shravana" to Pair(
        "శ్రవణం నక్షత్రం — నేడు విష్ణు పూజ విశేషం. విష్ణు సహస్రనామం పఠించి ఆశీర్వాదం పొందండి.",
        "Shravana Nakshatra — Special day for Vishnu worship. Recite Vishnu Sahasranama."
    ),
    "Dhanishtha" to Pair(
        "ధనిష్ఠ నక్షత్రం — నేడు అష్ట వసువుల పూజ శుభం. సంపద, సంగీతం ఆరాధించండి.",
        "Dhanishtha Nakshatra — Worship Ashta Vasus. Celebrate wealth and music."
    ),
    "Shatabhisha" to Pair(
        "శతభిషం నక్షత్రం — నేడు వరుణ పూజ మంచిది. నీటి వనరులకు కృతజ్ఞత చెప్పండి.",
        "Shatabhisha Nakshatra — Varuna puja is beneficial. Express gratitude for water resources."
    ),
    "Purvabhadra" to Pair(
        "పూర్వాభాద్ర నక్షత్రం — నేడు అజ ఏకపాత్ పూజ శుభం. ఆధ్యాత్మిక చింతనకు మంచి రోజు.",
        "Purvabhadra Nakshatra — Aja Ekapada worship is auspicious. Excellent day for spiritual contemplation."
    ),
    "Uttarabhadra" to Pair(
        "ఉత్తరాభాద్ర నక్షత్రం — నేడు అహిర్బుధ్న్య పూజ శుభం. ధ్యానానికి అత్యుత్తమ రోజు.",
        "Uttarabhadra Nakshatra — Ahirbudhnya worship is auspicious. Best day for deep meditation."
    ),
    "Revati" to Pair(
        "రేవతి నక్షత్రం — నేడు పూషన్ పూజ శుభం. ప్రయాణాలకు, పశువులకు రక్షణ పొందండి.",
        "Revati Nakshatra — Pushan puja is auspicious. Seek protection for travel and animals."
    ),
)

// Day-of-week auspicious activity (0=Monday..6=Sunday, kotlinx DayOfWeek ordinal)
private val DAY_ACTIVITIES = mapOf(
    0 to Pair("సోమవారం — శివ పూజకు పవిత్రమైన రోజు", "Monday — Sacred day for Shiva worship"),
    1 to Pair("మంగళవారం — హనుమంతుని ఆరాధనకు శుభం", "Tuesday — Auspicious for Hanuman worship"),
    2 to Pair("బుధవారం — గణేశుని పూజకు అనుకూలం", "Wednesday — Favorable for Ganesha worship"),
    3 to Pair("గురువారం — విష్ణు, బృహస్పతి పూజకు పవిత్రమైన రోజు", "Thursday — Sacred for Vishnu and Brihaspati worship"),
    4 to Pair("శుక్రవారం — లక్ష్మీ పూజకు అత్యంత శుభకరం", "Friday — Most auspicious for Lakshmi worship"),
    5 to Pair("శనివారం — శని, హనుమంతుని పూజకు శుభం", "Saturday — Auspicious for Shani and Hanuman worship"),
    6 to Pair("ఆదివారం — సూర్య, రాముని ఆరాధనకు శుభం", "Sunday — Auspicious for Surya and Rama worship"),
)

// Mantra recommendation per nakshatra (index in 27 list)
private val NAKSHATRA_MANTRAS = listOf(
    "అశ్విన్యై నమః — Ashwinyai Namah",
    "ఓం కాళికాయై నమః — Om Kalikayai Namah",
    "ఓం అగ్నయే నమః — Om Agnaye Namah",
    "ఓం లక్ష్మీ నారాయణాయ నమః — Om Lakshmi Narayanaya Namah",
    "ఓం సోమాయ నమః — Om Somaya Namah",
    "ఓం శివాయ నమః — Om Shivaya Namah",
    "ఓం అదితయే నమః — Om Aditaye Namah",
    "ఓం బృహస్పతయే నమః — Om Brihaspataye Namah",
    "ఓం సర్పేభ్యో నమః — Om Sarpebhyo Namah",
    "ఓం పితృభ్యో నమః — Om Pitribhyo Namah",
    "ఓం భగాయ నమః — Om Bhagaya Namah",
    "ఓం ఆర్యమ్ణే నమః — Om Aryamne Namah",
    "ఓం సవిత్రే నమః — Om Savitre Namah",
    "ఓం విశ్వకర్మణే నమః — Om Vishvakarmaṇe Namah",
    "ఓం వాయవే నమః — Om Vayave Namah",
    "ఓం ఇంద్రాగ్నిభ్యాం నమః — Om Indragnibyam Namah",
    "ఓం మిత్రాయ నమః — Om Mitraya Namah",
    "ఓం ఇంద్రాయ నమః — Om Indraya Namah",
    "ఓం నిర్రుతయే నమః — Om Nirrutaye Namah",
    "ఓం అపాం నపాతే నమః — Om Apam Napate Namah",
    "ఓం విశ్వేభ్యో దేవేభ్యో నమః — Om Vishvebhyo Devebhyo Namah",
    "ఓం విష్ణవే నమః — Om Vishnave Namah",
    "ఓం అష్ట వసుభ్యో నమః — Om Ashta Vasubhyo Namah",
    "ఓం వరుణాయ నమః — Om Varunaya Namah",
    "ఓం అజ ఏకపాదాయ నమః — Om Aja Ekapadaya Namah",
    "ఓం అహిర్బుధ్న్యాయ నమః — Om Ahirbudhnyaya Namah",
    "ఓం పూష్ణే నమః — Om Pushne Namah",
)

private val NAKSHATRA_NAMES_ENGLISH_LIST = listOf(
    "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira",
    "Ardra", "Punarvasu", "Pushyami", "Ashlesha", "Magha",
    "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitta", "Swati",
    "Vishakha", "Anuradha", "Jyeshtha", "Moola", "Purvashadha",
    "Uttarashadha", "Shravana", "Dhanishtha", "Shatabhisha", "Purvabhadra",
    "Uttarabhadra", "Revati",
)

@Composable
fun PersonalizedBriefingCard(
    userNakshatra: String,
    dayOfWeek: Int,
    modifier: Modifier = Modifier,
) {
    val guidance = NAKSHATRA_GUIDANCE[userNakshatra]
        ?: return // unknown nakshatra — don't render
    val dayActivity = DAY_ACTIVITIES[dayOfWeek % 7]
        ?: DAY_ACTIVITIES[0]!!
    val nakshatraIndex = NAKSHATRA_NAMES_ENGLISH_LIST.indexOf(userNakshatra)
    val mantra = if (nakshatraIndex >= 0) NAKSHATRA_MANTRAS[nakshatraIndex] else null

    var expanded by remember { mutableStateOf(false) }

    GlassmorphicCard(
        modifier = modifier,
        accentColor = TempleGold,
        cornerRadius = 14.dp,
        contentPadding = 0.dp,
    ) {
        // Collapsed header — always visible
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = TempleGold, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "నేటి మీ మార్గదర్శనం · YOUR DAILY GUIDANCE",
                style = NityaPoojaTextStyles.GoldLabel,
                color = TempleGold,
                modifier = Modifier.weight(1f),
            )
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = TempleGold,
                modifier = Modifier.size(18.dp),
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp).padding(bottom = 14.dp)) {
                HorizontalDivider(color = TempleGold.copy(alpha = 0.15f))
                Spacer(Modifier.height(12.dp))

                Text(
                    guidance.first,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    guidance.second,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                )

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = TempleGold.copy(alpha = 0.15f))
                Spacer(Modifier.height(12.dp))

                Text(
                    dayActivity.first,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    dayActivity.second,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (mantra != null) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = TempleGold.copy(alpha = 0.15f))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "నేటి మంత్రం · Today's Mantra",
                        style = NityaPoojaTextStyles.GoldLabel,
                        color = TempleGold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        mantra,
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    )
                }
            }
        }
    }
}
