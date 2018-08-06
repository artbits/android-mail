package com.smailnet.eamil;

import javax.mail.Session;
import javax.mail.Transport;

public class ConfigData {

    private Transport transport;
    private Session session;
    private String account;

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }
}
