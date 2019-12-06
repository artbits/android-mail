package com.smailnet.emailkit;

import android.text.TextUtils;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.util.ArrayList;
import java.util.List;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FromStringTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SubjectTerm;

/**
 * 该框架内部的核心类
 */
class EmailCore {

    /**
     * 发送邮件
     * @param config
     * @param draft
     * @param getSendCallback
     */
    static void send(EmailKit.Config config, Draft draft, EmailKit.GetSendCallback getSendCallback) {
        try {
            MimeMessage message = Converter.MessageUtils.toInternetMessage(config, draft);
            Transport transport = EmailUtils.getTransport(config);
            assert message != null;
            transport.sendMessage(message, message.getRecipients(javax.mail.Message.RecipientType.TO));
            if (draft.getCc() != null && draft.getCc().length != 0) {
                transport.sendMessage(message, message.getRecipients(javax.mail.Message.RecipientType.CC));
            }
            if (draft.getBcc() != null && draft.getBcc().length != 0) {
                transport.sendMessage(message, message.getRecipients(javax.mail.Message.RecipientType.BCC));
            }
            getSendCallback.onSuccess();
        } catch (MessagingException e) {
            e.printStackTrace();
            getSendCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 使用IMAP协议接收服务器上的邮件
     * @param config
     * @param folderName
     * @param getReceiveCallback
     */
     static void receive(EmailKit.Config config, String folderName, EmailKit.GetReceiveCallback getReceiveCallback) {
        try {
            IMAPStore store =  EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            javax.mail.Message[] messages = folder.getMessages();
            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);
            fetchProfile.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, fetchProfile);
            List<Message> messageList = new ArrayList<>();
            int total = messages.length, index = 0;
            for (javax.mail.Message msg: messages){
                Message message = Converter.MessageUtils.toLocalMessage(folder.getUID(msg), msg);
                messageList.add(message);
                getReceiveCallback.receiving(message, ++index, total);
            }
            getReceiveCallback.onFinish(messageList);
        } catch (MessagingException e) {
            e.printStackTrace();
            getReceiveCallback.onFailure(e.toString());
        }
    }

