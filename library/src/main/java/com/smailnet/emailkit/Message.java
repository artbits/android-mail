package com.smailnet.emailkit;

import java.io.File;
import java.util.List;

/**
 * Message类中可以获取每封邮件消息的相关数据
 */
public final class Message {

    private long uid;
    private boolean attachment;
    private String subject;
    private SentDate sentDate;
    private Sender sender;
    private Recipients recipients;
    private Flags flags;
    private Content content;

    Message(){ }

    Message setUID(long uid) {
        this.uid = uid;
        return this;
    }

    Message setAttachment(boolean attachment) {
        this.attachment = attachment;
        return this;
    }

    Message setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    Message setSentDate(SentDate sentDate) {
        this.sentDate = sentDate;
        return this;
    }

    Message setSender(Sender sender) {
        this.sender = sender;
        return this;
    }

    Message setRecipients(Recipients recipients) {
        this.recipients = recipients;
        return this;
    }

    Message setFlags(Flags flags) {
        this.flags = flags;
        return this;
    }

    Message setContent(Content content) {
        this.content = content;
        return this;
    }

    /**
     * 该封邮件的uid
     * @return  一个long型的数值
     */
    public long getUID() {
        return uid;
    }

    /**
     * 是否存在附件(还没开放)
     * @return
     */
    private boolean existAttachment() {
        return attachment;
    }

    /**
     * 该封邮件的主题
     * @return  String类型的主题
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 获取SentDate对象
     * @return  SentDate对象
     */
    public SentDate getSentDate() {
        return sentDate;
    }

    /**
     * 获取Sender对象
     * @return  Sender对象
     */
    public Sender getSender() {
        return sender;
    }

    /**
     * 获取Recipients对象
     * @return  Recipients对象
     */
    public Recipients getRecipients() {
        return recipients;
    }

    /**
     * 获取Flags对象
     * @return  Flags对象
     */
    public Flags getFlags() {
        return flags;
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

        SentDate() { }

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

        User() { }

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

        Sender() { }

    }

    /**
     * 接收人类
     */
    public static class Recipients {

        private List<To> toList;
        private List<Cc> ccList;

        Recipients() { }

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
     * 标记类
     */
    public static class Flags {

        private boolean read;
        private boolean star;

        Flags() { }

        Flags setRead(boolean read) {
            this.read = read;
            return this;
        }

        Flags setStar(boolean star) {
            this.star = star;
            return this;
        }

        public boolean isRead() {
            return read;
        }

        public boolean isStar() {
            return star;
        }

    }

    /**
     * 邮件内容类
     */
    public static class Content {

        private GetMainBodyCallback getMainBodyCallback;
        private GetAttachmentsCallback getAttachmentsCallback;

        Content() { }

        Content setMainBody(GetMainBodyCallback getMainBodyCallback) {
            this.getMainBodyCallback = getMainBodyCallback;
            return this;
        }

        Content setAttachments(GetAttachmentsCallback getAttachmentsCallback) {
            this.getAttachmentsCallback = getAttachmentsCallback;
            return this;
        }

        /**
         * 懒加载获取邮件正文
         * @return  邮件正文
         */
        public MainBody getMainBody() {
            return getMainBodyCallback.get();
        }

        /**
         * 懒加载获取附件
         * @return  附件
         */
        public List<Attachment> getAttachmentList() {
            return getAttachmentsCallback.get();
        }

        /**
         * 正文类
         */
        public static class MainBody {

            private String type;
            private String text;

            MainBody setType(String type) {
                this.type = type;
                return this;
            }

            MainBody setText(String text) {
                this.text = text;
                return this;
            }

            /**
             * 获取正文的类型
             * @return
             */
            public String getType() {
                return type;
            }

            /**
             * 获取正文内容
             * @return
             */
            public String getText() {
                return text;
            }

        }

        /**
         * 附件类
         */
        public static class Attachment {

            private int size;
            private String type;
            private String filename;
            private File file;
            private LazyLoading lazyLoading;

            Attachment setSize(int size) {
                this.size = size;
                return this;
            }

            Attachment setType(String type) {
                this.type = type;
                return this;
            }

            Attachment setFilename(String filename) {
                this.filename = filename;
                return this;
            }

            Attachment setFile(File file) {
                this.file = file;
                return this;
            }

            Attachment setLazyLoading(LazyLoading lazyLoading) {
                this.lazyLoading = lazyLoading;
                return this;
            }

            /**
             * 获取附件大小
             * @return
             */
            public int getSize() {
                return size;
            }

            /**
             * 附件类型
             * @return
             */
            public String getType() {
                return type;
            }

            /**
             * 获取附件的文件名
             * @return
             */
            public String getFilename() {
                return filename;
            }

            /**
             * 获取附件
             * @return
             */
            public File getFile() {
                return file;
            }

            /**
             * 下载附件
             * @param downloadCallback
             */
            public void download(EmailKit.GetDownloadCallback downloadCallback) {
                lazyLoading.loading(downloadCallback);
            }

            interface LazyLoading {
                void loading(EmailKit.GetDownloadCallback downloadCallback);
            }

        }

        interface GetMainBodyCallback {
            MainBody get();
        }

        interface GetAttachmentsCallback {
            List<Attachment> get();
        }

    }

}
