package com.smailnet.eamil;

import android.annotation.SuppressLint;

import com.smailnet.eamil.entity.From;
import com.smailnet.eamil.entity.SentDate;
import com.smailnet.eamil.entity.Message;
import com.smailnet.eamil.entity.To;

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
import java.util.Properties;

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
                case Email.MailType.QQ:
                case Email.MailType.FOXMAIL:
                    hashMap.put(Constant.SMTP_HOST, "smtp.qq.com");
                    hashMap.put(Constant.POP3_HOST, "pop.qq.com");
                    hashMap.put(Constant.IMAP_HOST, "imap.qq.com");
                    hashMap.put(Constant.SMTP_PORT, 465);
                    hashMap.put(Constant.POP3_PORT, 995);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.SMTP_SSL, true);
                    hashMap.put(Constant.POP3_SSL, true);
                    hashMap.put(Constant.IMAP_SSL, true);
                    return hashMap;
                case Email.MailType.EXMAIL:
                    hashMap.put(Constant.SMTP_HOST, "smtp.exmail.qq.com");
                    hashMap.put(Constant.POP3_HOST, "pop.exmail.qq.com");
                    hashMap.put(Constant.IMAP_HOST, "imap.exmail.qq.com");
                    hashMap.put(Constant.SMTP_PORT, 465);
                    hashMap.put(Constant.POP3_PORT, 995);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.SMTP_SSL, true);
                    hashMap.put(Constant.POP3_SSL, true);
                    hashMap.put(Constant.IMAP_SSL, true);
                    return hashMap;
                case Email.MailType.$163:
                    hashMap.put(Constant.SMTP_HOST, "smtp.163.com");
                    hashMap.put(Constant.POP3_HOST, "pop.163.com");
                    hashMap.put(Constant.IMAP_HOST, "imap.163.com");
                    hashMap.put(Constant.SMTP_PORT, 465);
                    hashMap.put(Constant.POP3_PORT, 995);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.SMTP_SSL, true);
                    hashMap.put(Constant.POP3_SSL, true);
                    hashMap.put(Constant.IMAP_SSL, true);
                    return hashMap;
                case Email.MailType.$126:
                    hashMap.put(Constant.SMTP_HOST, "smtp.126.com");
                    hashMap.put(Constant.POP3_HOST, "pop.126.com");
                    hashMap.put(Constant.IMAP_HOST, "imap.126.com");
                    hashMap.put(Constant.SMTP_PORT, 465);
                    hashMap.put(Constant.POP3_PORT, 995);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.SMTP_SSL, true);
                    hashMap.put(Constant.POP3_SSL, true);
                    hashMap.put(Constant.IMAP_SSL, true);
                    return hashMap;
                case Email.MailType.OUTLOOK:
                    hashMap.put(Constant.SMTP_HOST, "smtp-mail.outlook.com");
                    hashMap.put(Constant.IMAP_HOST, "imap-mail.outlook.com");
                    hashMap.put(Constant.POP3_HOST, "");
                    hashMap.put(Constant.SMTP_PORT, 25);
                    hashMap.put(Constant.IMAP_PORT, 993);
                    hashMap.put(Constant.POP3_PORT, 0);
                    hashMap.put(Constant.SMTP_SSL, false);
                    hashMap.put(Constant.IMAP_SSL, true);
                    hashMap.put(Constant.POP3_SSL, true);
                    return hashMap;
                default:
                    return null;
            }
        }

    }

    /**
     * 邮件地址转换
     */
    static class AddressConversion {

        //字符串类型地址转为数组类型
        static Address[] toArrays(String address) {
            try {
                int length = (new String[]{address}).length;
                Address[] addresses = new InternetAddress[length];
                addresses[0] = new InternetAddress(address);
                return addresses;
            } catch (AddressException e) {
                e.printStackTrace();
                return null;
            }
        }

        //获取邮件地址
        static String getAddress(Address[] addresses) {
            if (addresses != null && addresses.length != 0) {
                InternetAddress address = (InternetAddress) addresses[0];
                return address.getAddress();
            } else {
                return null;
            }
        }

        //获取该邮件地址对应的用户昵称
        static String getNickname(Address[] addresses) {
            if (addresses != null && addresses.length != 0) {
                InternetAddress address = (InternetAddress) addresses[0];
                return address.getPersonal();
            } else {
                return null;
            }
        }

    }

    /**
     * 日期转换
     */
    static class DateConversion {

        //Date对象转为字符串类型，格式为yyyy年M月d日 hh:mm
        @SuppressLint("SimpleDateFormat")
        static String toString(Date date){
            if (date != null){
                return new SimpleDateFormat("yyyy年M月d日 hh:mm").format(date);
            }else {
                return null;
            }
        }

        //Date对象转为长整型，时间单位为毫秒
        static long toLong(Date date) {
            if (date != null){
                return date.getTime();
            }else {
                return 0;
            }
        }

    }

    /**
     * 文本编码转换
     */
    static class CodingConversion {

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
    static class ContentConversion {

        private final static String TEXT_PLAIN = "text/plain";
        private final static String TEXT_HTML = "text/html";
        private final static String MULTIPART = "multipart/*";

        //解析邮件内容并转为字符串类型，参数isRecursion表示是否为递归调用
        static String toString(Part part, boolean isRecursion) throws IOException, MessagingException {
            StringBuilder stringBuilder = new StringBuilder();
            if (part.isMimeType(TEXT_PLAIN) || part.isMimeType(TEXT_HTML)) {
                stringBuilder.append(part.getContent());
            } else if (part.isMimeType(MULTIPART)) {
                boolean textFlag = false;
                Multipart multipart = (Multipart) part.getContent();
                for (int i = 0, counts = multipart.getCount(); i < counts; i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType(TEXT_PLAIN)) {
                        textFlag = (!textFlag && !isRecursion);
                        stringBuilder.append(bodyPart.getContent());
                    } else if (bodyPart.isMimeType(TEXT_HTML)) {
                        if (textFlag) {
                            stringBuilder = new StringBuilder();
                        }
                        stringBuilder.append(bodyPart.getContent());
                    } else if (bodyPart.isMimeType(MULTIPART)) {
                        stringBuilder.append(ContentConversion.toString(bodyPart, true));
                    }
                }
            }
            return stringBuilder.toString();
        }

        //获取全部附件s
        static List<File> getAttachments(Part part) throws MessagingException, IOException {
            List<File> fileList = new ArrayList<>();
            if (part.isMimeType(MULTIPART)) {
                Multipart multipart = (Multipart) part.getContent();
                for (int i = 0, counts = multipart.getCount(); i < counts; i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getDisposition() != null) {
                        String filename = bodyPart.getFileName();
                        filename = (filename.startsWith("=?"))? MimeUtility.decodeText(filename) : filename;
                        File file = new File(Manager.getDirectory() + filename);
                        BufferedInputStream bis = new BufferedInputStream(bodyPart.getInputStream());
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        for (int length; (length = bis.read()) != -1; ) {
                            bos.write(length);
                            bos.flush();
                        }
                        bos.close();
                        bis.close();
                        fileList.add(file);
                    } else if (bodyPart.isMimeType(MULTIPART)) {
                        ContentConversion.getAttachments(bodyPart);
                    }
                }
            }
            return fileList;
        }

        //判断是否存在附件
        static boolean isAttachment(Part part) throws MessagingException, IOException {
            if (part.isMimeType(MULTIPART)) {
                Multipart multipart = (Multipart) part.getContent();
                for (int i = 0, counts = multipart.getCount(); i < counts; i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getDisposition() != null) {
                        return true;
                    } else if (bodyPart.isMimeType(MULTIPART)) {
                        return ContentConversion.isAttachment(bodyPart);
                    }
                }
            }
            return false;
        }

    }

    /**
     * 消息数据转换
     */
    static class MessageConversion {

        //把JavaMail的Message对象的重要数据复制到Email的Message对象中
        static Message toLocalMessage(long uid, javax.mail.Message message, boolean isFast) throws MessagingException, IOException {
            //邮件主题
            String subject = message.getSubject();
            //邮件的发送日期
            SentDate sentDate = new SentDate(DateConversion.toLong(message.getSentDate()), DateConversion.toString(message.getSentDate()));
            //发件人
            From from = new From(AddressConversion.getAddress(message.getFrom()), AddressConversion.getNickname(message.getFrom()));
            //收件人
            To to = new To(AddressConversion.getAddress(message.getAllRecipients()), AddressConversion.getNickname(message.getAllRecipients()));
            //邮件文本内容
            String content = (isFast)? null : ContentConversion.toString(message, false);
            //邮件是否存在附件
            boolean isAttachment = (!isFast) && ContentConversion.isAttachment(message);
            //获取附件
            List<File> attachments = (isFast || !isAttachment) ? null : ContentConversion.getAttachments(message);
            //邮件是否已读
            boolean isSeen = message.getFlags().contains(Flags.Flag.SEEN);
            //返回转换结果
            return new Message(uid, subject, content, sentDate, from, to, attachments, isAttachment, isSeen);
        }

        //把Email的Message对象的数据复制到JavaMail的message对象
        static MimeMessage toInternetMessage(Email.Config config, HashMap<String, Object> messageMap) {
            try {
                //发件人昵称
                String nickname = (String) messageMap.get("nickname");
                //主题
                String subject = (String) messageMap.get("subject");
                //文本内容
                String text = (String) messageMap.get("text");
                //HTML内容
                Object content = messageMap.get("html");
                //附件
                File attachment = (File) messageMap.get("attachment");
                //收件人
                Address[] to = (Address[]) messageMap.get("to");
                //抄送人
                Address[] cc = (Address[]) messageMap.get("cc");
                //密送人
                Address[] bcc = (Address[]) messageMap.get("bcc");
                //发件人邮箱地址
                String account = (config != null) ? config.getAccount() : Manager.getGlobalConfig().getAccount();

                Properties properties = (config != null) ? EmailUtils.getProperties(config) : EmailUtils.getProperties();
                Session session = Session.getInstance(properties);

                //创建消息对象
                MimeMessage message = new MimeMessage(session);
                //收件人
                message.addRecipients(MimeMessage.RecipientType.TO, to);
                //判断是否存在抄送人
                if (cc != null) {
                    message.addRecipients(javax.mail.Message.RecipientType.CC, cc);
                }
                //判断是否存在密送人
                if (bcc != null) {
                    message.addRecipients(javax.mail.Message.RecipientType.BCC, bcc);
                }
                //发件人昵称+邮箱地址
                message.setFrom(new InternetAddress(nickname + "<" + account + ">"));
                //邮件主题
                message.setSubject(subject);
                //邮件发送日期
                message.setSentDate(new Date());
                //创建多重消息对象
                Multipart multipart = new MimeMultipart();
                //判断是否存在text内容
                if (text != null) {
                    MimeBodyPart textBodyPart = new MimeBodyPart();
                    textBodyPart.setText(text);
                    multipart.addBodyPart(textBodyPart);
                }
                //判断是否存在html内容
                if (content != null) {
                    MimeBodyPart htmlBodyPart = new MimeBodyPart();
                    htmlBodyPart.setContent(content, "text/html; charset=UTF-8");
                    multipart.addBodyPart(htmlBodyPart);
                }
                //判断附件是否存在附件
                if (attachment != null) {
                    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                    attachmentBodyPart.setFileName(CodingConversion.encodeText(attachment.getName()));
                    attachmentBodyPart.setDataHandler(new DataHandler(new FileDataSource(attachment.getPath())));
                    multipart.addBodyPart(attachmentBodyPart);
                }
                //设置消息内容
                message.setContent(multipart);
                return message;
            } catch (MessagingException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}
