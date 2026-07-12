# Keep our application class
-keep class com.transportsim.app.TransportsimApplication { *; }

# Keep Hilt-generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }

# Keep Moshi
-keep class com.squareup.moshi.** { *; }
-keep class * implements com.squareup.moshi.JsonAdapter { *; }

# Keep Coil
-keep class coil.** { *; }

# Keep native models
-keep class com.transportsim.bridge.models.** { *; }

# Keep all @Keep annotated classes
-keep class * { @com.transportsim.bridge.annotations.Keep *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}