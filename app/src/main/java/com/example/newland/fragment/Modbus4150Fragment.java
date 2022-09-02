package com.example.newland.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentModbus4150Binding;
import com.example.newland.utils.XToastUtils;
import com.nle.mylibrary.forUse.mdbus4150.MdBus4150RelayListener;
import com.nle.mylibrary.forUse.mdbus4150.MdBus4150SensorListener;
import com.nle.mylibrary.forUse.mdbus4150.Modbus4150;
import com.nle.mylibrary.transfer.DataBusFactory;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.widget.popupwindow.status.Status;
import com.xuexiang.xutil.XUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Page(name = "Modbus4150")
public class Modbus4150Fragment extends BaseFragment {
    private static final String TAG = "Modbus4150Fragment";
    FragmentModbus4150Binding binding;
    private Modbus4150 modbus4150;
    private MdBus4150RelayListener mdBus4150RelayListener;
    private MMKV kv;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentModbus4150Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        return null;
//        return super.initTitleBar();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViews() {
        kv = MMKV.mmkvWithID("InetInfo", MMKV.MULTI_PROCESS_MODE);
        binding.flowlayout4150DO.clearSelection();
        binding.select4150Mode.setOnTabSelectionChangedListener((title, value) -> {
            if (value.equals("1")) {
                binding.bt4150connection.setLabelText("网络");
                binding.bt4150connection.setLabelOrientation(1);
            } else if (value.equals("2")) {
                binding.bt4150connection.setLabelText("串口");
                binding.bt4150connection.setLabelOrientation(2);
            }
        });
        binding.adam4150Ip.setText("ip地址: " + kv.decodeString("ipaddress_4150", "请在设置中配置连接参数"));
        binding.adam4150Prot.setText("端口: " + kv.decodeInt("port_4150", 1000));
        binding.flowlayout4150DO.setOnTagSelectListener((parent, position, selectedList) -> {
            try {
                if (!selectedList.contains(position)) {
                    modbus4150.ctrlRelay(position, false, mdBus4150RelayListener);
                    Log.i("tag", "4150: 关闭继电器" + position);
                } else {
                    modbus4150.ctrlRelay(position, true, mdBus4150RelayListener);
                    Log.i("tag", "4150: 打开继电器" + position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.tvCheckState.setOnClickListener(view -> new Thread(() -> {
            List<Integer> do_stat = new ArrayList<>();
            for (int o = 0; o < 8; o++) {
                try {
                    int finalO = o;
                    modbus4150.getDOVal(o, new MdBus4150SensorListener() {
                        @Override
                        public void onVal(int i) {
                            if (i == 1) {
                                do_stat.add(finalO);
                            }
                        }

                        @Override
                        public void onFail(Exception e) {

                        }
                    });
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            XUtil.runOnUiThread(() -> {
                if (do_stat.size() == 0) {
                    binding.flowlayout4150DO.clearSelection();
                } else {
                    binding.flowlayout4150DO.setSelectedPositions(do_stat);
                    XToastUtils.success("更新成功!");
                }
            });
        }).start());
        binding.tvClear4150Select.setOnClickListener(view -> binding.flowlayout4150DO.clearSelection());
        binding.bt4150connection.setOnClickListener(view -> {
            if (modbus4150 == null) {
                if (binding.bt4150connection.getLabelText().equals("网络")) {
                    modbus4150 = new Modbus4150(DataBusFactory.newSocketDataBus(kv.decodeString("ipaddress_4150", "192.168.0.1"),
                            kv.decodeInt("port_4150", 1000)), b -> {
                        if (b) {
                            binding.status.setStatus(Status.COMPLETE);
                            binding.bt4150connection.setText("Socket已连接");
                        } else {
                            binding.status.setStatus(Status.ERROR);
                            modbus4150.stopConnect();
                            modbus4150 = null;
                        }
                    });
                } else {
                    modbus4150 = new Modbus4150(DataBusFactory.newSerialDataBus(binding.serialPorts4150.getSelectedIndex(), 9600), b -> {
                        if (b) {
                            binding.status.setStatus(Status.COMPLETE);
                            binding.bt4150connection.setText("串口已连接");
                        } else {
                            binding.status.setStatus(Status.ERROR);
                            modbus4150.stopConnect();
                            modbus4150 = null;
                        }
                    });
                }

                binding.status.setStatus(Status.LOADING);
            } else {
                modbus4150.stopConnect();
                modbus4150 = null;
                XToastUtils.info("已断开连接");
                binding.bt4150connection.setText("连接");
            }
        });

        binding.btOpen4150.setOnClickListener(view -> new Thread(() -> {
            for (int i = 0; i <= 7; i++) {
                try {
                    modbus4150.ctrlRelay(i, true, mdBus4150RelayListener);
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start());
        binding.btClose4150.setOnClickListener(view -> new Thread(() -> {
            for (int i = 0; i <= 7; i++) {
                try {
                    modbus4150.ctrlRelay(i, false, mdBus4150RelayListener);
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start());
        binding.tvGetdiInfo.setOnClickListener(view -> new Thread(() -> {
            ArrayList<TextView> textViewArrayList = new ArrayList<>();
            Collections.addAll(textViewArrayList, binding.tvDi0Info, binding.tvDi1Info, binding.tvDi2Info, binding.tvDi3Info, binding.tvDi4Info, binding.tvDi5Info, binding.tvDi6Info);
            ArrayList<Integer> di_info_list = new ArrayList<>();
            for (int di = 0; di < 7; di++) {
                try {
                    modbus4150.getDIVal(di, new MdBus4150SensorListener() {
                        @Override
                        public void onVal(int i) {
                            di_info_list.add(i);
                        }

                        @Override
                        public void onFail(Exception e) {

                        }
                    });
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                XUtil.runOnUiThread(() -> {
                    if (di_info_list.isEmpty()) {
                        XToastUtils.error("获取失败!");
                        return;
                    }
                    for (int i = 0; i < textViewArrayList.size(); i++) {
                        textViewArrayList.get(i).setText(di_info_list.get(i) + " ");
                    }
                    XToastUtils.success("获取成功!");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start());
    }

    @Override
    protected void initListeners() {
        binding.status.setOnCompleteClickListener(v -> binding.status.dismiss());
        binding.status.setOnErrorClickListener(v -> binding.status.dismiss());
        binding.status.setOnLoadingClickListener(v -> binding.status.dismiss());
        mdBus4150RelayListener = new MdBus4150RelayListener() {
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
        if (modbus4150 != null) {
            modbus4150.stopConnect();
        }
    }
}