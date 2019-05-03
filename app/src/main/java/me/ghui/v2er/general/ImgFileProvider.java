package me.ghui.v2er.general;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;

import java.io.FileInputStream;

/**
 * Created by ghui on 25/10/2017.
 */

public class ImgFileProvider extends FileProvider {

    private ImageHeaderParser mImageHeaderParser = new DefaultImageHeaderParser();

    private static String getTypeFromImageType(ImageHeaderParser.ImageType imageType,
                                               String defaultType) {
        String extension;
        switch (imageType) {
            case GIF:
                extension = "gif";
                break;
            case JPEG:
                extension = "jpg";
                break;
            case PNG_A:
            case PNG:
                extension = "png";
                break;
            case WEBP_A:
            case WEBP:
                extension = "webp";
                break;
            default:
                return defaultType;
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    @Override
    public String getType(Uri uri) {
        String type = super.getType(uri);
        if (!TextUtils.equals(type, "application/octet-stream")) {
            return type;
        }
        try {
            ParcelFileDescriptor parcelFileDescriptor = openFile(uri, "r");
            if (parcelFileDescriptor == null) {
                return type;
            }
            try {
                FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                try {
                    ImageHeaderParser.ImageType imageType = mImageHeaderParser.getType(fileInputStream);
                    type = getTypeFromImageType(imageType, type);
                } finally {
                    fileInputStream.close();
                }
            } finally {
                parcelFileDescriptor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
}
