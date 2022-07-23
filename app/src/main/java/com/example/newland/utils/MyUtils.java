package com.example.newland.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.newland.R;
import com.xuexiang.xui.widget.toast.XToast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * com.muxmu.newlandtest.utils
 *
 * @author：zl on 2021/11/14 14:41
 * info: 工具类
 */
public class MyUtils {

    private static TextToSpeech tts;

    public static synchronized void speakText(Context context, String text) {
        tts = new TextToSpeech(context, i -> {
            if (i == TextToSpeech.SUCCESS) {
                Log.i("success", "success");
                speak(text);
            } else {
                Log.e("error", "error");
            }
        });
    }

    private static void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }


    /**
     * 获取应用程序名称
     *
     * @param context 上下文
     * @return String AppName
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前程序版本号
     *
     * @param context content
     * @return int versioncode
     */
    public static int getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            // versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }

    /**
     * 返回当前程序版本名
     *
     * @return String versionName
     */
    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 将内容发送系统剪切板
     *
     * @param context      上下文
     * @param clip_content 将要复制到剪切板的内容
     */
    public static void sendClipboard(Context context, String clip_name, String clip_content) {
        //获取剪贴版
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("     text", clip_content);
        clipboard.setPrimaryClip(clip);
        XToast.success(context, clip_name + "已复制到剪切板!").show();
    }

    /**
     * 添加app快捷方式
     *
     * @param context    上下文
     * @param id         快捷方式id
     * @param label      快捷方式label
     * @param long_label 快捷方式长label
     * @param intent     快捷方式的意图
     */
    public static synchronized void addShortcuts(Context context, String id, String label, String long_label, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager mShortcutManager = context.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> shortcuts = mShortcutManager.getPinnedShortcuts();
            intent.setAction(Intent.ACTION_VIEW);
            ShortcutInfo info = new ShortcutInfo.Builder(context, id)
                    .setShortLabel(label)
                    .setLongLabel(long_label)
                    .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher_round))
                    .setIntent(intent)
                    .build();
            shortcuts.add(info);
            mShortcutManager.setDynamicShortcuts(shortcuts);
        }
    }

    /**
     * 移除快捷方式
     *
     * @param context 上下文
     * @param id      要移除快捷方式的id
     */
    public static void removeShortcuts(Context context, String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager mShortcutManager = context.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> shortcuts = mShortcutManager.getPinnedShortcuts();
            for (ShortcutInfo shortcut : shortcuts) {
                if (shortcut.getId().equals(id)) {
                    mShortcutManager.disableShortcuts(Collections.singletonList(shortcut.getId()), "无效的快捷方式");
                }
            }
            mShortcutManager.removeDynamicShortcuts(Collections.singletonList(id));
        }
    }

    /**
     * 手机震动
     *
     * @param context 上下文
     */
    @SuppressLint("MissingPermission")
    public static void setVIBRATE(Context context) {
        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] patter = {10, 200};
            vibrator.vibrate(patter, -1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getNowTime() {
        @SuppressLint({"SimpleDateFormat", "WeekBasedYear"})
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        return dateFormat.format(new Date());
    }

    /**
     * 获取设备ip地址
     *
     * @param context 上下文
     * @return ip地址
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress() + "(当前不是WiFi连接)";
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();//得到IPV4地址
                return (ipAddress & 0xFF) + "." +
                        ((ipAddress >> 8) & 0xFF) + "." +
                        ((ipAddress >> 16) & 0xFF) + "." +
                        (ipAddress >> 24 & 0xFF);
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            return "当前无网络连接,请在设置中打开网络";
        }
        return null;
    }

    /**
     * 获取设备ip地址
     * 不判断当前设备网络状态
     *
     * @param context 上下文
     * @return ip地址
     */
    public static String getSimIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
//                                return "请使用wifi连接网络!";
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();//得到IPV4地址
                return (ipAddress & 0xFF) + "." +
                        ((ipAddress >> 8) & 0xFF) + "." +
                        ((ipAddress >> 16) & 0xFF) + "." +
                        (ipAddress >> 24 & 0xFF);
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            return null;
        }
        return null;
    }


}

