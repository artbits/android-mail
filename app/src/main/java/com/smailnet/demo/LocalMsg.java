package com.smailnet.demo;

import android.support.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

/**
 * SQLite数据库的数据表字段
 */
public class LocalMsg extends LitePalSupport implements Comparable<LocalMsg> {

    private long uid;
    private String subject;
    private String senderAddress;
    private String senderNickname;
    private String date;
    private String folderName;

    public long getUID() {
        return uid;
    }

    LocalMsg setUID(long uid) {
        this.uid = uid;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    LocalMsg setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    LocalMsg setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
        return this;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    LocalMsg setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
        return this;
    }

    public String getDate() {
        return date;
    }

    LocalMsg setDate(String date) {
        this.date = date;
        return this;
    }

    public String getFolderName() {
        return folderName;
    }

    LocalMsg setFolderName(String folderName) {
        this.folderName = folderName;
        return this;
    }

    @Override
    public int compareTo(@NonNull LocalMsg localMsg) {
        return (int) (localMsg.getUID() - this.uid);
    }
}
