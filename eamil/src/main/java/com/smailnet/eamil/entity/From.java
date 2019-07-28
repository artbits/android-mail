package com.smailnet.eamil.entity;

public final class From {

    private String address;
    private String nickname;

    public From(String address, String nickname) {
        this.address = address;
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public String getNickname() {
        return nickname;
    }

}
