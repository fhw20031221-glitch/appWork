package com.example.appwork.contract;

import com.example.appwork.base.BasePresenter;
import com.example.appwork.base.BaseView;
import com.example.appwork.model.UserInfo;

public interface LoginContract {
    interface View extends BaseView {
        void loginSuccess(UserInfo userInfo);
        void loginFailed(String error);
    }

    interface Presenter extends BasePresenter<View> {
        void login(String username, String password);
        void logout();
    }
}
