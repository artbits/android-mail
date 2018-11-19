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

import android.text.TextUtils;

import com.smailnet.eamil.Utils.AddressUtil;
import com.smailnet.eamil.Utils.ConfigCheckUtil;
import com.smailnet.eamil.Utils.ContentUtil;
import com.smailnet.eamil.Utils.TimeUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.smailnet.eamil.Utils.ConstUtli.BLACK_HOLE;
import static com.smailnet.eamil.Utils.ConstUtli.IMAP;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_AUTH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_HOST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_POST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_SOCKETFACTORY_CLASS;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_SOCKETFACTORY_FALLBACK;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_SOCKETFACTORY_PORT;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_AUTH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_HOST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_POST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_SOCKETFACTORY_CLASS;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_SOCKETFACTORY_FALLBACK;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_SOCKETFACTORY_PORT;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_AUTH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_HOST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_POST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_SOCKETFACTORY_CLASS;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_SOCKETFACTORY_FALLBACK;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_SOCKETFACTORY_PORT;
import static com.smailnet.eamil.Utils.ConstUtli.POP3;
import static com.smailnet.eamil.Utils.ConstUtli.SMTP;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送和接收电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务
 * 器，即可使用，所见即所得哦！
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 2.3
 */
class EmailCore {

    private String smtpHost;
    private String popHost;
    private String imapHost;
    private String smtpPort;
    private String popPort;
    private String imapPort;
    private String account;
    private String password;
    private Session session;

    private Message message;

    /**
     * 默认构造器
     */
    EmailCore(){

    }

    /**
     * 在构造器中初始化Properties和Session
     * @param emailConfig
     */
    EmailCore(EmailConfig emailConfig){
        this.smtpHost = emailConfig.getSmtpHost();
        this.popHost = emailConfig.getPopHost();
        this.imapHost = emailConfig.getImapHost();
        this.smtpPort = String.valueOf(emailConfig.getSmtpPort());
        this.popPort = String.valueOf(emailConfig.getPopPort());
        this.imapPort = String.valueOf(emailConfig.getImapPort());
        this.account = emailConfig.getAccount();
        this.password = emailConfig.getPassword();
        Properties properties = new Properties();

        String sslSocketFactory = "javax.net.ssl.SSLSocketFactory";
        String isFallback = "false";
        if (ConfigCheckUtil.getResult(smtpHost, smtpPort)) {
            properties.put(MAIL_SMTP_SOCKETFACTORY_CLASS, sslSocketFactory);
            properties.put(MAIL_SMTP_SOCKETFACTORY_FALLBACK, isFallback);
            properties.put(MAIL_SMTP_SOCKETFACTORY_PORT, smtpPort);
            properties.put(MAIL_SMTP_POST, smtpPort);
            properties.put(MAIL_SMTP_HOST, smtpHost);
            properties.put(MAIL_SMTP_AUTH, true);
        }
        if (ConfigCheckUtil.getResult(popHost, popPort)) {
            properties.put(MAIL_POP3_SOCKETFACTORY_CLASS, sslSocketFactory);
            properties.put(MAIL_POP3_SOCKETFACTORY_FALLBACK, isFallback);
            properties.put(MAIL_POP3_SOCKETFACTORY_PORT, popPort);
            properties.put(MAIL_POP3_POST, popPort);
            properties.put(MAIL_POP3_HOST, popHost);
            properties.put(MAIL_POP3_AUTH, true);
        }
        if (ConfigCheckUtil.getResult(imapHost, imapPort)) {
            properties.put(MAIL_IMAP_SOCKETFACTORY_CLASS, sslSocketFactory);
            properties.put(MAIL_IMAP_SOCKETFACTORY_FALLBACK, isFallback);
            properties.put(MAIL_IMAP_SOCKETFACTORY_PORT, imapPort);
            properties.put(MAIL_IMAP_POST, imapPort);
            properties.put(MAIL_IMAP_HOST, imapHost);
            properties.put(MAIL_IMAP_AUTH, true);
        }

        session = Session.getInstance(properties);
    }

    /**
     * 验证邮箱帐户和服务器配置信息
     * @throws MessagingException
     */
    public void authentication() throws MessagingException {
        Transport transport = session.getTransport(SMTP);
        Store store = session.getStore(POP3);
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);

        if (ConfigCheckUtil.getResult(smtpHost, smtpPort)) {
            transport.connect(smtpHost, account, password);
        }
        if (ConfigCheckUtil.getResult(popHost, popPort)) {
            store.connect(popHost, account, password);
        }
        if (ConfigCheckUtil.getResult(imapHost, imapPort)) {
            imapStore.connect(imapHost, account, password);
        }
    }

    /**
     * 组装邮件的信息
     * @param nickname
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param content
     * @throws MessagingException
     */
    public EmailCore setMessage(String nickname, Address[] to, Address[] cc, Address[] bcc, String subject, String text, Object content) throws MessagingException {
        Message message = new MimeMessage(session);
        message.addRecipients(Message.RecipientType.TO, to);
        if (cc != null) {
            message.addRecipients(Message.RecipientType.CC, cc);
        }
        if (bcc != null) {
            message.addRecipients(Message.RecipientType.BCC, bcc);
        }
        message.setFrom(new InternetAddress(nickname + "<" + account + ">"));
        message.setSubject(subject);
        if (text != null){
            message.setText(text);
        }else if (content != null){
            message.setContent(content, "text/html");
        }
        message.setSentDate(new Date());
        message.saveChanges();
        this.message = message;
        return this;
    }

    /**
     * 使用SMTP协议发送邮件
     * @throws MessagingException
     */
    public void sendMail() throws MessagingException {
        Transport transport = session.getTransport(SMTP);
        transport.connect(smtpHost, account, password);
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    /**
     * 使用POP3协议接收服务器上的邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailMessage> popReceiveMail() throws MessagingException, IOException {
        Store store = session.getStore(POP3);
        store.connect(popHost, account, password);
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String subject, from, to, date, content;
        for (Message message : messages){
            subject = message.getSubject();
            from = AddressUtil.codeConver(String.valueOf(message.getFrom()[0]));
            to = Arrays.toString(message.getRecipients(Message.RecipientType.TO));
            date = TimeUtil.getDate(message.getSentDate());
            content = ContentUtil.getContent(message);
            EmailMessage emailMessage = new EmailMessage(subject, from, to, date, content);
            emailMessageList.add(emailMessage);
        }
        folder.close(false);
        store.close();
        return emailMessageList;
    }

    /**
     * 使用IMAP协议接收服务器上的邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailMessage> imapReceiveMail() throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        imapStore.connect(imapHost, account, password);
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String subject, from, to, date, content;
        for (Message message : messages){
            subject = message.getSubject();
            from = AddressUtil.codeConver(String.valueOf(message.getFrom()[0]));
            to = Arrays.toString(message.getRecipients(Message.RecipientType.TO));
            date = TimeUtil.getDate(message.getSentDate());
            content = ContentUtil.getContent(message);
            EmailMessage emailMessage = new EmailMessage(subject, from, to, date, content);
            emailMessageList.add(emailMessage);
        }
        folder.close(false);
        imapStore.close();
        return emailMessageList;
    }

    /**
     *
     * @param host
     * @throws UnknownHostException
     */
    public void spamCheck(String host) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(host);
        byte[] bytes = inetAddress.getAddress();
        StringBuilder query = new StringBuilder(BLACK_HOLE);
        for (byte octet : bytes){
            int unsignedByte = (octet < 0)? octet + 256 : octet;
            query.insert(0, unsignedByte + ".");
        }
        InetAddress.getByName(query.toString());
    }
}
