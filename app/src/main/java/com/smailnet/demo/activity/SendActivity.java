package com.smailnet.demo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.smailnet.demo.BaseActivity;
import com.smailnet.demo.EmailApplication;
import com.smailnet.demo.R;
import com.smailnet.demo.controls.Controls;
import com.smailnet.demo.controls.TitleBar;
import com.smailnet.emailkit.Draft;
import com.smailnet.emailkit.EmailKit;
import com.smailnet.microkv.MicroKV;

public class SendActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
    }

    @Override
    protected void initView() {
        Controls.getTitleBar()
                .display(this, "写邮件", R.drawable.send, new TitleBar.OnMultipleClickListener() {
                    @Override
                    public void onBack() {
                        finish();
                    }

                    @Override
                    public void onFunction() {
                        sendMsg();
                    }
                });
    }

    /**
     * 发送邮件
     */
    public void sendMsg() {
        String address = ((EditText) findViewById(R.id.activity_send_address_et)).getText().toString();
        String subject = ((EditText) findViewById(R.id.activity_send_subject_et)).getText().toString();
        String content = ((EditText) findViewById(R.id.activity_send_content_et)).getText().toString();

        if (TextUtils.isEmpty(address)) {
            Controls.toast("收件人地址不能为空");
            return;
        }

        MicroKV kv = MicroKV.customize("config", true);

        Draft draft = new Draft()
                .setNickname(kv.getString("nickname"))
                .setTo(address)
                .setSubject(subject)
                .setText(content);

        Controls.getProgressDialog(this)
                .setCancelable(false)
                .setMessage("发送中...")
                .show(progressDialog -> EmailKit.useSMTPService(EmailApplication.getConfig())
                        .send(draft, new EmailKit.GetSendCallback() {
                            @Override
                            public void onSuccess() {
                                Controls.toast("已发送");
                                progressDialog.dismiss();
                                finish();
                            }

                            @Override
                            public void onFailure(String errMsg) {
                                Controls.toast(errMsg);
                                progressDialog.dismiss();
                            }
                        }));

    }

}
