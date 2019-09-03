package com.smailnet.eamil;

interface Constant {

    String SMTP = "smtp";
    String POP3 = "pop3";
    String IMAP = "imap";
    String MAIL_SMTP_HOST = "mail.smtp.host";
    String MAIL_POP3_HOST = "mail.pop3.host";
    String MAIL_IMAP_HOST = "mail.imap.host";
    String MAIL_SMTP_POST = "mail.smtp.post";
    String MAIL_POP3_POST = "mail.pop3.post";
    String MAIL_IMAP_POST = "mail.imap.post";
    String MAIL_SMTP_AUTH = "mail.smtp.auth";
    String MAIL_POP3_AUTH = "mail.pop3.auth";
    String MAIL_IMAP_AUTH = "mail.imap.auth";
    String MAIL_SMTP_SOCKET_FACTORY_CLASS = "mail.smtp.socketFactory.class";
    String MAIL_POP3_SOCKET_FACTORY_CLASS = "mail.pop3.socketFactory.class";
    String MAIL_IMAP_SOCKET_FACTORY_CLASS = "mail.imap.socketFactory.class";
    String MAIL_SMTP_SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";
    String MAIL_POP3_SOCKET_FACTORY_FALLBACK = "mail.pop3.socketFactory.fallback";
    String MAIL_IMAP_SOCKET_FACTORY_FALLBACK = "mail.imap.socketFactory.fallback";
    String MAIL_SMTP_SOCKET_FACTORY_PORT = "mail.smtp.socketFactory.port";
    String MAIL_POP3_SOCKET_FACTORY_PORT = "mail.pop3.socketFactory.port";
    String MAIL_IMAP_SOCKET_FACTORY_PORT = "mail.imap.socketFactory.port";
    String SSL_SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
    String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    String MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
    String MAIL_POP_SSL_ENABLE = "mail.pop.ssl.enable";
    String MAIL_IMAP_SSL_ENABLE = "mail.imap.ssl.enable";

    String SMTP_HOST = "smtp_host";
    String POP3_HOST = "pop3_host";
    String SMTP_PORT = "smtp_port";
    String POP3_PORT = "pop3_port";
    String IMAP_HOST = "imap_host";
    String IMAP_PORT = "imap_port";
    String SMTP_SSL = "smtp_ssl";
    String POP3_SSL = "pop3_ssl";
    String IMAP_SSL = "imap_ssl";

    String GLOBAL_CONFIG_EXCEPTION = "The Email framework has not yet been configured globally.";
    String MESSAGE_EXCEPTION = "Message does not exist";
}
