package com.example.appwork.ui.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appwork.R;
import com.example.appwork.base.BaseActivity;
import com.example.appwork.contract.LoginContract;
import com.example.appwork.model.UserInfo;
import com.example.appwork.presenter.LoginPresenterImpl;
import com.example.appwork.utils.SPUtils;

public class LoginActivity extends BaseActivity implements LoginContract.View {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private LoginContract.Presenter presenter;
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            presenter.login(username, password);
        });
    }

    @Override
    protected void initData() {
        presenter = new LoginPresenterImpl();
        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("登录中...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void loginSuccess(UserInfo userInfo) {
        // 保存用户信息
        SPUtils.saveLoginStatus(this, true);
        SPUtils.saveUserInfo(this, userInfo);

        // 启动 MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        // 添加标志以清除任务栈
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // 结束当前活动
        finish();
    }

    @Override
    public void loginFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}