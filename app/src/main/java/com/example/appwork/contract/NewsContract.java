package com.example.appwork.contract;

import com.example.appwork.base.BasePresenter;
import com.example.appwork.base.BaseView;
import com.example.appwork.model.NewsItem;

import java.util.List;

public interface NewsContract {
    interface View extends BaseView {
        void showNewsList(List<NewsItem> newsList, boolean isRefresh);
        void showLoadMoreEnd();
        void showLoadMoreFailed();
    }

    interface Presenter extends BasePresenter<View> {
        void getNewsList(String category, boolean isRefresh);
        void loadMore();
    }
}
