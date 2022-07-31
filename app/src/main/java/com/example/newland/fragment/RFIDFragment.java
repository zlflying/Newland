package com.example.newland.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentRfidBinding;
import com.example.newland.utils.XToastUtils;
import com.nle.mylibrary.forUse.rfid.RFID;
import com.nle.mylibrary.forUse.rfid.SingleEpcListener;
import com.nle.mylibrary.transfer.DataBusFactory;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.widget.popupwindow.status.Status;
import com.xuexiang.xutil.XUtil;

@Page(name = "RFID")
public class RFIDFragment extends BaseFragment {
    FragmentRfidBinding binding;
    private MMKV kv;
    private RFID rfid;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentRfidBinding.inflate(inflater, container, false);
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
        CountDownButtonHelper countDownButtonHelper = new CountDownButtonHelper(binding.btReadLoop, 9);
        binding.selectRfidMode.setOnTabSelectionChangedListener((title, value) -> {
            if (value.equals("7")) {
                binding.btRfidconnection.setLabelText("网络");
                binding.btRfidconnection.setLabelOrientation(1);
            } else if (value.equals("8")) {
                binding.btRfidconnection.setLabelText("串口");
                binding.btRfidconnection.setLabelOrientation(2);
            }
        });
        binding.rfidIp.setText("ip地址: " + kv.decodeString("ipaddress_rfid", "请在设置中配置连接参数"));
        binding.rfidProt.setText("端口: " + kv.decodeInt("port_rfid", 1000));
        binding.btRfidconnection.setOnClickListener(view -> {
            if (rfid == null) {
                if (binding.btRfidconnection.getLabelText().equals("网络")) {
                    rfid = new RFID(DataBusFactory.newSocketDataBus(kv.decodeString("ipaddress_rfid", "192.168.0.1"),
                            kv.decodeInt("port_rfid", 1000)), b -> {
                        if (b) {
                            binding.status.setStatus(Status.COMPLETE);
                            binding.btRfidconnection.setText("Socket已连接");
                        } else {
                            binding.status.setStatus(Status.ERROR);
                            rfid.stopConnect();
                            rfid = null;
                        }
                    });
                } else {
                    rfid = new RFID(DataBusFactory.newSerialDataBus(binding.serialPortsRfid.getSelectedIndex(), 115200),
                            b -> {
                                if (b) {
                                    binding.status.setStatus(Status.COMPLETE);
                                    binding.btRfidconnection.setText("串口已连接");
                                } else {
                                    binding.status.setStatus(Status.ERROR);
                                    rfid.stopConnect();
                                    rfid = null;
                                }
                            });
                }
                binding.status.setStatus(Status.LOADING);
            } else {
                rfid.stopConnect();
                rfid = null;
                XToastUtils.info("已断开连接");
                binding.btRfidconnection.setText("连接");
            }
        });
        binding.ivReadRfid.setOnClickListener(view -> {
            try {
                rfid.readSingleEpc(new SingleEpcListener() {
                    @Override
                    public void onVal(String s) {
                        XToastUtils.success("读取成功!");
                        binding.tvRfidEpc.setText(s);
                    }

                    @Override
                    public void onFail(Exception e) {
                        binding.tvRfidEpc.setText("读取异常!");
                    }
                }, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.btReadLoop.setOnClickListener(v1 -> countDownButtonHelper.start());
        countDownButtonHelper.setOnCountDownListener(new CountDownButtonHelper.OnCountDownListener() {
            @Override
            public void onCountDown(int time) {
                binding.btReadLoop.setText("已读取" + (10 - time) + "次");
                try {
                    rfid.readSingleEpc(new SingleEpcListener() {
                        @Override
                        public void onVal(String s) {
                            XUtil.runOnUiThread(() -> {
                                binding.tvRfidEpc.setText(s);
                            });
                        }

                        @Override
                        public void onFail(Exception e) {
                            binding.tvRfidEpc.setText("error");
                        }
                    }, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                binding.btReadLoop.setText("点击重试");
            }
        });
    }

    @Override
    protected void initListeners() {
        binding.status.setOnCompleteClickListener(v -> binding.status.dismiss());
        binding.status.setOnErrorClickListener(v -> binding.status.dismiss());
        binding.status.setOnLoadingClickListener(v -> binding.status.dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rfid != null) {
            rfid.stopConnect();
        }
    }

}