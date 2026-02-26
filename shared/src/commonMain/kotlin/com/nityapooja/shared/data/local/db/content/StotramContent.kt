package com.nityapooja.shared.data.local.db.content

/**
 * Full text content for stotrams seeded in the database.
 * Each map is keyed by stotram ID matching [DatabaseSeeder] IDs.
 */
object StotramContent {

    /** Telugu script text keyed by stotram ID. */
    val teluguText: Map<Int, String> = mapOf(

        // ──────────────────────────────────────────────
        // 1 - Vishnu Sahasranamam (first ~30 names/shlokas)
        // ──────────────────────────────────────────────
        1 to """
శుక్లాంబరధరం విష్ణుం శశివర్ణం చతుర్భుజం
ప్రసన్నవదనం ధ్యాయేత్ సర్వవిఘ్నోపశాంతయే

యస్య ద్విరదవక్త్రాద్యాః పారిషద్యాః పరశ్శతం
విఘ్నం నిఘ్నన్తి సతతం విష్వక్సేనం తమాశ్రయే

వ్యాసం వసిష్ఠనప్తారం శక్తేః పౌత్రమకల్మషం
పరాశరాత్మజం వందే శుకతాతం తపోనిధిం

వ్యాసాయ విష్ణురూపాయ వ్యాసరూపాయ విష్ణవే
నమో వై బ్రహ్మనిధయే వాసిష్ఠాయ నమో నమః

అవికారాయ శుద్ధాయ నిత్యాయ పరమాత్మనే
సదైకరూపరూపాయ విష్ణవే సర్వజిష్ణవే

యస్య స్మరణమాత్రేణ జన్మసంసారబంధనాత్
విముచ్యతే నమస్తస్మై విష్ణవే ప్రభవిష్ణవే

ఓం నమో విష్ణవే ప్రభవిష్ణవే
శ్రీ వైశంపాయన ఉవాచ
శ్రుత్వా ధర్మానశేషేణ పావనాని చ సర్వశః
యుధిష్ఠిరః శాంతనవం పునరేవాభ్యభాషత

యుధిష్ఠిర ఉవాచ
కిమేకం దైవతం లోకే కిం వాప్యేకం పరాయణం
స్తువన్తః కం కమర్చన్తః ప్రాప్నుయుర్మానవాః శుభం

కో ధర్మః సర్వధర్మాణాం భవతః పరమో మతః
కిం జపన్ముచ్యతే జన్తుర్జన్మసంసారబంధనాత్

భీష్మ ఉవాచ
జగత్ప్రభుం దేవదేవమనంతం పురుషోత్తమం
స్తువన్నామసహస్రేణ పురుషః సతతోత్థితః

తమేవ చార్చయన్నిత్యం భక్త్యా పురుషమవ్యయం
ధ్యాయన్ స్తువన్నమస్యంశ్చ యజమానస్తమేవ చ

అనాదినిధనం విష్ణుం సర్వలోకమహేశ్వరం
లోకాధ్యక్షం స్తువన్నిత్యం సర్వదుఃఖాతిగో భవేత్

బ్రహ్మణ్యం సర్వధర్మజ్ఞం లోకానాం కీర్తివర్ధనం
లోకనాథం మహద్భూతం సర్వభూతభయంకరం

ఏష మే సర్వధర్మాణాం ధర్మోఽధికతమో మతః
యద్భక్త్యా పుండరీకాక్షం స్తవైరర్చేన్నరః సదా

పరమం యో మహత్తేజః పరమం యో మహత్తపః
పరమం యో మహద్బ్రహ్మ పరమం యః పరాయణం

పవిత్రాణాం పవిత్రం యో మంగళానాం చ మంగళం
దైవతం దేవతానాం చ భూతానాం యోఽవ్యయః పితా

యతః సర్వాణి భూతాని భవంత్యాదియుగాగమే
యస్మింశ్చ ప్రలయం యాంతి పునరేవ యుగక్షయే

తస్య లోకప్రధానస్య జగన్నాథస్య భూపతే
విష్ణోర్నామసహస్రం మే శృణు పాపభయాపహం

యాని నామాని గౌణాని విఖ్యాతాని మహాత్మనః
ఋషిభిః పరిగీతాని తాని వక్ష్యామి భూతయే

విశ్వం విష్ణుర్వషట్కారో భూతభవ్యభవత్ప్రభుః
భూతకృద్భూతభృద్భావో భూతాత్మా భూతభావనః

పూతాత్మా పరమాత్మా చ ముక్తానాం పరమా గతిః
అవ్యయః పురుషః సాక్షీ క్షేత్రజ్ఞోఽక్షర ఏవ చ

యోగో యోగవిదాం నేతా ప్రధానపురుషేశ్వరః
నారసింహవపుః శ్రీమాన్ కేశవః పురుషోత్తమః

సర్వః శర్వః శివః స్థాణుర్భూతాదిర్నిధిరవ్యయః
సంభవో భావనో భర్తా ప్రభవః ప్రభురీశ్వరః

స్వయంభూః శంభురాదిత్యః పుష్కరాక్షో మహాస్వనః
అనాదినిధనో ధాతా విధాతా ధాతురుత్తమః

అప్రమేయో హృషీకేశః పద్మనాభోఽమరప్రభుః
విశ్వకర్మా మనుస్త్వష్టా స్థవిష్ఠః స్థవిరో ధ్రువః

అగ్రాహ్యః శాశ్వతః కృష్ణో లోహితాక్షః ప్రతర్దనః
ప్రభూతస్త్రికకుబ్ధామ పవిత్రం మంగళం పరం

ఈశానః ప్రాణదః ప్రాణో జ్యేష్ఠః శ్రేష్ఠః ప్రజాపతిః
హిరణ్యగర్భో భూగర్భో మాధవో మధుసూదనః

ఈశ్వరో విక్రమీ ధన్వీ మేధావీ విక్రమః క్రమః
అనుత్తమో దురాధర్షః కృతజ్ఞః కృతిరాత్మవాన్

సురేశః శరణం శర్మ విశ్వరేతాః ప్రజాభవః
అహః సంవత్సరో వ్యాళః ప్రత్యయః సర్వదర్శనః
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 2 - Lalita Sahasranamam (first ~30 names/shlokas)
        // ──────────────────────────────────────────────
        2 to """
సింధూరారుణ విగ్రహాం త్రినయనాం మాణిక్యమౌళిస్ఫురత్
తారానాయక శేఖరాం స్మితముఖీం ఆపీనవక్షోరుహాం
పాణిభ్యామళిపూర్ణరత్నచషకం రక్తోత్పలం బిభ్రతీం
సౌమ్యాం రత్నఘటస్థరక్తచరణాం ధ్యాయేత్పరాంబికాం

అరుణాం కరుణాతరంగితాక్షీం
ధృతపాశాంకుశపుష్పబాణచాపాం
అణిమాదిభిరావృతాం మయూఖైః
అహమిత్యేవ విభావయే భవానీం

ధ్యాయేత్ పద్మాసనస్థాం వికసితవదనాం పద్మపత్రాయతాక్షీం
హేమాభాం పీతవస్త్రాం కరకలితలసద్ధేమపద్మాం వరాంగీం
సర్వాలంకారయుక్తాం సతతమభయదాం భక్తనమ్రాం భవానీం
శ్రీవిద్యాం శాంతమూర్తిం సకలసురనుతాం సర్వసంపత్ప్రదాత్రీం

ఓం శ్రీమాతా శ్రీమహారాజ్ఞీ శ్రీమత్సింహాసనేశ్వరీ
చిదగ్నికుండసంభూతా దేవకార్యసముద్యతా

ఉద్యద్భానుసహస్రాభా చతుర్బాహుసమన్వితా
రాగస్వరూపపాశాఢ్యా క్రోధాకారాంకుశోజ్జ్వలా

మనోరూపేక్షుకోదండా పంచతన్మాత్రసాయకా
నిజారుణప్రభాపూరమజ్జద్బ్రహ్మాండమండలా

చంపకాశోకపున్నాగసౌగంధికలసత్కచా
కురువిందమణిశ్రేణీకనత్కోటీరమండితా

అష్టమీచంద్రవిభ్రాజదళికస్థలశోభితా
ముఖచంద్రకళంకాభమృగనాభివిశేషకా

వదనస్మరమాంగళ్యగృహతోరణచిల్లికా
వక్త్రలక్ష్మీపరీవాహచలన్మీనాభలోచనా

నవచంపకపుష్పాభనాసాదండవిరాజితా
తారాకాంతితిరస్కారినాసాభరణభాసురా

కదంబమంజరీక్లుప్తకర్ణపూరమనోహరా
తాటంకయుగళీభూతతపనోడుపమండలా

పద్మరాగశిలాదర్శపరిభావికపోలభూః
నవవిద్రుమబింబశ్రీన్యక్కారిరదనచ్ఛదా

శుద్ధవిద్యాంకురాకారద్విజపంక్తిద్వయోజ్జ్వలా
కర్పూరవీటికామోదసమాకర్షద్దిగంతరా

నిజసల్లాపమాధుర్యవినిర్భర్త్సితకచ్ఛపీ
మందస్మితప్రభాపూరమజ్జత్కామేశమానసా

అనాకలితసాదృశ్యచిబుకశ్రీవిరాజితా
కామేశబద్ధమాంగళ్యసూత్రశోభితకంధరా

కనకాంగదకేయూరకమనీయభుజాన్వితా
రత్నగ్రైవేయచింతాకలోలముక్తాఫలాన్వితా

కామేశ్వరప్రేమరత్నమణిప్రతిపణస్తనీ
నాభ్యాలవాలరోమాళిలతాఫలకుచద్వయీ

లక్ష్యరోమలతాధారతాసమున్నేయమధ్యమా
స్తనభారదళన్మధ్యపట్టబంధవళిత్రయా

అరుణారుణకౌసుంభవస్త్రభాస్వత్కటీతటీ
రత్నకింకిణికారమ్యరశనాదామభూషితా

కామేశజ్ఞాతసౌభాగ్యమార్దవోరుద్వయాన్వితా
మాణిక్యమకుటాకారజానుద్వయవిరాజితా

ఇంద్రగోపపరిక్షిప్తస్మరతూణాభజంఘికా
గూఢగుల్ఫా కూర్మపృష్ఠజయిష్ణుప్రపదాన్వితా

నఖదీధితిసంఛన్ననమజ్జనతమోగుణా
పదద్వయప్రభాజాలపరాకృతసరోరుహా

శింజానమణిమంజీరమండితశ్రీపదాంబుజా
మరాళీమందగమనా మహాలావణ్యశేవధిః

సర్వారుణాఽనవద్యాంగీ సర్వాభరణభూషితా
శివకామేశ్వరాంకస్థా శివా స్వాధీనవల్లభా
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 3 - Aditya Hridayam (complete - 31 verses)
        // ──────────────────────────────────────────────
        3 to """
తతో యుద్ధపరిశ్రాన్తం సమరే చిన్తయా స్థితం
రావణం చాగ్రతో దృష్ట్వా యుద్ధాయ సముపస్థితం

దైవతైశ్చ సమాగమ్య ద్రష్టుమభ్యాగతో రణం
ఉపాగమ్యాబ్రవీద్రామం అగస్త్యో భగవాన్ ఋషిః

రామ రామ మహాబాహో శృణు గుహ్యం సనాతనం
యేన సర్వానరీన్ వత్స సమరే విజయిష్యసి

ఆదిత్యహృదయం పుణ్యం సర్వశత్రువినాశనం
జయావహం జపేన్నిత్యం అక్షయ్యం పరమం శివం

సర్వమంగళమాంగళ్యం సర్వపాపప్రణాశనం
చింతాశోకప్రశమనం ఆయుర్వర్ధనముత్తమం

రశ్మిమంతం సముద్యంతం దేవాసురనమస్కృతం
పూజయస్వ వివస్వంతం భాస్కరం భువనేశ్వరం

సర్వదేవాత్మకో హ్యేష తేజస్వీ రశ్మిభావనః
ఏష దేవాసురగణాన్ లోకాన్ పాతి గభస్తిభిః

ఏష బ్రహ్మా చ విష్ణుశ్చ శివః స్కందః ప్రజాపతిః
మహేంద్రో ధనదః కాలో యమః సోమో హ్యపాం పతిః

పితరో వసవః సాధ్యా హ్యశ్వినౌ మరుతో మనుః
వాయుర్వహ్నిః ప్రజాప్రాణ ఋతుకర్తా ప్రభాకరః

ఆదిత్యః సవితా సూర్యః ఖగః పూషా గభస్తిమాన్
సువర్ణసదృశో భానుర్హిరణ్యరేతా దివాకరః

హరిదశ్వః సహస్రార్చిః సప్తసప్తిర్మరీచిమాన్
తిమిరోన్మథనః శంభుస్త్వష్టా మార్తాండ అంశుమాన్

హిరణ్యగర్భః శిశిరస్తపనో భాస్కరో రవిః
అగ్నిగర్భోఽదితేః పుత్రః శంఖః శిశిరనాశనః

వ్యోమనాథస్తమోభేదీ ఋగ్యజుస్సామపారగః
ఘనవృష్టిరపాం మిత్రో వింధ్యవీథీప్లవంగమః

ఆతపీ మండలీ మృత్యుః పింగళః సర్వతాపనః
కవిర్విశ్వో మహాతేజాః రక్తః సర్వభవోద్భవః

నక్షత్రగ్రహతారాణామధిపో విశ్వభావనః
తేజసామపి తేజస్వీ ద్వాదశాత్మన్నమోఽస్తు తే

నమః పూర్వాయ గిరయే పశ్చిమాయాద్రయే నమః
జ్యోతిర్గణానాం పతయే దినాధిపతయే నమః

జయాయ జయభద్రాయ హర్యశ్వాయ నమో నమః
నమో నమః సహస్రాంశో ఆదిత్యాయ నమో నమః

నమ ఉగ్రాయ వీరాయ సారంగాయ నమో నమః
నమః పద్మప్రబోధాయ మార్తాండాయ నమో నమః

బ్రహ్మేశానాచ్యుతేశాయ సూర్యాయాదిత్యవర్చసే
భాస్వతే సర్వభక్షాయ రౌద్రాయ వపుషే నమః

తమోఘ్నాయ హిమఘ్నాయ శత్రుఘ్నాయామితాత్మనే
కృతఘ్నఘ్నాయ దేవాయ జ్యోతిషాం పతయే నమః

తప్తచామీకరాభాయ వహ్నయే విశ్వకర్మణే
నమస్తమోఽభినిఘ్నాయ రుచయే లోకసాక్షిణే

నాశయత్యేష వై భూతం తదేవ సృజతి ప్రభుః
పాయత్యేష తపత్యేష వర్షత్యేష గభస్తిభిః

ఏష సుప్తేషు జాగర్తి భూతేషు పరినిష్ఠితః
ఏష ఏవాగ్నిహోత్రం చ ఫలం చైవాగ్నిహోత్రిణాం

వేదాశ్చ క్రతవశ్చైవ క్రతూనాం ఫలమేవ చ
యాని కృత్యాని లోకేషు సర్వ ఏష రవిః ప్రభుః

ఏనమాపత్సు కృచ్ఛ్రేషు కాంతారేషు భయేషు చ
కీర్తయన్ పురుషః కశ్చిన్నావసీదతి రాఘవ

పూజయస్వైనమేకాగ్రో దేవదేవం జగత్పతిం
ఏతత్ త్రిగుణితం జప్త్వా యుద్ధేషు విజయిష్యసి

అస్మిన్ క్షణే మహాబాహో రావణం త్వం వధిష్యసి
ఏవముక్త్వా తదాగస్త్యో జగామ చ యథాగతం

ఏతచ్ఛ్రుత్వా మహాతేజాః నష్టశోకోఽభవత్తదా
ధారయామాస సుప్రీతో రాఘవః ప్రయతాత్మవాన్

ఆదిత్యం ప్రేక్ష్య జప్త్వా తు పరం హర్షమవాప్తవాన్
త్రిరాచమ్య శుచిర్భూత్వా ధనురాదాయ వీర్యవాన్

రావణం ప్రేక్ష్య హృష్టాత్మా యుద్ధాయ సముపాగమత్
సర్వయత్నేన మహతా వధే తస్య ధృతోఽభవత్

అథ రవిరవదన్నిరీక్ష్య రామం
ముదితమనాః పరమం ప్రహృష్యమాణః
నిశిచరపతిసంక్షయం విదిత్వా
సురగణమధ్యగతో వచస్త్వరేతి

ఏతద్ఆదిత్యహృదయం యో ధర్మజ్ఞో జపేత్సదా
సర్వశత్రువినాశం చ జయం ప్రాప్నోతి సర్వదా
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 4 - Shiv Tandav Stotram (complete - 16 verses)
        // ──────────────────────────────────────────────
        4 to """
జటాటవీగలజ్జలప్రవాహపావితస్థలే
గలేవలంబ్యలంబితాం భుజంగతుంగమాలికాం
డమడ్డమడ్డమడ్డమన్నినాదవడ్డమర్వయం
చకార చండతాండవం తనోతు నః శివః శివం

జటాకటాహసంభ్రమభ్రమన్నిలింపనిర్ఝరీ
విలోలవీచివల్లరీవిరాజమానమూర్ధని
ధగద్ధగద్ధగజ్జ్వలల్లలాటపట్టపావకే
కిశోరచంద్రశేఖరే రతిః ప్రతిక్షణం మమ

ధరాధరేంద్రనందినీవిలాసబంధుబంధుర
స్ఫురద్దిగంతసంతతిప్రమోదమానమానసే
కృపాకటాక్షధోరణీనిరుద్ధదుర్ధరాపది
క్వచిద్దిగంబరే మనో వినోదమేతు వస్తుని

జటాభుజంగపింగళస్ఫురత్ఫణామణిప్రభా
కదంబకుంకుమద్రవప్రలిప్తదిగ్వధూముఖే
మదాంధసింధురస్ఫురత్త్వగుత్తరీయమేదురే
మనో వినోదమద్భుతం బిభర్తు భూతభర్తరి

సహస్రలోచనప్రభృత్యశేషలేఖశేఖర
ప్రసూనధూళిధోరణీవిధూసరాంఘ్రిపీఠభూః
భుజంగరాజమాలయా నిబద్ధజాటజూటక
శ్రియై చిరాయ జాయతాం చకోరబంధుశేఖరః

లలాటచత్వరజ్వలద్ధనంజయస్ఫులింగభా
నిపీతపంచసాయకం నమన్నిలింపనాయకం
సుధామయూఖలేఖయా విరాజమానశేఖరం
మహాకపాలిసంపదేశిరోజటాలమస్తు నః

కరాళభాలపట్టికాధగద్ధగద్ధగజ్జ్వల
ద్ధనంజయాహుతీకృతప్రచండపంచసాయకే
ధరాధరేంద్రనందినీకుచాగ్రచిత్రపత్రక
ప్రకల్పనైకశిల్పిని త్రిలోచనే రతిర్మమ

నవీనమేఘమండలీనిరుద్ధదుర్ధరస్ఫురత్
కుహూనిశీథినీతమః ప్రబంధబంధుకంధరః
నిలింపనిర్ఝరీధరస్తనోతు కృత్తిసింధురః
కళానిధానబంధురః శ్రియం జగద్ధురంధరః

ప్రఫుల్లనీలపంకజప్రపంచకాలిమప్రభా
వలంబికంఠకందలీరుచిప్రబద్ధకంధరం
స్మరచ్ఛిదం పురచ్ఛిదం భవచ్ఛిదం మఖచ్ఛిదం
గజచ్ఛిదాంధకచ్ఛిదం తమంతకచ్ఛిదం భజే

అఖర్వసర్వమంగళాకళాకదంబమంజరీ
రసప్రవాహమాధురీవిజృంభణామధువ్రతం
స్మరాంతకం పురాంతకం భవాంతకం మఖాంతకం
గజాంతకాంధకాంతకం తమంతకాంతకం భజే

జయత్వదభ్రవిభ్రమభ్రమద్భుజంగమశ్వస
ద్వినిర్గమత్క్రమస్ఫురత్కరాళభాలహవ్యవాట్
ధిమిద్ధిమిద్ధిమిధ్వనన్మృదంగతుంగమంగళ
ధ్వనిక్రమప్రవర్తిత ప్రచండతాండవః శివః

దృషద్విచిత్రతల్పయోర్భుజంగమౌక్తికస్రజో
ర్గరిష్ఠరత్నలోష్ఠయోః సుహృద్విపక్షపక్షయోః
తృణారవిందచక్షుషోః ప్రజామహీమహేంద్రయోః
సమప్రవృత్తికః కదా సదాశివం భజామ్యహం

కదా నిలింపనిర్ఝరీనికుంజకోటరే వసన్
విముక్తదుర్మతిః సదా శిరఃస్థమంజలిం వహన్
విముక్తలోలలోచనో లలామభాలలగ్నకః
శివేతి మంత్రముచ్చరన్ కదా సుఖీ భవామ్యహం

ఇమం హి నిత్యమేవముక్తముత్తమోత్తమం స్తవం
పఠన్స్మరన్ బ్రువన్నరో విశుద్ధిమేతిసంతతం
హరే గురౌ సుభక్తిమాశు యాతి నాన్యథా గతిం
విమోహనం హి దేహినాం సుశంకరస్య చింతనం

పూజావసానసమయే దశవక్త్రగీతం
యః శంభుపూజనపరం పఠతి ప్రదోషే
తస్య స్థిరాం రథగజేంద్రతురంగయుక్తాం
లక్ష్మీం సదైవ సుముఖీం ప్రదదాతి శంభుః

ఇతి శ్రీరావణకృతం
శివతాండవస్తోత్రం సంపూర్ణం
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 5 - Venkateswara Stotram (complete - 10 verses)
        // ──────────────────────────────────────────────
        5 to """
కమలాకుచచూచుక కుంకుమతో
నియతారుణితాతుల నీలతనో
కమలాయతలోచన లోకపతే
విజయీ భవ వేంకటశైలపతే

సచతుర్ముఖషణ్ముఖపంచముఖ
ప్రముఖాఖిలదైవతమౌళిమణే
శరణాగతవత్సల సారనిధే
పరిపాలయ మాం వృషశైలపతే

అతివేలతయా తవ దుర్విషహై
రనువేలకృతైరపరాధశతైః
భరితం త్వరితం వృషశైలపతే
పరయా కృపయా పరిపాహి హరే

అధివేంకటశైలముదారమతేః
జనతాభిమతాధికదానరతాత్
పరదేవతయా గదితాన్నిగమైః
కమలాదయితాన్న పరం కలయే

కళవేణురవావశగోపవధూ
శతకోటివృతాత్స్మరకోటిసమాత్
ప్రతివల్లవికాభిమతాత్సుఖదాత్
వసుదేవసుతాన్న పరం కలయే

అభిరామగుణాకరదాశరథే
జగదేకధనుర్ధరధీరమతే
రఘునాయక రామ రమేశ విభో
వరదో భవ దేవ దయాజలధే

అవినాశమనాశ్రితదేహికదా
పరమేశ్వర పాహి కృపాళ తథా
ధనవాన్ ధనదాధికదానరతా
ధరణీధర ధన్య కృపాం కురు మే

శరణాగత పాహి శరణ్య విభో
పరిపాలయ భక్తజనార్తిహర
హరి సర్వజన ప్రియ దేవ విభో
కురు మామభయం కృపయా తవయా

దినమేవ గృహాణ మమార్చనమీ
ధనమత్ర విభో ధర్మమిత్యపి మే
పురుషోత్తమ పుణ్యచరాచరతే
విజయీ భవ వేంకటశైలపతే

శ్రీవేంకటేశచరణౌ శరణం ప్రపద్యే
శ్రీవేంకటేశచరణౌ శరణం ప్రపద్యే
శ్రీవేంకటేశచరణౌ శరణం ప్రపద్యే
ఓం నమో వేంకటేశాయ నమః
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 6 - Hanuman Chalisa (complete - 40 chaupais + dohas)
        // ──────────────────────────────────────────────
        6 to """
శ్రీ గురు చరణ సరోజ రజ నిజమన ముకుర సుధారి
బరనఉ రఘుబర బిమల జసు జో దాయకు ఫల చారి

బుద్ధిహీన తను జానికే సుమిరౌ పవన కుమార
బల బుద్ధి విద్యా దేహు మోహి హరహు కలేశ వికార

జయ హనుమాన జ్ఞాన గుణ సాగర
జయ కపీశ తిహు లోక ఉజాగర

రామదూత అతులిత బలధామా
అంజని పుత్ర పవనసుత నామా

మహావీర విక్రమ బజరంగీ
కుమతి నివార సుమతి కే సంగీ

కంచన బరన బిరాజ సువేశా
కానన కుండల కుంచిత కేశా

హాథ బజ్ర ఔ ధ్వజా బిరాజే
కాంధే మూంజ జనేవూ సాజే

శంకర సువన కేసరీ నందన
తేజ ప్రతాప మహా జగ బందన

విద్యావాన గుణీ అతి చాతుర
రామ కాజ కరిబే కో ఆతుర

ప్రభు చరిత్ర సునిబే కో రసియా
రామ లఖన సీతా మన బసియా

సూక్ష్మ రూప ధరి సియహిం దిఖావా
వికట రూప ధరి లంక జరావా

భీమ రూప ధరి అసుర సంహారే
రామచంద్ర కే కాజ సవారే

లాయ సజీవన లఖన జియాయే
శ్రీరఘువీర హరషి ఉర లాయే

రఘుపతి కీన్హీ బహుత బడాయీ
తుమ మమ ప్రియ భరతహి సమ భాయీ

సహస బదన తుమ్హరో జస గావై
అస కహి శ్రీపతి కంఠ లగావై

సనకాదిక బ్రహ్మాది మునీశా
నారద శారద సహిత అహీశా

యమ కుబేర దిగపాల జహాం తే
కవి కోవిద కహి సకే కహాం తే

తుమ ఉపకార సుగ్రీవహిం కీన్హా
రామ మిలాయ రాజపద దీన్హా

తుమ్హరో మంత్ర విభీషణ మానా
లంకేశ్వర భయే సబ జగ జానా

యుగ సహస్ర యోజన పర భానూ
లీల్యో తాహి మధుర ఫల జానూ

ప్రభు ముద్రికా మేలి ముఖ మాహీ
జలధి లాంఘి గయే అచరజ నాహీ

దుర్గమ కాజ జగత కే జేతే
సుగమ అనుగ్రహ తుమ్హరే తేతే

రామ దుఆరే తుమ రఖవారే
హోత న ఆజ్ఞా బిను పైసారే

సబ సుఖ లహై తుమ్హారీ శరణా
తుమ రక్షక కాహూ కో డర నా

ఆపన తేజ సంహారో ఆపై
తీనోం లోక హాంక తే కాంపై

భూత పిశాచ నికట నహిం ఆవై
మహావీర జబ నామ సునావై

నాసై రోగ హరై సబ పీరా
జపత నిరంతర హనుమత వీరా

సంకట తే హనుమాన ఛుడావై
మన క్రమ వచన ధ్యాన జో లావై

సబ పర రామ తపస్వీ రాజా
తిన కే కాజ సకల తుమ సాజా

ఔర మనోరథ జో కోయి లావై
సోయి అమిత జీవన ఫల పావై

చారోం యుగ పరతాప తుమ్హారా
హై పరసిద్ధ జగత ఉజియారా

సాధు సంత కే తుమ రఖవారే
అసుర నికందన రామ దులారే

అష్ట సిద్ధి నవ నిధి కే దాతా
అస వర దీన జానకీ మాతా

రామ రసాయన తుమ్హరే పాసా
సదా రహో రఘుపతి కే దాసా

తుమ్హరే భజన రామ కో పావై
జన్మ జన్మ కే దుఖ బిసరావై

అంతకాల రఘుబర పుర జాయీ
జహాం జన్మ హరిభక్త కహాయీ

ఔర దేవతా చిత్త న ధరయీ
హనుమత సేయి సర్వ సుఖ కరయీ

సంకట కటై మిటై సబ పీరా
జో సుమిరై హనుమత బలవీరా

జయ జయ జయ హనుమాన గోసాయీ
కృపా కరహు గురుదేవ కీ నాయీ

జో శత బార పాఠ కర కోయీ
ఛూటహి బంది మహా సుఖ హోయీ

జో యహ పడై హనుమాన చాలీసా
హోయ సిద్ధి సాఖీ గౌరీసా

తులసీదాస సదా హరి చేరా
కీజై నాథ హృదయ మహ డేరా

పవనతనయ సంకట హరన మంగళ మూరతి రూప
రామ లఖన సీతా సహిత హృదయ బసహు సుర భూప
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 7 - Saraswati Stotram (12 verses)
        // ──────────────────────────────────────────────
        7 to """
యా కుందేందు తుషారహారధవళా యా శుభ్రవస్త్రావృతా
యా వీణావరదండమండితకరా యా శ్వేతపద్మాసనా
యా బ్రహ్మాచ్యుతశంకరప్రభృతిభిర్దేవైస్సదా పూజితా
సా మాం పాతు సరస్వతీ భగవతీ నిశ్శేషజాడ్యాపహా

శుక్లాం బ్రహ్మవిచారసారపరమామాద్యాం జగద్వ్యాపినీం
వీణాపుస్తకధారిణీమభయదాం జాడ్యాంధకారాపహాం
హస్తే స్ఫాటికమాలికాం విదధతీం పద్మాసనే సంస్థితాం
వందే తాం పరమేశ్వరీం భగవతీం బుద్ధిప్రదాం శారదాం

ఓం సరస్వతీ మహాభాగే విద్యే కమలలోచనే
విద్యారూపే విశాలాక్షి విద్యాం దేహి నమోఽస్తుతే

యదక్షరపదభ్రష్టం మాత్రాహీనం చ యద్భవేత్
తత్సర్వం క్షమ్యతాం దేవి ప్రసీద పరమేశ్వరి

చతుర్భుజే చంద్రవిలాసహాసే
కుచోన్నతే కుంకుమరాగశోభే
పుణ్యాం లిఖంతీం విదధాతు బుద్ధిం
విద్యాం శ్రియం బ్రహ్మసుతాం నమామి

మహాసరస్వత్యై విద్మహే
అక్షమాలాధృతాయై ధీమహి
తన్నో దేవీ ప్రచోదయాత్

పాతు నో నిత్యం శ్వేతాంబరధారిణీ
విద్యాదాయినీ దేవి నమస్తే శారదాంబికే

జయ జయ దేవి చరాచరసారే
కుచయుగశోభిత ముక్తాహారే
వీణారంజితపుస్తకహస్తే
భగవతి భారతి దేవి నమస్తే

నమస్తే శారదాదేవి కాశ్మీరపురవాసిని
త్వామహం ప్రార్థయే నిత్యం విద్యాదానం చ దేహి మే

వీణాధరే విపులమంగళదానశీలే
భక్తార్తినాశిని విరించి హరీశ వంద్యే
కీర్తిప్రదే ఖిలమనోరథపూరకే తే
పద్మాననే పరమపావని వాగ్దేవతే

సరస్వతి నమస్తుభ్యం వరదే కామరూపిణి
విద్యారంభం కరిష్యామి సిద్ధిర్భవతు మే సదా

సరస్వతీం చ తాం నౌమి వాగ్దేవీం చ సురేశ్వరీం
విద్యాం చ బుద్ధిం చ దదాతు మే సదా
ఓం శ్రీ సరస్వత్యై నమః
""".trimIndent()
    )

