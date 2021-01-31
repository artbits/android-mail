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
package com.smailnet.emailkit;


import android.content.Context;

import java.io.File;
import java.util.List;

import javax.mail.MessagingException;

/**
 * EmailKit for Android是以JavaMail类库为基础进行封装的框架，它比JavaMail
 * 更简单易用，在使用它开发电子邮件客户端时，还能避免对电子邮件协议不熟悉的
 * 烦恼。目前EmailKit支持的电子邮件协议有SMTP和IMAP，它支持的功能有发送邮件，
 * 下载附件、获取文件夹列表、读取邮件、加载邮件、同步邮件，对邮件消息的移动，
 * 删除，保存到草稿箱等操作，同时支持邮箱的新邮件消息推送（需要邮件服务器支持
 * 相关命令），邮件搜索等功能。把它依赖到你的Android项目中，你只需简单配置邮件
 * 服务器的参数，再使用这些简易的接口，即可完成你所需的功能，所见即所得。
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 4.2.1
 */
public final class EmailKit {

    /**
     * EmailKit框架初始化，该方法应该在自定义的Application中调用。
     * @param context   上下文
     */
    public static void initialize(Context context) {
        ObjectManager.setContext(context);
        ObjectManager.setDirectory(null);
    }

    /**
     * EmailKit框架初始化，该方法应该在自定义的Application中调用。
     * @param context   上下文
     * @param directory 保持附件的目录路径
     */
    public static void initialize(Context context, String directory) {
        ObjectManager.setContext(context);
        ObjectManager.setDirectory(directory);
    }

