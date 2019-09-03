package com.smailnet.eamil;

import android.text.TextUtils;

import com.smailnet.eamil.entity.Message;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 该框架内部的核心类
 */
class EmailCore {

    //配置对象
    private Email.Config config;
    //JavaMail的消息对象
    private javax.mail.Message message;

    /**
     * 构造方法
     */
    EmailCore() {

    }

    /**
     * 构造方法
     * @param config
     */
    EmailCore(Email.Config config) {
        this.config = config;
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
            String account = (config != null) ? config.getAccount() : Manager.getGlobalConfig().getAccount();
            Properties properties = (config != null) ? EmailUtils.getProperties(config) : EmailUtils.getProperties();
            Session session = Session.getInstance(properties);
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
            Transport transport = (config != null) ? EmailUtils.getTransport(config) : Manager.getTransport();
            transport.sendMessage(message, message.getRecipients(javax.mail.Message.RecipientType.TO));
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
            if (protocol == Protocol.POP3) {
                POP3Store store = (config != null) ? EmailUtils.getPOP3Store(config) : Manager.getPOP3Store();
                POP3Folder folder = Manager.getInboxFolder(store);
                javax.mail.Message[] messages = folder.getMessages();
                List<Message> messageList = new ArrayList<>();
                int total = messages.length, index = 0;
                for (javax.mail.Message msg: messages){
                    Message message = Converter.InternetMessage.toLocalMessage(0, msg, false);
                    messageList.add(message);
                    getReceiveCallback.receiving(message, ++index, total);
                }
                getReceiveCallback.onFinish(messageList);
            } else if (protocol == Protocol.IMAP) {
                IMAPStore store = (config != null) ? EmailUtils.getIMAPStore(config) : Manager.getImapStore();
                IMAPFolder folder = (config != null) ? EmailUtils.getInboxFolder(store, config) : Manager.getInboxFolder(store);
                javax.mail.Message[] messages = folder.getMessages();
                List<Message> messageList = new ArrayList<>();
                int total = messages.length, index = 0;
                for (javax.mail.Message msg: messages){
                    Message message = Converter.InternetMessage.toLocalMessage(folder.getUID(msg), msg, false);
                    messageList.add(message);
                    getReceiveCallback.receiving(message, ++index, total);
                }
                getReceiveCallback.onFinish(messageList);
            }
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
            IMAPStore store = (config != null) ? EmailUtils.getIMAPStore(config) : Manager.getImapStore();
            IMAPFolder folder = (config != null) ? EmailUtils.getInboxFolder(store, config) : Manager.getInboxFolder(store);
            javax.mail.Message[] messages = folder.getMessages();
            List<Message> messageList = new ArrayList<>();
            int total = messages.length, index = 0;
            for (javax.mail.Message msg: messages) {
                Message message = Converter.InternetMessage.toLocalMessage(folder.getUID(msg), msg, true);
                messageList.add(message);
                getReceiveCallback.receiving(message, ++index, total);
            }
            getReceiveCallback.onFinish(messageList);
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
            if (protocol == Protocol.POP3) {
                POP3Store store = (config != null) ? EmailUtils.getPOP3Store(config) : Manager.getPOP3Store();
                POP3Folder folder = Manager.getInboxFolder(store);
                getCountCallback.onSuccess(folder.getMessageCount());
            } else if (protocol == Protocol.IMAP) {
                IMAPStore store = (config != null) ? EmailUtils.getIMAPStore(config) : Manager.getImapStore();
                IMAPFolder folder = (config != null) ? EmailUtils.getInboxFolder(store, config) : Manager.getInboxFolder(store);
                getCountCallback.onSuccess(folder.getMessageCount());
            }
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
            IMAPStore store = (config != null) ? EmailUtils.getIMAPStore(config) : Manager.getImapStore();
            IMAPFolder folder = (config != null) ? EmailUtils.getInboxFolder(store, config) : Manager.getInboxFolder(store);
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
            IMAPStore store = (config != null) ? EmailUtils.getIMAPStore(config) : Manager.getImapStore();
            IMAPFolder folder = (config != null) ? EmailUtils.getInboxFolder(store, config) : Manager.getInboxFolder(store);
            javax.mail.Message[] messages = folder.getMessages();

            //对原uid列表排序
            Arrays.sort(originalUidList);

            //需要返回的新消息和需要删除本地邮件的uid
            List<Message> messageList = new ArrayList<>();
            long[] deleteUidList = new long[]{};

            //判断同步类型
            switch (UIDHandle.proofreading(originalUidList, messages, folder)) {
                case UIDHandle.SyncType.SYNC_ALL:
                    for (javax.mail.Message msg: messages){
                        long uid = folder.getUID(msg);
                        Message message = Converter.InternetMessage.toLocalMessage(uid, msg, true);
                        messageList.add(message);
                    }
                    break;
                case UIDHandle.SyncType.DELETE_ALL:
                    for (javax.mail.Message msg : messages) {
                        MimeMessage mimeMessage = (MimeMessage) msg;
                        deleteUidList = UIDHandle.insertUid(deleteUidList, folder.getUID(mimeMessage));
                    }
                    break;
                case UIDHandle.SyncType.JUST_DELETE:
                    for (javax.mail.Message msg : messages) {
                        MimeMessage mimeMessage = (MimeMessage) msg;
                        long uid = folder.getUID(mimeMessage);
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
                        long uid = folder.getUID(msg);
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
                        long uid = folder.getUID(msg);
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
            IMAPStore store = (config != null) ? EmailUtils.getIMAPStore(config) : Manager.getImapStore();
            IMAPFolder folder = (config != null) ? EmailUtils.getInboxFolder(store, config) : Manager.getInboxFolder(store);
            javax.mail.Message[] messages = folder.getMessages();
            long[] uidList = new long[messages.length];
            for (int i = 0, len = messages.length; i < len; i++) {
                MimeMessage mimeMessage = (MimeMessage) messages[i];
                uidList[i] = folder.getUID(mimeMessage);
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
            IMAPStore store = (config != null) ? EmailUtils.getIMAPStore(config) : Manager.getImapStore();
            IMAPFolder folder = (config != null) ? EmailUtils.getInboxFolder(store, config) : Manager.getInboxFolder(store);
            javax.mail.Message msg = folder.getMessageByUID(uid);
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
            IMAPStore store = (config != null) ? EmailUtils.getIMAPStore(config) : Manager.getImapStore();
            IMAPFolder folder = (config != null) ? EmailUtils.getInboxFolder(store, config) : Manager.getInboxFolder(store);
            javax.mail.Message[] messages = folder.getMessagesByUID(uidList);
            List<Message> messageList = new ArrayList<>();
            for (javax.mail.Message msg : messages) {
                if (msg != null) {
                    Message message = Converter.InternetMessage.toLocalMessage(folder.getUID(msg), msg, false);
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
     * 标记邮件消息（待定，暂无时间开发）
     * @param uid
     * @param flag
     * @param getFlagCallback
     */
    void setFlagMessage(long uid, int flag, Email.GetFlagCallback getFlagCallback) {
        try {
            IMAPStore store = Manager.getImapStore();
            IMAPFolder folder = Manager.getInboxFolder(store);
            javax.mail.Message msg = folder.getMessageByUID(uid);
            if (msg != null) {
                msg.setFlag(Flags.Flag.DELETED, true);
                getFlagCallback.onSuccess();
            } else {
               getFlagCallback.onFailure(Constant.MESSAGE_EXCEPTION);
            }
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
            if (config != null) {
                if (!TextUtils.isEmpty(config.getSmtpHost()) && !TextUtils.isEmpty(String.valueOf(config.getSmtpPort()))) {
                    EmailUtils.getTransport(config);
                }
                if (!TextUtils.isEmpty(config.getPopHost()) && !TextUtils.isEmpty(String.valueOf(config.getPopPort()))) {
                    EmailUtils.getPOP3Store(config);
                }
                if (!TextUtils.isEmpty(config.getImapHost()) && !TextUtils.isEmpty(String.valueOf(config.getImapPort()))) {
                    EmailUtils.getIMAPStore(config);
                }
            } else {
                GlobalConfig config = Manager.getGlobalConfig();
                if (!TextUtils.isEmpty(config.getSmtpHost()) && !TextUtils.isEmpty(String.valueOf(config.getSmtpPort()))) {
                    EmailUtils.getTransport();
                }
                if (!TextUtils.isEmpty(config.getPopHost()) && !TextUtils.isEmpty(String.valueOf(config.getPopPort()))) {
                    EmailUtils.getPOP3Store();
                }
                if (!TextUtils.isEmpty(config.getImapHost()) && !TextUtils.isEmpty(String.valueOf(config.getImapPort()))) {
                    EmailUtils.getIMAPStore();
                }
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
