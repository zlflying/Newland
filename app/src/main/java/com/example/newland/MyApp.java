package com.example.newland;

import android.app.Application;

import com.example.newland.base.BaseActivity;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.PageConfig;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PageConfig.getInstance()
                .debug("PageLog")       //开启调试
                .setContainActivityClazz(BaseActivity.class) //设置默认的容器Activity
                .init(this);            //初始化页面配置

        String rootDir = MMKV.initialize(this);
        System.out.println("mmkv root: " + rootDir);

    }

}
