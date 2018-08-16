package com.smailnet.eamil;

import android.app.Activity;

import com.smailnet.eamil.Callback.GetConnectCallback;

import javax.mail.MessagingException;

public class EmailExamine {

    private EmailConfig emailConfig;

    public EmailExamine(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
    }

    public EmailExamine connectServer(final Activity activity, final GetConnectCallback getConnectCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EmailCore emailCore = new EmailCore(emailConfig);
                    emailCore.authentication();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getConnectCallback.loginSuccess();
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getConnectCallback.loginFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
        return this;
    }
}
