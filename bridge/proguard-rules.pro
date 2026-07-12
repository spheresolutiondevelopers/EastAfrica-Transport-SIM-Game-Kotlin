# Keep all JNI-annotated classes
-keep class com.transportsim.bridge.annotations.** { *; }
-keep class * { @com.transportsim.bridge.annotations.Keep *; }

# Keep all native models
-keep class com.transportsim.bridge.models.** { *; }

# Keep native method names
-keepclasseswithmembernames class com.transportsim.bridge.NativeEngine {
    native <methods>;
}

# Keep callback methods called from JNI
-keepclassmembers class com.transportsim.bridge.NativeCallbacks {
    public void on*(...);
}

# Keep Moshi-generated adapters
-keep class com.transportsim.bridge.models.**$$JsonAdapter { *; }
-keep class * implements com.squareup.moshi.JsonAdapter { *; }

# Don't obfuscate JNI method signatures
-keepclasseswithmembernames class * {
    native <methods>;
}