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

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#----------------- Glide End ----------------------------

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
#----------------- Jsoup End ----------------------------
