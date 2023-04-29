package com.github.artbits.androidmail.store;

import com.github.artbits.mailkit.MailKit;
import com.github.artbits.quickio.core.IOEntity;

import java.util.function.Consumer;

public final class UserInfo extends IOEntity {
    public String account;
    public String password;
    public String nickname;
    public String SMTPHost;
    public String IMAPHost;
    public Integer SMTPPort;
    public Integer IMAPPort;
    public Boolean SMTPSSLEnable;
    public Boolean IMAPSSLEnable;

    public static UserInfo of(Consumer<UserInfo> consumer) {
        UserInfo userInfo1 = new UserInfo();
        consumer.accept(userInfo1);
        return userInfo1;
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
