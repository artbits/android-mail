package com.smailnet.eamil;

public interface GetLoginCallback {
    void loginSuccess();
    void loginFailure(String errorMsg);
}
