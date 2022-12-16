package com.github.artbit.mailkit;

import com.sun.mail.imap.IMAPStore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.mail.Folder;
import javax.mail.MessagingException;

class IMAPService {

    private final MailKit.Config config;


    IMAPService(MailKit.Config config) {
        this.config = config;
    }


    public void getDefaultFolders(Consumer<List<String>> consumer1, Consumer<Exception> consumer2) {
        MailKit.thread.execute(() -> {
            try(IMAPStore store = Tools.getStore(config)) {
                List<String> folders = new ArrayList<>();
                for (Folder folder : store.getDefaultFolder().list()) {
                    if (folder.list().length == 0) {
                        folders.add(folder.getFullName());
                    }
                }
                MailKit.handler.post(() -> consumer1.accept(folders));
            } catch (MessagingException e) {
                MailKit.handler.post(() -> consumer2.accept(e));
            }
        });
    }


    public MailKit.IMAP.Folder getFolder(String folderName) {
        return new MailKit.IMAP.Folder(config, folderName);
    }


    public MailKit.IMAP.Inbox getInbox() {
        return new MailKit.IMAP.Inbox(config, "INBOX");
    }


    public MailKit.IMAP.DraftBox getDraftBox() {
        return new MailKit.IMAP.DraftBox(config, "Drafts");
    }

}
