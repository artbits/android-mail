package com.smailnet.demo.API;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;

import com.smailnet.eamil.EmailConfig;
import com.smailnet.eamil.EmailSendClient;
import com.smailnet.eamil.GetLoginCallback;
import com.smailnet.eamil.GetSendCallback;

@SuppressLint("Registered")
public class EmailApi extends AppCompatActivity {

    //配置服务器和登录邮箱
    EmailConfig emailConfig = new EmailConfig()
            .setSendHost("smtp.qq.com")             //设置发件服务器地址，网易邮箱为smtp.163.com
            .setSendPort(465)                       //设置发件服务器端口，网易邮箱为25
            .setAuth(true)                          //设置是否验证
            .setAccount("1234567@qq,com")           //你的邮箱地址
            .setPassword("abcdefg")                 //你的邮箱密码，QQ邮箱该处填授权码
            .login(this, new GetLoginCallback() {   //this是调用该代码的Activity
                @Override
                public void loginSuccess() {
                    //登录成功（这里可更新UI）
                }

                @Override
                public void loginFailure(String errorMsg) {
                    //登录失败，errorMsg是错误信息（这里可更新UI）
                }
            });



    //配置服务器和登录邮箱成功后，获取到emailConfig.getConfigData()，才能成功运行下面的代码
    EmailSendClient emailSendClient = new EmailSendClient(emailConfig.getConfigData())
            .setReceiver("9876543@qq.com")              //收件人的邮箱地址
            .setTitle("邮件测试")                        //邮件标题
            .setText("Hello World !")                   //邮件正文
            .sendAsync(this, new GetSendCallback() {    //this是调用该代码的Activity
                @Override
                public void sendSuccess() {
                    //发送成功（这里可更新UI）
                }

                @Override
                public void sendFailure(String errorMsg) {
                    //发送失败，errorMsg是错误信息（这里可更新UI）
                }
            });

    /**
     * 邮件正文有text和html两种，分别对应.setText()和.setContent()，二选一。
     */
}
