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

import java.io.StringReader;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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

    private String title = null;
    private String text = null;
    private Object content = null;
    private Address[] address;
    private ConfigData configData;

    public EmailSendClient(ConfigData configData){
        this.configData = configData;
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
     * 设置发件人的邮箱地址
     *
     * @param from
     * @return
     */
    /*
    public EmailSendClient setFrom(String from){
        this.from = from;
        return this;
    }
    */

    /**
     * 设置邮件标题
     *
     * @param title
     * @return
     */
    public EmailSendClient setTitle(String title){
        this.title = title;
        return this;
    }

    /**
     * 设置邮件正文（纯文本）
     *
     * @param text
     * @return
     */
    public EmailSendClient setText(String text){
        this.text = text;
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
     * 邮件组装
     *
     * @return
     */
    private Message getMessage(){
        Message message = new MimeMessage(configData.getSession());
        try {
            message.setRecipients(Message.RecipientType.TO, address);
            message.setFrom(new InternetAddress(configData.getAccount()));
            message.setSubject(title);
            if (text != null){
                message.setText(text);
            }else if (content !=null){
                message.setContent(content, "text/html");
            }
            message.setSentDate(new Date());
            message.saveChanges();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    /**
     * 异步发送邮件
     *
     * @param getSendCallback
     * @return
     */
    public EmailSendClient sendAsync(final Activity activity, final GetSendCallback getSendCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    configData.getTransport().sendMessage(getMessage(), getMessage().getAllRecipients());
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
