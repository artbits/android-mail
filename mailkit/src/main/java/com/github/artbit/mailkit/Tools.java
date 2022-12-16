package com.github.artbit.mailkit;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

final class Tools {

    static Session toSession(MailKit.Config config) {
        Properties properties = new Properties();
        if (config.SMTPHost != null && config.SMTPPort != null) {
            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.host", config.SMTPHost);
            properties.put("mail.smtp.port", config.SMTPPort);
            if (config.account.contains("@outlook.com") || config.account.contains("@office365.com")) {
                properties.put("mail.smtp.starttls.enable", config.SMTPSSLEnable);
                properties.put("mail.smtp.starttls.required", true);
            } else {
                properties.put("mail.smtp.ssl.enable", config.SMTPSSLEnable);
            }
        }
        if (config.IMAPHost != null && config.IMAPPort != null) {
            properties.put("mail.imap.auth", true);
            properties.put("mail.imap.host", config.IMAPHost);
            properties.put("mail.imap.port", config.IMAPPort);
            properties.put("mail.imap.ssl.enable", config.IMAPSSLEnable);
            properties.setProperty("mail.imap.partialfetch", "false");
            properties.setProperty("mail.imaps.partialfetch", "false");
        }
        return Session.getInstance(properties);
    }


    static Address[] toAddresses(String[] addresses) throws AddressException {
        Address[] internetAddresses = new InternetAddress[addresses.length];
        for (int i = 0, length = addresses.length; i < length; i++) {
            internetAddresses[i] = new InternetAddress(addresses[i]);
        }
        return internetAddresses;
    }


    static MimeMessage toMimeMessage(MailKit.Config config, MailKit.Draft draft)
            throws MessagingException, MalformedURLException {
        Session session = toSession(config);
        MimeMessage message = new MimeMessage(session);
        message.addRecipients(MimeMessage.RecipientType.TO, toAddresses(draft.to));
        if (draft.cc != null) {
            message.addRecipients(MimeMessage.RecipientType.CC, toAddresses(draft.cc));
        }
        if (draft.bcc != null) {
            message.addRecipients(MimeMessage.RecipientType.BCC, toAddresses(draft.bcc));
        }
        message.setFrom(new InternetAddress(config.nickname + "<" + config.account + ">"));
        message.setSubject(draft.subject, "UTF-8");
        message.setSentDate(new Date());
        if (draft.html != null) {
            message.setContent(draft.html, "text/html; charset=UTF-8");
        }
        if (draft.html == null && draft.text != null) {
            message.setText(draft.text, "UTF-8");
        }
        message.setFlag(Flags.Flag.RECENT, true);
        message.saveChanges();
        return message;
    }


    static MailKit.Msg toMsg(long uid, IMAPMessage imapMessage) throws MessagingException, IOException {
        long sentTime = imapMessage.getSentDate().getTime();
        String subject = imapMessage.getSubject();
        MailKit.Msg.Flags flags = getFlags(imapMessage.getFlags());
        MailKit.Msg.From from = getFrom(imapMessage.getFrom());
        List<MailKit.Msg.To> toList = getToList(imapMessage.getRecipients(MimeMessage.RecipientType.TO));
        List<MailKit.Msg.Cc> ccList = getCcList(imapMessage.getRecipients(MimeMessage.RecipientType.CC));
        MailKit.Msg.MainBody mainBody = getMainBody(imapMessage);
        return new MailKit.Msg(m -> {
            m.uid = uid;
            m.subject = subject;
            m.sentDate = sentTime;
            m.flags = flags;
            m.from = from;
            m.toList = toList;
            m.ccList = ccList;
            m.mainBody = mainBody;
        });
    }


    static MailKit.Msg getMsgHead(long uid, IMAPMessage imapMessage) {
        try {
            long sentDate = imapMessage.getSentDate().getTime();
            String subject = imapMessage.getSubject();
            MailKit.Msg.Flags flags = getFlags(imapMessage.getFlags());
            MailKit.Msg.From from = Tools.getFrom(imapMessage.getFrom());
            List<MailKit.Msg.To> toList = Tools.getToList(imapMessage.getRecipients(MimeMessage.RecipientType.TO));
            List<MailKit.Msg.Cc> ccList = Tools.getCcList(imapMessage.getRecipients(MimeMessage.RecipientType.CC));
            return new MailKit.Msg(msg -> {
                msg.uid = uid;
                msg.sentDate = sentDate;
                msg.subject = subject;
                msg.flags = flags;
                msg.from = from;
                msg.toList = toList;
                msg.ccList = ccList;
            });
        } catch (Exception e) {
            return null;
        }
    }


