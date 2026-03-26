# NityaPooja Home Screen UX Research Report

## Research Overview

**Objective**: Determine the optimal home screen layout for NityaPooja, a Telugu Hindu devotional app, to prioritize spiritual/devotional content while preserving panchangam utility.

**Core Problem**: The current home screen leads with dense panchangam information (TeluguCalendarCard + Muhurtam Quick Info), pushing deity and devotional content below the fold. Users open a pooja app expecting God first, not a calendar.

**Methods Used**: Competitive analysis (Sri Mandir, Drik Panchang, MyMandir, Hindu Calendar apps), design pattern research, engagement/retention research from analogous spiritual apps, and code audit of current HomeScreen.kt implementation.

**Date**: March 25, 2026

---

## 1. Current State Audit

### Current Home Screen Order (LazyColumn items)

| Position | Component | Approx Height | Devotional? |
|----------|-----------|---------------|-------------|
| 1 | Festival Greeting Card (conditional) | ~120dp | Yes |
| 2 | TeluguCalendarCard (Panchangam) | ~140dp | No - informational |
| 3 | Muhurtam Quick Info (Rahu/Abhijit/Sunrise) | ~90dp | No - informational |
| 4 | Daily Shloka Card | ~160dp | YES - core devotional |
| 5 | Deity of the Day | ~120dp | YES - core devotional |
| 6 | Quick Access Row (Aarti/Japa/Temples/Festivals/Rashifal) | ~90dp | Mixed |
| 7 | Banner Ad | ~60dp | No |
| 8 | Upcoming Festivals (LazyRow) | ~120dp | Yes |
| 9 | Grahanam Banner (conditional, within 7 days) | ~110dp | Informational |
| 10 | Moon Phase Card | ~80dp | Informational |
| 11 | All Deities (LazyRow) | ~100dp | Yes |
| 12 | Devotional Sections (Stotrams/Keertanalu) | ~90dp | Yes |
| 13 | Sankalpam Card (collapsible) | ~60dp collapsed | Ritual |
| 14 | Recently Viewed | Variable | Mixed |
| 15 | Bookmarks/Favorites | Variable | Mixed |

### Key Problem

On a standard mobile viewport (~700dp visible), items 1-3 consume approximately 350-430dp before any devotional content appears. The first thing a user sees is Samvatsara name, Masam, Tithi end times, Nakshatra end times, Yoga, Ayana, Rutu, sunrise/sunset, Rahu Kalam times, and Abhijit Muhurtam times. This creates the experience of opening a Hindu calendar app, not a pooja/devotional app.

The user's instinct is correct: "God should be first."

---

## 2. Competitive Analysis: What Leading Apps Do

### Sri Mandir (30M+ downloads, 20M MAU)

**Home screen pattern**: DEITY FIRST, always.

- Opens directly to user's personal shrine with deity image front and center
- Temple bells audio plays on open (multisensory immersion)
- Users can swipe between deity avatars
- Interactive aarti/pooja actions are immediately available
- Panchangam is a secondary tab, NOT on the home screen hero
- Audio player for bhajans is integrated into the deity view

**Key insight**: Sri Mandir treats the home screen as a virtual temple entrance. You see God first, hear bells, and can immediately begin worship. Calendar data is available but never competes with darshan.

### Drik Panchang (170K ratings, 4.82 stars)

**Home screen pattern**: CALENDAR FIRST (but this is a calendar app).

- Grid calendar is the primary view
- Daily panchangam details on tap
- Muhurta table with countdown timers
- Color-coded auspicious/inauspicious indicators

**Key insight**: Drik Panchang succeeds because it IS a calendar app. Its identity and user expectation are aligned. NityaPooja is a pooja app with calendar features, not the reverse. Copying Drik Panchang's layout for a devotional app is a category mismatch.

### MyMandir (50M+ users)

**Home screen pattern**: CONTENT-FIRST social feed.

- Daily dharmik photos and videos lead
- Panchang and Rashifal are listed features, not hero sections
- Community/social engagement drives the layout
- Temple darshan videos are prominent

