# ProGuard rules for CA Sahayak

# ─── Retrofit + Gson ──────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Gson data classes (Groq DTOs)
-keep class com.casahayak.app.data.model.** { *; }
-keepclassmembers class com.casahayak.app.data.model.** { *; }

# ─── Firebase ─────────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-keepattributes EnclosingMethod
-dontwarn com.google.firebase.**

# ─── Firestore data classes ───────────────────────────────────────────────────
# Firestore requires public no-arg constructors for deserialization
-keepclassmembers class com.casahayak.app.data.model.** {
    public <init>();
}

# ─── Hilt ─────────────────────────────────────────────────────────────────────
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# ─── Google Play Billing ──────────────────────────────────────────────────────
-keep class com.android.billingclient.** { *; }

# ─── AdMob ────────────────────────────────────────────────────────────────────
-keep class com.google.android.gms.ads.** { *; }

# ─── Compose ──────────────────────────────────────────────────────────────────
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# ─── Kotlin coroutines ────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**
