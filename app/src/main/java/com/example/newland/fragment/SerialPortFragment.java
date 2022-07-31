package com.example.newland.fragment;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newland.R;
import com.example.newland.databinding.FragmentSerialportBinding;
import com.io.Ports.SerialPortEx;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.widget.popupwindow.status.Status;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.data.DateUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;

@Page(name = "SerialPort")
public class SerialPortFragment extends XPageFragment {

    private static final String TAG = "SerialPortFragment";
    FragmentSerialportBinding binding;
    private SerialPortEx serialPortEx;
    private boolean serialMode = true;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentSerialportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void initViews() {
        binding.logger.setLogFormatter((logContent, logType) -> DateUtils.getNowString(new SimpleDateFormat("[HH:mm:ss]:")) + logContent);
    }

    @Override
    protected void initListeners() {
        binding.status.setOnCompleteClickListener(v -> binding.status.dismiss());
        binding.status.setOnErrorClickListener(v -> binding.status.dismiss());
        binding.status.setOnLoadingClickListener(v -> binding.status.dismiss());
        binding.selectSerialMode.setOnTabSelectionChangedListener((title, value) -> {
            if (value.equals("1")) {
                serialMode = true;
            } else {
                serialMode = false;
            }
        });
        binding.btSerialportconnection.setOnClickListener(view -> {
            binding.status.setStatus(Status.LOADING);
            @SuppressLint("Recycle")
            TypedArray baudrate_list = getResources().obtainTypedArray(R.array.ports_baudrate);
            int baudrate = baudrate_list.getInt(binding.portBaudrate.getSelectedIndex(), 38400);
            binding.logger.logSuccess(getResources().getStringArray(R.array.serial_ports)[binding.serialPorts.getSelectedIndex()] + "---" + baudrate);
            if (serialPortEx == null) {
                serialPortEx = new SerialPortEx(getResources().getStringArray(R.array.serial_ports)[binding.serialPorts.getSelectedIndex()],
                        baudrate);
                if (serialPortEx.Open()) {
                    binding.status.setStatus(Status.COMPLETE);
                    binding.btSerialportconnection.setText("打开成功");
                    setSerialListeners();
                } else {
                    binding.status.setStatus(Status.ERROR);
                }
            } else {
                if (serialPortEx.getIsOpend()) {
                    serialPortEx.Close();
                }
                serialPortEx = null;
            }
        });
    }

    private void setSerialListeners() {
        serialPortEx.setOnDataReceiveListener((bytes, i) -> {
            try {
                if (serialMode) {
                    String data = new String(bytes, 0, i, StandardCharsets.UTF_8);
                    XUtil.runOnUiThread(() -> binding.logger.logSuccess(data));
                } else {
                    XUtil.runOnUiThread(() -> binding.logger.logSuccess(Arrays.toString(bytes)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}