package com.example.newland.fragment;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentCameraBinding;
import com.example.newland.utils.XToastUtils;
import com.newland.CameraManager;
import com.newland.PTZ;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.popupwindow.status.Status;

@Page(name = "Camera")
public class CameraFragment extends BaseFragment {
    FragmentCameraBinding binding;
    private CameraManager cameraManager;
    private final Handler handler = new Handler();
    private MMKV kv;


    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        return null;
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    protected void initViews() {
        kv = MMKV.mmkvWithID("InetInfo", MMKV.MULTI_PROCESS_MODE);

        binding.cameraIp.setText("ip地址: " + kv.decodeString("ipaddress_camera", "请在设置中配置连接参数"));
        binding.cameraProt.setText("通道号: " + kv.decodeString("channel_camera", "11"));

        binding.btCameraconnection.setOnClickListener(view -> {
            binding.status.setStatus(Status.LOADING);
            cameraManager = CameraManager.getInstance();
            cameraManager.setBaseInfo(binding.playerView,
                            kv.decodeString("username_camera", "admin"),
                            kv.decodeString("password_camera", "admin"),
                            kv.decodeString("ipaddress_camera", "192.168.0.1"),
                            kv.decodeString("channel_camera", "11"))
                    .videoSetting(XUI.getContext());
            handler.postDelayed(() -> {
                try {
                    cameraManager.openCamera();
                } catch (Exception e) {
                    e.printStackTrace();
                    XToastUtils.error(e);
                    binding.status.setStatus(Status.ERROR);
                }
            }, 2000);
        });

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListeners() {
        binding.status.setOnCompleteClickListener(v -> binding.status.dismiss());
        binding.status.setOnErrorClickListener(v -> binding.status.dismiss());
        binding.status.setOnLoadingClickListener(v -> binding.status.dismiss());
        binding.btCapture.setOnClickListener(view -> {
            try {
                String fileData = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = System.currentTimeMillis() + ".png";
                cameraManager.capture(fileData, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.btUp.setOnTouchListener((v1, event) -> {
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cameraManager.controlDir(PTZ.Up);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    cameraManager.controlDir(PTZ.Stop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
        binding.btLeft.setOnTouchListener((v1, event) -> {
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cameraManager.controlDir(PTZ.Left);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    cameraManager.controlDir(PTZ.Stop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
        binding.btRight.setOnTouchListener((v1, event) -> {
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cameraManager.controlDir(PTZ.Right);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    cameraManager.controlDir(PTZ.Stop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
        binding.btDown.setOnTouchListener((v1, event) -> {
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cameraManager.controlDir(PTZ.Down);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    cameraManager.controlDir(PTZ.Stop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
    }
}