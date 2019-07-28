package com.smailnet.eamil;

import android.os.Handler;
import android.os.Looper;

import java.io.UnsupportedEncodingException;

import javax.mail.Address;
import javax.mail.internet.MimeUtility;

public final class SendService {

    private String nickname;
    private String subject;
    private String text;
    private Object content;
    private Address[] to;
    private Address[] cc;
    private Address[] bcc;

    private Handler handler;
    private EmailCore core;

    SendService() {
        handler = new Handler(Looper.getMainLooper());
        core = EmailCore.getAutoConfig();
    }

    SendService(Email.Config config) {
        handler = new Handler(Looper.getMainLooper());
        core = EmailCore.setConfig(config);
    }

    /**
     * 设置收件人的邮箱地址
     * @param to
     * @return
     */
    public SendService setTo(String to) {
        this.to = Converter.MailAddress.toArrays(to);
        return this;
    }

    /**
     * 设置抄送人的邮箱地址
     * @param cc
     * @return
     */
    public SendService setCc(String cc) {
        this.cc = Converter.MailAddress.toArrays(cc);
        return this;
    }

    /**
     * 设置密送人的邮箱地址
     * @param bcc
     * @return
     */
    public SendService setBcc(String bcc) {
        this.bcc = Converter.MailAddress.toArrays(bcc);
        return this;
    }

    /**
     * 设置发件人的昵称
     * @param nickname
     * @return
     */
    public SendService setNickname(String nickname) {
        try {
            this.nickname = MimeUtility.encodeText(nickname);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置邮件标题
     * @param subject
     * @return
     */
    public SendService setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * 设置邮件内容（纯文本）
     * @param text
     * @return
     */
    public SendService setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * 设置邮件内容（HTML）
     * @param content
     * @return
     */
    public SendService setContent(Object content) {
        this.content = content;
        return this;
    }

    /**
     * 发送邮件
     * @param getSendCallback
     */
    public void send(Email.GetSendCallback getSendCallback) {
        new Thread(() -> {
            core.setMessage(nickname, to, cc, bcc, subject, text, content);
            core.send(new Email.GetSendCallback() {
                @Override
                public void onSuccess() {
                    handler.post(getSendCallback::onSuccess);
                }

                @Override
                public void onFailure(String msg) {
                    handler.post(() -> getSendCallback.onFailure(msg));
                }
            });
        }).start();
    }

}
