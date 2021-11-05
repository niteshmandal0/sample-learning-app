# keep debug info for more useful stack traces
-keepattributes SourceFile,LineNumberTable
-dontobfuscate

# Kotlin-specific
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# EIDU-specific
-keep class com.eidu.** { *; }
