package com.moko.contacttracker.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Date 2017/12/7 0007
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.beacon.utils.MokoUtils
 */
public class Utils {



    public static String getVersionInfo(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packInfo != null) {
            String version = packInfo.versionName;
            return String.format("%s", version);
        }
        return "";
    }

    /**
     * @Date 2018/1/22
     * @Author wenzheng.liu
     * @Description 加密
     */
    public static byte[] encrypt(byte[] value, byte[] password) {
        try {
            SecretKeySpec key = new SecretKeySpec(password, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(value);// 加密
            byte[] data = Arrays.copyOf(result, 16);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }
}
