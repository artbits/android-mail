package com.smailnet.demo.table;

import org.litepal.crud.LitePalSupport;

public class LocalFile extends LitePalSupport {

    private int size;
    private String name;
    private String type;
    private String path;
    private LocalMsg localMsg = new LocalMsg();

    public int getSize() {
        return size;
    }

    public LocalFile setSize(int size) {
        this.size = size;
        return this;
    }

    public String getName() {
        return name;
    }

    public LocalFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public LocalFile setType(String type) {
        this.type = type;
        return this;
    }

    public String getPath() {
        return path;
    }

    public LocalFile setPath(String path) {
        this.path = path;
        return this;
    }

    public LocalMsg getLocalMsg() {
        return localMsg;
    }

    public LocalFile setLocalMsg(LocalMsg localMsg) {
        this.localMsg = localMsg;
        return this;
    }

}
