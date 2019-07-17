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
         * @param gotReceiveCallback
         */
        public void receive(Email.GotReceiveCallback gotReceiveCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .receiveAll(EmailCore.ProtocolType.POP3, new Email.GotReceiveCallback() {
                                @Override
                                public void complete(int total) {
                                    handler.post(() -> gotReceiveCallback.complete(total));
                                }

                                @Override
                                public void receiving(Message message) {
                                    handler.post(() -> gotReceiveCallback.receiving(message));
                                }

                                @Override
                                public void received(List<Message> messageList) {
                                    handler.post(() -> gotReceiveCallback.received(messageList));
                                }

                                @Override
                                public void failure(String msg) {
                                    handler.post(() -> gotReceiveCallback.failure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取全部邮件总数
         * @return
         */
        public void getMessageCount(Email.GotCountCallback gotCountCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getMessageCount(EmailCore.ProtocolType.POP3, new Email.GotCountCallback() {
                                @Override
                                public void success(int total) {
                                    handler.post(() -> gotCountCallback.success(total));
                                }

                                @Override
                                public void failure(String msg) {
                                    handler.post(() -> gotCountCallback.failure(msg));
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
         * @param gotReceiveCallback
         */
        public void receive(Email.GotReceiveCallback gotReceiveCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .receiveAll(EmailCore.ProtocolType.IMAP, new Email.GotReceiveCallback() {
                                @Override
                                public void complete(int total) {
                                    handler.post(() -> gotReceiveCallback.complete(total));
                                }

                                @Override
                                public void receiving(Message message) {
                                    handler.post(() -> gotReceiveCallback.receiving(message));
                                }

                                @Override
                                public void received(List<Message> messageList) {
                                    handler.post(() -> gotReceiveCallback.received(messageList));
                                }

                                @Override
                                public void failure(String msg) {
                                    handler.post(() -> gotReceiveCallback.failure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取全部邮件总数
         * @return
         */
        public void getMessageCount(Email.GotCountCallback gotCountCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getMessageCount(EmailCore.ProtocolType.IMAP, new Email.GotCountCallback() {
                                @Override
                                public void success(int total) {
                                    handler.post(() -> gotCountCallback.success(total));
                                }

                                @Override
                                public void failure(String msg) {
                                    handler.post(() -> gotCountCallback.failure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取未读邮件总数
         * @return
         */
        public void getUnreadMessageCount(Email.GotCountCallback gotCountCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getUnreadMessageCount(new Email.GotCountCallback() {
                                @Override
                                public void success(int total) {
                                    handler.post(() -> gotCountCallback.success(total));
                                }

                                @Override
                                public void failure(String msg) {
                                    handler.post(() -> gotCountCallback.failure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取全部UID
         * @param gotUIDListCallback
         */
        public void getUIDList(Email.GotUIDListCallback gotUIDListCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getUIDList(new Email.GotUIDListCallback() {
                                @Override
                                public void success(long[] uidList) {
                                    handler.post(() -> gotUIDListCallback.success(uidList));
                                }

                                @Override
                                public void failure(String msg) {
                                    handler.post(() -> gotUIDListCallback.failure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取某条邮件信息
         * @param uid
         * @param gotMessageCallback
         */
        public void getMessage(long uid, Email.GotMessageCallback gotMessageCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                            .getMessage(uid, new Email.GotMessageCallback() {
                                @Override
                                public void success(Message message) {
                                    handler.post(() -> gotMessageCallback.success(message));
                                    gotMessageCallback.success(message);
                                }

                                @Override
                                public void failure(String msg) {
                                    handler.post(() -> gotMessageCallback.failure(msg));
                                }
                            })
            ).start();
        }

        /**
         * 获取多条邮件信息
         * @param uidList
         * @param gotMessageListCallback
         */
        public void getMessageList(long[] uidList, Email.GotMessageListCallback gotMessageListCallback) {
            new Thread(() ->
                    EmailCore.setConfig(config)
                    .getMessageList(uidList, new Email.GotMessageListCallback() {
                        @Override
                        public void success(List<Message> messageList) {
                            handler.post(() -> gotMessageListCallback.success(messageList));
                        }

                        @Override
                        public void failure(String msg) {
                            handler.post(() -> gotMessageListCallback.failure(msg));
                        }
                    })
            ).start();
        }
    }
}
