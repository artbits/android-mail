package com.smailnet.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smailnet.eamil.Callback.GetConnectCallback;
import com.smailnet.eamil.Callback.GetSpamCheckCallback;
import com.smailnet.eamil.EmailExamine;
import com.smailnet.islands.Interface.OnRunningListener;
import com.smailnet.islands.Islands;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText account_editText;
    EditText password_editText;
    EditText send_host_editText;
    EditText send_port_editText;
    EditText receive_host_editText;
    EditText receive_port_editText;
    EditText imap_host_editText;
    EditText imap_port_editText;

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
        account_editText = findViewById(R.id.account_editText);
        password_editText = findViewById(R.id.password_editText);
        send_host_editText = findViewById(R.id.send_host_editText);
        send_port_editText = findViewById(R.id.send_port_editText);
        receive_host_editText = findViewById(R.id.receive_host_editText);
        receive_port_editText = findViewById(R.id.receive_port_editText);
        imap_host_editText = findViewById(R.id.imap_host_editText);
        imap_port_editText = findViewById(R.id.imap_port_editText);

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
        EmailApp.emailConfig()
                .setSmtpHost(send_host_editText.getText().toString())
                .setSmtpPort(Integer.parseInt(send_port_editText.getText().toString()))
                .setPopHost(receive_host_editText.getText().toString())
                .setPopPort(Integer.parseInt(receive_port_editText.getText().toString()))
                .setImapHost(imap_host_editText.getText().toString())
                .setImapPort(Integer.parseInt(imap_port_editText.getText().toString()))
                .setAccount(account_editText.getText().toString())
                .setPassword(password_editText.getText().toString());

        EmailExamine emailExamine = new EmailExamine(EmailApp.emailConfig());
        emailExamine.connectServer(this, new GetConnectCallback() {
            @Override
            public void loginSuccess() {
                progressDialog.dismiss();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void loginFailure(String errorMsg) {
                progressDialog.dismiss();
                Islands.ordinaryDialog(LoginActivity.this)
                        .setText(null, "登录失败 ：" + errorMsg)
                        .setButton("关闭", null, null)
                        .click()
                        .show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                Islands
                        .circularProgress(this)
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
