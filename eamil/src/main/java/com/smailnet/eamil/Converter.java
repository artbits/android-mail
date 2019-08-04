package com.smailnet.eamil;

import android.annotation.SuppressLint;

import com.smailnet.eamil.entity.From;
import com.smailnet.eamil.entity.SentDate;
import com.smailnet.eamil.entity.Message;
import com.smailnet.eamil.entity.To;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * 数据转换的工具类
 */
class Converter {

    /**
     * 常用邮箱类型的转换
     */
    static class MailType {

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
                    return hashMap;
                case Email.MailType.NETEASE:
                    hashMap.put(Constant.SMTP_HOST, "smtp.163.com");
                    hashMap.put(Constant.POP3_HOST, "pop.163.com");
                    hashMap.put(Constant.IMAP_HOST, "imap.163.com");
                    hashMap.put(Constant.SMTP_PORT, 25);
                    hashMap.put(Constant.POP3_PORT, 110);
                    hashMap.put(Constant.IMAP_PORT, 143);
                    return hashMap;
                default:
                    return null;
            }
        }

    }

    /**
     * 邮件地址转换
     */
    static class MailAddress {

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
            InternetAddress address = (InternetAddress) addresses[0];
            return address.getPersonal();
        }

    }

    /**
     * 日期转换
     */
    static class Date {

        //Date对象转为字符串类型，格式为yyyy年M月d日 hh:mm
        @SuppressLint("SimpleDateFormat")
        static String toString(java.util.Date date){
            if (date != null){
                return new SimpleDateFormat("yyyy年M月d日 hh:mm").format(date);
            }else {
                return null;
            }
        }

        //Date对象转为长整型，时间单位为毫秒
        static long toLong(java.util.Date date) {
            if (date != null){
                return date.getTime();
            }else {
                return 0;
            }
        }

    }

    /**
     * 消息内容转换
     */
    static class Content {

        //解析邮件内容并转为字符串类型
        static String toString(javax.mail.Message message) throws IOException, MessagingException {
            StringBuilder bodyText = new StringBuilder();
            if (message.isMimeType("text/plain")) {
                bodyText.append(message.getContent());
            } else if (message.isMimeType("text/html")) {
                bodyText.append(message.getContent());
            } else if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0, counts = multipart.getCount(); i < counts; i++) {
                    if (multipart.getBodyPart(i).isMimeType("text/plain")) {
                        continue;
                    }
                    bodyText.append(multipart.getBodyPart(i).getContent());
                }
            }
            return bodyText.toString();
        }

    }

    /**
     * 邮件数据转换
     */
    static class InternetMessage {

        //把JavaMail的Message对象的重要数据复制到Email的Message对象中
        static Message toLocalMessage(long uid, javax.mail.Message message, boolean isFast) throws MessagingException, IOException {
            //邮件主题
            String subject = message.getSubject();
            //邮件内容
            String content = (isFast)? null : Content.toString(message);
            //邮件的发送日期
            SentDate sentDate = new SentDate(Date.toLong(message.getSentDate()), Date.toString(message.getSentDate()));
            //发件人
            From from = new From(MailAddress.getAddress(message.getFrom()), MailAddress.getNickname(message.getFrom()));
            //收件人
            To to = new To(MailAddress.getAddress(message.getAllRecipients()), MailAddress.getNickname(message.getAllRecipients()));
            //邮件是否已读
            boolean isSeen = message.getFlags().contains(Flags.Flag.SEEN);
            //返回转换结果
            return new Message(uid, subject, content, sentDate, from, to, isSeen);
        }

    }

}
