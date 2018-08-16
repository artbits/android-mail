package com.smailnet.demo.API;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;

import com.smailnet.eamil.Callback.GetReceiveCallback;
import com.smailnet.eamil.EmailConfig;
import com.smailnet.eamil.EmailExamine;
import com.smailnet.eamil.EmailReceiveClient;
import com.smailnet.eamil.EmailSendClient;
import com.smailnet.eamil.Callback.GetConnectCallback;
import com.smailnet.eamil.Callback.GetSendCallback;
import com.smailnet.eamil.Entity.EmailMessage;

import java.util.List;

@SuppressLint("Registered")
public class EmailApi extends AppCompatActivity {

    //配置邮件服务器
    EmailConfig emailConfig = new EmailConfig()
            .setSmtpHost("smtp.qq.com")             //设置发件服务器地址，网易邮箱为smtp.163.com
            .setSmtpPort(465)                       //设置发件服务器端口，网易邮箱为25
            .setPopHost("pop.qq.com")               //设置收件服务器地址，网易邮箱为pop.163.com
            .setPopPort(995)                        //设置收件服务器端口，网易邮箱为110
            .setAccount("1234567@qq,com")           //你的邮箱地址
            .setPassword("abcdefg");                //你的邮箱密码，若QQ邮箱该处填授权码

    //邮件发送
    EmailSendClient emailSendClient = new EmailSendClient(emailConfig)
            .setReceiver("9876543@qq.com")              //收件人的邮箱地址
            .setSubject("邮件测试")                      //邮件标题
            .setContent("Hello World !")                //邮件正文
            .sendAsyn(this, new GetSendCallback() {     //this是调用该代码的Activity
                @Override
                public void sendSuccess() {
                    //发送成功（这里可更新UI）
                }

                @Override
                public void sendFailure(String errorMsg) {
                    //发送失败，errorMsg是错误信息（这里可更新UI）
                }
            });

    //获取邮件
    EmailReceiveClient emailReceiveClient = new EmailReceiveClient(emailConfig)
            .receiveAsyn(this, new GetReceiveCallback() {   //this是调用该代码的Activity
                @Override
                public void gainSuccess(List<EmailMessage> emailMessageList, int count) {
                    //获取邮件成功（这里可更新UI）
                }

                @Override
                public void gainFailure(String errorMsg) {
                    //获取邮件失败，errorMsg是错误信息（这里可更新UI）
                }
            });

    //验证邮箱和检查邮件服务器
    EmailExamine emailExamine = new EmailExamine(emailConfig)
            .connectServer(this, new GetConnectCallback() {     //this是调用该代码的Activity
                @Override
                public void loginSuccess() {
                    //邮箱登录成功（这里可更新UI）
                }

                @Override
                public void loginFailure(String errorMsg) {
                    //邮箱登录失败，errorMsg是错误信息（这里可更新UI）
                }
            });
}
