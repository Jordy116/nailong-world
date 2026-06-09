# ProGuard rules for 奶龍世界 (Nailong World) — Release build

# Keep data model classes used by Gson
-keepclassmembers class com.nailong.world.data.model.** {
    <fields>;
}

# Keep Compose
-dontwarn androidx.compose.**

# Keep Retrofit interfaces
-keep,allowobfuscation interface com.nailong.world.data.** {
    <methods>;
}

# OkHttp / Retrofit
-dontwarn okhttp3.**
-dontwarn retrofit2.**
