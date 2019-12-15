package com.smailnet.demo.adapter.item;

public class MsgItem {

    private long uid;
    private boolean read;
    private String senderNickname;
    private String subject;
    private String date;

    public long getUID() {
        return uid;
    }

    public MsgItem setUID(long uid) {
        this.uid = uid;
        return this;
    }

    public boolean isRead() {
        return read;
    }

    public MsgItem setRead(boolean read) {
        this.read = read;
        return this;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public MsgItem setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public MsgItem setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getDate() {
        return date;
    }

    public MsgItem setDate(String date) {
        this.date = date;
        return this;
    }
}