    /**
     * 加载邮件
     * @param lastUID
     * @param getLoadCallback
     */
    static void load(EmailKit.Config config, String folderName, long lastUID, EmailKit.GetLoadCallback getLoadCallback) {
        try {
            IMAPStore store =  EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            long[] uids = UIDHandler.nextUIDArray(folder, lastUID);
            javax.mail.Message[] messages = folder.getMessagesByUID(uids);
            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);
            fetchProfile.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, fetchProfile);
            List<Message> msgList = new ArrayList<>();
            for (javax.mail.Message msg: messages){
                Message message = Converter.MessageUtils.toLocalMessage(folder.getUID(msg), msg);
                msgList.add(message);
            }
            getLoadCallback.onSuccess(msgList);
        } catch (MessagingException e) {
            e.printStackTrace();
            getLoadCallback.onFailure(e.toString());
        }
    }

    /**
     * 同步uid
     * @param config
     * @param folderName
     * @param localUIDArray
     * @param getSyncCallback
     */
    static void sync(EmailKit.Config config, String folderName, long[] localUIDArray, EmailKit.GetSyncCallback getSyncCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            UIDHandler.Result result = UIDHandler.syncUIDArray(folder, localUIDArray);
            long[] newArray = result.getNewArray();
            long[] delArray = result.getDelArray();
            List<Message> newMsgList = new ArrayList<>();
            if (newArray.length > 0) {
                javax.mail.Message[] messages = folder.getMessagesByUID(newArray);
                FetchProfile fetchProfile = new FetchProfile();
                fetchProfile.add(FetchProfile.Item.ENVELOPE);
                fetchProfile.add(FetchProfile.Item.FLAGS);
                folder.fetch(messages, fetchProfile);
                for (javax.mail.Message msg : messages) {
                    Message message = Converter.MessageUtils.toLocalMessage(folder.getUID(msg), msg);
                    newMsgList.add(message);
                }
            }
            getSyncCallback.onSuccess(newMsgList, delArray);
        } catch (MessagingException e) {
            e.printStackTrace();
            getSyncCallback.onFailure(e.toString());
        }
    }

    /**
     * 获取数量统计
     * @param config
     * @param folderName
     * @param getCountCallback
     */
    static void getMsgCount(EmailKit.Config config, String folderName, EmailKit.GetCountCallback getCountCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            int count = folder.getMessageCount();
            int unread = folder.getUnreadMessageCount();
            getCountCallback.onSuccess(count, unread);
        } catch (MessagingException e) {
            e.printStackTrace();
            getCountCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 获取全部UID
     * @param config
     * @param folderName
     * @param getUIDListCallback
     */
    static void getUIDList(EmailKit.Config config, String folderName, EmailKit.GetUIDListCallback getUIDListCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
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
     * @param config
     * @param folderName
     * @param uid
     * @param getMsgCallback
     */
    static void getMsg(EmailKit.Config config, String folderName, long uid, EmailKit.GetMsgCallback getMsgCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            javax.mail.Message msg = folder.getMessageByUID(uid);
            if (msg != null) {
                Message message = Converter.MessageUtils.toLocalMessage(uid, msg);
                getMsgCallback.onSuccess(message);
            } else {
                getMsgCallback.onFailure("Message does not exist");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            getMsgCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 获取多封邮件消息
     * @param config
     * @param folderName
     * @param uidList
     * @param getMsgListCallback
     */
    static void getMsgList(EmailKit.Config config, String folderName, long[] uidList, EmailKit.GetMsgListCallback getMsgListCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            javax.mail.Message[] messages = folder.getMessagesByUID(uidList);
            List<Message> messageList = new ArrayList<>();
            for (javax.mail.Message msg : messages) {
                if (msg != null) {
                    Message message = Converter.MessageUtils.toLocalMessage(folder.getUID(msg), msg);
                    messageList.add(message);
                }
            }
            getMsgListCallback.onSuccess(messageList);
        }catch (MessagingException e) {
            e.printStackTrace();
            getMsgListCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 获取默认的文件夹列表
     * @param config
     * @param getFolderListCallback
     */
    static void getDefaultFolderList(EmailKit.Config config, EmailKit.GetFolderListCallback getFolderListCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            List<String> folderList = new ArrayList<>();
            for (Folder folder : store.getDefaultFolder().list()) {
                if (folder.list().length == 0) {
                    folderList.add(folder.getFullName());
                }
            }
            getFolderListCallback.onSuccess(folderList);
        } catch (MessagingException e) {
            e.printStackTrace();
            getFolderListCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 获取默认的文件夹列表（无回调）
     * @param config
     * @return
     */
    static List<String> getDefaultFolderList(EmailKit.Config config) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            List<String> folderList = new ArrayList<>();
            for (Folder folder : store.getDefaultFolder().list()) {
                if (folder.list().length == 0) {
                    folderList.add(folder.getFullName());
                }
            }
            return folderList;
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 移动消息
     * @param config
     * @param originalFolderName
     * @param targetFolderName
     * @param uid
     * @param getOperateCallback
     */
    static void moveMsg(EmailKit.Config config, String originalFolderName, String targetFolderName, long uid, EmailKit.GetOperateCallback getOperateCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder originalFolder = EmailUtils.getFolder(originalFolderName, store, config);
            IMAPFolder targetFolder = EmailUtils.getFolder(targetFolderName, store, config);
            javax.mail.Message msg = originalFolder.getMessageByUID(uid);
            if (msg != null) {
                originalFolder.copyMessages(new javax.mail.Message[]{msg}, targetFolder);
                originalFolder.setFlags(new javax.mail.Message[]{msg}, new Flags(Flags.Flag.DELETED), true);
                originalFolder.close(true);
                targetFolder.close(true);
                getOperateCallback.onSuccess();
            } else {
                originalFolder.close();
                targetFolder.close();
                getOperateCallback.onFailure("Message does not exist");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            getOperateCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 批量移动消息
     * @param config
     * @param originalFolderName
     * @param targetFolderName
     * @param uidList
     * @param getOperateCallback
     */
    static void moveMsgList(EmailKit.Config config, String originalFolderName, String targetFolderName, long[] uidList, EmailKit.GetOperateCallback getOperateCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder originalFolder = EmailUtils.getFolder(originalFolderName, store, config);
            IMAPFolder targetFolder = EmailUtils.getFolder(targetFolderName, store, config);
            javax.mail.Message[] msgList = originalFolder.getMessagesByUID(uidList);
            originalFolder.copyMessages(msgList, targetFolder);
            originalFolder.setFlags(msgList, new Flags(Flags.Flag.DELETED), true);
            originalFolder.close(true);
            targetFolder.close(true);
            getOperateCallback.onSuccess();
        } catch (MessagingException e) {
            e.printStackTrace();
            getOperateCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 星标消息
     * @param config
     * @param folderName
     * @param uid
     * @param star
     * @param getOperateCallback
     */
    static void starMsg(EmailKit.Config config, String folderName, long uid, boolean star, EmailKit.GetOperateCallback getOperateCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            javax.mail.Message msg = folder.getMessageByUID(uid);
            if (msg != null) {
                folder.setFlags(new javax.mail.Message[]{msg}, new Flags(Flags.Flag.FLAGGED), star);
                folder.close(true);
                getOperateCallback.onSuccess();
            } else {
                folder.close();
                getOperateCallback.onFailure("Message does not exist");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            getOperateCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 批量星标消息
     * @param config
     * @param folderName
     * @param uidList
     * @param star
     * @param getOperateCallback
     */
    static void starMsgList(EmailKit.Config config, String folderName, long[] uidList, boolean star, EmailKit.GetOperateCallback getOperateCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            javax.mail.Message[] msgList = folder.getMessagesByUID(uidList);
            folder.setFlags(msgList, new Flags(Flags.Flag.FLAGGED), star);
            folder.close(true);
            getOperateCallback.onSuccess();
        } catch (MessagingException e) {
            e.printStackTrace();
            getOperateCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 标记消息已读或未读
     * @param config
     * @param folderName
     * @param uid
     * @param read
     * @param getOperateCallback
     */
    static void readMsg(EmailKit.Config config, String folderName, long uid, boolean read, EmailKit.GetOperateCallback getOperateCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            javax.mail.Message msg = folder.getMessageByUID(uid);
            if (msg != null) {
                folder.setFlags(new javax.mail.Message[]{msg}, new Flags(Flags.Flag.SEEN), read);
                folder.close(true);
                getOperateCallback.onSuccess();
            } else {
                folder.close();
                getOperateCallback.onFailure("Message does not exist");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            getOperateCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 批量标记消息已读或未读
     * @param config
     * @param folderName
     * @param uidList
     * @param read
     * @param getOperateCallback
     */
    static void readMsgList(EmailKit.Config config, String folderName, long[] uidList, boolean read, EmailKit.GetOperateCallback getOperateCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            javax.mail.Message[] msgList = folder.getMessagesByUID(uidList);
            folder.setFlags(msgList, new Flags(Flags.Flag.SEEN), read);
            folder.close(true);
            getOperateCallback.onSuccess();
        } catch (MessagingException e) {
            e.printStackTrace();
            getOperateCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 删除消息
     * @param config
     * @param folderName
     * @param uid
     * @param getOperateCallback
     */
    static void deleteMsg(EmailKit.Config config, String folderName, long uid, EmailKit.GetOperateCallback getOperateCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            javax.mail.Message msg = folder.getMessageByUID(uid);
            if (msg != null) {
                folder.setFlags(new javax.mail.Message[]{msg}, new Flags(Flags.Flag.DELETED), true);
                folder.close(true);
                getOperateCallback.onSuccess();
            } else {
                folder.close();
                getOperateCallback.onFailure("Message does not exist");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            getOperateCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 批量删除邮件
     * @param config
     * @param folderName
     * @param uidList
     * @param getOperateCallback
     */
    static void deleteMsgList(EmailKit.Config config, String folderName, long[] uidList, EmailKit.GetOperateCallback getOperateCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder(folderName, store, config);
            javax.mail.Message[] msgList = folder.getMessagesByUID(uidList);
            folder.setFlags(msgList, new Flags(Flags.Flag.DELETED), true);
            folder.close(true);
            getOperateCallback.onSuccess();
        } catch (MessagingException e) {
            e.printStackTrace();
            getOperateCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 保存消息到草稿箱文件夹
     * @param config
     * @param draft
     * @param getOperateCallback
     */
    static void saveToDraft(EmailKit.Config config, Draft draft, EmailKit.GetOperateCallback getOperateCallback) {
        try {
            MimeMessage message = Converter.MessageUtils.toInternetMessage(config, draft);
            IMAPStore store =  EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder("Drafts", store, config);
            folder.appendMessages(new MimeMessage[]{message});
            folder.close(true);
            getOperateCallback.onSuccess();
        } catch (MessagingException e) {
            e.printStackTrace();
            getOperateCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 邮箱帐号及配置的检查
     * @param config
     * @param getAuthCallback
     */
    static void auth(EmailKit.Config config, EmailKit.GetAuthCallback getAuthCallback) {
        try {
            ObjectManager.setSession(null);
            ObjectManager.setTransport(null);
            ObjectManager.setStore(null);
            if (!TextUtils.isEmpty(config.getSMTPHost()) && !TextUtils.isEmpty(String.valueOf(config.getSMTPPort()))) {
                EmailUtils.getTransport(config);
            }
            if (!TextUtils.isEmpty(config.getIMAPHost()) && !TextUtils.isEmpty(String.valueOf(config.getIMAPPort()))) {
                EmailUtils.getStore(config);
            }
            getAuthCallback.onSuccess();
        } catch (MessagingException e) {
            e.printStackTrace();
            getAuthCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 监听收件箱的新消息，如果邮件服务器支持idle，即可正常使用这个方法
     * @param config
     * @param onMsgListener
     */
    static void monitorNewMsg(EmailKit.Config config, EmailKit.OnMsgListener onMsgListener) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder("INBOX", store, config);
            folder.addMessageCountListener(new MessageCountListener() {
                @Override
                public void messagesAdded(MessageCountEvent e) {
                    try {
                        List<Message> messageList = new ArrayList<>();
                        for (javax.mail.Message msg : e.getMessages()) {
                            Message message = Converter.MessageUtils.toLocalMessage(folder.getUID(msg), msg);
                            messageList.add(message);
                        }
                        onMsgListener.onMsg(messageList);
                    } catch (Exception ex) {
                        onMsgListener.onError(ex.getMessage());
                    }
                }

                @Override
                public void messagesRemoved(MessageCountEvent e) {

                }
            });
            while (true) folder.idle(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            onMsgListener.onError(ex.getMessage());
        }
    }

    /**
     * 通过邮件主题搜索
     * @param config
     * @param subject
     * @param getSearchCallback
     */
    static void searchSubject(EmailKit.Config config, String subject, EmailKit.GetSearchCallback getSearchCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder("INBOX", store, config);
            SubjectTerm subjectTerm = new SubjectTerm(subject);
            javax.mail.Message[] messages = folder.search(subjectTerm);
            List<Message> messageList = new ArrayList<>();
            for (javax.mail.Message msg : messages) {
                Message message = Converter.MessageUtils.toLocalMessage(folder.getUID(msg), msg);
                messageList.add(message);
            }
            getSearchCallback.onSuccess(messageList);
        } catch (MessagingException e) {
            e.printStackTrace();
            getSearchCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 通过发件人昵称搜索
     * @param config
     * @param nickname
     * @param getSearchCallback
     */
    static void searchSender(EmailKit.Config config, String nickname, EmailKit.GetSearchCallback getSearchCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder("INBOX", store, config);
            FromStringTerm fromStringTerm = new FromStringTerm(nickname);
            javax.mail.Message[] messages = folder.search(fromStringTerm);
            List<Message> messageList = new ArrayList<>();
            for (javax.mail.Message msg : messages) {
                Message message = Converter.MessageUtils.toLocalMessage(folder.getUID(msg), msg);
                messageList.add(message);
            }
            getSearchCallback.onSuccess(messageList);
        } catch (MessagingException e) {
            e.printStackTrace();
            getSearchCallback.onFailure(e.getMessage());
        }
    }

    /**
     * 通过收件人昵称搜索
     * @param config
     * @param nickname
     * @param getSearchCallback
     */
    static void searchRecipient(EmailKit.Config config, String nickname, EmailKit.GetSearchCallback getSearchCallback) {
        try {
            IMAPStore store = EmailUtils.getStore(config);
            IMAPFolder folder = EmailUtils.getFolder("INBOX", store, config);
            RecipientStringTerm stringTerm = new RecipientStringTerm(MimeMessage.RecipientType.TO, nickname);
            javax.mail.Message[] messages = folder.search(stringTerm);
            List<Message> messageList = new ArrayList<>();
            for (javax.mail.Message msg : messages) {
                Message message = Converter.MessageUtils.toLocalMessage(folder.getUID(msg), msg);
                messageList.add(message);
            }
            getSearchCallback.onSuccess(messageList);
        } catch (MessagingException e) {
            e.printStackTrace();
            getSearchCallback.onFailure(e.getMessage());
        }
    }

}
