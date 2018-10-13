package com.smailnet.demo;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smailnet.eamil.Callback.GetReceiveCallback;
import com.smailnet.eamil.EmailReceiveClient;
import com.smailnet.eamil.EmailSendClient;
import com.smailnet.eamil.Callback.GetSendCallback;
import com.smailnet.eamil.EmailMessage;
import com.smailnet.islands.Interface.OnRunningListener;
import com.smailnet.islands.Islands;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText address_editText;
    private EditText title_editText;
    private EditText text_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView(){
        address_editText = findViewById(R.id.address_editText);
        title_editText = findViewById(R.id.title_editText);
        text_editText = findViewById(R.id.text_editText);
        Button button = findViewById(R.id.send);
        Button button1 = findViewById(R.id.receive);
        Button button2 = findViewById(R.id.receive2);

        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    /**
     * 发送邮件
     */
    private void sendMessage(){
        EmailSendClient emailSendClient = new EmailSendClient(EmailApp.emailConfig());
        emailSendClient
                .setTo(address_editText.getText().toString())                //收件人的邮箱地址
                .setNickname("我的小可爱")                                    //发件人昵称
                .setSubject(title_editText.getText().toString())             //邮件标题
                .setContent(text_editText.getText().toString())              //邮件正文
                .sendAsyn(this, new GetSendCallback() {
                    @Override
                    public void sendSuccess() {
                        Toast.makeText(MainActivity.this, "已发送", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void sendFailure(String errorMsg) {
                        Islands.ordinaryDialog(MainActivity.this)
                                .setText(null, "发送失败 ：" + errorMsg)
                                .setButton("关闭", null, null)
                                .click().show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                sendMessage();
                break;
            case R.id.receive:
                /**
                 * 获取邮件
                 */
                Islands.circularProgress(this)
                        .setCancelable(false)
                        .setMessage("同步中...")
                        .show()
                        .run(new OnRunningListener() {
                            @Override
                            public void onRunning(final ProgressDialog progressDialog) {
                                EmailReceiveClient emailReceiveClient = new EmailReceiveClient(EmailApp.emailConfig());
                                emailReceiveClient
                                        .popReceiveAsyn(MainActivity.this, new GetReceiveCallback() {
                                            @Override
                                            public void gainSuccess(List<EmailMessage> messageList, int count) {
                                                progressDialog.dismiss();
                                                Log.i("oversee", "邮件总数：" + count + " 标题：" +  messageList.get(0).getSubject());
                                            }

                                            @Override
                                            public void gainFailure(String errorMsg) {
                                                progressDialog.dismiss();
                                                Log.e("oversee", "错误日志：" + errorMsg);
                                            }
                                        });
                            }
                        });
                break;
            case R.id.receive2:
                Islands.circularProgress(this)
                        .setCancelable(false)
                        .setMessage("同步中...")
                        .show()
                        .run(new OnRunningListener() {
                            @Override
                            public void onRunning(final ProgressDialog progressDialog) {
                                EmailReceiveClient emailReceiveClient = new EmailReceiveClient(EmailApp.emailConfig());
                                emailReceiveClient
                                        .imapReceiveAsyn(MainActivity.this, new GetReceiveCallback() {
                                            @Override
                                            public void gainSuccess(List<EmailMessage> messageList, int count) {
                                                progressDialog.dismiss();
                                                Log.i("oversee", "邮件总数：" + count + " 标题：" +  messageList.get(0).getSubject());
                                            }

                                            @Override
                                            public void gainFailure(String errorMsg) {
                                                progressDialog.dismiss();
                                                Log.e("oversee", "错误日志：" + errorMsg);
                                            }
                                        });
                            }
                        });
                break;
        }
    }
}
