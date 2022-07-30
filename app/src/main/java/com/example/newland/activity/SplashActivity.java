package com.example.newland.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.KeyEvent;

import com.example.newland.R;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.activity.BaseSplashActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseSplashActivity {

    @Override
    protected long getSplashDurationMillis() {
        return 500;
    }

    @Override
    protected void onCreateActivity() {
        //设置沉浸式状态栏
        StatusBarUtils.translucent(this);
        initSplashView(R.drawable.xui_config_bg_splash);
        startSplash(true);
    }

    @Override
    protected void onSplashFinished() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
    }

}