    static List<MailKit.Msg> getMsgHeads(IMAPFolder folder, Message[] messages) {
        List<MailKit.Msg> msgHeads = new ArrayList<>();
        for (Message message : messages) {
            try {
                IMAPMessage imapMessage = (IMAPMessage) message;
                long uid = folder.getUID(imapMessage);
                long sentDate = imapMessage.getSentDate().getTime();
                String subject = imapMessage.getSubject();
                MailKit.Msg.Flags flags = getFlags(imapMessage.getFlags());
                MailKit.Msg.From from = Tools.getFrom(imapMessage.getFrom());
                List<MailKit.Msg.To> toList = Tools.getToList(imapMessage.getRecipients(MimeMessage.RecipientType.TO));
                List<MailKit.Msg.Cc> ccList = Tools.getCcList(imapMessage.getRecipients(MimeMessage.RecipientType.CC));
                msgHeads.add(new MailKit.Msg(msg -> {
                    msg.uid = uid;
                    msg.sentDate = sentDate;
                    msg.subject = subject;
                    msg.flags = flags;
                    msg.from = from;
                    msg.toList = toList;
                    msg.ccList = ccList;
                }));
            } catch (Exception ignored) { }
        }
        return msgHeads;
    }


    static MailKit.Msg.From getFrom(Address[] addresses) {
        if (addresses != null && addresses.length != 0) {
            InternetAddress address = (InternetAddress) addresses[0];
            return new MailKit.Msg.From(f -> {
                f.address = address.getAddress();
                f.nickname = address.getPersonal();
            });
        }
        return null;
    }


    static List<MailKit.Msg.To> getToList(Address[] addresses) {
        if (addresses != null && addresses.length != 0) {
            List<MailKit.Msg.To> toList = new ArrayList<>();
            for (Address address : addresses) {
                InternetAddress internetAddress = (InternetAddress) address;
                toList.add(new MailKit.Msg.To(t -> {
                    t.address = internetAddress.getAddress();
                    t.nickname = internetAddress.getPersonal();
                }));
            }
            return toList;
        }
        return null;
    }


    static List<MailKit.Msg.Cc> getCcList(Address[] addresses) {
        if (addresses != null && addresses.length != 0) {
            List<MailKit.Msg.Cc> ccList = new ArrayList<>();
            for (Address address : addresses) {
                InternetAddress internetAddress = (InternetAddress) address;
                ccList.add(new MailKit.Msg.Cc(c -> {
                    c.address = internetAddress.getAddress();
                    c.nickname = internetAddress.getPersonal();
                }));
            }
            return ccList;
        }
        return null;
    }


    static MailKit.Msg.Flags getFlags(Flags internetFlags) {
        return new MailKit.Msg.Flags(f -> {
           f.isSeen =  internetFlags.contains(Flags.Flag.SEEN);
           f.isStar = internetFlags.contains(Flags.Flag.FLAGGED);
        });
    }


    static HashMap<String, StringBuilder> getMainBodyMap(Part part, HashMap<String, StringBuilder> map)
            throws MessagingException, IOException {
        StringBuilder text = new StringBuilder();
        StringBuilder html = new StringBuilder();
        if (part.isMimeType("text/plain")){
            map.put("text/plain", text.append(part.getContent()));
        } else if (part.isMimeType("text/html")) {
            map.put("text/html", html.append(part.getContent()));
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0, count = multipart.getCount(); i < count; i++) {
                getMainBodyMap(multipart.getBodyPart(i), map);
            }
        }
        return map;
    }


    static MailKit.Msg.MainBody getMainBody(IMAPMessage imapMessage) throws IOException, MessagingException {
        HashMap<String, StringBuilder> map = getMainBodyMap(imapMessage, new HashMap<>());
        imapMessage.setFlag(Flags.Flag.SEEN, true);
        if (map.get("text/html") != null) {
            return new MailKit.Msg.MainBody(m -> {
                m.type = "text/html";
                m.content = String.valueOf(map.get("text/html"));
            });
        } else if (map.get("text/plain") != null) {
            return new MailKit.Msg.MainBody(m -> {
                m.type = "text/plain";
                m.content = String.valueOf(map.get("text/plain"));
            });
        } else {
            return null;
        }
    }


    static Transport getTransport(MailKit.Config config) throws MessagingException {
        Session session = toSession(config);
        Transport transport = session.getTransport("smtp");
        transport.connect(config.SMTPHost, config.account, config.password);
        return transport;
    }


    static IMAPStore getStore(MailKit.Config config) throws MessagingException {
        Session session = toSession(config);
        IMAPStore store = (IMAPStore) session.getStore("imap");
        store.connect(config.IMAPHost, config.account, config.password);
        return store;
    }


    static IMAPFolder getFolder(IMAPStore store, String folderName, MailKit.Config config) throws MessagingException {
        IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
        boolean b1 = config.account.contains("@163.com");
        boolean b2 = config.account.contains("@126.com");
        boolean b3 = config.account.contains("@yeah.net");
        if (b1 || b2 || b3) {
            folder.doCommand(protocol -> {
                protocol.id("FUTONG");
                return null;
            });
        }
        folder.open(Folder.READ_WRITE);
        return folder;
    }

}