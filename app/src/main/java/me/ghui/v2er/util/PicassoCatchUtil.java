package me.ghui.v2er.util;

import android.os.Looper;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;

import java.io.File;
import java.math.BigDecimal;

import me.ghui.v2er.general.App;

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
public class PicassoCatchUtil {
    // TODO: 21/07/2017

    // 获取Glide磁盘缓存大小
    public static String getCacheSize() {
        StatsSnapshot snapshot = Picasso.with(App.get()).getSnapshot();
        return getFormatSize(snapshot.size);
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
