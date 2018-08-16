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

import com.smailnet.eamil.Callback.GetSendCallback;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务器，即
 * 可使用，所见即所得哦！
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 1.0
 */
public class EmailSendClient {

    private String subject = "";
    private Object content = "";
    private Address[] address;
    private EmailConfig emailConfig;

    public EmailSendClient(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
    }

    /**
     * 设置收件人的邮箱地址
     *
     * @param receiver
     * @return
     */
    public EmailSendClient setReceiver(String receiver){
        int length = (new String[]{receiver}).length;
        Address[] address = new InternetAddress[length];
        try {
            address[0] = new InternetAddress(receiver);
            this.address = address;
        } catch (AddressException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置邮件标题
     *
     * @param subject
     * @return
     */
    public EmailSendClient setSubject(String subject){
        this.subject = subject;
        return this;
    }

    /**
     * 设置邮件内容（包括HTML和纯文本）
     *
     * @param content
     * @return
     */
    public EmailSendClient setContent(Object content){
        this.content = content;
        return this;
    }

    /**
     * 异步发送邮件
     *
     * @param activity
     * @param getSendCallback
     * @return
     */
    public EmailSendClient sendAsyn(final Activity activity, final GetSendCallback getSendCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EmailCore emailCore = new EmailCore(emailConfig);
                    Message message = emailCore.setMessage(address, subject, content);
                    emailCore.sendMail(message);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSendCallback.sendSuccess();
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSendCallback.sendFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
        return this;
    }
}
