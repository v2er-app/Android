package me.ghui.v2er.util;

import android.os.Environment;

import java.io.File;
import java.util.UUID;

import me.ghui.v2er.general.App;
import okio.Okio;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by ghui on 24/10/2017.
 */

public class FileUtils {

    public static String saveImg(File imageFile, String imgType) {
        if (!isExternalStorageWritable()) return null;
        File imgRoot = App.get().getExternalMediaDirs()[0];
        File destFile = new File(imgRoot, UUID.randomUUID() + "." + imgType);
        try {
            Okio.buffer(Okio.source(imageFile))
                    .readAll(Okio.sink(destFile));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return destFile.getPath();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
