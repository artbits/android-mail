package com.smailnet.demo;

import android.app.Application;

import com.smailnet.eamil.EmailConfig;

public class EmailApp extends Application{

    private static EmailConfig emailConfig;     //设置全局emailConfig

    @Override
    public void onCreate() {
        super.onCreate();
        emailConfig = new EmailConfig();
    }

    /**
     * 获取emailConfig
     *
     * @return
     */
    public static EmailConfig emailConfig(){
        return emailConfig;
    }
}
