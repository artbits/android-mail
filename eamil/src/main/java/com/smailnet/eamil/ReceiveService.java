package com.smailnet.eamil;

import android.os.Handler;
import android.os.Looper;

import com.smailnet.eamil.entity.Message;

import java.util.List;

public final class ReceiveService {

    private static Handler handler;
    private static EmailCore core;

    ReceiveService() {
        handler = new Handler(Looper.getMainLooper());
        core = new EmailCore();
    }

    ReceiveService(Email.Config config) {
        handler = new Handler(Looper.getMainLooper());
        core = new EmailCore(config);
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

        /**
         * 使用POP3协议接收邮件
         * @param getReceiveCallback
         */
        public void receive(Email.GetReceiveCallback getReceiveCallback) {
            new Thread(() ->
                    core.receive(EmailCore.Protocol.POP3, new Email.GetReceiveCallback() {
                        @Override
                        public void receiving(Message message, int index, int total) {
                            handler.post(() -> getReceiveCallback.receiving(message, index, total));
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
                    core.getMessageCount(EmailCore.Protocol.POP3, new Email.GetCountCallback() {
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

        /**
         * 使用IMAP协议接收邮件
         * @param getReceiveCallback
         */
        public void receive(Email.GetReceiveCallback getReceiveCallback) {
            new Thread(() ->
                    core.receive(EmailCore.Protocol.IMAP, new Email.GetReceiveCallback() {
                        @Override
                        public void receiving(Message message, int index, int total) {
                            handler.post(() -> getReceiveCallback.receiving(message, index, total));
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
         * 使用IMAP协议快速接收服务器上的邮件，不解析邮件内容
         * @param getReceiveCallback
         */
        public void fastReceive(Email.GetReceiveCallback getReceiveCallback) {
            new Thread(() ->
                    core.fastReceive(new Email.GetReceiveCallback() {
                        @Override
                        public void receiving(Message message, int index, int total) {
                            handler.post(() -> getReceiveCallback.receiving(message, index, total));
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
                    core.getMessageCount(EmailCore.Protocol.IMAP, new Email.GetCountCallback() {
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
                    core.getUnreadMessageCount(new Email.GetCountCallback() {
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
         * 同步消息
         * @param originalUidList
         * @param getSyncMessageCallback
         */
        public void syncMessage(long[] originalUidList, Email.GetSyncMessageCallback getSyncMessageCallback) {
            new Thread(() ->
                    core.syncMessage(originalUidList, new Email.GetSyncMessageCallback() {
                        @Override
                        public void onSuccess(List<Message> messageList, long[] deleteUidList) {
                            handler.post(() -> getSyncMessageCallback.onSuccess(messageList, deleteUidList));
                        }

                        @Override
                        public void onFailure(String msg) {
                            handler.post(() -> getSyncMessageCallback.onFailure(msg));
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
                    core.getUIDList(new Email.GetUIDListCallback() {
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
                    core.getMessage(uid, new Email.GetMessageCallback() {
                        @Override
                        public void onSuccess(Message message) {
                            handler.post(() -> getMessageCallback.onSuccess(message));
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
                    core.getMessageList(uidList, new Email.GetMessageListCallback() {
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

        /**
         * 标记消息
         * @param uid
         * @param flag
         * @param getFlagCallback
         */
        private void setFlagMessage(long uid, int flag, Email.GetFlagCallback getFlagCallback) {
            new Thread(() ->
                    core.setFlagMessage(uid, flag, new Email.GetFlagCallback() {
                        @Override
                        public void onSuccess() {
                            handler.post(getFlagCallback::onSuccess);
                        }

                        @Override
                        public void onFailure(String msg) {
                            handler.post(() -> getFlagCallback.onFailure(msg));
                        }
                    })
            ).start();
        }

    }

}
