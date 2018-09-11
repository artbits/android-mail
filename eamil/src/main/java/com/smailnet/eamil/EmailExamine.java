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

import javax.mail.MessagingException;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送和接收电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务
 * 器，即可使用，所见即所得哦！
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 2.1
 */
public class EmailExamine {

    private EmailConfig emailConfig;

    public EmailExamine(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
    }

    /**
     * 检查邮件服务器是否可连接的接口
     *
     * @param activity
     * @param getConnectCallback
     * @return
     */
    public EmailExamine connectServer(final Activity activity, final GetConnectCallback getConnectCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EmailCore emailCore = new EmailCore(emailConfig);
                    emailCore.authentication();
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
        return this;
    }
}
