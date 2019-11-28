package com.smailnet.emailkit;

import java.io.File;
import java.util.Set;

import javax.mail.Address;

/**
 * 在发送邮件前，需要创建一个Draft对象，然后设置好草稿消息的相关的
 * 收件人、主题，正文等内容，同时已创建的草稿消息还能保持到草稿箱
 */
public final class Draft {

    private Address[] to;
    private Address[] cc;
    private Address[] bcc;
    private String nickname;
    private String subject;
    private String text;
    private String html;
    private File attachment;

    Address[] getTo() {
        return to;
    }

    /**
     * 设置一个邮件消息的收件人
     * @param to    单个收件人的邮件地址
     * @return
     */
    public Draft setTo(String to) {
        this.to = Converter.AddressUtils.toInternetAddresses(new String[]{to});
        return this;
    }

    /**
     * 设置多个邮件消息的收件人
     * @param toSet 多个收件人的邮件地址
     * @return
     */
    public Draft setTo(Set<String> toSet) {
        String[] toArray = toSet.toArray(new String[0]);
        this.to = Converter.AddressUtils.toInternetAddresses(toArray);
        return this;
    }

    Address[] getCc() {
        return cc;
    }

    /**
     * 设置一个邮件消息的抄送人
     * @param cc    单个抄送人的邮件地址
     * @return
     */
    public Draft setCc(String cc) {
        this.cc = Converter.AddressUtils.toInternetAddresses(new String[]{cc});
        return this;
    }

    /**
     * 设置多个邮件消息的抄送人
     * @param ccSet 多个抄送人的邮件地址
     * @return
     */
    public Draft setCc(Set<String> ccSet) {
        String[] ccArray = ccSet.toArray(new String[0]);
        this.cc = Converter.AddressUtils.toInternetAddresses(ccArray);
        return this;
    }

    Address[] getBcc() {
        return bcc;
    }

    /**
     * 设置一个邮件消息的密送人
     * @param bcc   单个密送人的邮件地址
     * @return
     */
    public Draft setBcc(String bcc) {
        this.bcc = Converter.AddressUtils.toInternetAddresses(new String[]{bcc});
        return this;
    }

    /**
     * 设置多个邮件消息的密送人
     * @param bccSet    多个密送人的邮件地址
     * @return
     */
    public Draft setBcc(Set<String> bccSet) {
        String[] bccArray = bccSet.toArray(new String[0]);
        this.bcc = Converter.AddressUtils.toInternetAddresses(bccArray);
        return this;
    }

    String getNickname() {
        return nickname;
    }

    /**
     * 设置发件人在发送邮件时所使用的昵称
     * @param nickname  昵称
     * @return
     */
    public Draft setNickname(String nickname) {
        this.nickname = Converter.TextUtils.encodeText(nickname);
        return this;
    }

    String getSubject() {
        return subject;
    }

    /**
     * 设置该封邮件的主题
     * @param subject   主题
     * @return
     */
    public Draft setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    String getText() {
        return text;
    }

    /**
     * 设置邮件的正文
     * @param text  仅支持纯文本
     * @return
     */
    public Draft setText(String text) {
        this.text = text;
        return this;
    }

    Object getHTML() {
        return html;
    }

    /**
     * 设置邮件正文
     * @param html  支持html页面源码
     * @return
     */
    public Draft setHTML(String html) {
        this.html = html;
        return this;
    }

    File getAttachment() {
        return attachment;
    }

    /**
     * 设置附件
     * @param attachment    附件
     * @return
     */
    public Draft setAttachment(File attachment) {
        this.attachment = attachment;
        return this;
    }

}
