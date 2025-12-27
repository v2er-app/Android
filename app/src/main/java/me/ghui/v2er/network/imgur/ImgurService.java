package me.ghui.v2er.network.imgur;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import me.ghui.v2er.BuildConfig;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.util.L;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Imgur upload service with independent OkHttpClient
 */
public class ImgurService {

    private static ImgurService sInstance;
    private final ImgurAPI mImgurAPI;

    private ImgurService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(ImgurConstants.UPLOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(ImgurConstants.UPLOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(ImgurConstants.UPLOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new HttpLoggingInterceptor(L::v)
                    .setLevel(HttpLoggingInterceptor.Level.BASIC));
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ImgurConstants.API_BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mImgurAPI = retrofit.create(ImgurAPI.class);
    }

    public static synchronized ImgurService get() {
        if (sInstance == null) {
            sInstance = new ImgurService();
        }
        return sInstance;
    }

    /**
     * Get the active Client ID (user's custom or default)
     */
    public static String getClientId() {
        String customClientId = Pref.read(ImgurConstants.PREF_KEY_IMGUR_CLIENT_ID);
        return Check.notEmpty(customClientId) ? customClientId : ImgurConstants.DEFAULT_CLIENT_ID;
    }

    /**
     * Get Authorization header value
     */
    public static String getAuthorizationHeader() {
        return "Client-ID " + getClientId();
    }

    /**
     * Upload image from file
     *
     * @param file Image file to upload
     * @return Observable with upload response
     */
    public Observable<ImgurUploadResponse> uploadImage(File file) {
        if (file.length() > ImgurConstants.MAX_FILE_SIZE_BYTES) {
            return Observable.error(new FileTooLargeException(
                    "File size exceeds 10MB limit: " + (file.length() / 1024 / 1024) + "MB"));
        }

        try {
            String base64Image = encodeFileToBase64(file);
            return mImgurAPI.uploadImage(getAuthorizationHeader(), base64Image);
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    /**
     * Upload image from Bitmap
     *
     * @param bitmap Bitmap to upload
     * @param quality Compression quality (0-100)
     * @return Observable with upload response
     */
    public Observable<ImgurUploadResponse> uploadImage(Bitmap bitmap, int quality) {
        String base64Image = encodeBitmapToBase64(bitmap, quality);

        // Check size after encoding
        int sizeBytes = base64Image.length() * 3 / 4; // Approximate decoded size
        if (sizeBytes > ImgurConstants.MAX_FILE_SIZE_BYTES) {
            return Observable.error(new FileTooLargeException(
                    "Image size exceeds 10MB limit"));
        }

        return mImgurAPI.uploadImage(getAuthorizationHeader(), base64Image);
    }

    /**
     * Upload image from Bitmap with default quality (80)
     */
    public Observable<ImgurUploadResponse> uploadImage(Bitmap bitmap) {
        return uploadImage(bitmap, 80);
    }

    private String encodeFileToBase64(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);
        fis.close();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private String encodeBitmapToBase64(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * Exception for files exceeding size limit
     */
    public static class FileTooLargeException extends Exception {
        public FileTooLargeException(String message) {
            super(message);
        }
    }
}
