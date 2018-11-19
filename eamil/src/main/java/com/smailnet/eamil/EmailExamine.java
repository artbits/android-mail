/*
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

import com.smailnet.eamil.Callback.GetConnectCallback;
import com.smailnet.eamil.Callback.GetSpamCheckCallback;

import java.net.UnknownHostException;

import javax.mail.MessagingException;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送和接收电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务
 * 器，即可使用，所见即所得哦！
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 2.3
 */
public class EmailExamine {

    private EmailConfig emailConfig;

    public EmailExamine(){

    }

    public EmailExamine(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
    }

    /**
     * 检查邮件服务器是否可连接的接口，检查完毕切回主线程
     * @param activity
     * @param getConnectCallback
     * @return
     */
    public void connectServer(final Activity activity, final GetConnectCallback getConnectCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Operator.Core(emailConfig).authentication();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getConnectCallback.loginSuccess();
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getConnectCallback.loginFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 检查邮件服务器是否可连接的接口，检查完毕切回主线程
     * @param getConnectCallback
     * @return
     */
    public void connectServer(final GetConnectCallback getConnectCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Operator.Core(emailConfig).authentication();
                    getConnectCallback.loginSuccess();
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    getConnectCallback.loginFailure(e.toString());
                }
            }
        }).start();
    }

    /**
     * 检查该IP地址是否为垃圾邮件发送者，检查完毕但不切回主线程
     * @param activity
     * @param host
     * @param getSpamCheckCallback
     */
    public void spamCheck(final Activity activity, final String host, final GetSpamCheckCallback getSpamCheckCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Operator.Core().spamCheck(host);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSpamCheckCallback.gotResult(true);
                        }
                    });
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSpamCheckCallback.gotResult(false);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 检查该IP地址是否为垃圾邮件发送者，检查完毕但不切回主线程
     * @param host
     * @param getSpamCheckCallback
     */
    public void spamCheck(final String host, final GetSpamCheckCallback getSpamCheckCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Operator.Core().spamCheck(host);
                    getSpamCheckCallback.gotResult(true);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    getSpamCheckCallback.gotResult(false);
                }
            }
        }).start();
    }
}
