package com.example.newland.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newland.R;
import com.example.newland.activity.MainActivity;
import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentSettingNetworkBinding;
import com.example.newland.utils.MyUtils;
import com.example.newland.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.layout.XUILinearLayout;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@Page(name = "SettingNetwork")
public class SettingNetworkFragment extends BaseFragment {
    FragmentSettingNetworkBinding binding;
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler();

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentSettingNetworkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        TitleBar titleBar = super.initTitleBar();
        titleBar.setTitle("设置网络参数")
                .setLeftClickListener(view -> popToBack())
                .addAction(new TitleBar.TextAction("保存") {
                    @Override
                    public void performAction(View view) {
                        if (saveContent()) {
                            handler.postDelayed(() -> {
                                PageOption.to(NavigationViewFragment.class) //跳转的fragment
                                        .setAnim(CoreAnim.fade) //页面转场动画
                                        .setNewActivity(false)
                                        .open(SettingNetworkFragment.this); //打开页面进行跳转
                            }, 500);
                        } else {
                            XToastUtils.error("保存失败:数据校验不通过");
                        }
                    }
                });
        return titleBar;
    }

    @Override
    protected void initViews() {
        sharedPreferences = MainActivity.getSharedPreferences();
        initLayout();
        initContent();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        binding.appAboutInfo.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));
    }

    @Override
    protected void initListeners() {

    }


    /**
     * 初始化内容
     */
    @SuppressLint("SetTextI18n")
    private void initContent() {
        String simIPAddress = MyUtils.getSimIPAddress(XUI.getContext());
        if (simIPAddress == null) simIPAddress = "192.168.0.1"; //如果当前无网络，则初始化为192.168.0.1
        binding.ipaddress4150.setText(sharedPreferences.getString("ipaddress_4150", simIPAddress));
        binding.prot4150.setText(sharedPreferences.getInt("port_4150", 6000) + "");
        binding.ipaddress4017.setText(sharedPreferences.getString("ipaddress_4017", simIPAddress));
        binding.prot4017.setText(sharedPreferences.getInt("port_4017", 6000) + "");
//        address_4017.setText(sharedPreferences.getInt("address_4017", 2) + "");
        binding.ipaddressZigbee.setText(sharedPreferences.getString("ipaddress_zigbee", simIPAddress));
        binding.protZigbee.setText(sharedPreferences.getInt("port_zigbee", 6000) + "");
        binding.ipaddressRfid.setText(sharedPreferences.getString("ipaddress_rfid", simIPAddress));
        binding.protRfid.setText(sharedPreferences.getInt("port_rfid", 6000) + "");
        binding.ipaddressLed.setText(sharedPreferences.getString("ipaddress_led", simIPAddress));
        binding.protLed.setText(sharedPreferences.getInt("port_led", 6000) + "");
        binding.ipaddressCamera.setText(sharedPreferences.getString("ipaddress_camera", simIPAddress));
        binding.usernameCamera.setText(sharedPreferences.getString("username_camera", "admin"));
        binding.passwordCamera.setText(sharedPreferences.getString("password_camera", "admin"));
        binding.channelCamera.setText(sharedPreferences.getString("channel_camera", "11"));
        CookieBar.builder(getActivity())
                .setTitle("提示")
                .setMessage(R.string.cookie_message)
                .setDuration(1200)
                .setBackgroundColor(R.color.tips)
                .setActionColor(android.R.color.white)
                .setTitleColor(android.R.color.white)
                .show();
    }

    /**
     * 保存设置信息内容
     *
     * @return isSuccess
     */
    private Boolean saveContent() {
        if (getValidate()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String ip_4150 = binding.ipaddress4150.getEditValue();
            int port_4150 = Integer.parseInt(binding.prot4150.getEditValue());
            String ip_4017 = binding.ipaddress4017.getEditValue();
            int port_4017 = Integer.parseInt(binding.prot4017.getEditValue());
//            int address_4017 = Integer.parseInt(this.address_4017.getEditValue());
            String ipaddress_zigbee = binding.ipaddressZigbee.getEditValue();
            int port_zigbee = Integer.parseInt(binding.protZigbee.getEditValue());
            String ipaddress_rfid = binding.ipaddressRfid.getEditValue();
            int port_rfid = Integer.parseInt(binding.protRfid.getEditValue());
            String ipaddress_led = binding.ipaddressLed.getEditValue();
            int port_led = Integer.parseInt(binding.protLed.getEditValue());
            String ipaddress_camera = binding.ipaddressCamera.getEditValue();
            String username_camera = binding.usernameCamera.getEditValue();
            String password_camera = Objects.requireNonNull(binding.passwordCamera.getText()).toString();
            String channel_camera = binding.channelCamera.getEditValue();
            //保存到偏好文件
            editor.putString("ipaddress_4150", ip_4150);
            editor.putInt("port_4150", port_4150);
            editor.putString("ipaddress_4017", ip_4017);
            editor.putInt("port_4017", port_4017);
//            editor.putInt("address_4017", address_4017);
            editor.putString("ipaddress_zigbee", ipaddress_zigbee);
            editor.putInt("port_zigbee", port_zigbee);
            editor.putString("ipaddress_rfid", ipaddress_rfid);
            editor.putInt("port_rfid", port_rfid);
            editor.putString("ipaddress_led", ipaddress_led);
            editor.putInt("port_led", port_led);
            editor.putString("ipaddress_camera", ipaddress_camera);
            editor.putString("username_camera", username_camera);
            editor.putString("password_camera", password_camera);
            editor.putString("channel_camera", channel_camera);
            return editor.commit();
        } else {
            return false;
        }
    }

    /**
     * 获取所有验证信息
     *
     * @return 验证是否通过
     */
    private Boolean getValidate() {
        ArrayList<MaterialEditText> materialEditTextArrayList = new ArrayList<>();
        Collections.addAll(materialEditTextArrayList, binding.ipaddress4150, binding.prot4150, binding.ipaddress4017,
                binding.prot4017, binding.ipaddressZigbee, binding.protZigbee, binding.ipaddressRfid, binding.protRfid, binding.ipaddressLed,
                binding.protLed, binding.ipaddressCamera, binding.channelCamera);
        for (MaterialEditText materialEditText : materialEditTextArrayList) {
            if (!materialEditText.validate()) return false;
        }
        return true;
    }

    /**
     * 设置item样式
     */
    private void initLayout() {
        ArrayList<XUILinearLayout> xuiLinearLayouts = new ArrayList<>();
        Collections.addAll(xuiLinearLayouts, binding.xuiLayout1, binding.xuiLayout2, binding.xuiLayout3, binding.xuiLayout4, binding.xuiLayout5, binding.xuiLayout6);
        for (XUILinearLayout xuiLinearLayout : xuiLinearLayouts) {
            xuiLinearLayout.setRadius(40);
            xuiLinearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            xuiLinearLayout.setShadowColor(R.color.selector_tag_color);
            xuiLinearLayout.setAlpha(0.75F);
            xuiLinearLayout.setElevation(11);
        }
    }

}