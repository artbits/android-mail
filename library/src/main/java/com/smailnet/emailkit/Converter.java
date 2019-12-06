package com.smailnet.emailkit;

import android.annotation.SuppressLint;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 数据转换的工具类
 */
class Converter {

    /**
     * 常用邮箱类型的转换
     */
    static class MailTypeUtils {

        /**
         * 获取该邮箱对应的服务器配置参数
         * @param type
         * @return
         */
        static EmailKit.Config getMailConfiguration(String type) {
            switch (type) {
                case EmailKit.MailType.QQ:
                case EmailKit.MailType.FOXMAIL:
                    return new EmailKit.Config()
                            .setSMTP("smtp.qq.com", 465, true)
                            .setIMAP("imap.qq.com", 993, true);
                case EmailKit.MailType.EXMAIL:
                    return new EmailKit.Config()
                            .setSMTP("smtp.exmail.qq.com", 465, true)
                            .setIMAP("imap.exmail.qq.com", 993, true);
                case EmailKit.MailType.OUTLOOK:
                    return new EmailKit.Config()
                            .setSMTP("smtp-mail.outlook.com", 25, false)
                            .setIMAP("imap-mail.outlook.com", 993, true);
                case EmailKit.MailType.YEAH:
                    return new EmailKit.Config()
                            .setSMTP("smtp.yeah.net", 465, true)
                            .setIMAP("imap.yeah.net", 993, true);
                case EmailKit.MailType.$163:
                    return new EmailKit.Config()
                            .setSMTP("smtp.163.com", 465, true)
                            .setIMAP("imap.163.com", 993, true);
                case EmailKit.MailType.$126:
                    return new EmailKit.Config()
                            .setSMTP("smtp.126.com", 465, true)
                            .setIMAP("imap.126.com", 993, true);
                default:
                    return null;
            }
        }

        /**
         * 判断是否为网易系的邮箱
         * @param account
         * @return
         */
        static boolean isNetEaseMail(String account) {
            return account.contains(EmailKit.MailType.$163) ||
                   account.contains(EmailKit.MailType.$126) ||
                   account.contains(EmailKit.MailType.YEAH);
        }

    }

    /**
     * 邮件地址转换
     */
    static class AddressUtils {

