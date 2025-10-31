package com.example.appwork.base;

public interface BaseView {
    void showLoading();
    void hideLoading();
    void showError(String message);
}