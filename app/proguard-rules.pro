# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/guliash/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
# Add any project specific keep options here:
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Okio
-dontwarn okio.**
# Joda
-dontwarn org.joda.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Retrolambda
-dontwarn java.lang.invoke.*
#RxJava
-dontwarn rx.internal.util.unsafe.*
-keep class rx.internal.util.unsafe.** { *; }
# Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
# Butterknife
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(...); }
# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

## New rules for EventBus 3.0.x ##
# http://greenrobot.org/eventbus/documentation/proguard/

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-libraryjars <java.home>/lib/rt.jar(java/**,javax/**)
-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; }
-keep class org.simpleframework.xml.core.**{ *; }
-keep class org.simpleframework.xml.util.**{ *; }

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keep class ru.yandex.whocallsya.network.pojo.YandexDoc { *; }
-keep class ru.yandex.whocallsya.network.pojo.YandexDoc$Passages { *; }
-keep class ru.yandex.whocallsya.network.pojo.YandexGroup { *; }
-keep class ru.yandex.whocallsya.network.pojo.YandexGroup$YandexCateg { *; }
-keep class ru.yandex.whocallsya.network.pojo.YandexSearch { *; }
-keep class ru.yandex.whocallsya.network.pojo.YandexSearch$YandexGrouping { *; }
-keep class ru.yandex.whocallsya.network.SpannableStringConverter { *; }
-keep class ru.yandex.whocallsya.network.SpannableStringListConverter { *; }