package com.example.newland.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.newland.fragment.NavigationViewFragment;
import com.xuexiang.xpage.base.XPageActivity;

import java.util.Arrays;

public class MainActivity extends XPageActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.VIBRATE
                }, 5);

        openPage(NavigationViewFragment.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: " + Arrays.toString(permissions));
        Log.i(TAG, "onRequestPermissionsResult: " + Arrays.toString(grantResults));
    }
}