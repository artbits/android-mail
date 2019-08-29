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
    static GlobalConfig globalConfig = null;

    /**
     * 设置Context
     * @param context
     */
    static void initContext(Context context) {
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
     * 设置全局变量
     */
    static GlobalConfig initGlobalConfig() {
        Manager.globalConfig = new GlobalConfig();
        return Manager.globalConfig;
    }

    /**
     * 获取全局变量
     * @return
     */
    static GlobalConfig getGlobalConfig() {
        if (globalConfig == null) {
            throw new RuntimeException(Constant.GLOBAL_CONFIG_EXCEPTION);
        }
        return globalConfig;
    }

    /**
     * 获取Transport
     * @return
     */
    static Transport getTransport() throws MessagingException {
        if (Manager.transport == null) {
            Manager.transport = EmailUtils.getTransport();
        }
        return transport;
    }

    /**
     * 获取IMAPStore
     * @return
     */
    static IMAPStore getImapStore() throws MessagingException {
        if (Manager.imapStore == null) {
            Manager.imapStore = EmailUtils.getIMAPStore();
        }
        return imapStore;
    }

    /**
     * 获取POP3Store
     * @return
     */
    static POP3Store getPOP3Store() throws MessagingException {
        if (Manager.pop3Store == null) {
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
        if (Manager.imapInboxFolder == null) {
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
        if (Manager.pop3InboxFolder == null) {
            Manager.pop3InboxFolder = EmailUtils.getInboxFolder(store);
        }
        return pop3InboxFolder;
    }
}