    /** English transliteration text keyed by stotram ID. */
    val englishText: Map<Int, String> = mapOf(

        // ──────────────────────────────────────────────
        // 1 - Vishnu Sahasranamam
        // ──────────────────────────────────────────────
        1 to """
Shuklambara Dharam Vishnum Shashi Varnam Chaturbhujam
Prasanna Vadanam Dhyayet Sarva Vighnopa Shantaye

Yasya Dvirada Vaktradyah Parishadyah Parashatam
Vighnam Nighnanti Satatam Vishvaksenam Tamashraye

Vyasam Vasishtha Naptaram Shakteh Pautram Akalmasham
Parasaratmajam Vande Shukatatum Taponidhim

Vyasaya Vishnu Roopaya Vyasa Roopaya Vishnave
Namo Vai Brahma Nidhaye Vasishthaya Namo Namah

Avikaraya Shuddhaya Nityaya Paramatmane
Sadaika Roopa Roopaya Vishnave Sarva Jishnave

Yasya Smaranamatrena Janma Samsara Bandhanat
Vimuchyate Namastasmai Vishnave Prabha Vishnave

Om Namo Vishnave Prabha Vishnave
Sri Vaishampaayana Uvacha
Shrutva Dharman Asheshena Pavanani Cha Sarvashah
Yudhishthirah Shantanavam Punar Evabhya Bhashata

Yudhishthira Uvacha
Kimekam Daivatam Loke Kim Vapyekam Parayanam
Stuvantah Kam Kamarchantah Prapnuyur Manavah Shubham

Ko Dharmah Sarva Dharmanam Bhavatah Paramo Matah
Kim Japan Muchyate Jantur Janma Samsara Bandhanat

Bhishma Uvacha
Jagat Prabhum Deva Devam Anantam Purushottamam
Stuvan Nama Sahasrena Purushah Satatotthitah

Tameva Charchayan Nityam Bhaktya Purusham Avyayam
Dhyayan Stuvan Namasyamshcha Yajamanastameva Cha

Anadi Nidhanam Vishnum Sarva Loka Maheshvaram
Lokadhyaksham Stuvan Nityam Sarva Duhkhatigo Bhavet

Brahmanyam Sarva Dharmajnam Lokanam Keertivardhanam
Lokanatham Mahadbhutam Sarva Bhuta Bhayankaram

Esha Me Sarva Dharmanam Dharmo Dhikatamo Matah
Yad Bhaktya Pundarikaksham Stavair Archen Narah Sada

Paramam Yo Mahat Tejah Paramam Yo Mahat Tapah
Paramam Yo Mahad Brahma Paramam Yah Parayanam

Pavitranaam Pavitram Yo Mangalanam Cha Mangalam
Daivatam Devatanam Cha Bhutanam Yo Avyayah Pita

Yatah Sarvani Bhutani Bhavanty Adi Yugagame
Yasmimshcha Pralayam Yanti Punareva Yugakshaye

Tasya Loka Pradhanasya Jagannathasya Bhupate
Vishnor Nama Sahasram Me Shrunu Papa Bhayapaham

Yani Namani Gaunani Vikhyatani Mahatmanah
Rishibhih Parigitani Tani Vakshyami Bhutaye

Vishvam Vishnur Vashatkaro Bhuta Bhavya Bhavat Prabhuh
Bhutakrid Bhutabhrid Bhavo Bhutatma Bhuta Bhavanah

Putatma Paramatma Cha Muktanam Parama Gatih
Avyayah Purushah Sakshi Kshetrajno Akshara Eva Cha

Yogo Yogavidam Neta Pradhana Purusheshvarah
Narasimha Vapuh Shriman Keshavah Purushottamah

Sarvah Sharvah Shivah Sthanur Bhutadir Nidhir Avyayah
Sambhavo Bhavano Bharta Prabhavah Prabhur Ishvarah

Svayambhuh Shambhur Adityah Pushkarakso Mahasvanah
Anadi Nidhano Dhata Vidhata Dhatur Uttamah

Aprameyo Hrishikesah Padmanabho Amara Prabhuh
Vishvakarma Manus Tvashta Stavishthah Sthaviro Dhruvah

Agrahyah Shashvatah Krishno Lohitakshah Pratardanah
Prabhutastri Kakubdhama Pavitram Mangalam Param

Ishanah Pranadah Prano Jyeshthah Shreshthah Prajapatih
Hiranyagarbho Bhugarbho Madhavo Madhusudanah

Ishvaro Vikrami Dhanvi Medhavi Vikramah Kramah
Anuttamo Duradharshah Kritajnah Kritir Atmavan

Sureshah Sharanam Sharma Vishvaretah Praja Bhavah
Ahah Samvatsaro Vyalah Pratyayah Sarva Darshanah
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 2 - Lalita Sahasranamam
        // ──────────────────────────────────────────────
        2 to """
Sindhooraruna Vigraham Trinayanam Manikya Mauli Sphurat
Tara Nayaka Shekharam Smitamukhim Apina Vakshoruhaam
Panibhyam Alipurna Ratna Chashakam Raktotpalam Bibhratim
Saumyam Ratna Ghatastha Rakta Charanam Dhyayet Parambikam

Arunam Karunataranga Takshim
Dhrita Pashankusha Pushpa Bana Chapam
Animadibhir Avritam Mayukhaih
Aham Ityeva Vibhavaye Bhavanim

Dhyayet Padmasanastham Vikasitavadanam Padma Patrayatakshim
Hemabham Pitavastram Karakalita Lasaddhema Padmam Varangim
Sarvalankara Yuktam Satatam Abhayadam Bhakta Namraam Bhavanim
Srividyam Shanta Murtim Sakala Sura Nutam Sarva Sampatpradatrim

Om Sri Mata Sri Maharajni Srimat Simhasaneshvari
Chidagni Kunda Sambhuta Deva Karya Samudyata

Udyad Bhanu Sahasrabha Chatur Bahu Samanvita
Raga Svaroopa Pashadhya Krodhakaran Kushojjvala

Manorupekshu Kodanda Pancha Tanmatra Sayaka
Nijaruna Prabhapoora Majjad Brahmanda Mandala

Champakashoka Punnaga Saugandhika Lasatkachaa
Kuruvinda Mani Shreni Kanat Kotira Mandita

Ashtami Chandra Vibhrajad Alika Sthala Shobhita
Mukha Chandra Kalankabha Mriganabhi Visheshaka

Vadana Smara Mangalya Griha Torana Chillika
Vaktra Lakshmi Parivaha Chalan Minabha Lochana

Nava Champaka Pushpabha Nasa Danda Virajita
Tarakanti Tiraskari Nasabharana Bhasura

Kadamba Manjari Klupta Karna Pura Manohara
Tatanka Yugali Bhuta Tapanodupa Mandala

Padmaraga Shiladarsha Paribhavi Kapolabhuh
Nava Vidruma Bimba Shri Nyakkari Radanacchada

Shuddha Vidyankurakara Dvija Pankti Dvayojjvala
Karpura Vitikamoda Samakarshad Digantara

Nija Sallapa Madhurya Vinirbhartsita Kachchhapi
Manda Smita Prabhapoora Majjat Kamesha Manasa

Anakalita Sadrishya Chibuka Shri Virajita
Kamesha Baddha Mangalya Sutra Shobhita Kandhara

Kanakangada Keyura Kamaniya Bhujanvita
Ratna Graiveya Chintaka Lola Muktaphalanvita

Kameshvara Prema Ratna Mani Pratipanastani
Nabhyalavala Romali Lataphala Kuchadvayee

Lakshya Roma Latadhara Tasam Unneya Madhyama
Stanabhara Dalan Madhya Pattabandha Valitrayaa

Arunaruna Kausumbha Vastra Bhasvat Katitati
Ratna Kinkinika Ramya Rashana Dama Bhushita

Kamesha Jnata Saubhagya Mardavoru Dvayanvita
Manikya Makutakara Janu Dvaya Virajita

Indragopa Parikshipta Smara Tunabha Janghika
Gudha Gulpha Kurma Prishtha Jayishnu Prapadanvita

Nakha Didhiti Sanchhanna Namajjana Tamoguna
Pada Dvaya Prabhajala Parakrita Saroruha

Shinjana Mani Manjira Mandita Sri Padambuja
Marali Manda Gamana Maha Lavanya Shevadhih

Sarvaruna Anavadyangi Sarvabharana Bhushita
Shiva Kameshvarankastha Shiva Svadhina Vallabha
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 3 - Aditya Hridayam (complete)
        // ──────────────────────────────────────────────
        3 to """
Tato Yuddha Parishrantam Samare Chintaya Sthitam
Ravanam Chagrato Drshtva Yuddhaya Samupasthitam

Daivataischa Samagamya Drashtum Abhyagato Ranam
Upagamya Abravid Ramam Agastyo Bhagavan Riship

Rama Rama Mahabaho Shrunu Guhyam Sanatanam
Yena Sarvanarin Vatsa Samare Vijayishyasi

Aditya Hridayam Punyam Sarva Shatru Vinashanam
Jayavaham Japen Nityam Akshayam Paramam Shivam

Sarva Mangala Mangalyam Sarva Papa Pranashanam
Chinta Shoka Prashamanam Ayur Vardhanam Uttamam

Rashmimantam Samudyantam Devasura Namaskritam
Pujayasva Vivasvantam Bhaskaram Bhuvaneshvaram

Sarva Devatmako Hyesha Tejaswi Rashmi Bhavanah
Esha Devasura Ganan Lokan Pati Gabhasthibhih

Esha Brahma Cha Vishnushcha Shivah Skandah Prajapatih
Mahendro Dhanadah Kalo Yamah Somo Hyapam Patih

Pitaro Vasavah Sadhya Hyashvinau Maruto Manuh
Vayur Vahnih Praja Prana Ritukarta Prabhakarah

Adityah Savita Suryah Khagah Pusha Gabhasthiman
Suvarna Sadrisho Bhanur Hiranyareta Divakarah

Haridashvah Sahasrarchih Sapta Saptir Marichiman
Timironmathanah Shambhus Tvashta Martanda Anshuman

Hiranyagarbhah Shishiras Tapano Bhaskaro Ravih
Agnigarbho Aditeh Putrah Shankhah Shishira Nashanah

Vyomanathas Tamobhedi Rig Yajus Sama Paragah
Ghana Vrishtirapam Mitro Vindhya Vithiplavan Gamah

Atapi Mandali Mrityuh Pingalah Sarvatapanah
Kavir Vishvo Mahatejah Raktah Sarvabhavodbhavah

Nakshatra Graha Taranam Adhipo Vishva Bhavanah
Tejasam Api Tejasvi Dvadashatman Namostu Te

Namah Purvaya Giraye Pashchimaya Adraye Namah
Jyotirgananam Pataye Dinadhipataye Namah

Jayaya Jayabhadraya Haryashvaya Namo Namah
Namo Namah Sahasramsho Adityaya Namo Namah

Nama Ugraya Viraya Sarangaya Namo Namah
Namah Padma Prabodhaya Martandaya Namo Namah

Brahmeshanachyuteshaya Suryaya Aditya Varchase
Bhasvate Sarva Bhakshaya Raudraya Vapushe Namah

Tamoghnaya Himaghnaya Shatrughnaya Amitatmane
Kritaghna Ghnaya Devaya Jyotisham Pataye Namah

Tapta Chamikara Abhaya Vahnaye Vishva Karmane
Namastamo Abhinighnaya Ruchaye Loka Sakshine

Nashayatyesha Vai Bhutam Tadeva Srijati Prabhuh
Payatyesha Tapatyesha Varshaty Esha Gabhasthibhih

Esha Supteshu Jagarti Bhuteshu Parinishtitah
Esha Evagnihotram Cha Phalam Chaivagni Hotrinam

Vedashcha Kratavashchaiva Kratunam Phalam Eva Cha
Yani Krityani Lokeshu Sarva Esha Ravih Prabhuh

Enam Apatsu Krichchhreshu Kantareshu Bhayeshu Cha
Kirtayan Purushah Kashchin Navasidati Raghava

Pujayasva Enam Ekagro Devadevan Jagatpatim
Etat Trigunitam Japtva Yuddhashu Vijayishyasi

Asmin Kshane Mahabaho Ravanam Tvam Vadhishyasi
Evam Uktva Tada Agastyo Jagama Cha Yathagatam

Etach Chrutva Mahatejah Nashta Shoko Abhavat Tada
Dharayamasa Suprito Raghavah Prayatatmavan

Adityam Prekshya Japtva Tu Param Harsham Avaptavan
Trirachamya Shuchir Bhutva Dhanuradaya Viryavan

Ravanam Prekshya Hrishtatma Yuddhaya Samupagamat
Sarva Yatnena Mahata Vadhe Tasya Dhrito Abhavat

Atha Ravir Avadan Nirikshya Ramam
Muditamanah Paramam Prahrushyamanah
Nishichara Pati Sankshayam Viditva
Sura Gana Madhyagato Vachastvareti

Etad Aditya Hridayam Yo Dharmajno Japet Sada
Sarva Shatru Vinasham Cha Jayam Prapnoti Sarvada
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 4 - Shiv Tandav Stotram (complete)
        // ──────────────────────────────────────────────
        4 to """
Jatatavi Galajjala Pravaha Pavita Sthale
Gale Avalambya Lambitam Bhujanga Tunga Malikam
Damad Damad Damad Daman Ninada Vadda Marvayam
Chakara Chanda Tandavam Tanotu Nah Shivah Shivam

Jata Kata Ha Sambhrama Bhraman Nilimpa Nirjhari
Vilola Vichi Vallari Viraja Mana Murdhani
Dhagad Dhagad Dhagaj Jvalal Lalata Patta Pavake
Kishora Chandra Shekhare Ratih Pratikshan Mama

Dharadharendra Nandini Vilasa Bandhu Bandhura
Sphurad Diganta Santati Pramodamana Manase
Kripa Kataksha Dhorani Niruddha Durdharapadi
Kvachid Digambare Mano Vinodam Etu Vastuni

Jata Bhujanga Pingala Sphurat Phana Mani Prabha
Kadamba Kumkuma Drava Pralipta Digvadhu Mukhe
Madandha Sindhura Sphurat Tvag Uttariya Medure
Mano Vinodam Adbhutam Bibhartu Bhuta Bhartari

Sahasra Lochana Prabhritya Shesha Lekha Shekhara
Prasoona Dhuli Dhorani Vidhusaranghri Pithabhuh
Bhujanga Raja Malaya Nibaddha Jata Jutaka
Shriyai Chiraya Jayatam Chakora Bandhu Shekharah

Lalata Chatvara Jvalad Dhananjaya Sphulingabha
Nipita Pancha Sayakam Naman Nilimpa Nayakam
Sudha Mayukha Lekhaya Virajamana Shekharam
Maha Kapali Sampadeshiro Jatala Mastu Nah

Karala Bhala Pattika Dhagad Dhagad Dhagaj Jvala
Ddhananjaya Ahuti Krita Prachanda Pancha Sayake
Dharadharendra Nandini Kuchagra Chitra Patraka
Prakalpanaika Shilpini Trilochane Ratir Mama

Navina Megha Mandali Niruddha Durdhara Sphurat
Kuhu Nishithinee Tamah Prabandha Bandhu Kandharah
Nilimpa Nirjhari Dharas Tanotu Kritti Sindhurah
Kalanidhanabandhurah Shriyam Jagad Dhurandharah

Praphulla Nila Pankaja Prapancha Kalima Prabha
Valambi Kantha Kandali Ruchi Prabaddha Kandharam
Smarachchhidam Purachchhidam Bhavachchhidam Makhachchhidam
Gajachchhidandhakachchhidam Tamantakachchhidam Bhaje

Akharva Sarva Mangala Kala Kadamba Manjari
Rasa Pravaha Madhuri Vijrimbhana Madhu Vratam
Smarantakam Purantakam Bhavantakam Makhantakam
Gajantakandha Kantakam Tamantakantakam Bhaje

Jayatvadabhra Vibhrama Bhramad Bhujangama Shvasa
Dvinirgamat Krama Sphurat Karala Bhala Havyavat
Dhimid Dhimid Dhimidhvanan Mridanga Tunga Mangala
Dhvani Krama Pravartita Prachanda Tandavah Shivah

Drishad Vichitra Talpayo Rbhujanga Mauktika Srajo
Rgarishtha Ratna Loshthayoh Suhrid Vipaksha Pakshayoh
Trina Aravinda Chakshushoh Prajamahi Mahendrayoh
Sama Pravrittikah Kada Sadashivam Bhajamyaham

Kada Nilimpa Nirjhari Nikunja Kotare Vasan
Vimukta Durmatih Sada Shirah Sthamanjali Vahan
Vimukta Lola Lochano Lalamabhala Lagnakah
Shiveti Mantram Uchcharan Kada Sukhi Bhavamyaham

Imam Hi Nityam Evam Uktam Uttamottamam Stavam
Patan Smaran Bruvan Naro Vishuddhim Eti Santatam
Hare Gurau Subhaktim Ashu Yati Nanyatha Gatim
Vimohanam Hi Dehinam Sushankarasya Chintanam

Pujavasana Samaye Dasha Vaktra Gitam
Yah Shambhu Pujana Param Pathati Pradoshe
Tasya Sthiram Ratha Gajendra Turanga Yuktam
Lakshmim Sadaiva Sumukhim Pradadati Shambhuh

Iti Sri Ravana Kritam
Shiva Tandava Stotram Sampurnam
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 5 - Venkateswara Stotram (complete)
        // ──────────────────────────────────────────────
        5 to """
Kamalakucha Choochuka Kumkumato
Niyata Arunitatula Neela Tano
Kamalayata Lochana Lokapate
Vijayi Bhava Venkatashailapate

Sachathurmukha Shanmukha Panchamukha
Pramukhakhila Daivata Maulimane
Sharanaagata Vatsala Saranidhe
Paripalaya Mam Vrishashailapate

Ativelathaya Tava Durvishahair
Anuvela Kritair Aparadha Shataih
Bharitam Tvaritam Vrishashailapate
Paraya Kripaya Paripahi Hare

Adhivenkata Shaila Mudaramateh
Janata Abhimata Adhika Danaratat
Paradevathaya Gaditaan Nigamaih
Kamaladayitan Na Param Kalaye

Kalavenu Ravavasha Gopadhavu
Shatakooti Vritatsmara Kootisamat
Prativallavi Kabhimatat Sukhadat
Vasudevasutan Na Param Kalaye

Abhirama Gunakara Dasharathe
Jagadeka Dhanurdhara Dheeramate
Raghunayaka Rama Ramesha Vibho
Varado Bhava Deva Dayajaladhe

Avinasham Anashrita Dehikada
Parameshvara Pahi Kripala Tatha
Dhanavan Dhanadaadhika Danarata
Dharaneedhara Dhanya Kripam Kuru Me

Sharanagata Pahi Sharanya Vibho
Paripalaya Bhakta Janarti Hara
Hari Sarva Jana Priya Deva Vibho
Kuru Mam Abhayam Kripaya Tavaya

Dinameva Grihana Mamarchanamee
Dhanam Atra Vibho Dharmam Ityapi Me
Purushottama Punya Characharate
Vijayi Bhava Venkatashailapate

Sri Venkatesha Charanau Sharanam Prapadye
Sri Venkatesha Charanau Sharanam Prapadye
Sri Venkatesha Charanau Sharanam Prapadye
Om Namo Venkateshaaya Namah
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 6 - Hanuman Chalisa (complete)
        // ──────────────────────────────────────────────
        6 to """
Shri Guru Charan Saroj Raj Nij Man Mukur Sudhari
Barnau Raghubar Bimal Jasu Jo Dayaku Phal Chari

Buddhihina Tanu Janike Sumirau Pavan Kumar
Bal Buddhi Vidya Dehu Mohi Harahu Kalesh Vikar

Jai Hanuman Gyan Gun Sagar
Jai Kapish Tihun Lok Ujagar

Ram Doot Atulit Bal Dhama
Anjani Putra Pavan Sut Nama

Mahavir Vikram Bajrangi
Kumati Nivar Sumati Ke Sangi

Kanchan Baran Biraj Subesa
Kanan Kundal Kunchit Kesha

Hath Bajra Aur Dhvaja Biraje
Kandhe Moonj Janevu Saje

Shankar Suvan Kesari Nandan
Tej Pratap Maha Jag Vandan

Vidyavan Guni Ati Chatur
Ram Kaj Karibe Ko Atur

Prabhu Charitra Sunibe Ko Rasiya
Ram Lakhan Sita Man Basiya

Sukshma Roop Dhari Siyahi Dikhawa
Vikat Roop Dhari Lanka Jarava

Bhim Roop Dhari Asur Sanhare
Ramachandra Ke Kaj Savare

Laye Sajivan Lakhan Jiyaye
Shri Raghuvir Harashi Ur Laye

Raghupati Kinhi Bahut Badai
Tum Mam Priya Bharatahi Sam Bhai

Sahas Badan Tumharo Jas Gavai
As Kahi Shripati Kanth Lagavai

Sanakadik Brahmadi Munisha
Narad Sharad Sahit Ahisha

Yam Kuber Digpal Jahan Te
Kavi Kovid Kahi Sake Kahan Te

Tum Upkar Sugrivahi Kinha
Ram Milaye Rajpad Dinha

Tumharo Mantra Vibhishan Mana
Lankeshvar Bhaye Sab Jag Jana

Yug Sahastra Yojan Par Bhanu
Lilyo Tahi Madhur Phal Janu

Prabhu Mudrika Meli Mukh Mahi
Jaladhi Langhi Gaye Acharaj Nahi

Durgam Kaj Jagat Ke Jete
Sugam Anugraha Tumhare Tete

Ram Duare Tum Rakhavare
Hot Na Ajna Binu Paisare

Sab Sukh Lahai Tumhari Sharna
Tum Rakshak Kahu Ko Dar Na

Apan Tej Samharo Apai
Tinon Lok Hank Te Kanpai

Bhoot Pishach Nikat Nahin Avai
Mahavir Jab Nam Sunavai

Nasei Rog Harai Sab Pira
Japat Nirantar Hanumat Vira

Sankat Te Hanuman Chhudavai
Man Kram Vachan Dhyan Jo Lavai

Sab Par Ram Tapasvi Raja
Tinke Kaj Sakal Tum Saja

Aur Manorath Jo Koi Lave
Soi Amit Jivan Phal Pave

Charon Yug Partap Tumhara
Hai Parsiddh Jagat Ujiyara

Sadhu Sant Ke Tum Rakhvare
Asur Nikandan Ram Dulare

Ashta Siddhi Nav Nidhi Ke Data
As Var Din Janki Mata

Ram Rasayan Tumhare Pasa
Sada Raho Raghupati Ke Dasa

Tumhare Bhajan Ram Ko Pavai
Janam Janam Ke Dukh Bisravai

Antakal Raghubar Pur Jayi
Jahan Janma Haribhakt Kahayi

Aur Devta Chitt Na Dharayi
Hanumat Sei Sarva Sukh Karayi

Sankat Kate Mite Sab Pira
Jo Sumirai Hanumat Balvira

Jai Jai Jai Hanuman Gosai
Kripa Karahu Gurudev Ki Nayi

Jo Sat Bar Path Kar Koi
Chhutahi Bandi Maha Sukh Hoi

Jo Yah Padhe Hanuman Chalisa
Hoy Siddhi Sakhi Gaurisa

Tulsidas Sada Hari Chera
Kije Nath Hridaya Mah Dera

Pavan Tanay Sankat Haran Mangal Murati Roop
Ram Lakhan Sita Sahit Hridaya Basahu Sur Bhup
""".trimIndent(),

        // ──────────────────────────────────────────────
        // 7 - Saraswati Stotram
        // ──────────────────────────────────────────────
        7 to """
Ya Kundendu Tusharahara Dhavala Ya Shubhra Vastravrita
Ya Veena Varadanda Mandita Kara Ya Shweta Padmasana
Ya Brahmachyuta Shankara Prabhritibhir Devais Sada Pujita
Sa Mam Patu Saraswati Bhagavati Nihshesha Jadyapaha

Shuklam Brahma Vichara Sara Paramam Adyam Jagad Vyapineem
Veena Pustaka Dharinim Abhaya Dam Jadyandhakarapaham
Haste Sphatika Malikam Vidadhatim Padmasane Samsthitam
Vande Tam Parameshvarim Bhagavatim Buddhipradam Sharadam

Om Saraswati Mahabhage Vidye Kamalalochane
Vidya Roope Vishalakshi Vidyam Dehi Namostute

Yad Akshara Pada Bhrashtam Matraheenam Cha Yad Bhavet
Tat Sarvam Kshamyatam Devi Prasida Parameshvari

Chaturbhuje Chandra Vilasa Hase
Kuchonnate Kumkuma Raga Shobhe
Punyam Likhantim Vidadhatu Buddhim
Vidyam Shriyam Brahma Sutam Namami

Maha Sarasvatyai Vidmahe
Akshamala Dhritayai Dheemahi
Tanno Devi Prachodayat

Patu No Nityam Shvetambara Dharinim
Vidya Dayini Devi Namaste Sharadambike

Jaya Jaya Devi Charachara Sare
Kuchayuga Shobhita Muktahare
Veena Ranjita Pustaka Haste
Bhagavati Bharati Devi Namaste

Namaste Sharada Devi Kashmira Pura Vasini
Tvam Aham Prarthaye Nityam Vidya Danam Cha Dehi Me

Veenadahare Vipula Mangala Dana Sheele
Bhaktarti Nashini Virinchi Harisha Vandye
Kirti Prade Khila Manoratha Purake Te
Padmanane Parama Pavani Vagdevate

Saraswati Namastubhyam Varade Kamarupini
Vidyarambham Karishyami Siddhir Bhavatu Me Sada

Saraswatim Cha Tam Naumi Vagdevim Cha Sureshvarim
Vidyam Cha Buddhim Cha Dadatu Me Sada
Om Shri Saraswatyai Namah
""".trimIndent()
    )
}
