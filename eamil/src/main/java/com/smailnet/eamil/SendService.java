package com.smailnet.eamil;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.HashMap;

import javax.mail.internet.MimeMessage;

public final class SendService {

    private Handler handler;
    private EmailCore core;
    private Email.Config config;
    private HashMap<String, Object> messageMap;

    SendService() {
        handler = new Handler(Looper.getMainLooper());
        core = new EmailCore();
        messageMap = new HashMap<>();
    }

    SendService(Email.Config config) {
        this.config = config;
        handler = new Handler(Looper.getMainLooper());
        core = new EmailCore(config);
        messageMap = new HashMap<>();
    }

    /**
     * 设置收件人的邮箱地址
     * @param to
     * @return
     */
    public SendService setTo(String to) {
        messageMap.put("to", Converter.AddressConversion.toArrays(to));
        return this;
    }

    /**
     * 设置抄送人的邮箱地址
     * @param cc
     * @return
     */
    public SendService setCc(String cc) {
        messageMap.put("cc", Converter.AddressConversion.toArrays(cc));
        return this;
    }

    /**
     * 设置密送人的邮箱地址
     * @param bcc
     * @return
     */
    public SendService setBcc(String bcc) {
        messageMap.put("bcc", Converter.AddressConversion.toArrays(bcc));
        return this;
    }

    /**
     * 设置发件人的昵称
     * @param nickname
     * @return
     */
    public SendService setNickname(String nickname) {
        messageMap.put("nickname", Converter.CodingConversion.encodeText(nickname));
        return this;
    }

    /**
     * 设置邮件标题
     * @param subject
     * @return
     */
    public SendService setSubject(String subject) {
        messageMap.put("subject", subject);
        return this;
    }

    /**
     * 设置邮件内容（纯文本）
     * @param text
     * @return
     */
    public SendService setText(String text) {
        messageMap.put("text", text);
        return this;
    }

    /**
     * 设置邮件内容（HTML）
     * @param html
     * @return
     */
    public SendService setHTML(String html) {
        messageMap.put("html", html);
        return this;
    }

    /**
     * 设置邮件内容（HTML），该方法已废弃，已被setHTML()方法替换
     * @param content
     * @return
     */
    @Deprecated
    public SendService setContent(Object content) {
        messageMap.put("html", content);
        return this;
    }

    /**
     * 设置附件
     * @param attachment
     * @return
     */
    public SendService setAttachment(File attachment) {
        messageMap.put("attachment", attachment);
        return this;
    }

    /**
     * 发送邮件
     * @param getSendCallback
     */
    public void send(Email.GetSendCallback getSendCallback) {
        new Thread(() -> {
            MimeMessage message = Converter.MessageConversion.toInternetMessage(config, messageMap);
             core.send(message, new Email.GetSendCallback() {
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
