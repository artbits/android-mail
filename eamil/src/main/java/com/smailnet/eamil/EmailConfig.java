/**
 * Copyright 2018 Lake Zhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smailnet.eamil;

import android.app.Activity;
import android.content.Context;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务器，即
 * 可使用，所见即所得哦！
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 1.0
 */
public class EmailConfig {

    private int sendPort;
    private boolean auth;
    private String sendHost;
    private String account;
    private String password;
    private Transport transport;
    private Session session;

    /**
     * 设置邮件服务器
     *
     * @param sendHost
     * @return
     */
    public EmailConfig setSendHost(String sendHost){
        this.sendHost = sendHost;
        return this;
    }

    /**
     * 设置端口
     *
     * @param sendPort
     * @return
     */
    public EmailConfig setSendPort(int sendPort){
       this.sendPort = sendPort;
       return this;
    }

    /**
     * 是否开启验证
     *
     * @param auth
     * @return
     */
    public EmailConfig setAuth(boolean auth){
        this.auth = auth;
        return this;
    }

    /**
     * 设置发件人的邮箱地址
     *
     * @param account
     * @return
     */
    public EmailConfig setAccount(String account){
        this.account = account;
        return this;
    }

    /**
     * 设置发件人的邮箱密码
     *
     * @param password
     * @return
     */
    public EmailConfig setPassword(String password){
        this.password = password;
        return this;
    }

    /**
     * 登录邮箱
     *
     * @return
     */
    public EmailConfig login(final Activity activity, final GetLoginCallback getLoginCallback){
        Properties properties = new Properties();
        properties.put("mail.smtp.host", sendHost);
        properties.put("mail.smtp.post", String.valueOf(sendPort));
        properties.put("mail.smtp.auth", auth);
        session = Session.getInstance(properties);
        try {
            transport = session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    transport.connect(sendHost, account, password);
                    getLoginCallback.loginSuccess();
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getLoginCallback.loginFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
        return this;
    }

    public ConfigData getConfigData(){
        ConfigData configData = new ConfigData();
        configData.setTransport(transport);
        configData.setSession(session);
        configData.setAccount(account);
        return configData;
    }
}