**Key insight**: MyMandir treats panchangam as a utility feature, not identity. The social/content feed creates daily return visits.

### Hallow / Glorify (Christian devotional apps, for cross-category reference)

**Home screen pattern**: DAILY DEVOTIONAL FIRST.

- Personalized daily content (verse, reflection, prayer) is the hero
- Streak counters and progress indicators are secondary
- Clean, atmospheric design with muted colors and imagery
- Audio-forward (guided prayers, music)

**Key insight**: The most successful devotional apps across all religions lead with the daily spiritual message, not utility/calendar data.

---

## 3. Research Findings and Recommendations

### FINDING 1: The Hero Section Should Be Deity/Spiritual Content

**Evidence**: Every successful devotional app (Sri Mandir at 30M downloads, Hallow at $100M+ revenue) places the divine encounter first. Users open a devotional app for darshan (seeing God), ashirvadam (blessing), and spiritual connection -- not to check Rahu Kalam.

**Recommendation**: Restructure the hero section as a "Daily Darshan" experience.

**Proposed Hero Section (positions 1-2, above the fold):**

```
+------------------------------------------+
| [Greeting Bar]                           |
| Subhodayam, [Name]                       |
+------------------------------------------+
|                                          |
|  [DEITY OF THE DAY - HERO CARD]         |
|  Large deity image/avatar (120dp+)       |
|  "Somavaram - Shiva"                     |
|  Brief blessing text in Telugu           |
|  [Tap for deity details]                 |
|                                          |
+------------------------------------------+
|                                          |
|  [TODAY'S SHLOKA / BLESSING]            |
|  Sanskrit verse                          |
|  Telugu meaning                          |
|  [Share] [Bookmark]                      |
|                                          |
+------------------------------------------+
|  [Compact Panchangam Strip]   [See More] |
|  Chaitra Shu. Dashami | Ashwini | 06:12  |
+------------------------------------------+
```

**Rationale**:
- Deity of the Day is the emotional anchor. It changes daily (tied to weekday), giving users a reason to open the app each day.
- Daily Shloka is the intellectual/spiritual anchor. A new blessing every day of the year.
- Together they answer the user's core question: "What is today's spiritual significance?"

### FINDING 2: Panchangam Should Be a Compact Strip, Not a Card

**Evidence**: The current TeluguCalendarCard displays 6 lines of panchangam data (Samvatsara, Masa/Paksha/Tithi with end time, Vaaram/Nakshatra with end time, Yoga/Ayana/Rutu, Sunrise/Sunset, plus a calendar icon). This is approximately 140dp of dense informational text that serves reference needs, not devotional needs.

Drik Panchang succeeds with dense information because users specifically seek calendar data. NityaPooja users primarily seek devotional content; panchangam is supporting context.

**Recommendation**: Replace the full TeluguCalendarCard with a one-line "Panchangam Strip" on the home screen.

**Compact Panchangam Strip Design:**

```
+------------------------------------------+
| [Om icon] Chaitra Shu. Dashami | Ashwini |
|           06:12 sunrise  [View Full >]   |
+------------------------------------------+
```

**Information hierarchy for the strip (one or two lines max):**
- Line 1: Masa + Paksha + Tithi + Nakshatra (the four most commonly needed items)
- Line 2: Sunrise time + "View Full Panchangam" link

**Where the full data goes:**
- The existing TeluguCalendarCard moves to the dedicated Panchangam screen (already exists)
- Tapping the strip navigates to the full Panchangam screen
- The Muhurtam Quick Info section (Rahu/Abhijit/Sunrise) is removed from home and placed on the Panchangam screen

**Alternative**: A "Panchangam Pill" that sits as an inline element within or just below the Deity of the Day card:

```
+------------------------------------------+
|  DEITY OF THE DAY                        |
|  [Shiva image]    Monday - Somavaram     |
|                   Om Namah Shivaya       |
|                                          |
|  [Chaitra Shu.Dashami | Ashwini | 06:12] |  <-- integrated pill
+------------------------------------------+
```

