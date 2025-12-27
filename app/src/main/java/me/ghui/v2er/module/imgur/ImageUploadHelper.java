package me.ghui.v2er.module.imgur;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.ghui.v2er.general.App;
import me.ghui.v2er.network.imgur.ImgurService;
import me.ghui.v2er.network.imgur.ImgurUploadHistory;
import me.ghui.v2er.network.imgur.ImgurUploadResponse;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Helper class for picking and uploading images to Imgur
 */
public class ImageUploadHelper {

    public static final int REQUEST_CODE_PICK_IMAGE = 1001;
    public static final int REQUEST_CODE_PERMISSION = 1002;

    private static final String[] REQUIRED_PERMISSIONS_LEGACY = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final Activity mActivity;
    private final OnUploadListener mListener;
    private Disposable mUploadDisposable;

    /**
     * Callback interface for upload events
     */
    public interface OnUploadListener {
        void onUploadStart();
        void onUploadSuccess(String imageLink);
        void onUploadFailed(String errorMsg);
    }

    public ImageUploadHelper(Activity activity, OnUploadListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    /**
     * Open image picker
     */
    public void pickImage() {
        // For Android 13+, READ_MEDIA_IMAGES is needed but usually granted with photo picker
        // For Android 10-12, scoped storage handles this
        // For Android 9 and below, need READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!EasyPermissions.hasPermissions(mActivity, REQUIRED_PERMISSIONS_LEGACY)) {
                EasyPermissions.requestPermissions(
                        mActivity,
                        "Need storage permission to select images",
                        REQUEST_CODE_PERMISSION,
                        REQUIRED_PERMISSIONS_LEGACY
                );
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mActivity.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * Handle activity result from image picker
     */
    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                uploadImage(imageUri);
            }
        }
    }

    /**
     * Upload image from Uri
     */
    private void uploadImage(Uri imageUri) {
        mListener.onUploadStart();

        mUploadDisposable = Observable.fromCallable(() -> uriToFile(imageUri))
                .subscribeOn(Schedulers.io())
                .flatMap(file -> ImgurService.get().uploadImage(file))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> handleUploadResponse(response),
                        throwable -> handleUploadError(throwable)
                );
    }

    private void handleUploadResponse(ImgurUploadResponse response) {
        if (response.isSuccess() && response.getData() != null) {
            String link = response.getData().getLink();
            // Save to history
            ImgurUploadHistory.get().addImage(response.getData());
            mListener.onUploadSuccess(link);
        } else {
            mListener.onUploadFailed("Upload failed, please try again");
        }
    }

    private void handleUploadError(Throwable throwable) {
        String errorMsg;
        if (throwable instanceof ImgurService.FileTooLargeException) {
            errorMsg = "Image size exceeds 10MB limit";
        } else {
            errorMsg = "Upload failed: " + throwable.getMessage();
        }
        mListener.onUploadFailed(errorMsg);
    }

    /**
     * Convert content Uri to File
     */
    private File uriToFile(Uri uri) throws Exception {
        InputStream inputStream = App.get().getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new Exception("Cannot open image stream");
        }

        File tempFile = File.createTempFile("upload_", ".jpg", App.get().getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();

        return tempFile;
    }

    /**
     * Clean up resources
     */
    public void onDestroy() {
        if (mUploadDisposable != null && !mUploadDisposable.isDisposed()) {
            mUploadDisposable.dispose();
        }
    }
}
