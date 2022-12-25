package com.github.artbits.androidmail.store;

import org.litepal.crud.LitePalSupport;

import java.util.function.Consumer;

public class Message extends LitePalSupport implements Comparable<Message> {

    public long uid;
    public long sentDate;
    public String folderName;
    public String subject;
    public String fromAddress;
    public String fromNickname;
    public String toAddress;
    public String toNickname;
    public String type;
    public String content;


    public Message() { }


    public Message(Consumer<Message> consumer) {
        consumer.accept(this);
    }


    @Override
    public int compareTo(Message message) {
        return (int) (message.uid - this.uid);
    }

}
