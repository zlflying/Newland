package com.example.newland.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newland.adapter.FragmentCacheAdapter;
import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentNavigationViewBinding;
import com.example.newland.utils.MultiPage;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.tabbar.TabSegment;
import com.xuexiang.xutil.XUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Page(name = "NavigationView", anim = CoreAnim.fade)
public class NavigationViewFragment extends BaseFragment implements TabSegment.OnTabSelectedListener {

    FragmentNavigationViewBinding binding;
    private FragmentCacheAdapter mAdapter;

    String[] pages = MultiPage.getPageNames();
    private int TAB_COUNT = MultiPage.size();
    private MultiPage mDestPage = MultiPage.首页;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentNavigationViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        TitleBar titleBar = super.initTitleBar();
        titleBar.setTitle("NewLand测试")
                .setLeftClickListener(view -> {
                    XUtil.exitApp();
                })
                .addAction(new TitleBar.TextAction("设置网络参数") {
                    @Override
                    public void performAction(View view) {
                        PageOption.to(SettingNetworkFragment.class)
                                .setAnim(CoreAnim.slide)
                                .open(NavigationViewFragment.this);
                    }
                });
        return titleBar;
    }

    @Override
    protected void initViews() {
        initTabLayout();
        initTabFragment();
    }

    private void initTabFragment() {

        List<XPageFragment> fragmentList = new ArrayList<>();
        Collections.addAll(fragmentList,
                new FirstpageFragment(),
                new Modbus4150Fragment(),
                new Modbus4017Fragment(),
                new ZigBeeFragment(),
                new RFIDFragment(),
                new LedScreenFragment(),
                new CameraFragment());
        for (int i = 0; i < fragmentList.size(); i++) {
            mAdapter.addFragment(fragmentList.get(i), pages[i]);
        }

        mAdapter.notifyDataSetChanged();
    }

    private void initTabLayout() {
        mAdapter = new FragmentCacheAdapter(getChildFragmentManager());
        binding.contentViewPager.setAdapter(mAdapter);
        binding.contentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        // 设置缓存的数量
        binding.contentViewPager.setOffscreenPageLimit(TAB_COUNT);


        int space = DensityUtils.dp2px(XUI.getContext(), 16);
        binding.tabSegment.setHasIndicator(true);
        binding.tabSegment.setMode(TabSegment.MODE_SCROLLABLE);
        binding.tabSegment.setItemSpaceInScrollMode(space);
        binding.tabSegment.setupWithViewPager(binding.contentViewPager, true);
        binding.tabSegment.setPadding(space, 0, space, 0);

    }

    @Override
    protected void initListeners() {
        binding.tabSegment.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(int index) {
        SnackbarUtils.Short(getView(), "选择: " + pages[index]).info().show();
    }

    @Override
    public void onTabUnselected(int index) {

    }

    @Override
    public void onTabReselected(int index) {

    }

    @Override
    public void onDoubleTap(int index) {

    }
}