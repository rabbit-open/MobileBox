package com.hualala.mobilebox.module.boot.contarct;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hualala.bi.framework.base.BaseContractor;
import com.hualala.bi.framework.base.BaseFragment;
import com.hualala.bi.framework.base.TabFragmentPagerAdapter;
import com.hualala.mobilebox.R;
import com.hualala.mobilebox.R2;
import com.hualala.mobilebox.module.UINavgation;
import com.hualala.mobilebox.module.boot.view.PictureListFragment;
import com.hualala.mobilebox.module.setting.SetListFragment;
import com.hualala.mobilebox.module.tv.TvListFragment;
import com.hualala.ui.widget.CommonHeader;
import com.hualala.ui.widget.viewpager.NoAnimationViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainTabVpContractor extends BaseContractor {

    @BindView(R2.id.tab)
    public TabLayout mTabLayout;

    @BindView(R2.id.commonHeader)
    public CommonHeader commonHeader;

    @BindView(R2.id.viewpager)
    public NoAnimationViewPager mViewPager;

    private List<String> tabIndicators;
    private List<BaseFragment> tabFragments;
    private ContentPagerAdapter tabAdapter;

    public MainTabVpContractor(FragmentActivity lifecycleOwner) {
        super(lifecycleOwner);

        View rootView = LayoutInflater.from(lifecycleOwner)
                .inflate(R.layout.activity_main_tabvp, null);
        lifecycleOwner.setContentView(rootView);

        ButterKnife.bind(this, rootView);

        tabIndicators = new ArrayList<>();
        tabIndicators.add("图片");
        tabIndicators.add("视频");
        tabIndicators.add("音乐");
        tabIndicators.add("电视");
        tabIndicators.add("设置");

        tabFragments = new ArrayList<>();
        tabFragments.add(PictureListFragment.newInstance(1));
        tabFragments.add(PictureListFragment.newInstance(2));
        tabFragments.add(PictureListFragment.newInstance(3));
        tabFragments.add(TvListFragment.newInstance());
        tabFragments.add(SetListFragment.newInstance());
        tabAdapter = new ContentPagerAdapter(mViewPager, getLifecycleOwner().getSupportFragmentManager());
        mViewPager.setAdapter(tabAdapter);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < tabIndicators.size(); i++) {
            TabLayout.Tab itemTab = mTabLayout.getTabAt(i);

            if (itemTab != null) {
                itemTab.setCustomView(R.layout.item_tab_item);
                TextView itemTv = itemTab.getCustomView().findViewById(R.id.tv_menu_item);
                itemTv.setText(tabIndicators.get(i));
            }
        }

        mTabLayout.getTabAt(0).select();
        commonHeader.getTitleTextView().setText(tabIndicators.get(0));
        
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                BaseFragment fragment = tabAdapter.findFragment(position);
                if (fragment != null) {
                    fragment.manualProcess();
                }

                commonHeader.getTitleTextView().setText(tabIndicators.get(position));
                mTabLayout.getTabAt(position).select();
            }
        });

        commonHeader.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UINavgation.startDevicesListActivity(getContext());
            }
        });

        commonHeader.getLeftButton().setVisibility(View.INVISIBLE);
    }

    class ContentPagerAdapter extends TabFragmentPagerAdapter {


        public ContentPagerAdapter(ViewPager viewPager, FragmentManager fm) {
            super(viewPager, fm);
        }

        @Override
        public BaseFragment getItemFragment(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabIndicators.get(position);
        }
    }
}
