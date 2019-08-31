package com.smailnet.eamil;

import android.text.TextUtils;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

class EmailUtils {

    /**
     * 获取属性
     * @return
     */
    static Properties getProperties() {
        //获取全局配置对象
        GlobalConfig config = Manager.getGlobalConfig();
        String smtpHost = config.getSmtpHost();
        String popHost = config.getPopHost();
        String imapHost = config.getImapHost();
        String smtpPort = String.valueOf(config.getSmtpPort());
        String popPort = String.valueOf(config.getPopPort());
        String imapPort = String.valueOf(config.getImapPort());

        //配置
        Properties properties = new Properties();
        if (!TextUtils.isEmpty(smtpHost) && !TextUtils.isEmpty(smtpPort)) {
            properties.put(Constant.MAIL_SMTP_HOST, smtpHost);
            properties.put(Constant.MAIL_SMTP_AUTH, true);
            if (config.isSmtpSSL()) {
                properties.put(Constant.MAIL_SMTP_SOCKET_FACTORY_CLASS, Constant.SSL_SOCKET_FACTORY);
                properties.put(Constant.MAIL_SMTP_SOCKET_FACTORY_FALLBACK, false);
                properties.put(Constant.MAIL_SMTP_SOCKET_FACTORY_PORT, smtpPort);
            } else {
                properties.put(Constant.MAIL_SMTP_STARTTLS_ENABLE, true);
                properties.put(Constant.MAIL_SMTP_POST, smtpPort);
            }
        }
        if (!TextUtils.isEmpty(popHost) && !TextUtils.isEmpty(popPort)) {
            properties.put(Constant.MAIL_POP3_HOST, popHost);
            properties.put(Constant.MAIL_POP3_AUTH, true);
            if (config.isPopSSL()) {
                properties.put(Constant.MAIL_POP3_SOCKET_FACTORY_CLASS, Constant.SSL_SOCKET_FACTORY);
                properties.put(Constant.MAIL_POP3_SOCKET_FACTORY_FALLBACK, false);
                properties.put(Constant.MAIL_POP3_SOCKET_FACTORY_PORT, popPort);
            } else {
                properties.put(Constant.MAIL_POP3_POST, popPort);
            }
        }
        if (!TextUtils.isEmpty(imapHost) && !TextUtils.isEmpty(imapPort)) {
            properties.put(Constant.MAIL_IMAP_HOST, imapHost);
            properties.put(Constant.MAIL_IMAP_AUTH, true);
            if (config.isImapSSL()) {
                properties.put(Constant.MAIL_IMAP_SOCKET_FACTORY_CLASS, Constant.SSL_SOCKET_FACTORY);
                properties.put(Constant.MAIL_IMAP_SOCKET_FACTORY_FALLBACK, false);
                properties.put(Constant.MAIL_IMAP_SOCKET_FACTORY_PORT, imapPort);
            } else {
                properties.put(Constant.MAIL_IMAP_POST, imapPort);
            }
        }

        //返回值
        return properties;
    }

    /**
     * 获取属性
     * @param config
     * @return
     */
    static Properties getProperties(Email.Config config) {
        //获取配置对象
        String smtpHost = config.getSmtpHost();
        String popHost = config.getPopHost();
        String imapHost = config.getImapHost();
        String smtpPort = String.valueOf(config.getSmtpPort());
        String popPort = String.valueOf(config.getPopPort());
        String imapPort = String.valueOf(config.getImapPort());

        //配置
        Properties properties = new Properties();
        if (!TextUtils.isEmpty(smtpHost) && !TextUtils.isEmpty(smtpPort)) {
            properties.put(Constant.MAIL_SMTP_HOST, smtpHost);
            properties.put(Constant.MAIL_SMTP_AUTH, true);
            if (config.isSmtpSSL()) {
                properties.put(Constant.MAIL_SMTP_SOCKET_FACTORY_CLASS, Constant.SSL_SOCKET_FACTORY);
                properties.put(Constant.MAIL_SMTP_SOCKET_FACTORY_FALLBACK, false);
                properties.put(Constant.MAIL_SMTP_SOCKET_FACTORY_PORT, smtpPort);
            } else {
                properties.put(Constant.MAIL_SMTP_STARTTLS_ENABLE, true);
                properties.put(Constant.MAIL_SMTP_POST, smtpPort);
            }
        }
        if (!TextUtils.isEmpty(popHost) && !TextUtils.isEmpty(popPort)) {
            properties.put(Constant.MAIL_POP3_HOST, popHost);
            properties.put(Constant.MAIL_POP3_AUTH, true);
            if (config.isPopSSL()) {
                properties.put(Constant.MAIL_POP3_SOCKET_FACTORY_CLASS, Constant.SSL_SOCKET_FACTORY);
                properties.put(Constant.MAIL_POP3_SOCKET_FACTORY_FALLBACK, false);
                properties.put(Constant.MAIL_POP3_SOCKET_FACTORY_PORT, popPort);
            } else {
                properties.put(Constant.MAIL_POP3_POST, popPort);
            }
        }
        if (!TextUtils.isEmpty(imapHost) && !TextUtils.isEmpty(imapPort)) {
            properties.put(Constant.MAIL_IMAP_HOST, imapHost);
            properties.put(Constant.MAIL_IMAP_AUTH, true);
            if (config.isImapSSL()) {
                properties.put(Constant.MAIL_IMAP_SOCKET_FACTORY_CLASS, Constant.SSL_SOCKET_FACTORY);
                properties.put(Constant.MAIL_IMAP_SOCKET_FACTORY_FALLBACK, false);
                properties.put(Constant.MAIL_IMAP_SOCKET_FACTORY_PORT, imapPort);
            } else {
                properties.put(Constant.MAIL_IMAP_POST, imapPort);
            }
        }

        //返回值
        return properties;
    }

