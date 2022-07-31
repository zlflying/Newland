package com.example.newland.fragment;

import static java.lang.String.format;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentZigbeeBinding;
import com.example.newland.utils.XToastUtils;
import com.nle.mylibrary.forUse.zigbee.ZigBee;
import com.nle.mylibrary.forUse.zigbee.ZigBeeControlListener;
import com.nle.mylibrary.transfer.DataBusFactory;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.widget.popupwindow.status.Status;
import com.xuexiang.xutil.XUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Page(name = "ZigBee")
public class ZigBeeFragment extends BaseFragment {
    private static final String TAG = "ZigBeeFragment";
    FragmentZigbeeBinding binding;
    private MMKV kv;
    private List<Integer> zigbee_sensor_list = new ArrayList<>();
    private ZigBee zigBee;
    //zigbee传感器数据
    private double val0;
    private double val1;
    private double val2;
    private double val3;
    private double val4;
    private double val5;
    private double val6;
    private double[] val7;
    private double val8;
    private ZigBeeControlListener zigBeeControlListener;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentZigbeeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        return null;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void initViews() {
        kv = MMKV.mmkvWithID("InetInfo", MMKV.MULTI_PROCESS_MODE);
        ArrayList<TextView> textViewArrayList = new ArrayList<>();
        binding.selectZigbeeMode.setOnTabSelectionChangedListener((title, value) -> {
            if (value.equals("5")) {
                binding.btZigbeeconnection.setLabelText("网络");
                binding.btZigbeeconnection.setLabelOrientation(1);
            } else if (value.equals("6")) {
                binding.btZigbeeconnection.setLabelText("串口");
                binding.btZigbeeconnection.setLabelOrientation(2);
            }
        });
        binding.zigbeeIp.setText("ip地址: " + kv.decodeString("ipaddress_zigbee", "请在设置中配置连接参数"));
        binding.zigbeeProt.setText("端口: " + kv.decodeInt("port_zigbee", 1000));

        binding.flowlayoutZigbeeInfo.setOnTagSelectListener((parent, position, selectedList) -> zigbee_sensor_list = selectedList);

        binding.btZigbeeconnection.setOnClickListener(view -> {
            if (zigBee == null) {
                if (binding.btZigbeeconnection.getLabelText().equals("网络")) {
                    zigBee = new ZigBee(DataBusFactory.newSocketDataBus(kv.decodeString("ipaddress_zigbee", "192.168.0.1"),
                            kv.decodeInt("port_zigbee", 1000)), b -> {
                        if (b) {
                            binding.status.setStatus(Status.COMPLETE);
                            binding.btZigbeeconnection.setText("Socket已连接");
                        } else {
                            binding.status.setStatus(Status.ERROR);
                            zigBee.stopConnect();
                            zigBee = null;
                        }
                    });
                } else {
                    zigBee = new ZigBee(DataBusFactory.newSerialDataBus(binding.serialPortsZigbee.getSelectedIndex(), 38400),
                            b -> {
                                if (b) {
                                    binding.status.setStatus(Status.COMPLETE);
                                    binding.btZigbeeconnection.setText("串口已连接");
                                } else {
                                    binding.status.setStatus(Status.ERROR);
                                    zigBee.stopConnect();
                                    zigBee = null;
                                }
                            });
                }
                binding.status.setStatus(Status.LOADING);
            } else {
                zigBee.stopConnect();
                zigBee = null;
                XToastUtils.info("已断开连接");
                binding.btZigbeeconnection.setText("连接");
            }
        });

        Collections.addAll(textViewArrayList, binding.zGetTemInfo, binding.zGetHumInfo, binding.zGetAlcoholInfo, binding.zGetCOInfo, binding.zGetFireInfo, binding.zGetLightInfo, binding.zGetPersonInfo, binding.zGetWeightInfo, binding.zGetGasInfo);
        binding.tvGetzigbeeInfo.setOnClickListener(view -> new Thread(() -> {
            try {
                for (Integer integer : zigbee_sensor_list) {
                    switch (integer) {
                        case 0:
                            val0 = zigBee.getTmpHum()[0];
                            break;
                        case 1:
                            val1 = zigBee.getTmpHum()[1];
                            break;
                        case 2:
                            val2 = zigBee.getAlcohol();
                            break;
                        case 3:
                            val3 = zigBee.getCO();
                            break;
                        case 4:
                            val4 = zigBee.getFire();
                            break;
                        case 5:
                            val5 = zigBee.getLight();
                            break;
                        case 6:
                            val6 = zigBee.getPerson();
                            break;
                        case 7:
                            val7 = zigBee.getWeight();
                            break;
                        case 8:
                            val8 = zigBee.getGas();
                            break;
                    }
                }

                XUtil.runOnUiThread(() -> {
                    for (Integer integer : zigbee_sensor_list) {
                        TextView textView = textViewArrayList.get(integer);
                        switch (integer) {
                            case 0:
                                textView.setText(val0 + "℃");
                                break;
                            case 1:
                                textView.setText(val1 + "%RH");
                                break;
                            case 2:
                                textView.setText(val2 + " ");
                                break;
                            case 3:
                                textView.setText(val3 + " ");
                                break;
                            case 4:
                                textView.setText(val4 + " ");
                                break;
                            case 5:
                                textView.setText(val5 + " ");
                                break;
                            case 6:
                                textView.setText(val6 + " ");
                                break;
                            case 7:
                                textView.setText(format("%.2f", val7[0]) + " ");
                                break;
                            case 8:
                                textView.setText(val8 + " ");
                                break;
                            default:
                                textView.setText("你还没有选择传感器类型");
                                break;
                        }
                    }
                });
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start());


        binding.swDoubleRelay.setOnCheckedChangeListener((compoundButton, b) -> {
            try {
                zigBee.ctrlDoubleRelay(Integer.parseInt(Objects.requireNonNull(binding.edCtrlDoubleRelay.getText()).toString()), 1, b, zigBeeControlListener);
                zigBee.ctrlDoubleRelay(Integer.parseInt(binding.edCtrlDoubleRelay.getText().toString()), 2, b, zigBeeControlListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.swSingleRelay.setOnCheckedChangeListener((compoundButton, b) -> {
            try {
                zigBee.ctrlSingleRelay(Integer.parseInt(Objects.requireNonNull(binding.edCtrlSingleRelay.getText()).toString()), b, zigBeeControlListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void initListeners() {
        binding.status.setOnCompleteClickListener(v -> binding.status.dismiss());
        binding.status.setOnErrorClickListener(v -> binding.status.dismiss());
        binding.status.setOnLoadingClickListener(v -> binding.status.dismiss());
        zigBeeControlListener = new ZigBeeControlListener() {
            @Override
            public void onCtrl(boolean b) {
                Log.i(TAG, "onCtrl: " + b);
            }

            @Override
            public void onFail(Exception e) {
                Log.i(TAG, "onFail: " + e.toString());
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (zigBee != null) {
            zigBee.stopConnect();
        }
    }

}