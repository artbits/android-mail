package com.smailnet.eamil;

import android.os.Handler;
import android.os.Looper;

import java.util.List;

public final class ReceiveService {

    private static Email.Config config;

    ReceiveService(Email.Config config) {
        ReceiveService.config = config;
    }

    /**
     * 获取POP3服务
     * @return
     */
    public POP3Service getPOP3Service() {
        return new POP3Service();
    }

    /**
     * 获取IMAP服务
     * @return
     */
    public IMAPService getIMAPService() {
        return new IMAPService();
    }

    /**
     * POP3服务
     */
    public static class POP3Service {

        private Handler handler;

        POP3Service() {
            handler = new Handler(Looper.getMainLooper());
        }

        /**
         * 使用POP3协议接收邮件
         * @param getReceiveCallback
         */
        public void receive(Email.GetReceiveCallback getReceiveCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .receiveAll(EmailCore.Protocol.POP3, new Email.GetReceiveCallback() {
                                @Override
                                public void receiving(Message message) {
                                    handler.post(() -> getReceiveCallback.receiving(message));
                                }

                                @Override
                                public void onFinish(List<Message> messageList) {
                                    handler.post(() -> getReceiveCallback.onFinish(messageList));
                                }

                                @Override
                                public void onFailure(String msg) {
                                    handler.post(() -> getReceiveCallback.onFailure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取全部邮件总数
         * @param getCountCallback
         */
        public void getMessageCount(Email.GetCountCallback getCountCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getMessageCount(EmailCore.Protocol.POP3, new Email.GetCountCallback() {
                                @Override
                                public void onSuccess(int total) {
                                    handler.post(() -> getCountCallback.onSuccess(total));
                                }

                                @Override
                                public void onFailure(String msg) {
                                    handler.post(() -> getCountCallback.onFailure(msg));
                                }
                            })
            ).start();
        }

    }

    /**
     * IMAP服务
     */
    public static class IMAPService {

        private Handler handler;

        IMAPService() {
            handler = new Handler(Looper.getMainLooper());
        }

        /**
         * 使用IMAP协议接收邮件
         * @param getReceiveCallback
         */
        public void receive(Email.GetReceiveCallback getReceiveCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .receiveAll(EmailCore.Protocol.IMAP, new Email.GetReceiveCallback() {
                                @Override
                                public void receiving(Message message) {
                                    handler.post(() -> getReceiveCallback.receiving(message));
                                }

                                @Override
                                public void onFinish(List<Message> messageList) {
                                    handler.post(() -> getReceiveCallback.onFinish(messageList));
                                }

                                @Override
                                public void onFailure(String msg) {
                                    handler.post(() -> getReceiveCallback.onFailure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取全部邮件总数
         * @param getCountCallback
         */
        public void getMessageCount(Email.GetCountCallback getCountCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getMessageCount(EmailCore.Protocol.IMAP, new Email.GetCountCallback() {
                                @Override
                                public void onSuccess(int total) {
                                    handler.post(() -> getCountCallback.onSuccess(total));
                                }

                                @Override
                                public void onFailure(String msg) {
                                    handler.post(() -> getCountCallback.onFailure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取未读邮件总数
         * @param getCountCallback
         */
        public void getUnreadMessageCount(Email.GetCountCallback getCountCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getUnreadMessageCount(new Email.GetCountCallback() {
                                @Override
                                public void onSuccess(int total) {
                                    handler.post(() -> getCountCallback.onSuccess(total));
                                }

                                @Override
                                public void onFailure(String msg) {
                                    handler.post(() -> getCountCallback.onFailure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取全部UID
         * @param getUIDListCallback
         */
        public void getUIDList(Email.GetUIDListCallback getUIDListCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getUIDList(new Email.GetUIDListCallback() {
                                @Override
                                public void onSuccess(long[] uidList) {
                                    handler.post(() -> getUIDListCallback.onSuccess(uidList));
                                }

                                @Override
                                public void onFailure(String msg) {
                                    handler.post(() -> getUIDListCallback.onFailure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取某条邮件信息
         * @param uid
         * @param getMessageCallback
         */
        public void getMessage(long uid, Email.GetMessageCallback getMessageCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getMessage(uid, new Email.GetMessageCallback() {
                                @Override
                                public void onSuccess(Message message) {
                                    handler.post(() -> getMessageCallback.onSuccess(message));
                                    getMessageCallback.onSuccess(message);
                                }

                                @Override
                                public void onFailure(String msg) {
                                    handler.post(() -> getMessageCallback.onFailure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取多条邮件信息
         * @param uidList
         * @param getMessageListCallback
         */
        public void getMessageList(long[] uidList, Email.GetMessageListCallback getMessageListCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                    .getMessageList(uidList, new Email.GetMessageListCallback() {
                        @Override
                        public void onSuccess(List<Message> messageList) {
                            handler.post(() -> getMessageListCallback.onSuccess(messageList));
                        }

                        @Override
                        public void onFailure(String msg) {
                            handler.post(() -> getMessageListCallback.onFailure(msg));
                        }
                    })
            ).start();
        }

    }
}
