package com.smailnet.eamil.entity;

import java.io.File;
import java.util.List;

public final class Message {

    private long uid;
    private String subject;
    private String content;
    private SentDate sentDate;
    private From from;
    private To to;
    private List<File> attachments;
    private boolean attachment;
    private boolean seen;

    public Message(long uid, String subject, String content, SentDate sentDate, From from, To to, List<File> attachments, boolean attachment, boolean seen){
        this.uid = uid;
        this.subject = subject;
        this.content = content;
        this.sentDate = sentDate;
        this.from = from;
        this.to = to;
        this.attachments = attachments;
        this.attachment = attachment;
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

    public List<File> getAttachments() {
        return attachments;
    }

    public boolean isAttachment() {
        return attachment;
    }

    public boolean isSeen() {
        return seen;
    }

}
