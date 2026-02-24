package com.nityapooja.app.data.model

data class IndianCity(
    val name: String,
    val nameTelugu: String,
    val lat: Double,
    val lng: Double,
    val state: String,
    val timezone: String = "Asia/Kolkata",
)

val indianCities = listOf(
    // ═══ India — Telangana ═══
    IndianCity("Hyderabad", "హైదరాబాద్", 17.385, 78.4867, "Telangana"),
    IndianCity("Warangal", "వరంగల్", 17.9784, 79.5941, "Telangana"),
    IndianCity("Nizamabad", "నిజామాబాద్", 18.6725, 78.0940, "Telangana"),
    IndianCity("Karimnagar", "కరీంనగర్", 18.4386, 79.1288, "Telangana"),
    IndianCity("Khammam", "ఖమ్మం", 17.2473, 80.1514, "Telangana"),
    IndianCity("Mahbubnagar", "మహబూబ్‌నగర్", 16.7488, 77.9853, "Telangana"),

    // ═══ India — Andhra Pradesh ═══
    IndianCity("Visakhapatnam", "విశాఖపట్నం", 17.6868, 83.2185, "Andhra Pradesh"),
    IndianCity("Vijayawada", "విజయవాడ", 16.5062, 80.6480, "Andhra Pradesh"),
    IndianCity("Tirupati", "తిరుపతి", 13.6288, 79.4192, "Andhra Pradesh"),
    IndianCity("Guntur", "గుంటూరు", 16.3067, 80.4365, "Andhra Pradesh"),
    IndianCity("Nellore", "నెల్లూరు", 14.4426, 79.9865, "Andhra Pradesh"),
    IndianCity("Kurnool", "కర్నూలు", 15.8281, 78.0373, "Andhra Pradesh"),
    IndianCity("Rajahmundry", "రాజమహేంద్రవరం", 17.0005, 81.8040, "Andhra Pradesh"),
    IndianCity("Kakinada", "కాకినాడ", 16.9891, 82.2475, "Andhra Pradesh"),
    IndianCity("Anantapur", "అనంతపురం", 14.6819, 77.6006, "Andhra Pradesh"),
    IndianCity("Kadapa", "కడప", 14.4674, 78.8241, "Andhra Pradesh"),
    IndianCity("Ongole", "ఒంగోలు", 15.5057, 80.0499, "Andhra Pradesh"),
    IndianCity("Srikakulam", "శ్రీకాకుళం", 18.2949, 83.8938, "Andhra Pradesh"),
    IndianCity("Eluru", "ఏలూరు", 16.7107, 81.0952, "Andhra Pradesh"),

    // ═══ India — Other States ═══
    IndianCity("Chennai", "చెన్నై", 13.0827, 80.2707, "Tamil Nadu"),
    IndianCity("Bangalore", "బెంగళూరు", 12.9716, 77.5946, "Karnataka"),
    IndianCity("Mumbai", "ముంబై", 19.0760, 72.8777, "Maharashtra"),
    IndianCity("Delhi", "ఢిల్లీ", 28.7041, 77.1025, "Delhi"),
    IndianCity("Kolkata", "కోల్‌కతా", 22.5726, 88.3639, "West Bengal"),
    IndianCity("Pune", "పూణే", 18.5204, 73.8567, "Maharashtra"),
    IndianCity("Ahmedabad", "అహ్మదాబాద్", 23.0225, 72.5714, "Gujarat"),
    IndianCity("Jaipur", "జయపూర్", 26.9124, 75.7873, "Rajasthan"),
    IndianCity("Lucknow", "లక్నో", 26.8467, 80.9462, "Uttar Pradesh"),
    IndianCity("Bhopal", "భోపాల్", 23.2599, 77.4126, "Madhya Pradesh"),
    IndianCity("Varanasi", "వారణాసి", 25.3176, 82.9739, "Uttar Pradesh"),
    IndianCity("Patna", "పాట్నా", 25.6093, 85.1376, "Bihar"),
    IndianCity("Indore", "ఇండోర్", 22.7196, 75.8577, "Madhya Pradesh"),
    IndianCity("Nagpur", "నాగపూర్", 21.1458, 79.0882, "Maharashtra"),
    IndianCity("Coimbatore", "కోయంబత్తూరు", 11.0168, 76.9558, "Tamil Nadu"),
    IndianCity("Madurai", "మధురై", 9.9252, 78.1198, "Tamil Nadu"),
    IndianCity("Thiruvananthapuram", "తిరువనంతపురం", 8.5241, 76.9366, "Kerala"),
    IndianCity("Kochi", "కొచ్చి", 9.9312, 76.2673, "Kerala"),
    IndianCity("Bhubaneswar", "భువనేశ్వర్", 20.2961, 85.8245, "Odisha"),
    IndianCity("Mangalore", "మంగళూరు", 12.9141, 74.8560, "Karnataka"),
    IndianCity("Mysore", "మైసూరు", 12.2958, 76.6394, "Karnataka"),

    // ═══ South Asia ═══
    IndianCity("Colombo", "", 6.9271, 79.8612, "Sri Lanka", "Asia/Colombo"),
    IndianCity("Kathmandu", "", 27.7172, 85.3240, "Nepal", "Asia/Kathmandu"),
    IndianCity("Dhaka", "", 23.8103, 90.4125, "Bangladesh", "Asia/Dhaka"),
    IndianCity("Karachi", "", 24.8607, 67.0011, "Pakistan", "Asia/Karachi"),
    IndianCity("Lahore", "", 31.5204, 74.3587, "Pakistan", "Asia/Karachi"),

    // ═══ Southeast Asia ═══
    IndianCity("Singapore", "", 1.3521, 103.8198, "Singapore", "Asia/Singapore"),
    IndianCity("Kuala Lumpur", "", 3.1390, 101.6869, "Malaysia", "Asia/Kuala_Lumpur"),
    IndianCity("Bangkok", "", 13.7563, 100.5018, "Thailand", "Asia/Bangkok"),
    IndianCity("Jakarta", "", -6.2088, 106.8456, "Indonesia", "Asia/Jakarta"),

    // ═══ East Asia ═══
    IndianCity("Tokyo", "", 35.6762, 139.6503, "Japan", "Asia/Tokyo"),
    IndianCity("Seoul", "", 37.5665, 126.9780, "South Korea", "Asia/Seoul"),
    IndianCity("Hong Kong", "", 22.3193, 114.1694, "China", "Asia/Hong_Kong"),
    IndianCity("Taipei", "", 25.0330, 121.5654, "Taiwan", "Asia/Taipei"),

    // ═══ Middle East ═══
    IndianCity("Dubai", "", 25.2048, 55.2708, "UAE", "Asia/Dubai"),
    IndianCity("Doha", "", 25.2854, 51.5310, "Qatar", "Asia/Qatar"),
    IndianCity("Muscat", "", 23.5880, 58.3829, "Oman", "Asia/Muscat"),
    IndianCity("Riyadh", "", 24.7136, 46.6753, "Saudi Arabia", "Asia/Riyadh"),
    IndianCity("Kuwait City", "", 29.3759, 47.9774, "Kuwait", "Asia/Kuwait"),
    IndianCity("Bahrain", "", 26.0667, 50.5577, "Bahrain", "Asia/Bahrain"),

    // ═══ Europe ═══
    IndianCity("London", "", 51.5074, -0.1278, "United Kingdom", "Europe/London"),
    IndianCity("Paris", "", 48.8566, 2.3522, "France", "Europe/Paris"),
    IndianCity("Berlin", "", 52.5200, 13.4050, "Germany", "Europe/Berlin"),
    IndianCity("Amsterdam", "", 52.3676, 4.9041, "Netherlands", "Europe/Amsterdam"),
    IndianCity("Frankfurt", "", 50.1109, 8.6821, "Germany", "Europe/Berlin"),
    IndianCity("Zurich", "", 47.3769, 8.5417, "Switzerland", "Europe/Zurich"),

    // ═══ North America — USA (East) ═══
    IndianCity("New York", "", 40.7128, -74.0060, "USA", "America/New_York"),
    IndianCity("Boston", "", 42.3601, -71.0589, "USA", "America/New_York"),
    IndianCity("Philadelphia", "", 39.9526, -75.1652, "USA", "America/New_York"),
    IndianCity("Washington DC", "", 38.9072, -77.0369, "USA", "America/New_York"),
    IndianCity("Miami", "", 25.7617, -80.1918, "USA", "America/New_York"),
    IndianCity("Atlanta", "", 33.7490, -84.3880, "USA", "America/New_York"),
    IndianCity("Charlotte", "", 35.2271, -80.8431, "USA", "America/New_York"),
    IndianCity("Orlando", "", 28.5383, -81.3792, "USA", "America/New_York"),
    IndianCity("Tampa", "", 27.9506, -82.4572, "USA", "America/New_York"),
    IndianCity("Raleigh", "", 35.7796, -78.6382, "USA", "America/New_York"),
    IndianCity("Baltimore", "", 39.2904, -76.6122, "USA", "America/New_York"),
    IndianCity("Pittsburgh", "", 40.4406, -79.9959, "USA", "America/New_York"),
    IndianCity("Newark", "", 40.7357, -74.1724, "USA", "America/New_York"),
    IndianCity("Jersey City", "", 40.7178, -74.0431, "USA", "America/New_York"),
    IndianCity("Richmond", "", 37.5407, -77.4360, "USA", "America/New_York"),
    IndianCity("Buffalo", "", 42.8864, -78.8784, "USA", "America/New_York"),
    IndianCity("Hartford", "", 41.7658, -72.6734, "USA", "America/New_York"),
    IndianCity("Columbus", "", 39.9612, -82.9988, "USA", "America/New_York"),
    IndianCity("Cincinnati", "", 39.1031, -84.5120, "USA", "America/New_York"),
    IndianCity("Cleveland", "", 41.4993, -81.6944, "USA", "America/New_York"),

    // ═══ North America — USA (Central) ═══
    IndianCity("Chicago", "", 41.8781, -87.6298, "USA", "America/Chicago"),
    IndianCity("Houston", "", 29.7604, -95.3698, "USA", "America/Chicago"),
    IndianCity("Dallas", "", 32.7767, -96.7970, "USA", "America/Chicago"),
    IndianCity("Austin", "", 30.2672, -97.7431, "USA", "America/Chicago"),
    IndianCity("San Antonio", "", 29.4241, -98.4936, "USA", "America/Chicago"),
    IndianCity("Minneapolis", "", 44.9778, -93.2650, "USA", "America/Chicago"),
    IndianCity("Nashville", "", 36.1627, -86.7816, "USA", "America/Chicago"),
    IndianCity("Indianapolis", "", 39.7684, -86.1581, "USA", "America/Indiana/Indianapolis"),
    IndianCity("Kansas City", "", 39.0997, -94.5786, "USA", "America/Chicago"),
    IndianCity("Memphis", "", 35.1495, -90.0490, "USA", "America/Chicago"),
    IndianCity("Milwaukee", "", 43.0389, -87.9065, "USA", "America/Chicago"),
    IndianCity("St. Louis", "", 38.6270, -90.1994, "USA", "America/Chicago"),
    IndianCity("Irving", "", 32.8140, -96.9489, "USA", "America/Chicago"),
    IndianCity("Plano", "", 33.0198, -96.6989, "USA", "America/Chicago"),
    IndianCity("Frisco", "", 33.1507, -96.8236, "USA", "America/Chicago"),

    // ═══ North America — USA (Mountain) ═══
    IndianCity("Denver", "", 39.7392, -104.9903, "USA", "America/Denver"),
    IndianCity("Phoenix", "", 33.4484, -112.0740, "USA", "America/Phoenix"),
    IndianCity("Salt Lake City", "", 40.7608, -111.8910, "USA", "America/Denver"),

    // ═══ North America — USA (Pacific) ═══
    IndianCity("Los Angeles", "", 34.0522, -118.2437, "USA", "America/Los_Angeles"),
    IndianCity("San Francisco", "", 37.7749, -122.4194, "USA", "America/Los_Angeles"),
    IndianCity("Seattle", "", 47.6062, -122.3321, "USA", "America/Los_Angeles"),
    IndianCity("San Jose", "", 37.3382, -121.8863, "USA", "America/Los_Angeles"),
    IndianCity("Portland", "", 45.5155, -122.6789, "USA", "America/Los_Angeles"),
    IndianCity("Las Vegas", "", 36.1699, -115.1398, "USA", "America/Los_Angeles"),
    IndianCity("San Diego", "", 32.7157, -117.1611, "USA", "America/Los_Angeles"),
    IndianCity("Sacramento", "", 38.5816, -121.4944, "USA", "America/Los_Angeles"),
    IndianCity("Fremont", "", 37.5485, -121.9886, "USA", "America/Los_Angeles"),
    IndianCity("Cupertino", "", 37.3230, -122.0322, "USA", "America/Los_Angeles"),

    // ═══ North America — USA (Michigan) ═══
    IndianCity("Detroit", "", 42.3314, -83.0458, "USA", "America/Detroit"),

    // ═══ North America — Canada ═══
    IndianCity("Toronto", "", 43.6532, -79.3832, "Canada", "America/Toronto"),
    IndianCity("Vancouver", "", 49.2827, -123.1207, "Canada", "America/Vancouver"),

    // ═══ Oceania ═══
    IndianCity("Sydney", "", -33.8688, 151.2093, "Australia", "Australia/Sydney"),
    IndianCity("Melbourne", "", -37.8136, 144.9631, "Australia", "Australia/Melbourne"),
    IndianCity("Auckland", "", -36.8485, 174.7633, "New Zealand", "Pacific/Auckland"),
    IndianCity("Fiji", "", -17.7134, 178.0650, "Fiji", "Pacific/Fiji"),
)
