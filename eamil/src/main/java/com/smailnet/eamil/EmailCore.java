package com.smailnet.eamil;

import com.smailnet.eamil.Entity.EmailMessage;
import com.smailnet.eamil.Utils.CodeUtil;
import com.smailnet.eamil.Utils.ContentUtil;
import com.smailnet.eamil.Utils.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class EmailCore {

    private EmailConfig emailConfig;
    private Session session;

    /**
     * 在构造器中初始化Properties和Session
     *
     * @param emailConfig
     */
    EmailCore(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
        Properties properties = new Properties();
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.socketFactory.port", String.valueOf(emailConfig.getSmtpPort()));
        properties.put("mail.smtp.post", String.valueOf(emailConfig.getSmtpPort()));
        properties.put("mail.smtp.host", emailConfig.getSmtpHost());
        properties.put("mail.smtp.auth", true);
        properties.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.pop3.socketFactory.fallback", "false");
        properties.put("mail.pop3.socketFactory.port", String.valueOf(emailConfig.getPopPort()));
        properties.put("mail.pop3.post", String.valueOf(emailConfig.getPopPort()));
        properties.put("mail.pop3.host", emailConfig.getPopHost());
        properties.put("mail.pop3.auth", true);
        session = Session.getInstance(properties);
    }

    /**
     * 验证邮箱帐户和服务器配置信息
     *
     * @throws MessagingException
     */
    public void authentication() throws MessagingException {
        Transport transport = session.getTransport("smtp");
        Store store = session.getStore("pop3");
        transport.connect(emailConfig.getSmtpHost(), emailConfig.getAccount(), emailConfig.getPassword());
        store.connect(emailConfig.getPopHost(), emailConfig.getAccount(), emailConfig.getPassword());
    }

    /**
     * 组装邮件的信息
     *
     * @param address
     * @param subject
     * @param content
     * @return
     * @throws MessagingException
     */
    public Message setMessage(Address[] address, String subject, Object content) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setRecipients(Message.RecipientType.TO, address);
        message.setFrom(new InternetAddress(emailConfig.getAccount()));
        message.setSubject(subject);
        message.setContent(content, "text/html");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    /**
     * 发送邮件
     *
     * @param message
     * @throws MessagingException
     */
    public void sendMail(Message message) throws MessagingException {
        Transport transport = session.getTransport("smtp");
        transport.connect(emailConfig.getSmtpHost(), emailConfig.getAccount(), emailConfig.getPassword());
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    /**
     * 获取收件服务器上的邮件
     *
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailMessage> receiveMail() throws MessagingException, IOException {
        Store store = session.getStore("pop3");
        store.connect(emailConfig.getPopHost(), emailConfig.getAccount(), emailConfig.getPassword());
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String subject, from, to, date, content;
        for (Message message : messages){
            subject = message.getSubject();
            from = CodeUtil.conver(String.valueOf(message.getFrom()[0]));
            to = Arrays.toString(message.getRecipients(Message.RecipientType.TO));
            date = TimeUtil.getDate(message.getSentDate());
            content = ContentUtil.getContent(message);
            EmailMessage emailMessage = new EmailMessage(subject, from, to, date, content);
            emailMessageList.add(emailMessage);
        }
        folder.close(false);
        store.close();
        return emailMessageList;
    }
}
