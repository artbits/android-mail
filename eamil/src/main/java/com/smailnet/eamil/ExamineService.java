package com.smailnet.eamil;

import android.os.Handler;
import android.os.Looper;

public final class ExamineService {

    private Email.Config config;
    private Handler handler;

    ExamineService(Email.Config config) {
        this.config = config;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 连接邮件服务器检查
     * @param gotConnectCallback
     */
    public void connect(Email.GotConnectCallback gotConnectCallback) {
        new Thread(() ->
                EmailCore.setConfig(config)
                        .connect(new Email.GotConnectCallback() {
                            @Override
                            public void success() {
                                handler.post(gotConnectCallback::success);
                            }

                            @Override
                            public void failure(String msg) {
                                handler.post(() -> gotConnectCallback.failure(msg));
                            }
                        })
        ).start();
    }
}
