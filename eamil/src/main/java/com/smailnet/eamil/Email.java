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


import android.content.Context;

import com.smailnet.eamil.entity.Message;

import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;

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
        private int type;
        private boolean smtpSSL;
        private boolean popSSL;
        private boolean imapSSL;

        public Config setMailType(int type) {
            this.type = type;
            HashMap<String, Object> hashMap = Converter.MailType.getParam(type);
            if (hashMap != null) {
                this.smtpHost = String.valueOf(hashMap.get(Constant.SMTP_HOST));
                this.smtpPort = (int) hashMap.get(Constant.SMTP_PORT);
                this.popHost = String.valueOf(hashMap.get(Constant.POP3_HOST));
                this.popPort = (int) hashMap.get(Constant.POP3_PORT);
                this.imapHost = String.valueOf(hashMap.get(Constant.IMAP_HOST));
                this.imapPort = (int) hashMap.get(Constant.IMAP_PORT);
                this.smtpSSL = (boolean) hashMap.get(Constant.SMTP_SSL);
                this.popSSL = (boolean) hashMap.get(Constant.POP3_SSL);
                this.imapSSL = (boolean) hashMap.get(Constant.IMAP_SSL);
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

        public Config setSMTP(String host, int port, boolean ssl) {
            this.smtpHost = host;
            this.smtpPort = port;
            this.smtpSSL = ssl;
            return this;
        }

        public Config setPOP3(String host, int port, boolean ssl) {
            this.popHost = host;
            this.popPort = port;
            this.popSSL = ssl;
            return this;
        }

        public Config setIMAP(String host, int port, boolean ssl) {
            this.imapHost = host;
            this.imapPort = port;
            this.imapSSL = ssl;
            return this;
        }

        int getType() {
            return type;
        }

        String getAccount() {
            return account;
        }

        String getPassword() {
            return password;
        }

        String getSmtpHost() {
            return smtpHost;
        }

        String getPopHost() {
            return popHost;
        }

        String getImapHost() {
            return imapHost;
        }

        int getSmtpPort() {
            return smtpPort;
        }

        int getPopPort() {
            return popPort;
        }

        int getImapPort() {
            return imapPort;
        }

        boolean isSmtpSSL() {
            return smtpSSL;
        }

        boolean isPopSSL() {
            return popSSL;
        }

        boolean isImapSSL() {
            return imapSSL;
        }
    }

    /**
     * Email框架初始化，该方法应该在自定义的Application中调用。
     * @param context
     */
    public static void initialize(Context context) {
        Manager.setContext(context);
    }

    /**
     * 销毁Email框架内部的某些对象。
     * @return
     */
    public static boolean destroy() {
        try {
            Manager.destroy();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取全局配置对象
     * @return
     */
    public static GlobalConfig getGlobalConfig() {
        return Manager.getGlobalConfig();
    }

    /**
     * 获取发送邮件服务
     * @return
     */
    public static SendService getSendService() {
        return new SendService();
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
     * @return
     */
    public static ReceiveService getReceiveService() {
        return new ReceiveService();
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
     * @return
     */
    public static ExamineService getExamineService() {
        return new ExamineService();
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
    public interface GetSendCallback {
        void onSuccess();
        void onFailure(String msg);
    }

    /**
     * 邮件接收回调
     */
    public interface GetReceiveCallback {
        void receiving(Message message, int index, int total);
        void onFinish(List<Message> messageList);
        void onFailure(String msg);
    }

    /**
     * 获取消息回调
     */
    public interface GetMessageCallback {
        void onSuccess(Message message);
        void onFailure(String msg);
    }

    /**
     * 获取全部消息回调
     */
    public interface GetMessageListCallback {
        void onSuccess(List<Message> messageList);
        void onFailure(String msg);
    }

    /**
     * 获取邮件数量回调
     */
    public interface GetCountCallback {
        void onSuccess(int total);
        void onFailure(String msg);
    }

    public interface GetSyncMessageCallback {
        void onSuccess(List<Message> messageList, long[] deleteUidList);
        void onFailure(String msg);
    }

    /**
     * 获取邮箱的全部UID回调
     */
    public interface GetUIDListCallback {
        void onSuccess(long[] uidList);
        void onFailure(String msg);
    }

    /**
     * 连接回调
     */
    public interface GetConnectCallback {
        void onSuccess();
        void onFailure(String msg);
    }

    /**
     * 标记回调
     */
    public interface GetFlagCallback {
        void onSuccess();
        void onFailure(String msg);
    }

    /**
     * 常用的邮箱的类型选择
     */
    public interface MailType {
        int QQ = 1;
        int FOXMAIL = 2;
        int EXMAIL = 3;
        int $163 = 4;
        int $126 = 5;
        int OUTLOOK = 6;
    }

}
