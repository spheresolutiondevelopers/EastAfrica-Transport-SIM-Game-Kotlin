# Keep all native method names
-keepclasseswithmembernames class com.transportsim.bridge.NativeEngine {
    native <methods>;
}

# Keep the native callback class
-keep class com.transportsim.bridge.NativeCallbacks {
    public void on*(...);
}

# Keep Moshi adapters for native models
-keep class com.transportsim.bridge.models.** { *; }
-keep class com.transportsim.bridge.models.**$$JsonAdapter { *; }