package com.example.appwork.base;

public interface BasePresenter<V extends BaseView> {
    void attachView(V view);
    void detachView();
}