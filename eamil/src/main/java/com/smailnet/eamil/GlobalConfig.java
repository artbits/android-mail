package com.smailnet.eamil;

import java.util.HashMap;

public final class GlobalConfig {

    private String account;
    private String password;
    private String smtpHost;
    private String popHost;
    private String imapHost;
    private int smtpPort;
    private int popPort;
    private int imapPort;
    private int type;
    private boolean smtpSSL;
    private boolean popSSL;
    private boolean imapSSL;

    public GlobalConfig setMailType(int type) {
        this.type = type;
        HashMap<String, Object> hashMap = Converter.MailTypeConversion.getParam(type);
        if (hashMap != null) {
            this.smtpHost = String.valueOf(hashMap.get(Constant.SMTP_HOST));
            this.smtpPort = (int) hashMap.get(Constant.SMTP_PORT);
            this.popHost = String.valueOf(hashMap.get(Constant.POP3_HOST));
            this.popPort = (int) hashMap.get(Constant.POP3_PORT);
            this.imapHost = String.valueOf(hashMap.get(Constant.IMAP_HOST));
            this.imapPort = (int) hashMap.get(Constant.IMAP_PORT);
            this.smtpSSL = (boolean) hashMap.get(Constant.SMTP_SSL);
            this.popSSL = (boolean) hashMap.get(Constant.POP3_SSL);
            this.imapSSL = (boolean) hashMap.get(Constant.IMAP_SSL);
        }
        return this;
    }

    int getType() {
        return type;
    }

    public GlobalConfig setAccount(String account) {
        this.account = account;
        return this;
    }

    public GlobalConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public GlobalConfig setSMTP(String host, int port, boolean ssl) {
        this.smtpHost = host;
        this.smtpPort = port;
        this.smtpSSL = ssl;
        return this;
    }

    public GlobalConfig setPOP3(String host, int port, boolean ssl) {
        this.popHost = host;
        this.popPort = port;
        this.popSSL = ssl;
        return this;
    }

    public GlobalConfig setIMAP(String host, int port, boolean ssl) {
        this.imapHost = host;
        this.imapPort = port;
        this.imapSSL = ssl;
        return this;
    }

    String getAccount() {
        return account;
    }

    String getPassword() {
        return password;
    }

    String getSmtpHost() {
        return smtpHost;
    }

    String getPopHost() {
        return popHost;
    }

    String getImapHost() {
        return imapHost;
    }

    int getSmtpPort() {
        return smtpPort;
    }

    int getPopPort() {
        return popPort;
    }

    int getImapPort() {
        return imapPort;
    }

    boolean isSmtpSSL() {
        return smtpSSL;
    }

    boolean isPopSSL() {
        return popSSL;
    }

    boolean isImapSSL() {
        return imapSSL;
    }
}
