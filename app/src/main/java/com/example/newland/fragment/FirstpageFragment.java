package com.example.newland.fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newland.R;
import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentFirstpageBinding;
import com.example.newland.utils.MyUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;

@Page(name = "首页")
public class FirstpageFragment extends BaseFragment {
    FragmentFirstpageBinding binding;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentFirstpageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        return null;
//        return super.initTitleBar();
    }

    @Override
    protected void initViews() {
        binding.btGetip.setOnClickListener(view -> {
            MyUtils.setVIBRATE(getContext());
            CookieBar.builder(getActivity())
                    .setTitle("本机 IP 地址")
                    .setMessage(MyUtils.getIPAddress(getContext()))
                    .setBackgroundColor(R.color.tips)
                    .setTitleColor(android.R.color.white)
                    .setLayoutGravity(Gravity.TOP)
                    .show();
        });
//        XGPushManager.registerPush(this, new XGIOperateCallback() {
//            @Override
//            public void onSuccess(Object data, int flag) {
//                //token在设备卸载重装的时候有可能会变
//                Log.d("TPush", "注册成功，设备token为：" + data);
//                binding.token.setText((String) data);
//            }
//
//            @Override
//            public void onFail(Object data, int errCode, String msg) {
//                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
//            }
//        });

        binding.copyToken.setOnClickListener(view -> {
            String token_str = binding.token.getText().toString().trim();
            MyUtils.sendClipboard(getContext(), "token", token_str);
        });
    }

    @Override
    protected void initListeners() {

    }
}