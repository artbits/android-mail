package com.github.artbit.mailkit;

import com.sun.mail.imap.IMAPStore;

import java.util.function.Consumer;

import javax.mail.MessagingException;
import javax.mail.Transport;

final class AuthService {

    private final MailKit.Config config;


    AuthService(MailKit.Config config) {
        this.config = config;
    }


    void auth(Runnable runnable, Consumer<Exception> consumer) {
        MailKit.thread.execute(() -> {
            int count = 0;
            if (config.SMTPHost != null && config.SMTPPort != null) {
                try(Transport transport = Tools.getTransport(config)) {
                    count++;
                } catch (MessagingException e) {
                    MailKit.handler.post(() -> consumer.accept(e));
                }
            }
            if (config.IMAPHost != null && config.IMAPPort != null) {
                try(IMAPStore store = Tools.getStore(config)) {
                    count++;
                } catch (MessagingException e) {
                    MailKit.handler.post(() -> consumer.accept(e));
                }
            }
            if (count == 2) {
                MailKit.handler.post(runnable);
            }
        });
    }

}
