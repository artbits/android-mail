package com.smailnet.eamil;

import java.io.File;
import java.util.List;

/**
 * Message类中可以获取每封邮件消息的相关数据
 */
public final class Message {

    private long uid;
    private String subject;
    private SentDate sentDate;
    private Sender sender;
    private Recipients recipients;
    private Content content;

    Message(){

    }

    Message setUID(long uid) {
        this.uid = uid;
        return this;
    }

    /**
     * 该封邮件的uid
     * @return  一个long型的数值
     */
    public long getUID() {
        return uid;
    }

    Message setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * 该封邮件的主题
     * @return  String类型的主题
     */
    public String getSubject() {
        return subject;
    }

    Message setSentDate(SentDate sentDate) {
        this.sentDate = sentDate;
        return this;
    }

    /**
     * 获取SentDate对象
     * @return  SentDate对象
     */
    public SentDate getSentDate() {
        return sentDate;
    }

    Message setSender(Sender sender) {
        this.sender = sender;
        return this;
    }

    /**
     * 获取Sender对象
     * @return  Sender对象
     */
    public Sender getSender() {
        return sender;
    }

    Message setRecipients(Recipients recipients) {
        this.recipients = recipients;
        return this;
    }

    /**
     * 获取Recipients对象
     * @return  Recipients对象
     */
    public Recipients getRecipients() {
        return recipients;
    }

    Message setContent(Content content) {
        this.content = content;
        return this;
    }

    /**
     * 获取Content对象
     * @return  Content对象
     */
    public Content getContent() {
        return content;
    }


    /**
     * 发送日期类
     */
    public static  class SentDate {

        private long millisecond;
        private String text;

        SentDate() {

        }

        SentDate setMillisecond(long millisecond) {
            this.millisecond = millisecond;
            return this;
        }

        SentDate setText(String text) {
            this.text = text;
            return this;
        }

        /**
         * 获取发送日期的时间戳
         * @return  时间戳
         */
        public long getMillisecond() {
            return millisecond;
        }

        /**
         * 获取发送时间，以文本的方式返回“年、月、日、时、分、秒”
         * @return  返回文本类型的日期时间
         */
        public String getText() {
            return text;
        }
    }

    /**
     * Sender、To、Cc类的父类
     */
    public static class User {

        private String address;
        private String nickname;

        User() {

        }

        void setAddress(String address) {
            this.address = address;
        }

        void setNickname(String nickname) {
            this.nickname = nickname;
        }

        /**
         * 获取邮件地址
         * @return  邮件地址
         */
        public String getAddress() {
            return address;
        }

        /**
         * 获取昵称
         * @return
         */
        public String getNickname() {
            return nickname;
        }

    }

    /**
     * 发送人类
     */
    public static class Sender extends User {

        Sender() {

        }

    }

    /**
     * 接收人类
     */
    public static class Recipients {

        private List<To> toList;
        private List<Cc> ccList;

        Recipients() {

        }

        Recipients setToList(List<To> toList) {
            this.toList = toList;
            return this;
        }

        Recipients setCcList(List<Cc> ccList) {
            this.ccList = ccList;
            return this;
        }

        public List<To> getToList() {
            return toList;
        }

        public List<Cc> getCcList() {
            return ccList;
        }


        /**
         * 收件人类
         */
        public static class To extends User {

            To() {

            }

        }

        /**
         * 抄送人类
         */
        public static class Cc extends User {

            Cc() {

            }

        }
    }

    /**
     * 邮件内容类
     */
    public static class Content {

        private GetMainBodyCallback getMainBodyCallback;
        private GetAttachmentsCallback getAttachmentsCallback;

        Content() {

        }

        Content setMainBody(GetMainBodyCallback getMainBodyCallback) {
            this.getMainBodyCallback = getMainBodyCallback;
            return this;
        }

        /**
         * 懒加载获取邮件正文
         * @return  邮件正文
         */
        public String getMainBody() {
            return getMainBodyCallback.get();
        }

        Content setAttachments(GetAttachmentsCallback getAttachmentsCallback) {
            this.getAttachmentsCallback = getAttachmentsCallback;
            return this;
        }

        /**
         * 懒加载获取附件
         * @return  附件
         */
        public List<File> getAttachments() {
            return getAttachmentsCallback.get();
        }

        interface GetMainBodyCallback {
            String get();
        }

        interface GetAttachmentsCallback {
            List<File> get();
        }

    }

}
