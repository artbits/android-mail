package com.smailnet.eamil;

import android.annotation.SuppressLint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

/**
 * 数据转换的工具类
 */
class Converter {

    /**
     * 邮件地址转换
     */
    public static class MailAddress {

        public static Address[] toArrays(String address) {
            int length = (new String[]{address}).length;
            Address[] addresses = new InternetAddress[length];
            try {
                addresses[0] = new InternetAddress(address);
                return addresses;
            } catch (AddressException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String toString(Address[] addresses) {
            if (addresses != null && addresses.length != 0) {
                String address = String.valueOf(addresses[0]);
                try {
                    if (address.startsWith("=?GB") || address.startsWith("=?gb")
                            || address.startsWith("=?UTF") || address.startsWith("=?utf")) {
                        address = MimeUtility.decodeText(address);
                    }
                    return address;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }else {
                return null;
            }
        }

    }

    /**
     * 常用邮件类型的转换
     */
    public static class MailType {

        public static HashMap<String, Object> getResult(int mailType) {
            HashMap<String, Object> hashMap = new HashMap<>();
            switch (mailType) {
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
     * 日期转换
     */
    public static class Date {

        @SuppressLint("SimpleDateFormat")
        public static String toString(java.util.Date date){
            if (date != null){
                return new SimpleDateFormat("yyyy-MM-dd").format(date);
            }else {
                return null;
            }
        }

    }

    /**
     * 消息内容转换
     */
    public static class Content {

        public static String toString(Message message) throws IOException, MessagingException {
            StringBuilder bodyText = new StringBuilder();
            if (message.isMimeType("text/plain")) {
                bodyText.append(String.valueOf(message.getContent()));
            } else if (message.isMimeType("text/html")) {
                bodyText.append(String.valueOf(message.getContent()));
            } else if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0, counts = multipart.getCount(); i < counts; i++) {
                    bodyText.append(multipart.getBodyPart(i).getContent());
                }
            }
            return bodyText.toString();
        }

    }

}
