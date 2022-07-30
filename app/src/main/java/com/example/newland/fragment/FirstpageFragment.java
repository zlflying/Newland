package com.example.newland.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import com.xuexiang.xui.XUI;
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
    }

    @Override
    protected void initViews() {
        binding.btGetip.setOnClickListener(view -> {
            MyUtils.setVIBRATE(getContext());
            CookieBar.builder(getActivity())
                    .setTitle("本机 IP 地址")
                    .setMessage(MyUtils.getIPAddress(XUI.getContext()))
                    .setBackgroundColor(R.color.tips)
                    .setTitleColor(android.R.color.white)
                    .setLayoutGravity(Gravity.TOP)
                    .show();
        });

        doScaleAnimation(binding.imageView);
//        doTranslationAnimation(binding.imageView);
        doRotationAnimation(binding.imageView);
    }

    /**
     * 缩放动画
     *
     * @param view
     */
    private void doScaleAnimation(View view) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1F, 0.5F, 1F, 0.5F, 1.2F, 1F);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1F, 0.5F, 1F, 0.5F, 1.2F, 1F);
        animatorX.setRepeatCount(ValueAnimator.INFINITE);
        animatorY.setRepeatCount(ValueAnimator.INFINITE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animatorX).with(animatorY);
        animatorSet.setDuration(10000);
        animatorSet.start();
    }

    /**
     * 平移动画
     *
     * @param view
     */
    private void doTranslationAnimation(View view) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "translationX", 0, 200, 0, -200, 0);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "translationY", 0, 200, 0, -200, 0);
        animatorX.setRepeatCount(ValueAnimator.INFINITE);
        animatorY.setRepeatCount(ValueAnimator.INFINITE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animatorX).before(animatorY);
        animatorSet.setDuration(2000);
        animatorSet.start();
    }


    /**
     * 旋转动画
     *
     * @param view
     */
    private void doRotationAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, 360, 0);
        animator.setDuration(10000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

    }

    @Override
    protected void initListeners() {

    }
}