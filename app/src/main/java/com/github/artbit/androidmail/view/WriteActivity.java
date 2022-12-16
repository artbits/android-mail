package com.github.artbit.androidmail.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.artbit.androidmail.R;
import com.github.artbit.androidmail.databinding.ActivityWriteBinding;
import com.github.artbit.androidmail.store.UserInfo;
import com.github.artbit.androidmail.Utils;
import com.github.artbit.mailkit.MailKit;

import org.litepal.LitePal;

public class WriteActivity extends BaseActivity {

    private ActivityWriteBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = setContentView(this, R.layout.activity_write);
        setToolbar(binding.toolbar, "写邮件", true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.send) {
            sendMail();
        }
        return super.onOptionsItemSelected(item);
    }


    private void sendMail() {
        String to = binding.addressText.getText().toString();
        String subject = binding.subjectText.getText().toString();
        String content = binding.contentText.getText().toString();
        if (Utils.isNullOrEmpty(to, subject, content)) {
            Utils.toast(this, "收件人地址、邮件主题或内容不能为空");
            return;
        }

        UserInfo userInfo = LitePal.findFirst(UserInfo.class);
        if (userInfo == null) {
            Utils.toast(this, "服务器配置异常，请重试");
            return;
        }

        LoadingDialog dialog = new LoadingDialog().setTipWord("发送中...");
        dialog.show();

        MailKit.Config config = userInfo.toConfig();
        MailKit.SMTP smtp = new MailKit.SMTP(config);
        smtp.send(new MailKit.Draft(d -> {
            d.to = new String[]{to};
            d.subject = subject;
            d.text = content;
        }), () -> {
            dialog.dismiss();
            Utils.toast(this, "发送成功");
            finish();
        }, e -> {
            dialog.dismiss();
            Utils.toast(this, e.getMessage());
        });
    }

}