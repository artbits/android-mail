package com.smailnet.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.smailnet.eamil.EmailSendClient;
import com.smailnet.eamil.EmailSendConfig;
import com.smailnet.eamil.getResultCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maim_activity);
        Button button = findViewById(R.id.bt_1);
        button.setOnClickListener(this);
    }

    private void sendMail(){
        //配置发件服务器
        EmailSendConfig emailSendConfig = new EmailSendConfig()
                .setHost("smtp.qq.com")             //设置服务器地址，网易邮箱为smtp.163.com
                .setPost(465)                       //设置端口，网易邮箱为25
                .setAuth(true)                      //设置是否验证
                .setAccount("xxx@foxmail.com")      //你的邮箱地址
                .setPassword("abcdefg")             //你的邮箱密码，QQ邮箱该处填授权码
                .sava();                            //保存配置

        //邮件发送
        EmailSendClient emailSendClient = new EmailSendClient(emailSendConfig);
        emailSendClient
                .setReceiver("yyy@foxmail.com")         //收件人的邮箱地址
                .setFrom("xxx@foxmail.com")             //发件人的邮箱地址
                .setTitle("邮件测试")                    //邮件标题
                .setText("Hello World !")               //邮件正文
                .sendAsync(new getResultCallback() {    //异步发送邮件，然后回调发送结果
                    @Override
                    public void getResult(boolean result) {
                        Log.i("oversee", (result) ? "发送成功" : "发送失败");
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_1:
                sendMail();
                break;
        }
    }
}
