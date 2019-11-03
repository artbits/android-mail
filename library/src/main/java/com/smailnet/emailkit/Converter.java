package com.smailnet.emailkit;

import android.annotation.SuppressLint;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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
    static class MailTypeConversion {

        //获取该邮箱对应的服务器配置参数
        static HashMap<String, Object> getParam(int type) {
            HashMap<String, Object> hashMap = new HashMap<>();
            switch (type) {
                case EmailKit.MailType.QQ:
                case EmailKit.MailType.FOXMAIL:
                    hashMap.put(Constant.SMTP_HOST, "smtp.qq.com");
                    hashMap.put(Constant.IMAP_HOST, "imap.qq.com");
                    hashMap.put(Constant.SMTP_PORT, 465);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.SMTP_SSL, true);
                    hashMap.put(Constant.IMAP_SSL, true);
                    return hashMap;
                case EmailKit.MailType.EXMAIL:
                    hashMap.put(Constant.SMTP_HOST, "smtp.exmail.qq.com");
                    hashMap.put(Constant.IMAP_HOST, "imap.exmail.qq.com");
                    hashMap.put(Constant.SMTP_PORT, 465);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.SMTP_SSL, true);
                    hashMap.put(Constant.IMAP_SSL, true);
                    return hashMap;
                case EmailKit.MailType.$163:
                    hashMap.put(Constant.SMTP_HOST, "smtp.163.com");
                    hashMap.put(Constant.IMAP_HOST, "imap.163.com");
                    hashMap.put(Constant.SMTP_PORT, 465);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.SMTP_SSL, true);
                    hashMap.put(Constant.IMAP_SSL, true);
                    return hashMap;
                case EmailKit.MailType.$126:
                    hashMap.put(Constant.SMTP_HOST, "smtp.126.com");
                    hashMap.put(Constant.IMAP_HOST, "imap.126.com");
                    hashMap.put(Constant.SMTP_PORT, 465);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.SMTP_SSL, true);
                    hashMap.put(Constant.IMAP_SSL, true);
                    return hashMap;
                case EmailKit.MailType.OUTLOOK:
                    hashMap.put(Constant.SMTP_HOST, "smtp-mail.outlook.com");
                    hashMap.put(Constant.IMAP_HOST, "imap-mail.outlook.com");
                    hashMap.put(Constant.SMTP_PORT, 25);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.SMTP_SSL, false);
                    hashMap.put(Constant.IMAP_SSL, true);
                    return hashMap;
                default:
                    return null;
            }
        }

    }

    /**
     * 邮件地址转换
     */
    static class AddressUtils {

        //字符串类型地址转为数组类型
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
    private static class TextUtils {

        static String encodeText(String text) {
            try {
                return MimeUtility.encodeText(text);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
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
         * 获取text或惠html文本内容
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
         * @param part
         * @return
         * @throws IOException
         * @throws MessagingException
         */
        static String getMainBody(Part part) throws IOException, MessagingException {
            HashMap<String, StringBuilder> map = ContentUtils.getTexts(part, new HashMap<>());
            if (map.get(TEXT_HTML) != null) {
                return map.get(TEXT_HTML).toString();
            } else if (map.get(TEXT_PLAIN) != null) {
                return map.get(TEXT_PLAIN).toString();
            }
            return "";
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
                    } else if (bodyPart.isMimeType(MULTIPART)) {
                        return ContentUtils.getAttachmentPart(bodyPart, bodyPartList);
                    }
                }
            }
            return bodyPartList;
        }

        /**
         * 获取全部附件
         * @return
         */
        static List<File> getAttachments(Part part) throws MessagingException, IOException {
            List<File> fileList = new ArrayList<>();
            List<BodyPart> bodyPartList = getAttachmentPart(part, new ArrayList<>());
            for (BodyPart bodyPart : bodyPartList) {
                String filename = bodyPart.getFileName();
                filename = (filename != null && filename.startsWith("=?"))? MimeUtility.decodeText(filename) : "unknown";
                File file = new File(ObjectManager.getDirectory() + filename);
                BufferedInputStream bis = new BufferedInputStream(bodyPart.getInputStream());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                for (int length; (length = bis.read()) != -1; ) {
                    bos.write(length);
                    bos.flush();
                }
                bos.close();
                bis.close();
                fileList.add(file);
            }
            return fileList;
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
            //邮件内容（正文与附件）
            Message.Content content = new Message.Content()
                    .setMainBody(() -> {
                        try {
                            Future<String> future = ObjectManager.getMultiThreadService()
                                    .submit(() -> ContentUtils.getMainBody(message));
                            return future.get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .setAttachments(() -> {
                        try {
                            Future<List<File>> future = ObjectManager.getMultiThreadService()
                                    .submit(() -> ContentUtils.getAttachments(message));
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
                message.setSubject(draft.getSubject());
                //邮件发送日期
                message.setSentDate(new Date());
                //创建多重消息对象
                Multipart multipart = new MimeMultipart();
                //判断是否存在text内容
                if (draft.getText() != null) {
                    MimeBodyPart textBodyPart = new MimeBodyPart();
                    textBodyPart.setText(draft.getText());
                    multipart.addBodyPart(textBodyPart);
                }
                //判断是否存在html内容
                if (draft.getHTML() != null) {
                    MimeBodyPart htmlBodyPart = new MimeBodyPart();
                    htmlBodyPart.setContent(draft.getHTML(), "text/html; charset=UTF-8");
                    multipart.addBodyPart(htmlBodyPart);
                }
                //判断附件是否存在附件
                if (draft.getAttachment() != null) {
                    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                    attachmentBodyPart.setFileName(TextUtils.encodeText(draft.getAttachment().getName()));
                    attachmentBodyPart.setDataHandler(new DataHandler(new FileDataSource(draft.getAttachment().getPath())));
                    multipart.addBodyPart(attachmentBodyPart);
                }
                //设置消息内容
                message.setContent(multipart);
                //保存到已发送文件夹
                message.setFlag(Flags.Flag.RECENT, true);
                message.saveChanges();
                //返回结果
                return message;
            } catch (MessagingException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}
