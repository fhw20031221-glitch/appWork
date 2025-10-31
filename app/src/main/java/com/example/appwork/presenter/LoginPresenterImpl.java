package com.example.appwork.presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;


import com.example.appwork.api.ApiService;
import com.example.appwork.api.RetrofitClient;
import com.example.appwork.contract.LoginContract;
import com.example.appwork.model.BaseResponse;
import com.example.appwork.model.UserInfo;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;



public class LoginPresenterImpl implements LoginContract.Presenter {
    private LoginContract.View mView;
    private static final String TEST_USERNAME = "admin";
    private static final String TEST_PASSWORD = "123456";
    private CompositeDisposable compositeDisposable;

    public LoginPresenterImpl() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(LoginContract.View view) {
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
    public void login(String username, String password) {
        if (mView == null) {
            Log.e("LoginPresenter", "mView is null");
            return;
        }

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Log.d("LoginPresenter", "username or password is empty");
            mView.showError("用户名或密码不能为空");
            return;
        }

        Log.d("LoginPresenter", "start login: " + username);
        mView.showLoading();

        // 首先检查是否是测试账号
        if (TEST_USERNAME.equals(username) && TEST_PASSWORD.equals(password)) {
            handleTestAccount(username);
        } else {
            // 不是测试账号，调用真实API
            handleRealLogin(username, password);
        }
    }

    private void handleTestAccount(String username) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (mView == null) {
                Log.e("LoginPresenter", "mView became null during delay");
                return;
            }

            Log.d("LoginPresenter", "login success with test account");
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(username);
            userInfo.setAvatar("https://api.multiavatar.com/" + username + ".png");
            userInfo.setToken("test_token");

            mView.hideLoading();
            mView.loginSuccess(userInfo);
        }, 1000);
    }

    private void handleRealLogin(String username, String password) {
        ApiService apiService = RetrofitClient.getInstance().getLoginApiService();
        apiService.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull BaseResponse<UserInfo> response) {
                        if (mView == null) return;
                        mView.hideLoading();
                        if (response.isSuccess()) {
                            mView.loginSuccess(response.getData());
                        } else {
                            mView.loginFailed(response.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (mView == null) return;
                        mView.hideLoading();
                        mView.showError("网络错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    // 可以移除未使用的logout方法，或者实现一个空方法
    @Override
    public void logout() {
        // 不需要实现
    }
}

