package com.example.appwork.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appwork.R;
import com.example.appwork.base.BaseActivity;
import com.example.appwork.model.NewsCategory;
import com.example.appwork.model.UserInfo;
import com.example.appwork.ui.adapter.ViewPagerAdapter;
import com.example.appwork.ui.fragment.NewsListFragment;
import com.example.appwork.ui.fragment.UserCenterFragment;
import com.example.appwork.utils.SPUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private UserCenterFragment userCenterFragment;
    private View newsContainer;
    private FrameLayout userFragmentContainer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void initView() {
        Log.d(TAG, "initView: Starting initialization");

        try {
            // 初始化Fragment
            userCenterFragment = new UserCenterFragment();

            // 查找视图
            TabLayout tabLayout = findViewById(R.id.tab_layout);
            ViewPager2 viewPager = findViewById(R.id.view_pager);
            newsContainer = findViewById(R.id.news_container);
            userFragmentContainer = findViewById(R.id.user_fragment_container);

            // 设置ViewPager2的Adapter
            List<NewsCategory> categories = Arrays.asList(NewsCategory.values());
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, categories);
            viewPager.setAdapter(adapter);

            // 关联TabLayout和ViewPager2
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                tab.setText(categories.get(position).getName());
            }).attach();

            // 设置底部导航点击事件
            TextView tvTabNews = findViewById(R.id.tv_tab_news);
            TextView tvTabUser = findViewById(R.id.tv_tab_user);

            if (tvTabNews == null || tvTabUser == null) {
                Log.e(TAG, "Navigation views not found");
                return;
            }

            // 设置底部导航点击事件
            tvTabNews.setOnClickListener(v -> {
                Log.d(TAG, "News tab clicked");
                tvTabNews.setTextColor(getResources().getColor(R.color.purple_500));
                tvTabUser.setTextColor(getResources().getColor(android.R.color.black));
                showNewsView();
            });

            tvTabUser.setOnClickListener(v -> {
                Log.d(TAG, "User tab clicked");
                tvTabUser.setTextColor(getResources().getColor(R.color.purple_500));
                tvTabNews.setTextColor(getResources().getColor(android.R.color.black));
                showUserCenterView();
            });

            // 默认显示新闻列表并设置选中状态
            tvTabNews.setTextColor(getResources().getColor(R.color.purple_500));
            tvTabUser.setTextColor(getResources().getColor(android.R.color.black));
            showNewsView();

            // 将UserCenterFragment添加到容器中
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.user_fragment_container, userCenterFragment)
                    .commit();

        } catch (Exception e) {
            Log.e(TAG, "Error in initView", e);
            e.printStackTrace();
        }
    }

    private void showNewsView() {
        newsContainer.setVisibility(View.VISIBLE);
        userFragmentContainer.setVisibility(View.GONE);
    }

    private void showUserCenterView() {
        newsContainer.setVisibility(View.GONE);
        userFragmentContainer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        Log.d(TAG, "initData: Checking login status");
        if (SPUtils.isLogin(this)) {
            UserInfo userInfo = SPUtils.getUserInfo(this);
            if (userCenterFragment != null) {
                Log.d(TAG, "Updating UI for logged in user: " + userInfo.getUsername());
                userCenterFragment.updateUIForLogin(userInfo);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // 检查当前显示的Fragment
        // The logic for checking the current fragment visibility is removed as ViewPager2 handles the visibility.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}