        /**
         * 字符串类型地址转为数组类型
         * @param addresses
         * @return
         */
        static Address[] toInternetAddresses(String[] addresses) {
            try {
                Address[] internetAddresses = new InternetAddress[addresses.length];
                for (int i = 0, length = addresses.length; i < length; i++)
                    internetAddresses[i] = new InternetAddress(addresses[i]);
                return internetAddresses;
            } catch (AddressException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 获取发件人信息
         * @param addresses
         * @return
         */
        static Message.Sender getSenderInfo(Address[] addresses) {
            if (addresses != null && addresses.length != 0) {
                InternetAddress address = (InternetAddress) addresses[0];
                Message.Sender sender = new Message.Sender();
                sender.setAddress(address.getAddress());
                sender.setNickname(address.getPersonal());
                return sender;
            }
            return null;
        }

        /**
         * 获取收件人信息
         * @param addresses
         * @return
         */
        static List<Message.Recipients.To> getToInfoList(Address[] addresses) {
            if (addresses == null || addresses.length == 0) return null;
            List<Message.Recipients.To> toList = new ArrayList<>();
            for (Address address : addresses) {
                Message.Recipients.To to = new Message.Recipients.To();
                to.setAddress(((InternetAddress) address).getAddress());
                to.setNickname(((InternetAddress) address).getPersonal());
                toList.add(to);
            }
            return toList;
        }

        /**
         * 获取抄送人信息
         * @param addresses
         * @return
         */
        static List<Message.Recipients.Cc> getCcInfoList(Address[] addresses) {
            if (addresses == null || addresses.length == 0) return null;
            List<Message.Recipients.Cc> ccList = new ArrayList<>();
            for (Address address : addresses) {
                Message.Recipients.Cc cc = new Message.Recipients.Cc();
                cc.setAddress(((InternetAddress) address).getAddress());
                cc.setNickname(((InternetAddress) address).getPersonal());
                ccList.add(cc);
            }
            return ccList;
        }

    }

    /**
     * 日期转换
     */
    private static class DateUtils {

        @SuppressLint("SimpleDateFormat")
        static Message.SentDate getDateInfo(Date date) {
            if (date != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日 hh:mm");
                return new Message.SentDate()
                        .setText(format.format(date))
                        .setMillisecond(date.getTime());
            } else {
                return new Message.SentDate()
                        .setText("unknown")
                        .setMillisecond(0);
            }
        }

    }

    /**
     * 文本编码转换
     */
    static class TextUtils {

        static String encodeText(String s) {
            try {
                return MimeUtility.encodeText(s);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return s;
            }
        }

        static String decodeText(String s) {
            try {
                return (s.startsWith("=?")) ? MimeUtility.decodeText(s) : s;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return s;
            }
        }

        static String getMimeType(String filename) {
            int begin = filename.lastIndexOf('.');
            String suffix = filename.substring(begin + 1);
            return MimeTypeMap
                    .getSingleton()
                    .getMimeTypeFromExtension(suffix);
        }

    }

    /**
     * 消息内容转换
     */
    private static class ContentUtils {

        private final static String TEXT_PLAIN = "text/plain";
        private final static String TEXT_HTML = "text/html";
        private final static String MULTIPART = "multipart/*";

        /**
         * 获取text或html文本内容
         * @param part
         * @param map
         * @return
         * @throws MessagingException
         * @throws IOException
         */
        static HashMap<String, StringBuilder> getTexts(Part part, HashMap<String, StringBuilder> map) throws MessagingException, IOException {
            StringBuilder text = new StringBuilder();
            StringBuilder html = new StringBuilder();
            if (part.isMimeType(TEXT_PLAIN)){
                map.put(TEXT_PLAIN, text.append(part.getContent()));
            } else if (part.isMimeType(TEXT_HTML)) {
                map.put(TEXT_HTML, html.append(part.getContent()));
            } else if (part.isMimeType(MULTIPART)) {
                Multipart multipart = (Multipart) part.getContent();
                for (int i = 0, count = multipart.getCount(); i < count; i++) {
                    ContentUtils.getTexts(multipart.getBodyPart(i), map);
                }
            }
            return map;
        }

        /**
         * 获取邮件正文
         * @param message
         * @return
         * @throws IOException
         * @throws MessagingException
         */
        static Message.Content.MainBody getMainBody(javax.mail.Message message) throws IOException, MessagingException {
            HashMap<String, StringBuilder> map = ContentUtils.getTexts(message, new HashMap<>());
            message.setFlag(Flags.Flag.SEEN, true);
            if (map.get(TEXT_HTML) != null) {
                return new Message.Content.MainBody()
                        .setType(TEXT_HTML)
                        .setText(map.get(TEXT_HTML).toString());
            } else if (map.get(TEXT_PLAIN) != null) {
                return new Message.Content.MainBody()
                        .setType(TEXT_PLAIN)
                        .setText(map.get(TEXT_PLAIN).toString());
            } else {
                return new Message.Content.MainBody()
                        .setType(null)
                        .setText(null);
            }
        }

        /**
         * 获取消息中包含附件的部分
         * @param part
         * @return
         */
        static List<BodyPart> getAttachmentPart(Part part, List<BodyPart> bodyPartList) throws MessagingException, IOException {
            if (part.isMimeType(MULTIPART)) {
                Multipart multipart = (Multipart) part.getContent();
                for (int i = 0, count = multipart.getCount(); i < count; i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    String disposition = bodyPart.getDisposition();
                    if (disposition != null && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {
                        bodyPartList.add(bodyPart);
                    }
                }
            }
            return bodyPartList;
        }

        /**
         * 获取全部附件
         * @return
         */
        static List<Message.Content.Attachment> getAttachmentList(Part part) throws IOException, MessagingException {
            List<Message.Content.Attachment> attachmentList = new ArrayList<>();
            List<BodyPart> bodyPartList = getAttachmentPart(part, new ArrayList<>());
            for (BodyPart bodyPart : bodyPartList) {
                String filename = TextUtils.decodeText(bodyPart.getFileName());
                Message.Content.Attachment attachment = new Message.Content.Attachment()
                        .setFilename(filename)
                        .setSize(bodyPart.getSize())
                        .setType(TextUtils.getMimeType(filename))
                        .setFile(new File(ObjectManager.getDirectory() + filename))
                        .setLazyLoading(downloadCallback -> ObjectManager.getMultiThreadService()
                                .execute(() -> {
                                    InputStream is = null;
                                    FileOutputStream fos = null;
                                    try {
                                        File file = new File(ObjectManager.getDirectory() + filename);
                                        if (file.exists()) {
                                            ObjectManager.getHandler().post(() -> downloadCallback.download(file));
                                        } else {
                                            is = bodyPart.getInputStream();
                                            fos = new FileOutputStream(file.getPath());
                                            byte[] bytes = new byte[1024];
                                            for (int length; (length = is.read(bytes)) != -1; )
                                                fos.write(bytes, 0, length);
                                            ObjectManager.getHandler().post(() -> downloadCallback.download(file));
                                        }
                                    } catch (IOException | MessagingException e) {
                                        try {
                                            fos.close();
                                            is.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        e.printStackTrace();
                                        downloadCallback.download(null);
                                    }
                                }));
                attachmentList.add(attachment);

            }
            return attachmentList;
        }

    }

    /**
     * 标记状态转换
     */
    private static class FlagsUtils {

        /**
         * 获取邮件是否已读和是否被星标的状态
         * @param internetFlags
         * @return
         */
        static Message.Flags getFlags(Flags internetFlags) {
            return new Message.Flags()
                    .setRead(internetFlags.contains(Flags.Flag.SEEN))
                    .setStar(internetFlags.contains(Flags.Flag.FLAGGED));
        }

    }

    /**
     * 消息数据转换
     */
    static class MessageUtils {

        /**
         * 网络消息转本地消息
         * @param uid
         * @param message
         * @return
         * @throws MessagingException
         */
        static Message toLocalMessage(long uid, javax.mail.Message message) throws MessagingException {
            //邮件主题
            String subject = message.getSubject();
            //邮件的发送时间
            Message.SentDate sentDate = DateUtils.getDateInfo(message.getSentDate());
            //发件人
            Message.Sender sender = AddressUtils.getSenderInfo(message.getFrom());
            //收件人
            List<Message.Recipients.To> toList = AddressUtils.getToInfoList(message.getRecipients(RecipientType.TO));
            //抄送人
            List<Message.Recipients.Cc> ccList = AddressUtils.getCcInfoList(message.getRecipients(RecipientType.CC));
            //接收人
            Message.Recipients recipients = new Message.Recipients().setToList(toList).setCcList(ccList);
            //邮件标记状态
            Message.Flags flags = FlagsUtils.getFlags(message.getFlags());
            //邮件内容（正文与附件）
            Message.Content content = new Message.Content()
                    .setMainBody(() -> {
                        try {
                            Future<Message.Content.MainBody> future = ObjectManager.getMultiThreadService()
                                    .submit(() -> ContentUtils.getMainBody(message));
                            return future.get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .setAttachments(() -> {
                        try {
                            Future<List<Message.Content.Attachment>> future = ObjectManager.getMultiThreadService()
                                    .submit(() -> ContentUtils.getAttachmentList(message));
                            return future.get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                            return null;
                        }
                    });
            //返回消息
            return new Message()
                    .setUID(uid)
                    .setSubject(subject)
                    .setSentDate(sentDate)
                    .setSender(sender)
                    .setRecipients(recipients)
                    .setFlags(flags)
                    .setContent(content);
        }

        /**
         * 草稿消息转网络消息
         * @param config
         * @param draft
         * @return
         */
        static MimeMessage toInternetMessage(EmailKit.Config config, Draft draft) {
            try {
                Session session = EmailUtils.getSession(config);
                //创建消息对象
                MimeMessage message = new MimeMessage(session);
                //收件人
                message.addRecipients(RecipientType.TO, draft.getTo());
                //判断是否存在抄送人
                if (draft.getCc() != null) {
                    message.addRecipients(RecipientType.CC, draft.getCc());
                }
                //判断是否存在密送人
                if (draft.getBcc() != null) {
                    message.addRecipients(RecipientType.BCC, draft.getBcc());
                }
                //发件人昵称+邮箱地址
                message.setFrom(new InternetAddress(draft.getNickname() + "<" + config.getAccount() + ">"));
                //邮件主题
                message.setSubject(draft.getSubject(), "UTF-8");
                //邮件发送日期
                message.setSentDate(new Date());
                //邮件内容
                if (draft.getText() != null && draft.getHTML() == null && draft.getAttachment() == null) {
                    message.setText(draft.getText(), "UTF-8");
                } else if (draft.getHTML() != null && draft.getAttachment() == null) {
                    message.setContent(draft.getHTML(), "text/html; charset=UTF-8");
                } else if (draft.getAttachment() != null) {
                    //创建多重消息对象
                    Multipart multipart = new MimeMultipart();
                    //文本内容
                    if (draft.getText() != null) {
                        MimeBodyPart textBodyPart = new MimeBodyPart();
                        textBodyPart.setText(draft.getText(), "UTF-8");
                        multipart.addBodyPart(textBodyPart);
                    }
                    //HTML内容
                    if (draft.getHTML() != null) {
                        MimeBodyPart htmlBodyPart = new MimeBodyPart();
                        htmlBodyPart.setContent(draft.getHTML(), "text/html; charset=UTF-8");
                        multipart.addBodyPart(htmlBodyPart);
                    }
                    //设置附件
                    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                    File file = draft.getAttachment();
                    URL url = file.toURI().toURL();
                    DataSource source = new URLDataSource(url);
                    attachmentBodyPart.setFileName(TextUtils.encodeText(file.getName()));
                    attachmentBodyPart.setDataHandler(new DataHandler(source));
                    multipart.addBodyPart(attachmentBodyPart);
                    //设置消息对象
                    message.setContent(multipart);
                }
                //保存到已发送文件夹
                message.setFlag(Flags.Flag.RECENT, true);
                message.saveChanges();
                //返回结果
                return message;
            } catch (MessagingException | MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}
