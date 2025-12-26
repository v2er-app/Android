package me.ghui.v2er.network.imgur;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Imgur API interface for Retrofit
 */
public interface ImgurAPI {

    /**
     * Upload image using Base64 encoding
     *
     * @param authorization Authorization header in format "Client-ID {clientId}"
     * @param base64Image   Base64 encoded image data
     * @return Observable with upload response
     */
    @FormUrlEncoded
    @POST("3/image")
    Observable<ImgurUploadResponse> uploadImage(
            @Header("Authorization") String authorization,
            @Field("image") String base64Image
    );
}
