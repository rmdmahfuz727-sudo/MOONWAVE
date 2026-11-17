# Proguard rules for MOONWAVE
-keepattributes *Annotation*
-dontwarn javax.annotation.**
-keepclassmembers class * {
    @androidx.room.* *;
}
-keep class com.moonwave.music.core.model.** { *; }
