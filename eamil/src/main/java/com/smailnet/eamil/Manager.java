package com.smailnet.eamil;

import android.annotation.SuppressLint;
import android.content.Context;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

import javax.mail.MessagingException;
import javax.mail.Transport;

class Manager {

    //Application的Context对象
    @SuppressLint("StaticFieldLeak")
    private static Context context = null;
    //Transport对象
    private static Transport transport = null;
    //POP3Store对象
    private static POP3Store pop3Store = null;
    //IMAPStore对象
    private static IMAPStore imapStore = null;
    //IMAPFolder对象
    private static IMAPFolder imapInboxFolder = null;
    //POP3Folder对象
    private static POP3Folder pop3InboxFolder = null;
    //全局配置对象
    private static GlobalConfig globalConfig = null;

    /**
     * 设置Context
     * @param context
     */
    static void setContext(Context context) {
        Manager.context = context;
    }

    /**
     * 获取Context
     * @return 返回一个Context对象
     */
    static Context getContext(){
        return context;
    }

    /**
     * 获取全局变量
     * @return
     */
    static GlobalConfig getGlobalConfig() {
        if (globalConfig == null) {
            globalConfig = new GlobalConfig();
        }
        return globalConfig;
    }

    /**
     * 获取Transport
     * @return
     */
    static Transport getTransport() throws MessagingException {
        if (Manager.transport == null || !Manager.transport.isConnected()) {
            Manager.transport = EmailUtils.getTransport();
        }
        return transport;
    }

    /**
     * 获取IMAPStore
     * @return
     */
    static IMAPStore getImapStore() throws MessagingException {
        if (Manager.imapStore == null || !Manager.imapStore.isConnected()) {
            Manager.imapStore = EmailUtils.getIMAPStore();
        }
        return imapStore;
    }

    /**
     * 获取POP3Store
     * @return
     */
    static POP3Store getPOP3Store() throws MessagingException {
        if (Manager.pop3Store == null || !Manager.pop3Store.isConnected()) {
            Manager.pop3Store = EmailUtils.getPOP3Store();
        }
        return pop3Store;
    }

    /**
     * 获取IMAPFolder
     * @param store
     * @return
     * @throws MessagingException
     */
    static IMAPFolder getInboxFolder(IMAPStore store) throws MessagingException {
        if (Manager.imapInboxFolder == null || !Manager.imapInboxFolder.isOpen()) {
            Manager.imapInboxFolder = EmailUtils.getInboxFolder(store);
        }
        return imapInboxFolder;
    }

    /**
     * 获取POP3Folder
     * @param store
     * @return
     * @throws MessagingException
     */
    static POP3Folder getInboxFolder(POP3Store store) throws MessagingException {
        if (Manager.pop3InboxFolder == null || !Manager.pop3InboxFolder.isOpen()) {
            Manager.pop3InboxFolder = EmailUtils.getInboxFolder(store);
        }
        return pop3InboxFolder;
    }

    /**
     * 销毁对象
     * @throws MessagingException
     */
    static void destroy() throws MessagingException {
        if (transport != null && transport.isConnected()) {
            transport.close();
        }
        if (imapStore != null && imapStore.isConnected()) {
            imapStore.close();
        }
        if (pop3Store != null && pop3Store.isConnected()) {
            pop3Store.close();
        }
        if (imapInboxFolder != null && imapInboxFolder.isOpen()) {
            imapInboxFolder.close();
        }
        if (pop3InboxFolder != null && pop3InboxFolder.isOpen()) {
            pop3InboxFolder.close();
        }
    }
}
