package com.github.artbit.mailkit;

import java.net.MalformedURLException;
import java.util.function.Consumer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

class SMTPService {

    private final MailKit.Config config;


    SMTPService(MailKit.Config config) {
        this.config = config;
    }


    public void send(MailKit.Draft draft, Runnable runnable, Consumer<Exception> consumer) {
        MailKit.thread.execute(() -> {
            try(Transport transport = Tools.getTransport(config)) {
                MimeMessage message = Tools.toMimeMessage(config, draft);
                transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
                if (draft.cc != null && draft.cc.length != 0) {
                    transport.sendMessage(message, message.getRecipients(Message.RecipientType.CC));
                }
                if (draft.bcc != null && draft.bcc.length != 0) {
                    transport.sendMessage(message, message.getRecipients(Message.RecipientType.BCC));
                }
                MailKit.handler.post(runnable);
            } catch (MessagingException | MalformedURLException e) {
                MailKit.handler.post(() -> consumer.accept(e));
            }
        });
    }


    private void reply(MailKit.Draft draft, String folderName, long originUID, Runnable runnable, Consumer<Exception> consumer) {
        MailKit.IMAP imap = new MailKit.IMAP(config);
        MailKit.IMAP.Folder folder = imap.getFolder(folderName);
        folder.getMsg(originUID, msg -> {
            draft.text = draft.text + msg.mainBody.content;
            send(draft, runnable, consumer);
        }, consumer);
    }


    private void forward(MailKit.Draft draft, String folderName, long originUID, Runnable runnable, Consumer<Exception> consumer) {
        MailKit.IMAP imap = new MailKit.IMAP(config);
        MailKit.IMAP.Folder folder = imap.getFolder(folderName);
        folder.getMsg(originUID, msg -> {
            draft.text = draft.text + msg.mainBody.content;
            send(draft, runnable, consumer);
        }, consumer);
    }

}
