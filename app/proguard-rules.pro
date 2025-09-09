# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ghui/Developer/android-sdk-macosx/tools/proguard/proguard-android.txt
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

#----------------- Retrofit Start ------------------------
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn retrofit2.**
#----------------- Retrofit End ----------------------------


#----------------- Glide Start ----------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
-dontwarn com.bumptech.glide.load.resource.bitmap.Downsampler
-dontwarn com.bumptech.glide.load.resource.bitmap.HardwareConfigState

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
-keep public class * implements com.bumptech.glide.module.GlideModule
#----------------- Glide End ----------------------------

-dontwarn com.squareup.okhttp.**
-keep class okhttp3.internal.publicsuffix.PublicSuffixDatabase

#----------------- PersistentCookieJar Start ---------------------------
-dontwarn com.franmontiel.persistentcookiejar.**
-keep class com.franmontiel.persistentcookiejar.**
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#----------------- PersistentCookieJar End ----------------------------

#----------------- Bugly Start ----------------------------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#----------------- Bugly End ----------------------------

#----------------- Jsoup Start ----------------------------
-keeppackagenames org.jsoup.nodes
-keep class org.jsoup.**
#----------------- Jsoup End ----------------------------

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { *; }
-keep class me.ghui.v2er.network.bean.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------
-keep class android.support.v7.widget.SearchView { *; }
##END

## WEIXIN
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}
##END

## eventbus START
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
## END

-dontwarn android.content.IContentProvider

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class me.ghui.fruit.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends me.ghui.fruit.reflect.TypeToken

-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking class arrow.retrofit.**

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Keep generic signature of RxJava3 (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Flowable
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Maybe
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Observable
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Single

#----------------- Additional Rules for V2er App ------------------------

# Keep RxJava2 (current version used in app)
-keep class io.reactivex.** { *; }
-keep interface io.reactivex.** { *; }
-dontwarn io.reactivex.**

# Dagger 2 rules
-dontwarn com.google.errorprone.annotations.**
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.Module
-keep class * extends dagger.Component
-keep @dagger.Component class *
-keep @dagger.Module class * { *; }
-keep @dagger.Provides class * { *; }
-keep @javax.inject.Inject class * { *; }

# ButterKnife rules  
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Custom Fruit HTML parsing library
-keep class me.ghui.fruit.** { *; }
-dontwarn me.ghui.fruit.**
-keep @me.ghui.fruit.annotations.* class * { *; }
-keepclasseswithmembernames class * {
    @me.ghui.fruit.annotations.* <methods>;
}
-keepclasseswithmembernames class * {
    @me.ghui.fruit.annotations.* <fields>;
}

# RxLifecycle
-keep class com.trello.rxlifecycle2.** { *; }
-dontwarn com.trello.rxlifecycle2.**

# Logger library
-keep class com.orhanobut.logger.** { *; }
-dontwarn com.orhanobut.logger.**

# Flurry Analytics (referenced in CLAUDE.md)
-keep class com.flurry.** { *; }
-dontwarn com.flurry.**

# Sentry (referenced in CLAUDE.md) 
-keep class io.sentry.** { *; }
-dontwarn io.sentry.**

# Keep only essential application classes (allow most classes to be obfuscated)
-keep class me.ghui.v2er.BuildConfig { *; }
-keep class me.ghui.v2er.R { *; }
-keep class me.ghui.v2er.R$* { *; }

# Keep Application class and Activities (required by Android)  
-keep class me.ghui.v2er.Application { *; }
-keep class * extends android.app.Application { *; }

# Keep data models used for JSON parsing (Gson/Fruit)
-keep class me.ghui.v2er.network.bean.** { *; }

# Keep classes with native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all Activities, Services, BroadcastReceivers
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep view constructors (for layout inflation)
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep custom views and their constructors
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}