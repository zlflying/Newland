package com.example.newland.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentModbus4017Binding;
import com.example.newland.utils.XToastUtils;
import com.nle.mylibrary.forUse.mdbus4017.MD4017;
import com.nle.mylibrary.forUse.mdbus4017.MD4017ValConvert;
import com.nle.mylibrary.forUse.mdbus4017.MD4017ValListener;
import com.nle.mylibrary.forUse.mdbus4017.Md4017VIN;
import com.nle.mylibrary.transfer.DataBusFactory;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.widget.popupwindow.status.Status;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xutil.XUtil;

import java.util.ArrayList;
import java.util.Collections;

@Page(name = "Modbus4017")
public class Modbus4017Fragment extends BaseFragment {
    FragmentModbus4017Binding binding;
    private MD4017 md4017;
    private MMKV kv;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentModbus4017Binding.inflate(inflater, container, false);
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
        ArrayList<TextView> textViewArrayList = new ArrayList<>();
        ArrayList<MaterialSpinner> materialSpinnerArrayList = new ArrayList<>();
        int[] sensor = new int[8];
        binding.select4017Mode.setOnTabSelectionChangedListener((title, value) -> {
            if (value.equals("3")) {
                binding.bt4017connection.setLabelText("网络");
                binding.bt4017connection.setLabelOrientation(1);
            } else if (value.equals("4")) {
                binding.bt4017connection.setLabelText("串口");
                binding.bt4017connection.setLabelOrientation(2);
            }
        });
        Collections.addAll(textViewArrayList, binding.tvVin0, binding.tvVin1, binding.tvVin2, binding.tvVin3, binding.tvVin4, binding.tvVin5, binding.tvVin6, binding.tvVin7);
        binding.adam4017Ip.setText("ip地址: " + kv.decodeString("ipaddress_4017", "请在设置中配置连接参数"));
        binding.adam4017Prot.setText("端口: " + kv.decodeInt("port_4017", 1000));
        Collections.addAll(materialSpinnerArrayList, binding.ms4017Vin0selectSenor, binding.ms4017Vin1selectSenor, binding.ms4017Vin2selectSenor, binding.ms4017Vin3selectSenor,
                binding.ms4017Vin4selectSenor, binding.ms4017Vin5selectSenor, binding.ms4017Vin6selectSenor, binding.ms4017Vin7selectSenor);
        binding.bt4017connection.setOnClickListener(view -> {
            if (md4017 == null) {
                if (binding.bt4017connection.getLabelText().equals("网络")) {
                    md4017 = new MD4017(DataBusFactory.newSocketDataBus(kv.decodeString("ipaddress_4017", "192.168.0.1"),
                            kv.decodeInt("port_4017", 1000)), b -> {
                        if (b) {
                            binding.status.setStatus(Status.COMPLETE);
                            binding.bt4017connection.setText("Socket已连接");
                        } else {
                            binding.status.setStatus(Status.ERROR);
                            md4017.stopConnect();
                            md4017 = null;
                        }
                    });
                } else {
                    md4017 = new MD4017(DataBusFactory.newSerialDataBus(binding.serialPorts4017.getSelectedIndex(), 9600),
                            b -> {
                                if (b) {
                                    binding.status.setStatus(Status.COMPLETE);
                                    binding.bt4017connection.setText("串口已连接");
                                } else {
                                    binding.status.setStatus(Status.ERROR);
                                    md4017.stopConnect();
                                    md4017 = null;
                                }
                            });
                }
                binding.status.setStatus(Status.LOADING);
            } else {
                md4017.stopConnect();
                md4017 = null;
                XToastUtils.info("已断开连接");
                binding.bt4017connection.setText("连接");
            }
        });
        binding.tvGetvinInfo.setOnClickListener(view -> {
            XToastUtils.info("传感器数值将2秒更新一次");
            new Thread(() -> {
                try {
                    while (true) {
                        md4017.getVin(new MD4017ValListener() {
                            @Override
                            public void onVal(int[] ints) {
                                System.arraycopy(ints, 0, sensor, 0, ints.length);
                            }

                            @Override
                            public void onFail(Exception e) {
                            }

                        });
                        for (int i = 0; i < sensor.length; i++) {
                            String senorinfo = get4017Senorinfo(sensor[i], materialSpinnerArrayList.get(i).getSelectedIndex());
                            int finalI = i;
                            XUtil.runOnUiThread(() -> textViewArrayList.get(finalI).setText(senorinfo));
                        }
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    @Override
    protected void initListeners() {
        binding.status.setOnCompleteClickListener(v -> binding.status.dismiss());
        binding.status.setOnErrorClickListener(v -> binding.status.dismiss());
        binding.status.setOnLoadingClickListener(v -> binding.status.dismiss());
    }

    /**
     * 获取4017传感器类型转换数值
     *
     * @param senorInt    vin口电流值
     * @param selectIndex spinner index
     * @return 格式化转换后的数据
     */
    private String get4017Senorinfo(int senorInt, int selectIndex) {
        String data;
        switch (selectIndex) {
            case 1:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.TEM, senorInt) + Md4017VIN.TEM.getUnit();
                break;
            case 2:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.HUM, senorInt) + Md4017VIN.HUM.getUnit();
                break;
            case 3:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.LIGHT, senorInt) + Md4017VIN.LIGHT.getUnit();
                break;
            case 4:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.WIN, senorInt) + Md4017VIN.WIN.getUnit();
                break;
            case 5:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.PRE, senorInt) + Md4017VIN.PRE.getUnit();
                break;
            case 6:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.CO2, senorInt) + Md4017VIN.CO2.getUnit();
                break;
            case 7:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.AIR, senorInt) + Md4017VIN.AIR.getUnit();
                break;
            case 8:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.SOIL_TEM, senorInt) + Md4017VIN.SOIL_TEM.getUnit();
                break;
            case 9:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.SOIL_WATER, senorInt) + Md4017VIN.SOIL_WATER.getUnit();
                break;
            case 10:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.WATER_TEM, senorInt) + Md4017VIN.WATER_TEM.getUnit();
                break;
            case 11:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.WATER_LEV, senorInt) + Md4017VIN.WATER_LEV.getUnit();
                break;
            case 12:
                data = MD4017ValConvert.getRealValByType(Md4017VIN.NOISE, senorInt) + Md4017VIN.NOISE.getUnit();
                break;
            default:
                data = "请选择传感器类型";
                break;
        }
        return data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (md4017 != null) {
            md4017.stopConnect();
        }
    }

}