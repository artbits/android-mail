package com.smailnet.eamil.entity;

public final class SentDate {

    private long millisecond;
    private String text;

    public SentDate(long millisecond, String text) {
        this.millisecond = millisecond;
        this.text = text;
    }

    public long getMillisecond() {
        return millisecond;
    }

    public String getText() {
        return text;
    }
}
