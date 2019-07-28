package com.smailnet.eamil.entity;

public final class Message {

    private long uid;
    private String subject;
    private String content;
    private SentDate sentDate;
    private From from;
    private To to;
    private boolean seen;

    public Message(long uid, String subject, String content, SentDate sentDate, From from, To to, boolean seen){
        this.uid = uid;
        this.subject = subject;
        this.content = content;
        this.sentDate = sentDate;
        this.from = from;
        this.to = to;
        this.seen = seen;
    }

    public long getUid() {
        return uid;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public SentDate getSentDate() {
        return sentDate;
    }

    public From getFrom() {
        return from;
    }

    public To getTo() {
        return to;
    }

    public boolean isSeen() {
        return seen;
    }
}
