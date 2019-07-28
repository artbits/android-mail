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

    public GlobalConfig setMailType(int mailType) {
        HashMap<String, Object> hashMap = Converter.MailType.getParam(mailType);
        if (hashMap != null) {
            this.smtpHost = String.valueOf(hashMap.get(Constant.SMTP_HOST));
            this.smtpPort = (int) hashMap.get(Constant.SMTP_PORT);
            this.popHost = String.valueOf(hashMap.get(Constant.POP3_HOST));
            this.popPort = (int) hashMap.get(Constant.POP3_PORT);
            this.imapHost = String.valueOf(hashMap.get(Constant.IMAP_HOST));
            this.imapPort = (int) hashMap.get(Constant.IMAP_PORT);
        }
        return this;
    }

    public GlobalConfig setAccount(String account) {
        this.account = account;
        return this;
    }

    public GlobalConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public GlobalConfig setSMTP(String host, int port) {
        this.smtpHost = host;
        this.smtpPort = port;
        return this;
    }

    public GlobalConfig setPOP3(String host, int port) {
        this.popHost = host;
        this.popPort = port;
        return this;
    }

    public GlobalConfig setIMAP(String host, int port) {
        this.imapHost = host;
        this.imapPort = port;
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
}
