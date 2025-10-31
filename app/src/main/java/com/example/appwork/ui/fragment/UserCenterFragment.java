package com.example.appwork.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appwork.R;
import com.example.appwork.db.NewsDao;
import com.example.appwork.model.NewsItem;
import com.example.appwork.model.UserInfo;
import com.example.appwork.ui.activity.LoginActivity;
import com.example.appwork.ui.activity.MainActivity;
import com.example.appwork.ui.activity.NewsDetailActivity;
import com.example.appwork.ui.adapter.NewsAdapter;
import com.example.appwork.utils.SPUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.annotations.NonNull;

public class UserCenterFragment extends Fragment {
    private static final int REQUEST_CODE_LOGIN = 1001;
    private static final String TAG = "UserCenterFragment";


    private CircleImageView ivAvatar;
    private TextView tvUsername;
    private Button btnLogout;
    private RecyclerView rvFavorites, rvHistory;
    private TextView tvClearHistory;
    private NewsAdapter favoritesAdapter, historyAdapter;
    private NewsDao newsDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // 加载fragment_user_center布局
        return inflater.inflate(R.layout.fragment_user_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        
        // 初始化DAO
        newsDao = NewsDao.getInstance(requireContext());
        
        initView(view);
        // 初始化时检查并更新UI状态
        updateUI();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 每次回到该页面时刷新数据
        loadFavorites();
        loadHistory();
    }

    private void initView(View view) {
        Log.d(TAG, "initView");
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        btnLogout = view.findViewById(R.id.btn_logout);
        rvFavorites = view.findViewById(R.id.rv_favorites);
        rvHistory = view.findViewById(R.id.rv_history);
        tvClearHistory = view.findViewById(R.id.tv_clear_history);

        if (ivAvatar == null || tvUsername == null || btnLogout == null) {
            Log.e(TAG, "Failed to find views");
            return;
        }

        btnLogout.setOnClickListener(v -> {
            if (SPUtils.isLogin(requireContext())) {
                logout();
            } else {
                startLoginActivity();
            }
        });

        tvClearHistory.setOnClickListener(v -> {
            newsDao.clearHistory();
            Toast.makeText(requireContext(), "已清空历史记录", Toast.LENGTH_SHORT).show();
            loadHistory(); // 刷新历史记录列表
        });
        
        // 设置RecyclerView
        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        // 收藏列表
        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        favoritesAdapter = new NewsAdapter(requireContext());
        rvFavorites.setAdapter(favoritesAdapter);
        favoritesAdapter.setOnItemClickListener(this::navigateToDetail);

        // 历史记录列表
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyAdapter = new NewsAdapter(requireContext());
        rvHistory.setAdapter(historyAdapter);
        historyAdapter.setOnItemClickListener(this::navigateToDetail);
    }
    
    private void loadFavorites() {
        List<NewsItem> favoriteItems = newsDao.getFavorites();
        favoritesAdapter.setData(favoriteItems);
    }

    private void loadHistory() {
        List<NewsItem> historyItems = newsDao.getHistory();
        historyAdapter.setData(historyItems);
    }

    private void navigateToDetail(NewsItem newsItem) {
        Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ITEM, newsItem);
        startActivity(intent);
    }

    private void startLoginActivity() {
        try {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LOGIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK) {
            // 登录成功，更新UI
            updateUI();
            Log.d(TAG, "Login success, updating UI");
        }
    }

    private void logout() {
        SPUtils.clearUserInfo(requireContext());
        updateUI(); // 注销后更新UI
    }

    // 检查登录状态并更新UI
    private void updateUI() {
        if (getActivity() == null) return;

        if (SPUtils.isLogin(requireContext())) {
            UserInfo userInfo = SPUtils.getUserInfo(requireContext());
            Log.d(TAG, "Updating UI for login user: " + (userInfo != null ? userInfo.getUsername() : "null"));
            updateUIForLogin(userInfo);
        } else {
            Log.d(TAG, "Updating UI for guest");
            updateUIForGuest();
        }
    }





    // 更新为游客模式UI
    public void updateUIForLogin(UserInfo userInfo) {
        if (userInfo == null) return;

        Log.d(TAG, "Updating UI for login: " + userInfo.getUsername());
        if (tvUsername != null) {
            tvUsername.setText(userInfo.getUsername());
            btnLogout.setText("注销登录");
            Glide.with(this)
                    .load(userInfo.getAvatar())
                    .error(R.drawable.img1)
                    .placeholder(R.drawable.img1)
                    .into(ivAvatar);
        }
    }

    public void updateUIForGuest() {
        Log.d(TAG, "Updating UI for guest");
        if (tvUsername != null) {
            tvUsername.setText("游客");
            btnLogout.setText("登录");
            Glide.with(this)
                    .load(R.drawable.img1)
                    .into(ivAvatar);
        }
    }
}