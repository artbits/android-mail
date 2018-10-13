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

import com.smailnet.eamil.Callback.GetReceiveCallback;

import java.io.IOException;
import java.util.List;

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
public class EmailReceiveClient {

    private EmailConfig emailConfig;

    public EmailReceiveClient(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
    }

    /**
     * 使用POP3协议异步接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void popReceiveAsyn(final Activity activity, final GetReceiveCallback getReceiveCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<EmailMessage> messageList = Operator.Core(emailConfig).popReceiveMail();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size());
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 使用POP3协议异步接收邮件，接收完毕但不切回主线程
     * @param getReceiveCallback
     */
    public void popReceiveAsyn(final GetReceiveCallback getReceiveCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EmailMessage> messageList = Operator.Core(emailConfig).popReceiveMail();
                    getReceiveCallback.gainSuccess(messageList, messageList.size());
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    getReceiveCallback.gainFailure(e.toString());
                } catch (final IOException e) {
                    e.printStackTrace();
                    getReceiveCallback.gainFailure(e.toString());
                }
            }
        }).start();
    }

    /**
     * 使用imap协议接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void imapReceiveAsyn(final Activity activity, final GetReceiveCallback getReceiveCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<EmailMessage> messageList = Operator.Core(emailConfig).imapReceiveMail();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size());
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 使用imap协议接收邮件，接收完毕但不切回主线程
     * @param getReceiveCallback
     */
    public void imapReceiveAsyn(final GetReceiveCallback getReceiveCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EmailMessage> messageList = Operator.Core(emailConfig).imapReceiveMail();
                    getReceiveCallback.gainSuccess(messageList, messageList.size());
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    getReceiveCallback.gainFailure(e.toString());
                } catch (final IOException e) {
                    e.printStackTrace();
                    getReceiveCallback.gainFailure(e.toString());
                }
            }
        }).start();
    }
}
