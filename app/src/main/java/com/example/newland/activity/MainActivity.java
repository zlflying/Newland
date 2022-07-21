package com.example.newland.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.newland.fragment.NavigationViewFragment;
import com.xuexiang.xpage.base.XPageActivity;

public class MainActivity extends XPageActivity {
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //共享偏好文件初始化
        sharedPreferences = getSharedPreferences("device_setting", MODE_PRIVATE);
        openPage(NavigationViewFragment.class);
    }

    /**
     * 获取共享偏好文件
     *
     * @return SharedPreferences
     */
    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}