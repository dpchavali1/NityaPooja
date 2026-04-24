# Room — keep DAO interfaces, generated impls, and database class
-keep @androidx.room.Dao interface *
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract *;
}
-keep class * extends androidx.room.migration.Migration { *; }
-dontwarn androidx.room.**

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.nityapooja.**$$serializer { *; }
-keepclassmembers class com.nityapooja.** {
    *** Companion;
}
-keepclasseswithmembers class com.nityapooja.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# Koin
-keep class org.koin.** { *; }
-keepnames class * implements org.koin.core.component.KoinComponent
-dontwarn org.koin.**

# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**

# Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# Kotlin reflect
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# Keep app classes
-keep class com.nityapooja.** { *; }

# General Android
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Retrofit
-keepattributes Signature
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }

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
