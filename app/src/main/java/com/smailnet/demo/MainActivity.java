package com.smailnet.demo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.smailnet.eamil.Email;
import com.smailnet.eamil.Message;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //配置邮件服务器参数
        Email.Config config = new Email.Config()
                .setMailType(Email.MailType.FOXMAIL)
                .setAccount("from@qq.com")              //发送者的邮箱
                .setPassword("password");               //发送者的邮箱密码或授权码

        //发送邮件
        findViewById(R.id.send).setOnClickListener(view ->
                Email.getSendService(config)
                        .setTo("to@qq.com")             //接收者的邮箱
                        .setNickname("测试员")
                        .setSubject("这是一封测试邮件")
                        .setText("你好")
                        .send(new Email.GotSendCallback() {
                            @Override
                            public void success() {
                                Utils.showToast(MainActivity.this, "发送成功！");
                            }

                            @Override
                            public void failure(String msg) {
                                Utils.showToast(MainActivity.this, "错误：" + msg);
                                Log.i(TAG, "错误：" + msg);
                            }
                        })
        );

        //使用POP3协议接收邮件
        findViewById(R.id.pop3_rec).setOnClickListener(view ->
                Email.getReceiveService(config)
                        .getPOP3Service()
                        .receive(new Email.GotReceiveCallback() {
                            @Override
                            public void complete(int total) {
                                Utils.showToast(MainActivity.this, "邮件总数：" + total);
                            }

                            @Override
                            public void receiving(Message message) {
                                //每读取一封邮件即回调该封邮件的结果
                                Log.i(TAG, "标题：" + message.getSubject() + " 时间：" + message.getDate());
                            }

                            @Override
                            public void received(List<Message> messageList) {
                                //全部邮件读取读取完毕后，再一次性回调全部结果
                            }

                            @Override
                            public void failure(String msg) {
                                Utils.showToast(MainActivity.this, "错误：" + msg);
                                Log.i(TAG, "错误：" + msg);
                            }
                        })
        );

        //使用POP3协议获取全部邮件的数量
        findViewById(R.id.pop3_rec_count).setOnClickListener(view ->
                Email.getReceiveService(config)
                        .getPOP3Service()
                        .getMessageCount(new Email.GotCountCallback() {
                            @Override
                            public void success(int total) {
                                Utils.showToast(MainActivity.this, "全部邮件数量：" + total);
                            }

                            @Override
                            public void failure(String msg) {
                                Utils.showToast(MainActivity.this, "错误：" + msg);
                            }
                        })
        );

        //使用IMAP协议接收邮件
        findViewById(R.id.imap_rec).setOnClickListener(view ->
                Email.getReceiveService(config)
                        .getIMAPService()
                        .receive(new Email.GotReceiveCallback() {
                            @Override
                            public void complete(int total) {
                                Utils.showToast(MainActivity.this, "邮件总数：" + total);
                            }

                            @Override
                            public void receiving(Message message) {
                                //每读取一封邮件即回调该封邮件的结果
                                Log.i(TAG, "标题：" + message.getSubject() + " 时间：" + message.getDate());
                            }

                            @Override
                            public void received(List<Message> messageList) {
                                //全部邮件读取读取完毕后，再一次性回调全部结果
                            }

                            @Override
                            public void failure(String msg) {
                                Utils.showToast(MainActivity.this, "错误：" + msg);
                                Log.i(TAG, "错误：" + msg);
                            }
                        })
        );

        //使用IMAP协议获取全部邮件的数量
        findViewById(R.id.imap_rec_count).setOnClickListener(view ->
                Email.getReceiveService(config)
                        .getIMAPService()
                        .getMessageCount(new Email.GotCountCallback() {
                            @Override
                            public void success(int total) {
                                Utils.showToast(MainActivity.this, "全部邮件数量：" + total);
                            }

                            @Override
                            public void failure(String msg) {
                                Utils.showToast(MainActivity.this, "错误：" + msg);
                            }
                        })
        );

        //使用IMAP协议获取未读邮件的数量
        findViewById(R.id.imap_rec_unread_count).setOnClickListener(view ->
                Email.getReceiveService(config)
                        .getIMAPService()
                        .getUnreadMessageCount(new Email.GotCountCallback() {
                            @Override
                            public void success(int total) {
                                Utils.showToast(MainActivity.this, "未读邮件数量：" + total);
                            }

                            @Override
                            public void failure(String msg) {
                                Utils.showToast(MainActivity.this, "错误：" + msg);
                            }
                        })
        );

        //使用IMAP协议获取全部邮件的UID
        findViewById(R.id.imap_rec_uid).setOnClickListener(view ->
                Email.getReceiveService(config)
                        .getIMAPService()
                        .getUIDList(new Email.GotUIDListCallback() {
                            @Override
                            public void success(long[] uidList) {
                                for (long i : uidList) {
                                    Log.i(TAG, "UID：" + i);
                                }
                            }

                            @Override
                            public void failure(String msg) {
                                Utils.showToast(MainActivity.this, "错误：" + msg);
                            }
                        })
        );
    }

    static class Utils {
        public static void showToast(Context context, String s) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }
    }
}
