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

import com.smailnet.eamil.Entity.EmailMessage;
import com.smailnet.eamil.Utils.CodeUtil;
import com.smailnet.eamil.Utils.ConfigCheckUtil;
import com.smailnet.eamil.Utils.ContentUtil;
import com.smailnet.eamil.Utils.TimeUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送和接收电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务
 * 器，即可使用，所见即所得哦！
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 2.1
 */
class EmailCore {

    private EmailConfig emailConfig;
    private Session session;

    /**
     * 在构造器中初始化Properties和Session
     *
     * @param emailConfig
     */
    EmailCore(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
        Properties properties = new Properties();

        if (ConfigCheckUtil.getResult(emailConfig.getSmtpHost(), emailConfig.getSmtpPort())) {
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");
            properties.put("mail.smtp.socketFactory.port", String.valueOf(emailConfig.getSmtpPort()));
            properties.put("mail.smtp.post", String.valueOf(emailConfig.getSmtpPort()));
            properties.put("mail.smtp.host", emailConfig.getSmtpHost());
            properties.put("mail.smtp.auth", true);
        }

        if (ConfigCheckUtil.getResult(emailConfig.getPopHost(), emailConfig.getPopPort())) {
            properties.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.pop3.socketFactory.fallback", "false");
            properties.put("mail.pop3.socketFactory.port", String.valueOf(emailConfig.getPopPort()));
            properties.put("mail.pop3.post", String.valueOf(emailConfig.getPopPort()));
            properties.put("mail.pop3.host", emailConfig.getPopHost());
            properties.put("mail.pop3.auth", true);
        }

        if (ConfigCheckUtil.getResult(emailConfig.getImapHost(), emailConfig.getImapPort())) {
            properties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.imap.socketFactory.fallback", "false");
            properties.put("mail.imap.socketFactory.port", String.valueOf(emailConfig.getImapPort()));
            properties.put("mail.imap.post", String.valueOf(emailConfig.getImapPort()));
            properties.put("mail.imap.host", emailConfig.getImapHost());
            properties.put("mail.imap.auth", true);
        }

        session = Session.getInstance(properties);
    }

    /**
     * 验证邮箱帐户和服务器配置信息
     *
     * @throws MessagingException
     */
    public void authentication() throws MessagingException {
        Transport transport = session.getTransport("smtp");
        Store store = session.getStore("pop3");
        IMAPStore imapStore = (IMAPStore) session.getStore("imap");

        if (ConfigCheckUtil.getResult(emailConfig.getSmtpHost(), emailConfig.getSmtpPort())) {
            transport.connect(emailConfig.getSmtpHost(), emailConfig.getAccount(), emailConfig.getPassword());
        }

        if (ConfigCheckUtil.getResult(emailConfig.getPopHost(), emailConfig.getPopPort())) {
            store.connect(emailConfig.getPopHost(), emailConfig.getAccount(), emailConfig.getPassword());
        }

        if (ConfigCheckUtil.getResult(emailConfig.getImapHost(), emailConfig.getImapPort())) {
            imapStore.connect(emailConfig.getImapHost(), emailConfig.getAccount(), emailConfig.getPassword());
        }
    }

    /**
     * 组装邮件的信息
     *
     * @param address
     * @param subject
     * @param content
     * @return
     * @throws MessagingException
     */
    public Message setMessage(Address[] address, String subject, Object content) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setRecipients(Message.RecipientType.TO, address);
        message.setFrom(new InternetAddress(emailConfig.getAccount()));
        message.setSubject(subject);
        message.setContent(content, "text/html");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    /**
     * 发送邮件
     *
     * @param message
     * @throws MessagingException
     */
    public void sendMail(Message message) throws MessagingException {
        Transport transport = session.getTransport("smtp");
        transport.connect(emailConfig.getSmtpHost(), emailConfig.getAccount(), emailConfig.getPassword());
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    /**
     * 获取收件服务器上的邮件
     *
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailMessage> popReceiveMail() throws MessagingException, IOException {
        Store store = session.getStore("pop3");
        store.connect(emailConfig.getPopHost(), emailConfig.getAccount(), emailConfig.getPassword());
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String subject, from, to, date, content;
        for (Message message : messages){
            subject = message.getSubject();
            from = CodeUtil.conver(String.valueOf(message.getFrom()[0]));
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

    public List<EmailMessage> imapReceiveMail() throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore("imap");
        imapStore.connect(emailConfig.getImapHost(), emailConfig.getAccount(), emailConfig.getPassword());
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String subject, from, to, date, content;
        for (Message message : messages){
            subject = message.getSubject();
            from = CodeUtil.conver(String.valueOf(message.getFrom()[0]));
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
}