This approach embeds the most essential panchangam data as context within the devotional card rather than as a competing section.

### FINDING 3: Daily Content Rotation and Streaks Drive Return Visits

**Evidence**: Research from Plotline (2025) shows apps using streak mechanics see 40-60% higher DAU, and users are 2.3x more likely to engage daily after building a 7+ day streak. Forrester's 2024 research shows streak+milestone systems reduce 30-day churn by 35%.

Sri Mandir drives daily engagement through interactive rituals (aarti, offerings). Hallow uses daily streaks for prayer/meditation sessions. YouVersion Bible app uses reading streaks.

**Recommendation**: Implement a "Daily Devotion Streak" system.

**Streak mechanics for NityaPooja:**

1. **Daily Darshan Streak**: Track consecutive days the user opens the app and views the Deity of the Day. Display as a small flame/diya counter on the home screen.

```
+------------------------------------------+
| [Diya icon] 12-day streak                |
| "Your daily darshan keeps the diya lit"  |
+------------------------------------------+
```

2. **Japa Counter Integration**: Show today's japa progress on the home screen as a subtle progress ring or count.

3. **Milestone Celebrations**: At 7, 21, 40 (significant in Hindu tradition -- 40 days = mandala), 108 days, show special blessing messages.

4. **Gentle, Not Punitive**: Following the Finch app model, frame breaks compassionately. Instead of "You lost your streak!", say "Welcome back, bhakta. The divine is always waiting." Hindu philosophy does not punish absence; it celebrates return.

