package com.example.newland.activity;

import android.os.Bundle;

import com.example.newland.fragment.NavigationViewFragment;
import com.xuexiang.xpage.base.XPageActivity;

public class MainActivity extends XPageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openPage(NavigationViewFragment.class);
    }
}