    /**
     * 获取Transport
     * @return
     */
    static Transport getTransport() throws MessagingException {
        GlobalConfig config = Manager.getGlobalConfig();
        Session session = Session.getInstance(EmailUtils.getProperties());
        Transport transport = session.getTransport(Constant.SMTP);
        transport.connect(config.getSmtpHost(), config.getAccount(), config.getPassword());
        return transport;
    }

    /**
     * 获取Transport
     * @param config
     * @return
     * @throws MessagingException
     */
    static Transport getTransport(Email.Config config) throws MessagingException {
        Session session = Session.getInstance(EmailUtils.getProperties(config));
        Transport transport = session.getTransport(Constant.SMTP);
        transport.connect(config.getSmtpHost(), config.getAccount(), config.getPassword());
        return transport;
    }

    /**
     * 获取POP3Store
     * @return
     */
    static POP3Store getPOP3Store() throws MessagingException {
        GlobalConfig config = Manager.getGlobalConfig();
        Session session = Session.getInstance(getProperties());
        POP3Store store = (POP3Store) session.getStore(Constant.POP3);
        store.connect(config.getPopHost(), config.getAccount(), config.getPassword());
        return store;
    }

    /**
     * 获取POP3Store
     * @param config
     * @return
     * @throws MessagingException
     */
    static POP3Store getPOP3Store(Email.Config config) throws MessagingException {
        Session session = Session.getInstance(getProperties(config));
        POP3Store store = (POP3Store) session.getStore(Constant.POP3);
        store.connect(config.getPopHost(), config.getAccount(), config.getPassword());
        return store;
    }

    /**
     * 获取IMAPStore
     * @return
     * @throws MessagingException
     */
    static IMAPStore getIMAPStore() throws MessagingException {
        GlobalConfig config = Manager.getGlobalConfig();
        Session session = Session.getInstance(getProperties());
        IMAPStore store = (IMAPStore) session.getStore(Constant.IMAP);
        store.connect(config.getImapHost(), config.getAccount(), config.getPassword());
        return store;
    }

    /**
     * 获取IMAPStore
     * @param config
     * @return
     * @throws MessagingException
     */
    static IMAPStore getIMAPStore(Email.Config config) throws MessagingException {
        Session session = Session.getInstance(getProperties(config));
        IMAPStore store = (IMAPStore) session.getStore(Constant.IMAP);
        store.connect(config.getImapHost(), config.getAccount(), config.getPassword());
        return store;
    }

    /**
     * 获取POP3Folder
     * @param store
     * @return
     * @throws MessagingException
     */
    static POP3Folder getInboxFolder(POP3Store store) throws MessagingException {
        POP3Folder folder = (POP3Folder) store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    /**
     * 获取InboxFolder
     * @param store
     * @return
     * @throws MessagingException
     */
    static IMAPFolder getInboxFolder(IMAPStore store) throws MessagingException {
        GlobalConfig config = Manager.getGlobalConfig();
        IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX");
        if (config.getType() == Email.MailType.$163 || config.getType() == Email.MailType.$126) {
            folder.doCommand(protocol -> {
                protocol.id("FUTONG");
                return null;
            });
        }
        folder.open(Folder.READ_WRITE);
        return folder;
    }

}
