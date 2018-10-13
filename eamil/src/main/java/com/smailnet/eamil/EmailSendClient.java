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
import com.smailnet.eamil.Utils.AddressUtil;

import java.io.UnsupportedEncodingException;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务器，即
 * 可使用，所见即所得哦！
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 2.3
 */
public class EmailSendClient {

    private String nickname;
    private String subject;
    private String text;
    private Object content;
    private Address[] to;
    private Address[] cc;
    private Address[] bcc;
    private EmailConfig emailConfig;

    public EmailSendClient(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
    }

    /**
     * 设置收件人的邮箱地址（该方法将在2.3版本上弃用）
     * @param to
     * @return
     */
    @Deprecated
    public EmailSendClient setReceiver(String to){
        this.to = AddressUtil.getInternetAddress(to);
        return this;
    }

    /**
     * 设置发件人的昵称
     * @param nickname
     * @return
     */
    public EmailSendClient setNickname(String nickname){
        try {
            this.nickname = MimeUtility.encodeText(nickname);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置收件人的邮箱地址
     * @param to
     * @return
     */
    public EmailSendClient setTo(String to){
        this.to = AddressUtil.getInternetAddress(to);
        return this;
    }

    /**
     * 设置抄送人的邮箱地址
     * @param cc
     * @return
     */
    public EmailSendClient setCc(String cc){
        this.cc = AddressUtil.getInternetAddress(cc);
        return this;
    }

    /**
     * 设置密送人的邮箱地址
     * @param bcc
     * @return
     */
    public EmailSendClient setBcc(String bcc){
        this.bcc = AddressUtil.getInternetAddress(bcc);
        return this;
    }

    /**
     * 设置邮件标题
     * @param subject
     * @return
     */
    public EmailSendClient setSubject(String subject){
        this.subject = subject;
        return this;
    }

    /**
     * 设置邮件内容（纯文本）
     * @param text
     * @return
     */
    public EmailSendClient setText(String text){
        this.text = text;
        return this;
    }

    /**
     * 设置邮件内容（HTML）
     * @param content
     * @return
     */
    public EmailSendClient setContent(Object content){
        this.content = content;
        return this;
    }

    /**
     * 异步发送邮件，发送完毕回调并切回到主线程
     * @param activity
     * @param getSendCallback
     * @return
     */
    public void sendAsyn(final Activity activity, final GetSendCallback getSendCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Operator.Core(emailConfig)
                            .setMessage(nickname, to, cc, bcc, subject, text, content)
                            .sendMail();
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
    }

    /**
     * 异步发送邮件，发送完毕回调不切回到主线程
     * @param getSendCallback
     * @return
     */
    public void sendAsyn(final GetSendCallback getSendCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Operator.Core(emailConfig)
                            .setMessage(nickname, to, cc, bcc, subject, text, content)
                            .sendMail();
                    getSendCallback.sendSuccess();
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    getSendCallback.sendFailure(e.toString());
                }
            }
        }).start();
    }
}
