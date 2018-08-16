package com.smailnet.eamil.Callback;

import com.smailnet.eamil.Entity.EmailMessage;

import java.util.List;

public interface GetMailMessageCallback {
    void gainSuccess(List<EmailMessage>  emailMessageList, int count);
    void gainFailure(String errorMsg);
}
