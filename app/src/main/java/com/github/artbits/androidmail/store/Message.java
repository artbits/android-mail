package com.github.artbits.androidmail.store;

import com.github.artbits.quickio.core.IOEntity;

import java.util.function.Consumer;

public final class Message extends IOEntity {
    public Long uid;
    public Long sentDate;
    public String folderName;
    public String subject;
    public String fromAddress;
    public String fromNickname;
    public String toAddress;
    public String toNickname;
    public String type;
    public String content;

    public static Message of(Consumer<Message> consumer) {
        Message message = new Message();
        consumer.accept(message);
        return message;
    }
}
