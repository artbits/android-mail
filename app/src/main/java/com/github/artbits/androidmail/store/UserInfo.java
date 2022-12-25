package com.github.artbits.androidmail.store;

import com.github.artbits.mailkit.MailKit;

import org.litepal.crud.LitePalSupport;

import java.util.function.Consumer;

public class UserInfo extends LitePalSupport {

    public int id;
    public String account;
    public String password;
    public String nickname;
    public String SMTPHost;
    public String IMAPHost;
    public int SMTPPort;
    public int IMAPPort;
    public boolean SMTPSSLEnable;
    public boolean IMAPSSLEnable;


    public UserInfo() { }


    public UserInfo(Consumer<UserInfo> consumer) {
        consumer.accept(this);
    }


    public MailKit.Config toConfig() {
        return new MailKit.Config(c -> {
           c.account = account;
           c.password = password;
           c.nickname = nickname;
           c.SMTPHost = SMTPHost;
           c.SMTPPort = SMTPPort;
           c.IMAPHost = IMAPHost;
           c.IMAPPort = IMAPPort;
           c.SMTPSSLEnable = SMTPSSLEnable;
           c.IMAPSSLEnable = IMAPSSLEnable;
        });
    }

}
