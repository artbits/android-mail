package com.smailnet.eamil.entity;

public final class To {

    private String address;
    private String nickname;

    public To(String address, String nickname) {
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
