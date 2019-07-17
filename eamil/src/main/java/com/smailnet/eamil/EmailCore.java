package com.smailnet.eamil;

import android.text.TextUtils;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.smailnet.eamil.Constant.IMAP;
import static com.smailnet.eamil.Constant.POP3;
import static com.smailnet.eamil.Constant.SMTP;

/**
 * 该框架内部的核心类
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
    private javax.mail.Message message;

    private EmailCore(Email.Config config) {
        this.account = config.getAccount();
        this.password = config.getPassword();
        this.smtpHost = config.getSmtpHost();
        this.popHost = config.getPopHost();
        this.imapHost = config.getImapHost();
        this.smtpPort = String.valueOf(config.getSmtpPort());
        this.popPort = String.valueOf(config.getPopPort());
        this.imapPort = String.valueOf(config.getImapPort());

        String sslSocketFactory = "javax.net.ssl.SSLSocketFactory";
        String isFallback = "false";

        Properties properties = new Properties();
        if (!TextUtils.isEmpty(smtpHost) && !TextUtils.isEmpty(smtpPort)) {
            properties.put(Constant.MAIL_SMTP_SOCKET_FACTORY_CLASS, sslSocketFactory);
            properties.put(Constant.MAIL_SMTP_SOCKET_FACTORY_FALLBACK, isFallback);
            properties.put(Constant.MAIL_SMTP_SOCKET_FACTORY_PORT, smtpPort);
            properties.put(Constant.MAIL_SMTP_POST, smtpPort);
            properties.put(Constant.MAIL_SMTP_HOST, smtpHost);
            properties.put(Constant.MAIL_SMTP_AUTH, true);
        }
        if (!TextUtils.isEmpty(popHost) && !TextUtils.isEmpty(popPort)) {
            properties.put(Constant.MAIL_POP3_SOCKET_FACTORY_CLASS, sslSocketFactory);
            properties.put(Constant.MAIL_POP3_SOCKET_FACTORY_FALLBACK, isFallback);
            properties.put(Constant.MAIL_POP3_SOCKET_FACTORY_PORT, popPort);
            properties.put(Constant.MAIL_POP3_POST, popPort);
            properties.put(Constant.MAIL_POP3_HOST, popHost);
            properties.put(Constant.MAIL_POP3_AUTH, true);
        }
        if (!TextUtils.isEmpty(imapHost) && !TextUtils.isEmpty(imapPort)) {
            properties.put(Constant.MAIL_IMAP_SOCKET_FACTORY_CLASS, sslSocketFactory);
            properties.put(Constant.MAIL_IMAP_SOCKET_FACTORY_FALLBACK, isFallback);
            properties.put(Constant.MAIL_IMAP_SOCKET_FACTORY_PORT, imapPort);
            properties.put(Constant.MAIL_IMAP_POST, imapPort);
            properties.put(Constant.MAIL_IMAP_HOST, imapHost);
            properties.put(Constant.MAIL_IMAP_AUTH, true);
        }
        session = Session.getInstance(properties);
    }

    static EmailCore setConfig(Email.Config config) {
        return new EmailCore(config);
    }

    /**
     * 组装邮件的信息
     * @param nickname
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param text
     * @param content
     * @return
     * @throws MessagingException
     */
    EmailCore setMessage(String nickname, Address[] to, Address[] cc, Address[] bcc, String subject, String text, Object content) {
        try {
            javax.mail.Message message = new MimeMessage(session);
            message.addRecipients(javax.mail.Message.RecipientType.TO, to);
            if (cc != null) {
                message.addRecipients(javax.mail.Message.RecipientType.CC, cc);
            }
            if (bcc != null) {
                message.addRecipients(javax.mail.Message.RecipientType.BCC, bcc);
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
        } catch (MessagingException e) {
            e.printStackTrace();
            this.message = null;
        }
        return this;
    }

    /**
     * 发送邮件
     * @throws MessagingException
     */
    void send(Email.GotSendCallback gotSendCallback) {
        try {
            Transport transport = session.getTransport(SMTP);
            transport.connect(smtpHost, account, password);
            transport.sendMessage(message, message.getRecipients(javax.mail.Message.RecipientType.TO));
            transport.close();
            gotSendCallback.success();
        } catch (MessagingException e) {
            e.printStackTrace();
            gotSendCallback.failure(e.getMessage());
        }
    }

    /**
     * 使用POP3协议或IMAP协议接收服务器上的邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    void receiveAll(int protocolType, Email.GotReceiveCallback gotMessageCallback) {
        try {
            Store store;
            if (protocolType == ProtocolType.POP3) {
                store = session.getStore(POP3);
                store.connect(popHost, account, password);
            } else {
                store = session.getStore(IMAP);
                store.connect(imapHost, account, password);
            }
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            javax.mail.Message[] messages = folder.getMessages();
            String subject, from, to, date, content;
            List<Message> messageList = new ArrayList<>();
            for (javax.mail.Message msg: messages){
                subject = msg.getSubject();
                from = Converter.MailAddress.toString(msg.getFrom());
                to = Converter.MailAddress.toString(msg.getAllRecipients());
                date = Converter.Date.toString(msg.getSentDate());
                content = Converter.Content.toString(msg);
                Message message = new Message(subject, from, to, date, content);
                messageList.add(message);
                gotMessageCallback.receiving(message);
            }
            gotMessageCallback.received(messageList);
            gotMessageCallback.complete(folder.getMessageCount());
            folder.close(false);
            store.close();
        }catch (MessagingException e) {
            e.printStackTrace();
            gotMessageCallback.failure(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            gotMessageCallback.failure(e.toString());
        }
    }

    /**
     * 获取全部邮件数量
     * @param protocolType
     * @param gotCountCallback
     */
    void getMessageCount(int protocolType, Email.GotCountCallback gotCountCallback) {
        try {
            Store store;
            if (protocolType == ProtocolType.POP3) {
                store = session.getStore(POP3);
                store.connect(popHost, account, password);
            } else {
                store = session.getStore(IMAP);
                store.connect(imapHost, account, password);
            }
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            gotCountCallback.success(folder.getMessageCount());
        }catch (MessagingException e) {
            e.printStackTrace();
            gotCountCallback.failure(e.getMessage());
        }
    }

    /**
     * 获取未读邮件数量
     * @param gotCountCallback
     */
    void getUnreadMessageCount(Email.GotCountCallback gotCountCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            gotCountCallback.success(folder.getUnreadMessageCount());
        }catch (MessagingException e) {
            e.printStackTrace();
            gotCountCallback.failure(e.getMessage());
        }
    }

    /**
     * 获取全部UID
     * @param gotUIDListCallback
     */
    void getUIDList(Email.GotUIDListCallback gotUIDListCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            IMAPFolder imapFolder = (IMAPFolder) folder;
            javax.mail.Message[] messages = imapFolder.getMessages();
            long[] uidList = new long[messages.length];
            for (int i = 0, len = messages.length; i < len; i++) {
                MimeMessage mimeMessage = (MimeMessage) messages[i];
                uidList[i] = imapFolder.getUID(mimeMessage);
            }
            gotUIDListCallback.success(uidList);
        }catch (MessagingException e) {
            e.printStackTrace();
            gotUIDListCallback.failure(e.getMessage());
        }
    }

    /**
     * 获取某一封邮件消息
     * @param uid
     * @param gotMessageCallback
     */
    void getMessage(long uid, Email.GotMessageCallback gotMessageCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            IMAPFolder imapFolder = (IMAPFolder) folder;
            javax.mail.Message msg = imapFolder.getMessageByUID(uid);
            String subject = msg.getSubject();
            String from = Converter.MailAddress.toString(msg.getFrom());
            String to = Converter.MailAddress.toString(msg.getAllRecipients());
            String date = Converter.Date.toString(msg.getSentDate());
            String content = Converter.Content.toString(msg);
            Message message = new Message(subject, from, to, date, content);
            gotMessageCallback.success(message);
        }catch (MessagingException e) {
            e.printStackTrace();
            gotMessageCallback.failure(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            gotMessageCallback.failure(e.getMessage());
        }
    }

    /**
     * 获取多封邮件消息
     * @param uidList
     * @param gotMessageListCallback
     */
    void getMessageList(long[] uidList, Email.GotMessageListCallback gotMessageListCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            IMAPFolder imapFolder = (IMAPFolder) folder;
            javax.mail.Message[] messages = imapFolder.getMessagesByUID(uidList);
            List<Message> messageList = new ArrayList<>();
            for (javax.mail.Message msg : messages) {
                String subject = msg.getSubject();
                String from = Converter.MailAddress.toString(msg.getFrom());
                String to = Converter.MailAddress.toString(msg.getAllRecipients());
                String date = Converter.Date.toString(msg.getSentDate());
                String content = Converter.Content.toString(msg);
                Message message = new Message(subject, from, to, date, content);
                messageList.add(message);
            }
            gotMessageListCallback.success(messageList);
        }catch (MessagingException e) {
            e.printStackTrace();
            gotMessageListCallback.failure(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            gotMessageListCallback.failure(e.getMessage());
        }
    }

    /**
     * 邮箱帐号检查
     */
    void connect(Email.GotConnectCallback gotConnectCallback) {
        try {
            Transport transport = session.getTransport(SMTP);
            POP3Store pop3Store = (POP3Store) session.getStore(POP3);
            IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
            if (!TextUtils.isEmpty(smtpHost) && !TextUtils.isEmpty(smtpPort)) {
                transport.connect(smtpHost, account, password);
            }
            if (!TextUtils.isEmpty(popHost) && !TextUtils.isEmpty(popPort)) {
                pop3Store.connect(popHost, account, password);
            }
            if (!TextUtils.isEmpty(imapHost) && !TextUtils.isEmpty(imapPort)) {
                imapStore.connect(imapHost, account, password);
            }
            gotConnectCallback.success();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            gotConnectCallback.failure(e.getMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
            gotConnectCallback.failure(e.getMessage());
        }
    }

    /**
     * 邮件协议类型
     */
    interface ProtocolType {
        int POP3 = 0;
        int IMAP = 1;
    }
}
