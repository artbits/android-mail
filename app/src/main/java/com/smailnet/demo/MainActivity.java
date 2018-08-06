package com.smailnet.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smailnet.eamil.EmailSendClient;
import com.smailnet.eamil.GetSendCallback;
import com.smailnet.islands.Islands;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText address;
    private EditText title;
    private EditText text;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView(){
        address = findViewById(R.id.address);
        title = findViewById(R.id.title);
        text = findViewById(R.id.text);
        button = findViewById(R.id.send);

        button.setOnClickListener(this);
    }

    /**
     * 发送邮件
     */
    private void sendMessage(){
        EmailSendClient emailSendClient = new EmailSendClient(EmailApplication.getEmailConfig().getConfigData());
        emailSendClient
                .setReceiver(address.getText().toString())          //收件人的邮箱地址
                .setTitle(title.getText().toString())               //邮件标题
                .setText(text.getText().toString())                 //邮件正文
                .sendAsync(this, new GetSendCallback() {
                    @Override
                    public void sendSuccess() {
                        Toast.makeText(MainActivity.this, "已发送", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void sendFailure(String errorMsg) {
                        new Islands.OrdinaryDialog(MainActivity.this)
                                .setText(null, "发送失败 ：" + errorMsg)
                                .setButton("关闭", null, null)
                                .click().show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                sendMessage();
                break;
        }
    }
}
