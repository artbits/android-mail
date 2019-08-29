package com.smailnet.eamil;

import android.os.Handler;
import android.os.Looper;

public final class ExamineService {

    private Handler handler;
    private EmailCore core;

    ExamineService() {
        handler = new Handler(Looper.getMainLooper());
        core = new EmailCore();
    }

    ExamineService(Email.Config config) {
        handler = new Handler(Looper.getMainLooper());
        core = new EmailCore(config);
    }

    /**
     * 连接邮件服务器检查
     * @param getConnectCallback
     */
    public void connect(Email.GetConnectCallback getConnectCallback) {
        new Thread(() ->
                core.connect(new Email.GetConnectCallback() {
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
