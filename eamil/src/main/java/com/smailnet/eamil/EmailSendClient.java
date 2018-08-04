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

import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Transport;
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

    private EmailSendConfig emailConfigure;
    private boolean state = false;
    private String tag = null;

    public EmailSendClient(EmailSendConfig emailConfigure){
        this.emailConfigure = emailConfigure;
    }

    /**
     * 设置是否打印异常日志
     *
     * @param state 开关状态
     * @param tag
     * @return
     */
    public EmailSendClient setExceptionLog(boolean state, String tag){
        this.state = state;
        this.tag = tag;
        return this;
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
        } catch (AddressException e) {
            e.printStackTrace();
        }
        try {
            emailConfigure.message.setRecipients(Message.RecipientType.TO, address);
        } catch (MessagingException e) {
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
    public EmailSendClient setFrom(String from){
        try {
            emailConfigure.message.setFrom(new InternetAddress(from));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置邮件标题
     *
     * @param title
     * @return
     */
    public EmailSendClient setTitle(String title){
        try {
            emailConfigure.message.setSubject(title);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置邮件正文（纯文本）
     *
     * @param text
     * @return
     */
    public EmailSendClient setText(String text){
        try {
            emailConfigure.message.setText(text);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置邮件内容（包括HTML和纯文本）
     *
     * @param content
     * @return
     */
    public EmailSendClient setContent(Object content){
        try {
            emailConfigure.message.setContent(content, "text/html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 异步发送邮件
     *
     * @param onSendingListener
     * @return
     */
    public EmailSendClient sendAsync(final getResultCallback onSendingListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendingPreparation(new getResultCallback() {
                    @Override
                    public void getResult(boolean result) {
                        if (result){
                            onSendingListener.getResult(true);
                        }else {
                            onSendingListener.getResult(false);
                        }
                    }
                });
            }
        }).start();
        return this;
    }

    private void sendingPreparation(getResultCallback onSendingListener){
        try {
            emailConfigure.message.setSentDate(new Date());
            emailConfigure.message.saveChanges();
        } catch (MessagingException e) {
            onSendingListener.getResult(false);
            LogPrint.print(state, tag, e);
            e.printStackTrace();
        }

        Transport transport = null;
        try {
            transport = emailConfigure.session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            onSendingListener.getResult(false);
            LogPrint.print(state, tag, e);
            e.printStackTrace();
        }

        try {
            assert transport != null;
            transport.connect(emailConfigure.host, emailConfigure.account, emailConfigure.password);
            transport.sendMessage(emailConfigure.message, emailConfigure.message.getAllRecipients());
            transport.close();
            onSendingListener.getResult(true);
        } catch (MessagingException e) {
            onSendingListener.getResult(false);
            LogPrint.print(state, tag, e);
            e.printStackTrace();
        }
    }
}
