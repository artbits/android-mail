package com.smailnet.demo;

import android.app.Application;

import com.smailnet.eamil.EmailConfig;
import com.smailnet.eamil.GetLoginCallback;

public class EmailApplication extends Application{

    private static EmailConfig emailConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        emailConfig = new EmailConfig();
    }

    public static EmailConfig getEmailConfig(){
        return emailConfig;
    }
}
