package com.smailnet.eamil;


import android.app.Activity;

import com.smailnet.eamil.Callback.GetMailMessageCallback;
import com.smailnet.eamil.Entity.EmailMessage;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

public class EmailReceiveClient {

    private EmailConfig emailConfig;

    public EmailReceiveClient(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
    }

    /**
     * 接收邮件
     *
     * @param getMailMessageCallback
     */
    public EmailReceiveClient receive(final Activity activity, final GetMailMessageCallback getMailMessageCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EmailCore emailCore = new EmailCore(emailConfig);
                    final List<EmailMessage> emailMessageList = emailCore.receiveMail();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getMailMessageCallback.gainSuccess(emailMessageList, emailMessageList.size());
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getMailMessageCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getMailMessageCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
        return this;
    }
}
