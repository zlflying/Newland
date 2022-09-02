package com.example.newland.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.newland.fragment.NavigationViewFragment;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;

import java.util.Arrays;

public class MainActivity extends XPageActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置沉浸式状态栏
//        StatusBarUtils.translucent(this);
//        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, 5);

//        findViewById(R.id.cloud_debug).setOnClickListener((view) -> {
//
//        });
//        findViewById(R.id.local_debug).setOnClickListener((view) -> {
//
//        });
        PageOption.to(NavigationViewFragment.class)
                .setAnim(CoreAnim.fade)
                .open(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: " + Arrays.toString(permissions));
        Log.i(TAG, "onRequestPermissionsResult: " + Arrays.toString(grantResults));
    }
}