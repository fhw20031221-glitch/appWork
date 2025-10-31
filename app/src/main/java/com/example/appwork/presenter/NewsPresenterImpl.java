package com.example.appwork.presenter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import com.example.appwork.api.ApiService;
import com.example.appwork.api.RetrofitClient;
import com.example.appwork.contract.NewsContract;
import com.example.appwork.model.BaseResponse;
import com.example.appwork.model.NewsItem;
import com.example.appwork.model.NewsResponse;
import com.example.appwork.utils.NetworkUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NewsPresenterImpl implements NewsContract.Presenter {
    private static final String TAG = "NewsPresenterImpl";
    private NewsContract.View mView;
    private final CompositeDisposable compositeDisposable;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private final Context context;
    private String currentCategory;

    public NewsPresenterImpl(Context context) {
        this.context = context;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(NewsContract.View view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    @Override
    public void getNewsList(String category, boolean isRefresh) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            if (mView != null) {
                mView.showError("网络不可用，请检查网络连接");
            }
            return;
        }

        if (isRefresh) {
            currentPage = 1;
        }
        if (isLoading) return;
        isLoading = true;
        currentCategory = category;

        ApiService apiService = RetrofitClient.getInstance().getNewsApiService();
        apiService.getNewsList(category, currentPage, PAGE_SIZE)
                .subscribeOn(Schedulers.io())

                // 添加超时设置
                .timeout(5, TimeUnit.SECONDS)
                // 添加重试机制
                .retry(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull NewsResponse response) {
                        isLoading = false;
                        Log.d(TAG, "Response: " + response.getReason());

                        if (response.getError_code() == 0) {
                            if (response.getResult() != null && response.getResult().getData() != null && !response.getResult().getData().isEmpty()) {
                                if (mView != null) {
                                    mView.showNewsList(response.getResult().getData(), isRefresh);
                                }
                                currentPage++;
                            } else {
                                if (mView != null) {
                                    mView.showLoadMoreEnd();
                                }
                            }
                        } else {
                            if (mView != null) {
                                mView.showError("错误：" + response.getReason());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        isLoading = false;
                        Log.e(TAG, "Error loading news", e);
                        if (mView != null) {
                            mView.showError("网络错误：" + e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        // 可选实现
                    }
                });
    }

    @Override
    public void loadMore() {
        getNewsList(currentCategory, false);
    }


}
