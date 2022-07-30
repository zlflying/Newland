package com.example.newland.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.example.newland.utils.XToastUtils;

public class NetWorkStateReceiver extends BroadcastReceiver {


    private static NetStateListener myListener = null;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                XToastUtils.warning("当前不是WiFi连接");
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo connectionInfo = wifiMgr.getConnectionInfo();
                XToastUtils.info("已连接WIFI:" + connectionInfo.getSSID().replace("\"", ""));
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            XToastUtils.error("当前无网络连接");
        }
    }

    public void setMyListener(NetStateListener myListener) {
        NetWorkStateReceiver.myListener = myListener;
    }

    public interface NetStateListener {
        public void onChange(String info);
    }
}