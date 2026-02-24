# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }

# Gson
-keep class com.nityapooja.app.data.** { *; }

# Media3
-keep class androidx.media3.** { *; }

# Spotify
-keep class com.spotify.** { *; }
-dontwarn com.fasterxml.jackson.**
-dontwarn com.spotify.base.**

# Firebase
-keep class com.google.firebase.** { *; }

# Google Mobile Ads (AdMob)
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
