package com.example.newland.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newland.R;
import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentSettingNetworkBinding;
import com.example.newland.utils.MyUtils;
import com.example.newland.utils.XToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.xuexiang.xutil.XUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@Page(name = "SettingNetwork", anim = CoreAnim.fade)
public class SettingNetworkFragment extends BaseFragment {
    FragmentSettingNetworkBinding binding;
    private final Handler handler = new Handler();
    private MMKV kv;
    private MiniLoadingDialog miniLoadingDialog;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentSettingNetworkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        TitleBar titleBar = super.initTitleBar();
        titleBar.setTitle("设置网络参数", "请填写网络设备参数", TitleBar.CENTER_LEFT)
                .setLeftClickListener(view -> {
                    if (kv.count() == 0) {
                        new MaterialDialog.Builder(getContext())
                                .iconRes(R.mipmap.ic_launcher_round)
                                .title(R.string.tip_infos)
                                .content("是否保存修改")
                                .positiveText("是")
                                .onPositive((dialog, which) -> {
                                    if (saveContent()) {
                                        dialog.dismiss();
                                        handler.postDelayed(() -> {
                                            PageOption.to(NavigationViewFragment.class)
                                                    .setAnim(CoreAnim.fade)
                                                    .open(SettingNetworkFragment.this);
                                            onDestroyView();
                                        }, 500);
                                    }
                                })
                                .negativeText("否")
                                .onNegative((dialog, which) -> {
                                    PageOption.to(NavigationViewFragment.class)
                                            .setAnim(CoreAnim.fade)
                                            .open(SettingNetworkFragment.this);
                                })
                                .show();
                    } else {
                        PageOption.to(NavigationViewFragment.class)
                                .setAnim(CoreAnim.fade)
                                .open(SettingNetworkFragment.this);
                    }
                })
                .addAction(new TitleBar.TextAction("保存") {
                    @Override
                    public void performAction(View view) {
                        if (saveContent()) {
                            handler.postDelayed(() -> {
                                PageOption.to(NavigationViewFragment.class)
                                        .setAnim(CoreAnim.fade)
                                        .open(SettingNetworkFragment.this);
                                onDestroyView();
                            }, 500);
                        }
                    }
                });
        return titleBar;
    }

    @Override
    protected void initViews() {
        kv = MMKV.mmkvWithID("InetInfo", MMKV.MULTI_PROCESS_MODE);
        initContent();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        binding.appAboutInfo.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));
    }

    @Override
    protected void initListeners() {
        binding.clearAllInfo.setOnClickListener(view -> {
            kv.clearAll();
            initContent();
            CookieBar.builder(getActivity())
                    .setTitle("提示")
                    .setMessage(R.string.cookie_message)
                    .setDuration(1200)
                    .setBackgroundColor(R.color.tips)
                    .setActionColor(android.R.color.white)
                    .setTitleColor(android.R.color.white)
                    .show();
        });
    }


    /**
     * 初始化内容
     */
    @SuppressLint("SetTextI18n")
    private void initContent() {
        String simIPAddress = MyUtils.getSimIPAddress(XUI.getContext());
        if (simIPAddress == null) simIPAddress = "192.168.0.1"; //如果当前无网络，则初始化为192.168.0.1
        binding.ipaddress4150.setText(kv.decodeString("ipaddress_4150", simIPAddress));
        binding.prot4150.setText(kv.getInt("port_4150", 6000) + "");
        binding.ipaddress4017.setText(kv.decodeString("ipaddress_4017", simIPAddress));
        binding.prot4017.setText(kv.decodeInt("port_4017", 6000) + "");
        binding.ipaddressZigbee.setText(kv.decodeString("ipaddress_zigbee", simIPAddress));
        binding.protZigbee.setText(kv.decodeInt("port_zigbee", 6000) + "");
        binding.ipaddressRfid.setText(kv.decodeString("ipaddress_rfid", simIPAddress));
        binding.protRfid.setText(kv.decodeInt("port_rfid", 6000) + "");
        binding.ipaddressLed.setText(kv.decodeString("ipaddress_led", simIPAddress));
        binding.protLed.setText(kv.decodeInt("port_led", 6000) + "");
        binding.ipaddressCamera.setText(kv.decodeString("ipaddress_camera", simIPAddress));
        binding.usernameCamera.setText(kv.decodeString("username_camera", "admin"));
        binding.passwordCamera.setText(kv.decodeString("password_camera", "admin"));
        binding.channelCamera.setText(kv.decodeString("channel_camera", "11"));
    }

    /**
     * 保存设置信息内容
     */
    private boolean saveContent() {
        if (getValidate()) {
            miniLoadingDialog = WidgetUtils.getMiniLoadingDialog(getAttachContext());
            miniLoadingDialog.updateMessage("保存中");
            XUtil.runOnUiThread(miniLoadingDialog::show);
            String ip_4150 = binding.ipaddress4150.getEditValue();
            int port_4150 = Integer.parseInt(binding.prot4150.getEditValue());
            String ip_4017 = binding.ipaddress4017.getEditValue();
            int port_4017 = Integer.parseInt(binding.prot4017.getEditValue());
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
            kv.encode("ipaddress_4150", ip_4150);
            kv.encode("port_4150", port_4150);
            kv.encode("ipaddress_4017", ip_4017);
            kv.encode("port_4017", port_4017);
            kv.encode("ipaddress_zigbee", ipaddress_zigbee);
            kv.encode("port_zigbee", port_zigbee);
            kv.encode("ipaddress_rfid", ipaddress_rfid);
            kv.encode("port_rfid", port_rfid);
            kv.encode("ipaddress_led", ipaddress_led);
            kv.encode("port_led", port_led);
            kv.encode("ipaddress_camera", ipaddress_camera);
            kv.encode("username_camera", username_camera);
            kv.encode("password_camera", password_camera);
            kv.encode("channel_camera", channel_camera);
            return true;
        } else {
            XToastUtils.error("数据效验未通过！");
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


    @Override
    public void onDestroyView() {
        miniLoadingDialog.dismiss();
        super.onDestroyView();
    }
}