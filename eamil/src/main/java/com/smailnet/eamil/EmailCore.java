package com.smailnet.eamil;

import android.text.TextUtils;

import com.smailnet.eamil.entity.Message;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
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

    private static EmailCore core;

    /**
     * 构造方法
     */
    private EmailCore() {

        GlobalConfig globalConfig = Email.getGlobalConfig();
        this.account = globalConfig.getAccount();
        this.password = globalConfig.getPassword();
        this.smtpHost = globalConfig.getSmtpHost();
        this.popHost = globalConfig.getPopHost();
        this.imapHost = globalConfig.getImapHost();
        this.smtpPort = String.valueOf(globalConfig.getSmtpPort());
        this.popPort = String.valueOf(globalConfig.getPopPort());
        this.imapPort = String.valueOf(globalConfig.getImapPort());

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

    /**
     * 构造方法
     * @param config
     */
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

    /**
     * 设置配置参数
     * @param config
     * @return
     */
    static EmailCore setConfig(Email.Config config) {
        if (config == null) {
            throw new RuntimeException(Constant.CONFIG_EXCEPTION);
        }
        return new EmailCore(config);
    }

    /**
     * 获取自动配置
     * @return
     */
    static EmailCore getAutoConfig() {
        core = (core == null)? new EmailCore() : core;
        return core;
    }

    /**
     * 组装需要发送的邮件信息
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
    void setMessage(String nickname, Address[] to, Address[] cc, Address[] bcc, String subject, String text, Object content) {
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
            if (text != null) {
                message.setText(text);
            } else if (content != null) {
                message.setContent(content, "text/html");
            }
            message.setSentDate(new Date());
            message.saveChanges();
            this.message = message;
        } catch (MessagingException e) {
            e.printStackTrace();
            this.message = null;
        }
    }

    /**
     * 发送邮件
     * @param getSendCallback
     */
    void send(Email.GetSendCallback getSendCallback) {
        try {
            Transport transport = session.getTransport(SMTP);
            transport.connect(smtpHost, account, password);
            transport.sendMessage(message, message.getRecipients(javax.mail.Message.RecipientType.TO));
            transport.close();
            getSendCallback.onSuccess();
        } catch (MessagingException e) {
            e.printStackTrace();
            getSendCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 使用POP3协议或IMAP协议接收服务器上的邮件
     * @param protocol
     * @param getReceiveCallback
     */
    void receive(int protocol, Email.GetReceiveCallback getReceiveCallback) {
        try {
            Store store;
            if (protocol == Protocol.POP3) {
                store = session.getStore(POP3);
                store.connect(popHost, account, password);
            } else {
                store = session.getStore(IMAP);
                store.connect(imapHost, account, password);
            }
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            javax.mail.Message[] messages = folder.getMessages();
            List<Message> messageList = new ArrayList<>();
            int total = messages.length;
            int index = 0;
            for (javax.mail.Message msg: messages){
                long uid = (protocol == Protocol.POP3)? 0 : ((IMAPFolder) folder).getUID(msg);
                Message message = Converter.InternetMessage.toLocalMessage(uid, msg, false);
                messageList.add(message);
                getReceiveCallback.receiving(message, ++index, total);
            }
            getReceiveCallback.onFinish(messageList);
            folder.close(false);
            store.close();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            getReceiveCallback.onFailure(e.toString());
        }
    }

    /**
     * 使用IMAP协议快速接收服务器上的邮件，不解析邮件内容
     * @param getReceiveCallback
     */
    void fastReceive(Email.GetReceiveCallback getReceiveCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            IMAPFolder imapFolder = (IMAPFolder) store.getFolder("INBOX");
            imapFolder.open(Folder.READ_ONLY);
            javax.mail.Message[] messages = imapFolder.getMessages();
            List<Message> messageList = new ArrayList<>();
            int total = messages.length;
            int index = 0;
            for (javax.mail.Message msg: messages){
                Message message = Converter.InternetMessage.toLocalMessage(imapFolder.getUID(msg), msg, true);
                messageList.add(message);
                getReceiveCallback.receiving(message, ++index, total);
            }
            getReceiveCallback.onFinish(messageList);
            imapFolder.close(false);
            store.close();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            getReceiveCallback.onFailure(e.toString());
        }
    }

    /**
     * 获取全部邮件数量
     * @param protocol
     * @param getCountCallback
     */
    void getMessageCount(int protocol, Email.GetCountCallback getCountCallback) {
        try {
            Store store;
            if (protocol == Protocol.POP3) {
                store = session.getStore(POP3);
                store.connect(popHost, account, password);
            } else {
                store = session.getStore(IMAP);
                store.connect(imapHost, account, password);
            }
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            getCountCallback.onSuccess(folder.getMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            getCountCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 获取未读邮件数量
     * @param getCountCallback
     */
    void getUnreadMessageCount(Email.GetCountCallback getCountCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            getCountCallback.onSuccess(folder.getUnreadMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            getCountCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 同步消息，极快。
     * @param originalUidList
     * @param getSyncMessageCallback
     */
    void syncMessage(long[] originalUidList, Email.GetSyncMessageCallback getSyncMessageCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            IMAPFolder imapFolder = (IMAPFolder) store.getFolder("INBOX");
            imapFolder.open(Folder.READ_ONLY);
            javax.mail.Message[] messages = imapFolder.getMessages();

            //对原uid列表排序
            Arrays.sort(originalUidList);

            //需要返回的新消息和需要删除本地邮件的uid
            List<Message> messageList = new ArrayList<>();
            long[] deleteUidList = new long[]{};

            //判断同步类型
            switch (UIDHandle.proofreading(originalUidList, messages, imapFolder)) {
                case UIDHandle.SyncType.SYNC_ALL:
                    for (javax.mail.Message msg: messages){
                        long uid = imapFolder.getUID(msg);
                        Message message = Converter.InternetMessage.toLocalMessage(uid, msg, true);
                        messageList.add(message);
                    }
                    break;
                case UIDHandle.SyncType.DELETE_ALL:
                    for (javax.mail.Message msg : messages) {
                        MimeMessage mimeMessage = (MimeMessage) msg;
                        deleteUidList = UIDHandle.insertUid(deleteUidList, imapFolder.getUID(mimeMessage));
                    }
                    break;
                case UIDHandle.SyncType.JUST_DELETE:
                    for (javax.mail.Message msg : messages) {
                        MimeMessage mimeMessage = (MimeMessage) msg;
                        long uid = imapFolder.getUID(mimeMessage);
                        if (UIDHandle.binarySearch(originalUidList, uid)) {
                            originalUidList = UIDHandle.deleteUid(originalUidList, uid);
                        }
                    }
                    for (long uid : originalUidList) {
                        deleteUidList = UIDHandle.insertUid(deleteUidList, uid);
                    }
                    break;
                case UIDHandle.SyncType.JUST_ADD:
                    int originalLength = originalUidList.length;
                    long originalLastUid = originalUidList[originalLength-1];
                    for (int index = messages.length-1; index >= 0; index--) {
                        javax.mail.Message msg = messages[index];
                        long uid = imapFolder.getUID(msg);
                        if (uid > originalLastUid) {
                            Message message = Converter.InternetMessage.toLocalMessage(uid, msg, true);
                            messageList.add(message);
                        } else {
                            break;
                        }
                    }
                    break;
                case UIDHandle.SyncType.ADD_And_DELETE:
                    originalLength = originalUidList.length;
                    originalLastUid = originalUidList[originalLength-1];
                    for (int index = messages.length-1; index >= 0; index--) {
                        javax.mail.Message msg = messages[index];
                        long uid = imapFolder.getUID(msg);
                        if (uid > originalLastUid) {
                            Message message = Converter.InternetMessage.toLocalMessage(uid, msg, true);
                            messageList.add(message);
                        } else if (UIDHandle.binarySearch(originalUidList, uid)) {
                            originalUidList = UIDHandle.deleteUid(originalUidList, uid);
                        }
                    }
                    for (long uid : originalUidList) {
                        deleteUidList = UIDHandle.insertUid(deleteUidList, uid);
                    }
                    break;
                case UIDHandle.SyncType.NO_SYNC:
                    break;
            }

            //结果回调
            getSyncMessageCallback.onSuccess(messageList, deleteUidList);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            getSyncMessageCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 获取全部UID
     * @param getUIDListCallback
     */
    void getUIDList(Email.GetUIDListCallback getUIDListCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            IMAPFolder imapFolder = (IMAPFolder) store.getFolder("INBOX");
            imapFolder.open(Folder.READ_ONLY);
            javax.mail.Message[] messages = imapFolder.getMessages();
            long[] uidList = new long[messages.length];
            for (int i = 0, len = messages.length; i < len; i++) {
                MimeMessage mimeMessage = (MimeMessage) messages[i];
                uidList[i] = imapFolder.getUID(mimeMessage);

            }
            getUIDListCallback.onSuccess(uidList);
        } catch (MessagingException e) {
            e.printStackTrace();
            getUIDListCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 获取某一封邮件消息
     * @param uid
     * @param getMessageCallback
     */
    void getMessage(long uid, Email.GetMessageCallback getMessageCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            IMAPFolder imapFolder = (IMAPFolder) store.getFolder("INBOX");
            imapFolder.open(Folder.READ_ONLY);
            javax.mail.Message msg = imapFolder.getMessageByUID(uid);
            if (msg != null) {
                Message message = Converter.InternetMessage.toLocalMessage(uid, msg, false);
                getMessageCallback.onSuccess(message);
            } else {
                getMessageCallback.onFailure(Constant.MESSAGE_EXCEPTION);
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            getMessageCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 获取多封邮件消息
     * @param uidList
     * @param getMessageListCallback
     */
    void getMessageList(long[] uidList, Email.GetMessageListCallback getMessageListCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            IMAPFolder imapFolder = (IMAPFolder) store.getFolder("INBOX");
            imapFolder.open(Folder.READ_ONLY);
            javax.mail.Message[] messages = imapFolder.getMessagesByUID(uidList);
            List<Message> messageList = new ArrayList<>();
            for (javax.mail.Message msg : messages) {
                if (msg != null) {
                    Message message = Converter.InternetMessage.toLocalMessage(imapFolder.getUID(msg), msg, false);
                    messageList.add(message);
                }
            }
            getMessageListCallback.onSuccess(messageList);
        }catch (MessagingException | IOException e) {
            e.printStackTrace();
            getMessageListCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 标记邮件消息
     * @param uid
     * @param flag
     * @param getFlagCallback
     */
    void setFlagMessage(long uid, int flag, Email.GetFlagCallback getFlagCallback) {
        try {
            Store store = session.getStore(IMAP);
            store.connect(imapHost, account, password);
            IMAPFolder imapFolder = (IMAPFolder) store.getFolder("INBOX");
            imapFolder.open(Folder.READ_WRITE);
            javax.mail.Message msg = imapFolder.getMessageByUID(uid);
            if (msg != null) {
                msg.setFlag(Flags.Flag.DELETED, true);
                getFlagCallback.onSuccess();
            } else {
               getFlagCallback.onFailure(Constant.MESSAGE_EXCEPTION);
            }
            imapFolder.close();
            store.close();
        }catch (MessagingException e) {
            e.printStackTrace();
            getFlagCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 邮箱帐号及配置的检查
     * @param getConnectCallback
     */
    void connect(Email.GetConnectCallback getConnectCallback) {
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
            getConnectCallback.onSuccess();
        } catch (MessagingException e) {
            e.printStackTrace();
            getConnectCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 邮件协议类型
     */
    interface Protocol {
        int POP3 = 0;
        int IMAP = 1;
    }

}