5. **Daily Rotation Content** (things that change each day to create "what's new today" anticipation):
   - Deity of the Day (already exists, tied to weekday)
   - Daily Shloka (already exists, tied to day-of-year)
   - Daily Mantra suggestion (new: recommend a mantra based on the day's deity)
   - Panchangam data (naturally changes daily)

### FINDING 4: Balance Information Density with Spiritual Atmosphere

**Evidence**: The Sri Mandir UX audit specifically notes that the app "remixed many conventional app design guidelines" to create a spiritual experience. Temple bell sounds, animation, warm gold colors, and reduced information density create the feel of entering a temple, not checking a dashboard.

The current NityaPooja home screen has 15 distinct sections in the LazyColumn. This creates a dashboard/utility feel rather than a temple/spiritual feel.

**Recommendation**: Reduce home screen sections from 15 to 8-9, using progressive disclosure.

**Proposed Simplified Layout:**

| Position | Component | Purpose |
|----------|-----------|---------|
| 1 | Festival Greeting (conditional) | Celebration overlay on festival days |
| 2 | Deity of the Day (HERO) | Emotional/spiritual anchor, large visual |
| 3 | Daily Shloka/Blessing | Intellectual/spiritual anchor |
| 4 | Panchangam Strip (1-2 lines) | Essential context, not detail |
| 5 | Quick Access Row | Navigation to features |
| 6 | Banner Ad | Monetization |
| 7 | Upcoming Festivals (LazyRow) | Anticipation/planning |
| 8 | All Deities (LazyRow) | Exploration |
| 9 | Devotional Sections + Sankalpam | Deep engagement |

**What gets removed or relocated from home screen:**
- TeluguCalendarCard (full version) --> Panchangam screen only
- Muhurtam Quick Info (Rahu/Abhijit/Sunrise row) --> Panchangam screen
- Moon Phase Card --> Panchangam screen
- Grahanam Banner --> Panchangam screen (or notification only)
- Recently Viewed --> Profile screen or a separate "My Activity" section
- Bookmarks --> Profile screen or bottom nav "Favorites" tab

**Atmosphere design principles:**
- More vertical breathing room between sections (current 20dp spacing is good, but reduce section count)
- Use the GlassmorphicCard consistently for devotional content with warm gold accents
- Consider a subtle gradient background that shifts with time of day (dawn gold, midday warm, evening deep saffron, night deep blue)
- The deity card should be the largest visual element on screen, not competing with data cards

### FINDING 5: What Makes Users Come Back Daily

**Evidence from competitive analysis and engagement research:**

| Mechanism | App Example | Effectiveness | NityaPooja Opportunity |
|-----------|-------------|---------------|----------------------|
| Daily changing content | All successful apps | HIGH - creates "what's new today" | Deity + Shloka already rotate; add daily mantra |
| Streak/progress tracking | Hallow, Glorify, YouVersion | HIGH - 2.3x more likely to return at 7+ days | Add darshan streak with diya metaphor |
| Audio immersion | Sri Mandir (temple bells) | HIGH - multisensory = stronger habit | Play brief bell chime or Om on app open |
| Interactive ritual | Sri Mandir (virtual aarti) | HIGH - active participation > passive reading | Virtual Pooja Room exists; promote from home |
| Personalized blessing | Multiple apps | MEDIUM - emotional connection | Show blessing with user's name and nakshatra |
| Push notifications at pooja time | All apps with reminders | MEDIUM - external trigger for habit | Already have notification system; tie to Brahma Muhurta |
| Social sharing | MyMandir, YouVersion | MEDIUM - creates social accountability | Share shloka already exists; add "morning darshan" sharing |
| Festival countdown | Multiple Hindu apps | MEDIUM - creates anticipation | Already exists with upcoming festivals |
| Gamification (spiritual currency) | Sri Mandir (Punya Mudras) | MEDIUM - can feel transactional if overdone | Consider "Pooja Points" but keep it subtle |

**Top 5 recommendations for daily return, in priority order:**

1. **Daily Darshan Streak** with diya/flame metaphor (low effort, high impact)
2. **Personalized morning blessing** using user's name, gotra, and today's deity (already have the data, just need the card)
3. **Daily mantra suggestion** linked to Deity of the Day (extends content rotation)
4. **Gentle pooja-time reminder** notification at user's preferred time (already have notification infrastructure)
5. **"Share Today's Blessing"** prominent action on the hero card (viral loop)

---

## 4. Recommended Home Screen Redesign

### Proposed Layout (Top to Bottom)

```
================================================================
TOP APP BAR
  "Subhodayam, [Name]"
  "Your Spiritual Companion"
  [Search] [Settings]
================================================================

--- ABOVE THE FOLD (what users see without scrolling) ---

[1] FESTIVAL GREETING CARD (conditional - only on festival days)
    Full-width celebration card with festival-specific colors
    Appears ABOVE the deity card on festival days only

[2] DEITY OF THE DAY (HERO SECTION) *** NEW PRIORITY ***
    +--------------------------------------------------+
    |                                                  |
    |  [Large Deity Avatar - 96dp+]                    |
    |                                                  |
    |  Somavaram - Monday                              |
    |  Sri Shiva                                   |
    |  శ్రీ శివుడు                                     |
    |                                                  |
    |  "Om Namah Shivaya"                              |
    |  (Today's deity mantra in gold)                  |
    |                                                  |
    |  [12-day streak diya]  [Tap for full darshan >]  |
    |                                                  |
    |  Chaitra Shu. Dashami | Ashwini | 06:12 >        |
    |  (Compact panchangam strip integrated at bottom) |
    +--------------------------------------------------+

[3] TODAY'S SHLOKA / DAILY BLESSING
    +--------------------------------------------------+
    |  "Today's Blessing" (నేటి శ్లోకం)                |
    |                                                  |
    |  [Sanskrit verse in serif/display font]          |
    |  [Telugu meaning]                                |
    |  [English meaning - smaller]                     |
    |                                                  |
    |  [Share]  [Bookmark]  [Listen - if audio avail]  |
    +--------------------------------------------------+

--- BELOW THE FOLD (scroll to see) ---

[4] QUICK ACCESS ROW
    [Aarti] [Japa] [Temples] [Festivals] [Panchangam]
    (Note: Panchangam moves here as a quick-access entry
     instead of being a hero card. Rashifal can go to More.)

[5] BANNER AD

[6] UPCOMING FESTIVALS (LazyRow countdown cards)
    [Ugadi - 3 days] [Ram Navami - 12 days] [...]

[7] ALL DEITIES (LazyRow avatars)
    Horizontal scroll of all deity avatars for exploration

[8] DEVOTIONAL SECTIONS
    [Stotrams] [Keertanalu]
    (Consider adding: [Mantras] [Chalisas])

[9] SANKALPAM (collapsible, for ritual use)
    Collapsed by default, expands for formal ritual recitation

================================================================
```

### What Changed and Why

| Change | Rationale |
|--------|-----------|
| Deity of the Day moves from position 5 to position 2 (hero) | "God should be first" -- aligns with user intent and Sri Mandir's proven pattern |
| Deity card becomes larger with integrated panchangam strip | Single card serves both devotional and informational needs |
| Daily Shloka moves from position 4 to position 3 | Second most important devotional element, stays above fold |
| TeluguCalendarCard removed from home screen | Full panchangam data moves to dedicated Panchangam screen |
| Muhurtam Quick Info removed from home screen | Rahu Kalam / Abhijit details move to Panchangam screen |
| Moon Phase Card removed from home screen | Moves to Panchangam screen |
| Grahanam Banner removed from home screen | Appears on Panchangam screen; notification handles awareness |
| Panchangam becomes a Quick Access icon | Still one tap away, but does not dominate the home screen |
| Darshan streak counter added to deity card | Daily return mechanism with culturally appropriate diya metaphor |
| Recently Viewed removed from home screen | Moves to Profile or a dedicated section |
| Bookmarks removed from home screen | Moves to Profile or bottom nav Favorites tab |

---

## 5. Implementation Priority

### Phase 1: Quick Wins (1-2 days)

1. **Reorder existing items**: Move Deity of the Day card above TeluguCalendarCard and Muhurtam Quick Info. This single change immediately puts "God first" with zero new code.

2. **Move Daily Shloka above panchangam sections**: Shloka should be position 3, right after Deity of the Day.

3. **Collapse Muhurtam Quick Info**: Make it collapsed by default (similar to how Sankalpam currently works).

### Phase 2: Panchangam Compression (2-3 days)

4. **Create PanchangamStrip composable**: A one-line summary (Masa + Tithi + Nakshatra + Sunrise) that replaces the full TeluguCalendarCard on the home screen.

5. **Remove Muhurtam Quick Info from home screen**: Move to Panchangam screen.

6. **Add Panchangam to Quick Access row**: Replace Rashifal (or add a 6th circle) with a Panchangam quick-access icon.

### Phase 3: Enhanced Deity Hero (3-5 days)

7. **Redesign Deity of the Day card**: Larger avatar (96-120dp), integrated daily mantra for the deity, panchangam strip at bottom.

8. **Add personalized blessing text**: Use user's name and gotra in the deity card. "Sri [Name], [Gotra] gotra, today worship [Deity] with [Mantra]."

### Phase 4: Engagement Mechanics (1 week)

9. **Daily darshan streak**: Track app opens in UserPreferencesManager, display diya counter on deity card.

10. **Daily mantra suggestion**: Map each weekday deity to a recommended mantra from existing MantraEntity data.

11. **Remove lower-priority sections from home**: Moon Phase, Grahanam Banner, Recently Viewed, Bookmarks relocate to appropriate screens.

---

## 6. Success Metrics

### Quantitative (measure after 30 days)

| Metric | Current Baseline | Target |
|--------|-----------------|--------|
| DAU / MAU ratio | Measure current | +20% improvement |
| Average session duration | Measure current | +15% improvement |
| Deity detail screen visits from home | Measure current | +40% improvement |
| Panchangam screen visits (should stay same or increase) | Measure current | Maintain or +10% |
| Share button taps on Daily Shloka | Measure current | +30% improvement |
| Scroll depth on home screen | Measure current | More users reaching Quick Access row |

### Qualitative (user feedback)

- Users describe the app as "devotional" or "spiritual" rather than "calendar" or "information"
- Reduced complaints about information overload on home screen
- Increased positive mentions of Deity of the Day feature
- Users report the app feels like "opening a temple" rather than "checking a dashboard"

---

## 7. Risks and Mitigations

| Risk | Mitigation |
|------|------------|
| Power users miss panchangam data on home screen | Panchangam Strip provides essential data; full screen is one tap away via Quick Access |
| Users who relied on Rahu Kalam home visibility | Add optional "Rahu Kalam active" badge/indicator to the Panchangam Strip when Rahu Kalam is currently active (red dot) |
| Streak pressure causing anxiety | Use gentle language; never punish breaks; celebrate returns. "The diya awaits you" not "You lost your streak" |
| Reduced screen sections may feel "empty" | Increase visual richness of remaining sections; deity card should feel premium and immersive |
| Festival greeting + deity card may be too tall on small screens | Festival greeting can overlay/replace the deity card on festival days rather than stacking above it |

---

## 8. Appendix: Screen-by-Screen Content Redistribution

### Home Screen (after redesign)
- Festival Greeting (conditional)
- Deity of the Day (hero, with streak + panchangam strip)
- Daily Shloka
- Quick Access Row
- Banner Ad
- Upcoming Festivals
- All Deities Row
- Devotional Sections
- Sankalpam (collapsed)

### Panchangam Screen (receives relocated content)
- Full TeluguCalendarCard (already exists here)
- Muhurtam Quick Info (Rahu/Abhijit/Sunrise -- relocated from home)
- Moon Phase Card (relocated from home)
- Grahanam information (relocated from home)
- Full Sankalpam (already exists here)

### Profile Screen (receives relocated content)
- Recently Viewed history
- Bookmarks / Favorites
- Darshan streak history and milestones

---

## Sources

- [Sri Mandir UX Audit - The India Notes](https://newsletter.theindianotes.com/p/sri-mandir-ux-audit)
- [Sri Mandir - Apps on Google Play](https://play.google.com/store/apps/details?id=com.mandir&hl=en_IN)
- [10 Million Users And Rising Fast: Sri Mandir's Quest To Put A Temple In Every Phone](https://www.tigerfeathers.in/p/10-million-users-and-rising-fast)
- [Sri Mandir keeps investors hooked as digital devotion grows - TechCrunch](https://techcrunch.com/2025/06/30/sri-mandir-keeps-investors-hooked-as-digital-devotion-grows/)
- [Sri Mandir - TechCrunch Feature](https://techcrunch.com/2024/09/09/sri-mandir-is-on-a-quest-to-digitize-indias-devotional-journey/)
- [How AppsForBharat Grows - The Growth Loop](https://thegrowthloop.beehiiv.com/p/how-appsforbharat-grows)
- [Hindu Calendar - Drik Panchang - Apps on Google Play](https://play.google.com/store/apps/details?id=com.drikp.core&hl=en_US)
- [Drik Panchang Mobile Apps](https://www.drikpanchang.com/apps/apps.html)
- [mymandir - Hindu Pooja App - Google Play](https://play.google.com/store/apps/details?id=com.mymandir&hl=en_IN)
- [Why and How to Develop a Devotional App - Matellio](https://www.matellio.com/blog/why-and-how-to-develop-a-devotional-app/)
- [Shemaroo Bhakti Case Study - Codevian](https://codevian.com/case-study/shemaroo-bhakti-devotional-mobile-app-case-study/)
- [Streaks and Milestones for Gamification - Plotline](https://www.plotline.so/blog/streaks-for-gamification-in-mobile-apps/)
- [Best Daily Devotional Apps - Wegile](https://wegile.com/insights/best-daily-devotional-app.php)
- [Devotional App Design Inspiration - Dribbble](https://dribbble.com/tags/devotional-app)
- [12 Best Apps for Consistent Bible Reading 2026 - FaithTime](https://www.faithtime.ai/content/general/best-apps-for-consistent-bible-reading/)
- [AppsForBharat Growth Strategy](https://businessmodelcanvastemplate.com/blogs/growth-strategy/appsforbharat-growth-strategy)
