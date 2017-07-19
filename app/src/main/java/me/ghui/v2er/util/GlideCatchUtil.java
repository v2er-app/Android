package me.ghui.v2er.util;

import android.os.Looper;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by YaphetZhao
 * on 2016/12/19.
 * <p>
 * QQ:11613371
 * GitHub:https://github.com/YaphetZhao
 * Email:yaphetzhao@foxmail.com
 * Email_EN:yaphetzhao@gmail.com
 * <p>
 * Glide缓存工具类
 */
public class GlideCatchUtil {

    // 获取Glide磁盘缓存大小
    public static String getCacheSize() {
        // TODO: 19/07/2017
        try {
//            Logger.d("Glide cache path: " + Glide.getPhotoCacheDir(App.get()));
//            return getFormatSize(getFolderSize(Glide.getPhotoCacheDir(App.get())));
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "获取失败";
        }
    }

    // 清除图片磁盘缓存，调用Glide自带方法
    public static boolean clearDiskCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
//                new Thread(() -> Picasso.get(App.get()).clearDiskCache()).start();
            } else {
//                Picasso.get(App.get()).clearDiskCache();
//                Picasso.with(App.get()).
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 清除Glide内存缓存
    public static boolean clearCacheMemory() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
//                Glide.get(App.get()).clearMemory();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    // 获取指定文件夹内所有文件大小的和
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    // 格式化单位
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

}
