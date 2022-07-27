package com.example.newland;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.example.newland.base.BaseActivity;
import com.example.newland.receiver.NetWorkStateReceiver;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.PageConfig;

public class MyApp extends Application {
    private NetWorkStateReceiver netStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        PageConfig.getInstance()
                .debug("PageLog")       //开启调试
                .setContainActivityClazz(BaseActivity.class) //设置默认的容器Activity
                .init(this);            //初始化页面配置

        String rootDir = MMKV.initialize(this);
        System.out.println("MMKV root: " + rootDir);

        registerReceiver();
    }


    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        unregisterReceiver();
        super.onTerminate();
    }

    /**
     * 注册网络状态监听器（广播接收者）
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        netStateReceiver = new NetWorkStateReceiver();
        this.registerReceiver(netStateReceiver, filter);
    }

    /**
     * 注销网络状态监听器
     */
    private void unregisterReceiver() {
        if (netStateReceiver != null) {
            this.unregisterReceiver(netStateReceiver);
        }
    }

    public void setNetStateMyListener(NetWorkStateReceiver.NetStateListener myListener) {
        if (netStateReceiver != null) {
            netStateReceiver.setMyListener(myListener);
        }
    }
}
