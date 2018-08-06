package com.smailnet.demo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.smailnet.eamil.EmailConfig;
import com.smailnet.eamil.EmailSendClient;
import com.smailnet.eamil.GetLoginCallback;
import com.smailnet.eamil.GetSendCallback;
import com.smailnet.islands.Islands;
import com.smailnet.islands.OnRunningListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText account;
    EditText password;
    EditText host;
    EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView(){
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        host = findViewById(R.id.host);
        port = findViewById(R.id.port);

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(this);
    }

    /**
     * 登录邮箱
     *
     * @param progressDialog
     */
    private void login(final ProgressDialog progressDialog){
        //配置发件服务器
        EmailApplication.getEmailConfig()
                .setSendHost(host.getText().toString())
                .setSendPort(Integer.parseInt(port.getText().toString()))
                .setAuth(true)
                .setAccount(account.getText().toString())
                .setPassword(password.getText().toString())
                .login(this, new GetLoginCallback() {
                    @Override
                    public void loginSuccess() {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void loginFailure(String errorMsg) {
                        progressDialog.dismiss();
                        new Islands.OrdinaryDialog(LoginActivity.this)
                                .setText(null, "登录失败 ：" + errorMsg)
                                .setButton("关闭", null, null)
                                .click().show();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                new Islands.CircularProgress(this)
                        .setMessage("登录中...")
                        .setCancelable(false)
                        .show()
                        .run(new OnRunningListener() {
                            @Override
                            public void onRunning(ProgressDialog progressDialog) {
                                login(progressDialog);
                            }
                        });
                break;
        }
    }
}