    /**
     * 销毁EmailKit内部的已和邮件服务器连接的对象
     * 和线程池的销毁
     */
    public static void destroy() {
        ObjectManager.getMultiThreadService()
                .submit(() -> {
                    try {
                        ObjectManager.destroy();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 获取SMTP服务
     * @param config    EmailKit.Config对象
     * @return  SMTPService类的对象
     */
    public static SMTPService useSMTPService(Config config) {
        return new SMTPService(config);
    }

    /**
     * 获取IMAP服务
     * @param config    EmailKit.Config对象
     * @return  IMAPService类的对象
     */
    public static IMAPService useIMAPService(Config config) {
        return new IMAPService(config);
    }

    /**
     * 当已配置完服务器参数时，可通过该方法来验证配置的邮件服务器参数
     * 和邮箱账号密码是否正确
     * @param config
     * @return  配置参数全部正确返回值为true，否则为false
     */
    public static void auth(EmailKit.Config config, GetAuthCallback getAuthCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.auth(config, new GetAuthCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getAuthCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getAuthCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 邮件发送回调
     */
    public interface GetSendCallback {
        void onSuccess();
        void onFailure(String errMsg);
    }

    /**
     * 邮件接收回调
     */
    public interface GetReceiveCallback {
        void receiving(Message msg, int index, int total);
        void onFinish(List<Message> msgList);
        void onFailure(String errMsg);
    }

    /**
     * 邮件加载回调
     */
    public interface GetLoadCallback {
        void onSuccess(List<Message> msgList);
        void onFailure(String errMsg);
    }

    /**
     * 邮件同步回调
     */
    public interface GetSyncCallback {
        void onSuccess(List<Message> newMsgList, long[] deletedUID);
        void onFailure(String errMsg);
    }

    /**
     * 获取邮件消息回调
     */
    public interface GetMsgCallback {
        void onSuccess(Message msg);
        void onFailure(String errMsg);
    }

    /**
     * 获取多封邮件消息回调
     */
    public interface GetMsgListCallback {
        void onSuccess(List<Message> msgList);
        void onFailure(String errMsg);
    }

    /**
     * 获取邮件数量回调
     */
    public interface GetCountCallback {
        void onSuccess(int total, int unread);
        void onFailure(String errMsg);
    }

    /**
     * 获取邮箱的全部UID回调
     */
    public interface GetUIDListCallback {
        void onSuccess(long[] uidList);
        void onFailure(String errMsg);
    }

    /**
     * 验证回调
     */
    public interface GetAuthCallback {
        void onSuccess();
        void onFailure(String errMsg);
    }

    /**
     * 获取邮箱的系统（默认）文件夹列表回调
     */
    public interface GetFolderListCallback {
        void onSuccess(List<String> folderList);
        void onFailure(String errMsg);
    }

    /**
     * 操作邮件消息回调
     */
    public interface GetOperateCallback {
        void onSuccess();
        void onFailure(String errMsg);
    }

    /**
     * 搜索邮件回调
     */
    public interface GetSearchCallback {
        void onSuccess(List<Message> msgList);
        void onFailure(String errMsg);
    }

    /**
     * 新邮件消息监听回调
     */
    public interface OnMsgListener {
        void onMsg(List<Message> msgList);
        void onError(String errMsg);
    }

    /**
     * 附件下载回调
     */
    public interface GetDownloadCallback {
        void download(File file);
    }

    /**
     * 常用的邮箱的类型选择
     */
    public interface MailType {
        String QQ = "@qq.com";
        String FOXMAIL = "@foxmail.com";
        String EXMAIL = "@exmail.qq.com";
        String OUTLOOK = "@outlook.com";
        String OFFICE365 = "@office365.com";
        String GMAIL = "@gmail.com";
        String YEAH = "@yeah.net";
        String $163 = "@163.com";
        String $126 = "@126.com";
    }

    /**
     * 邮件的服务器配置内容
     */
    public static class Config {

        private String account;
        private String password;
        private String smtpHost;
        private String imapHost;
        private int smtpPort;
        private int imapPort;
        private boolean smtpSSL;
        private boolean imapSSL;

        /**
         * 快速配置邮件服务器参数，目前主流邮箱有QQ邮箱，Foxmail、
         * 腾讯企业邮（EXMAIL）、Outlook、163和126邮箱
         * @param type  选择邮箱的类型
         * @return
         */
        public Config setMailType(String type) {
            Config config = Converter.MailTypeUtils.getMailConfiguration(type);
            if (config != null) {
                this.smtpHost = config.getSMTPHost();
                this.smtpPort = config.getSMTPPort();
                this.imapHost = config.getIMAPHost();
                this.imapPort = config.getIMAPPort();
                this.smtpSSL = config.isSMTPSSL();
                this.imapSSL = config.isIMAPSSL();
            }
            return this;
        }

        /**
         * 设置账号，即是电子邮件的地址
         * @param account   电子邮件地址
         * @return
         */
        public Config setAccount(String account) {
            this.account = account;
            return this;
        }

        /**
         * 设置电子邮件的密码或授权码（授权码需要到邮箱服务提供商
         * 的官网中获取）
         * @param password  密码或授权码
         * @return
         */
        public Config setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * 设置SMTP服务器的主机地址和端口号，相关host和port可以
         * 到邮箱服务提供商中获取。
         * @param host  主机地址，例QQ邮箱为：“smtp.qq.com”
         * @param port  端口号，例QQ邮箱为：465
         * @param ssl   若端口号支持ssl，则设置为true，否则为false
         * @return
         */
        public Config setSMTP(String host, int port, boolean ssl) {
            this.smtpHost = host;
            this.smtpPort = port;
            this.smtpSSL = ssl;
            return this;
        }

        /**
         * 设置IMAP服务器的主机地址和端口号，相关host和port可以
         * 到邮箱服务提供商中获取。
         * @param host  主机地址，例QQ邮箱为：“imap.qq.com”
         * @param port  端口号，例QQ邮箱为：993
         * @param ssl   若端口号支持ssl，则设置为true，否则为false
         * @return
         */
        public Config setIMAP(String host, int port, boolean ssl) {
            this.imapHost = host;
            this.imapPort = port;
            this.imapSSL = ssl;
            return this;
        }

        String getAccount() {
            return account;
        }

        String getPassword() {
            return password;
        }

        String getSMTPHost() {
            return smtpHost;
        }

        String getIMAPHost() {
            return imapHost;
        }

        int getSMTPPort() {
            return smtpPort;
        }

        int getIMAPPort() {
            return imapPort;
        }

        boolean isSMTPSSL() {
            return smtpSSL;
        }

        boolean isIMAPSSL() {
            return imapSSL;
        }
    }

}
