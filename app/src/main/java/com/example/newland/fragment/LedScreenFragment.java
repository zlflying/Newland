package com.example.newland.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentLedscreenBinding;
import com.example.newland.utils.XToastUtils;
import com.nle.mylibrary.enums.led.PlayType;
import com.nle.mylibrary.enums.led.ShowSpeed;
import com.nle.mylibrary.forUse.led.LedListener;
import com.nle.mylibrary.forUse.led.LedScreen;
import com.nle.mylibrary.transfer.DataBusFactory;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.widget.popupwindow.status.Status;

@Page(name = "LedScreen")
public class LedScreenFragment extends BaseFragment {
    private static final String TAG = "LedScreenFragment";
    FragmentLedscreenBinding binding;
    private LedScreen ledScreen;
    private LedListener ledListener;
    private MMKV kv;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentLedscreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViews() {
        kv = MMKV.mmkvWithID("InetInfo", MMKV.MULTI_PROCESS_MODE);
        binding.selectLedMode.setOnTabSelectionChangedListener((title, value) -> {
            if (value.equals("9")) {
                binding.btLedconnection.setLabelText("网络");
                binding.btLedconnection.setLabelOrientation(1);
            } else if (value.equals("10")) {
                binding.btLedconnection.setLabelText("串口");
                binding.btLedconnection.setLabelOrientation(2);
            }
        });
        binding.ledscreenIp.setText("ip地址: " + kv.decodeString("ipaddress_led", "请在设置中配置连接参数"));
        binding.ledscreenProt.setText("端口: " + kv.decodeInt("port_led", 1000));
        binding.flowlayoutLedTypeSelect.setOnTagClickListener((parent, view, position) -> {
            try {
                switch (position) {
                    case 0:
                        ledScreen.sendTxt(binding.sendLedText.getContentText(), PlayType.LEFT, ShowSpeed.SPEED3, 1, 100, ledListener);
                        break;
                    case 1:
                        ledScreen.sendTxt(binding.sendLedText.getContentText(), PlayType.UP, ShowSpeed.SPEED3, 1, 100, ledListener);
                        break;
                    case 2:
                        ledScreen.sendTxt(binding.sendLedText.getContentText(), PlayType.DOWN, ShowSpeed.SPEED3, 1, 100, ledListener);
                        break;
                    case 3:
                        ledScreen.sendTxt(binding.sendLedText.getContentText(), PlayType.DOWN_OVER, ShowSpeed.SPEED3, 1, 100, ledListener);
                        break;
                    case 4:
                        ledScreen.sendTxt(binding.sendLedText.getContentText(), PlayType.UP_OVER, ShowSpeed.SPEED3, 1, 100, ledListener);
                        break;
                    case 5:
                        ledScreen.sendTxt(binding.sendLedText.getContentText(), PlayType.WHITE, ShowSpeed.SPEED3, 1, 100, ledListener);
                        break;
                    case 6:
                        ledScreen.sendTxt(binding.sendLedText.getContentText(), PlayType.SPANGLE, ShowSpeed.SPEED3, 1, 100, ledListener);
                        break;
                    case 7:
                        ledScreen.sendTxt(binding.sendLedText.getContentText(), PlayType.NOW, ShowSpeed.SPEED3, 1, 100, ledListener);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.btLedconnection.setOnClickListener(view -> {
            if (ledScreen == null) {
                if (binding.btLedconnection.getLabelText().equals("网络")) {
                    ledScreen = new LedScreen(DataBusFactory.newSocketDataBus(kv.decodeString("ipaddress_led", "192.168.0.1")
                            , kv.decodeInt("port_led", 1000)), b -> {
                        if (b) {
                            binding.status.setStatus(Status.COMPLETE);
                            binding.btLedconnection.setText("Socket已连接");
                        } else {
                            binding.status.setStatus(Status.ERROR);
                            ledScreen.stopConnect();
                            ledScreen = null;
                        }
                    });
                } else {
                    ledScreen = new LedScreen(DataBusFactory.newSerialDataBus(binding.serialPortsLed.getSelectedIndex(), 9600),
                            b -> {
                                if (b) {
                                    binding.status.setStatus(Status.COMPLETE);
                                    binding.btLedconnection.setText("串口已连接");
                                } else {
                                    binding.status.setStatus(Status.ERROR);
                                    ledScreen.stopConnect();
                                    ledScreen = null;
                                }
                            });
                }
                binding.status.setStatus(Status.LOADING);
            } else {
                ledScreen.stopConnect();
                ledScreen = null;
                XToastUtils.info("已断开连接");
                binding.btLedconnection.setText("连接");
            }
        });
    }

    @Override
    protected void initListeners() {
        binding.status.setOnCompleteClickListener(v -> binding.status.dismiss());
        binding.status.setOnErrorClickListener(v -> binding.status.dismiss());
        binding.status.setOnLoadingClickListener(v -> binding.status.dismiss());
        ledListener = new LedListener() {
            @Override
            public void onSuccess(boolean b) {
                if (b) {
                    Log.i(TAG, "onSuccess: 发送成功!");
                    XToastUtils.success("发送成功!");
                } else {
                    Log.i(TAG, "onSuccess: 发送失败!");
                    XToastUtils.error("发送失败!");
                }
            }

            @Override
            public void onFail(Exception e) {
                Log.i(TAG, "onSuccess: 发送异常:" + e.toString());
                XToastUtils.warning("发送异常!");
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ledScreen != null) {
            ledScreen.stopConnect();
        }
    }
}