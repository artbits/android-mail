package com.github.artbit.androidmail.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.github.artbit.androidmail.R;
import com.github.artbit.androidmail.databinding.ActivityMainBinding;
import com.github.artbit.androidmail.store.Folder;
import com.github.artbit.androidmail.store.UserInfo;
import com.github.artbit.androidmail.Utils;
import com.github.artbit.mailkit.MailKit;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = setContentView(this, R.layout.activity_main);
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.write) {
            startActivity(new Intent(this, WriteActivity.class));
        }
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, ConfigActivity.class));
        }
        if (item.getItemId() == R.id.exit) {
            exit();
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {
        UserInfo userInfo = LitePal.findFirst(UserInfo.class);
        if (userInfo == null) {
            setToolbar(binding.toolbar, "Android-Mail", false);
            return;
        }
        setToolbar(binding.toolbar, userInfo.nickname, userInfo.account, false);

        List<Folder> folders = LitePal.findAll(Folder.class);
        if (folders == null || folders.size() == 0) {
            MailKit.Config config = userInfo.toConfig();
            MailKit.IMAP imap = new MailKit.IMAP(config);
            imap.getDefaultFolders(strings -> {
                strings.forEach(s -> new Folder(s).save());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
                binding.foldersListView.setAdapter(adapter);
            }, e -> Utils.toast(this, e.getMessage()));
        } else {
            List<String> strings = new ArrayList<>();
            folders.forEach(folder -> strings.add(folder.name));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
            binding.foldersListView.setAdapter(adapter);
        }

        binding.foldersListView.setOnItemClickListener((parent, view, position, id) -> {
            String folderName = parent.getAdapter().getItem(position).toString();
            Intent intent = new Intent(this, FolderActivity.class);
            intent.putExtra("folderName", folderName);
            startActivity(intent);
        });
    }


    private void exit() {
        new MessageDialog()
                .setTitle("退出帐户")
                .setMessage("退出帐户将会清除本地的帐户数据")
                .setNegativeButton("取消", (dialogInterface, integer) -> {})
                .setPositiveButton("退出", (dialogInterface, integer) -> {
                    LitePal.deleteDatabase("store");
                    startActivity(new Intent(this, ConfigActivity.class));
                    finish();
                }).show();
    }

}