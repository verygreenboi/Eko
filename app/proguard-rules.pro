# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/mrsmith/IDE/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

#-keepclassmembers public class java.io.IOException

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Facebook 3.2

#-keep class com.facebook.** { *; }
#-keepattributes Signature

# ZXing

-keep class com.google.zxing.** { *; }

# PrettyTime

-keep class com.ocpsoft.** { *; }

# Parse.com

-keep class com.parse.** { *; }
-dontwarn com.parse.**
-keep public class android.net.**{ *; }
-dontwarn android.net.**
-dontwarn org.apache.http.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.lang.**