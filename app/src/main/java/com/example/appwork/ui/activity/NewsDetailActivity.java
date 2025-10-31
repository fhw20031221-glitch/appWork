package com.example.appwork.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appwork.R;
import com.example.appwork.db.NewsDao;
import com.example.appwork.model.NewsItem;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NEWS_ITEM = "news_item";
    private WebView webView;
    private ImageView ivBack, ivFavorite, ivShare;
    private NewsItem currentNewsItem;
    private NewsDao newsDao;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // 初始化DAO
        newsDao = NewsDao.getInstance(this);

        // 初始化视图
        webView = findViewById(R.id.webView);
        ivBack = findViewById(R.id.iv_back);
        ivFavorite = findViewById(R.id.iv_favorite);
        ivShare = findViewById(R.id.iv_share);

        // 配置WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  // 启用JavaScript
        webSettings.setDomStorageEnabled(true);  // 启用DOM storage
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // 添加进度显示
        ProgressBar progressBar = new ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                10));

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        // 获取传递的新闻对象
        currentNewsItem = (NewsItem) getIntent().getSerializableExtra(EXTRA_NEWS_ITEM);

        if (currentNewsItem != null) {
            webView.loadUrl(currentNewsItem.getUrl());
            // 添加到历史记录
            newsDao.addHistory(currentNewsItem);
            // 检查是否已收藏
            checkIfFavorite();
        }

        // 设置点击事件
        ivBack.setOnClickListener(v -> finish());
        ivFavorite.setOnClickListener(v -> toggleFavorite());
        ivShare.setOnClickListener(v -> shareNews());
    }

    private void checkIfFavorite() {
        isFavorite = newsDao.isFavorite(currentNewsItem.getUniquekey());
        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            ivFavorite.setImageResource(R.drawable.ic_star_filled); // 收藏状态的图标
        } else {
            ivFavorite.setImageResource(R.drawable.ic_star_border); // 未收藏状态的图标
        }
    }

    private void toggleFavorite() {
        if (isFavorite) {
            newsDao.removeFavorite(currentNewsItem.getUniquekey());
            Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
        } else {
            newsDao.addFavorite(currentNewsItem);
            Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
        }
        isFavorite = !isFavorite;
        updateFavoriteIcon();
    }

    private void shareNews() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "分享这篇新闻给你: " + currentNewsItem.getTitle() + "\n" + currentNewsItem.getUrl());
        startActivity(Intent.createChooser(shareIntent, "分享到..."));
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}