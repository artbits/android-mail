/*
 * Copyright 2018 张观湖
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


import java.util.HashMap;
import java.util.List;

/**
 * Email for Android是基于JavaMail封装的电子邮件框架，简化了开发者在Android客户端
 * 中编写发送电子邮件的的代码，同时还支持读取邮箱中的邮件。把它集成到你的Android项
 * 目中，你只需简单配置邮件服务器的参数，调用一些简易的API，即可完成你所需的功能，
 * 所见即所得。
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 3.0.0
 */
public final class Email {

    /**
     * 常用的邮箱的类型选择
     */
    public interface MailType {
        int QQ = 1;
        int FOXMAIL = 2;
        int NETEASE = 3;
    }

    /**
     * 邮件的服务器配置内容
     */
    public static class Config {

        private String account;
        private String password;
        private String smtpHost;
        private String popHost;
        private String imapHost;
        private int smtpPort;
        private int popPort;
        private int imapPort;

        public Config setMailType(int mailType) {
            HashMap<String, Object> hashMap = Converter.MailType.getResult(mailType);
            if (hashMap != null) {
                this.smtpHost = String.valueOf(hashMap.get(Constant.SMTP_HOST));
                this.smtpPort = (int) hashMap.get(Constant.SMTP_PORT);
                this.popHost = String.valueOf(hashMap.get(Constant.POP3_HOST));
                this.popPort = (int) hashMap.get(Constant.POP3_PORT);
                this.imapHost = String.valueOf(hashMap.get(Constant.IMAP_HOST));
                this.imapPort = (int) hashMap.get(Constant.IMAP_PORT);
            }
            return this;
        }

        public Config setAccount(String account) {
            this.account = account;
            return this;
        }

        public Config setPassword(String password) {
            this.password = password;
            return this;
        }

        public Config setSMTP(String host, int port) {
            this.smtpHost = host;
            this.smtpPort = port;
            return this;
        }

        public Config setPOP3(String host, int port) {
            this.popHost = host;
            this.popPort = port;
            return this;
        }

        public Config setIMAP(String host, int port) {
            this.imapHost = host;
            this.imapPort = port;
            return this;
        }

        protected String getAccount() {
            return account;
        }

        protected String getPassword() {
            return password;
        }

        protected String getSmtpHost() {
            return smtpHost;
        }

        protected String getPopHost() {
            return popHost;
        }

        protected String getImapHost() {
            return imapHost;
        }

        protected int getSmtpPort() {
            return smtpPort;
        }

        protected int getPopPort() {
            return popPort;
        }

        protected int getImapPort() {
            return imapPort;
        }

    }

    /**
     * 获取发送邮件服务
     * @param config
     * @return
     */
    public static SendService getSendService(Config config) {
        return new SendService(config);
    }

    /**
     * 获取接收邮件服务
     * @param config
     * @return
     */
    public static ReceiveService getReceiveService(Config config) {
        return new ReceiveService(config);
    }

    /**
     * 检查邮件服务器
     * @param config
     * @return
     */
    public static ExamineService getExamineService(Config config) {
        return new ExamineService(config);
    }

    /**
     * 邮件发送回调
     */
    public interface GotSendCallback {
        void success();
        void failure(String msg);
    }

    /**
     * 邮件接收回调
     */
    public interface GotReceiveCallback {
        void complete(int total);
        void receiving(Message message);
        void received(List<Message> messageList);
        void failure(String msg);
    }

    /**
     * 获取消息回调
     */
    public interface GotMessageCallback {
        void success(Message message);
        void failure(String msg);
    }

    public interface GotMessageListCallback {
        void success(List<Message> messageList);
        void failure(String msg);
    }

    /**
     * 获取邮件数量回调
     */
    public interface GotCountCallback {
        void success(int total);
        void failure(String msg);
    }

    /**
     * 获取邮箱的全部UID
     */
    public interface GotUIDListCallback {
        void success(long[] uidList);
        void failure(String msg);
    }

    /**
     * 连接回调
     */
    public interface GotConnectCallback {
        void success();
        void failure(String msg);
    }

}
