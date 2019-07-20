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
     * @param getConnectCallback
     */
    public void connect(Email.GetConnectCallback getConnectCallback) {
        new Thread(() ->
                EmailCore.setConfig(config)
                        .connect(new Email.GetConnectCallback() {
                            @Override
                            public void onSuccess() {
                                handler.post(getConnectCallback::onSuccess);
                            }

                            @Override
                            public void onFailure(String msg) {
                                handler.post(() -> getConnectCallback.onFailure(msg));
                            }
                        })
        ).start();
    }
}
