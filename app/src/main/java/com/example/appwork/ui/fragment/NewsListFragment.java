package com.example.appwork.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;



import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appwork.R;
import com.example.appwork.contract.NewsContract;
import com.example.appwork.model.NewsItem;
import com.example.appwork.presenter.NewsPresenterImpl;
import com.example.appwork.ui.activity.NewsDetailActivity;
import com.example.appwork.ui.adapter.NewsAdapter;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;




public class NewsListFragment extends Fragment implements NewsContract.View {
    private static final String ARG_CATEGORY = "category";
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private NewsContract.Presenter presenter;
    private String category;

    public static NewsListFragment newInstance(String category) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
        initView(view);
        initData();
    }

    private void initView(View view) {
        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);

        // 配置 SmartRefreshLayout
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext())); // 添加经典下拉刷新头部
        refreshLayout.setEnableRefresh(true);  // 启用下拉刷新功能
        refreshLayout.setEnableLoadMore(true); // 启用上拉加载功能

        // 设置主题颜色
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);

        // 设置刷新超时时间（毫秒）
        //refreshLayout.setRefreshTimeout(10000);

        // 设置刷新监听器
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // 显示刷新动画
            refreshLayout.autoRefresh();
            presenter.getNewsList(category, true);
        });

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new NewsAdapter(getContext());
        recyclerView.setAdapter(newsAdapter);

        // 设置上拉加载更多监听器
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            presenter.loadMore();
        });

        // 添加点击事件
        newsAdapter.setOnItemClickListener(newsItem -> {
            // TODO: 处理新闻点击事件，例如跳转到详情页
            Intent intent = new Intent(getContext(), NewsDetailActivity.class);
            intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ITEM, newsItem);
            startActivity(intent);});

        // 设置没有更多数据时的文字提示
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()).setFinishDuration(300));
    }
    @Override
    public void showNewsList(List<NewsItem> newsList, boolean isRefresh) {
        if (isRefresh) {
            // 延迟 300ms 结束刷新，让用户能看到刷新动画
            refreshLayout.postDelayed(() -> {
                refreshLayout.finishRefresh(true);
                newsAdapter.setData(newsList);
            }, 300);
        } else {
            refreshLayout.finishLoadMore();
            newsAdapter.addData(newsList);
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        // 延迟结束刷新，显示失败状态
        refreshLayout.postDelayed(() -> {
            refreshLayout.finishRefresh(false);
            refreshLayout.finishLoadMore(false);
        }, 300);
    }


    private void initData() {
        // 传入 Context
        presenter = new NewsPresenterImpl(requireContext());
        presenter.attachView(this);
        // 首次加载数据
        presenter.getNewsList(category, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.detachView();
        }
    }


    @Override
    public void showLoadMoreEnd() {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public void showLoadMoreFailed() {
        refreshLayout.finishLoadMore(false);
    }

    @Override
    public void showLoading() {
        // 使用SmartRefreshLayout的刷新动画，不需要额外的loading
    }

    @Override
    public void hideLoading() {
        // 使用SmartRefreshLayout的刷新动画，不需要额外的loading
    }


}
