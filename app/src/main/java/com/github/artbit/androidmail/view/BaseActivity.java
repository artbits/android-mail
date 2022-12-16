package com.github.artbit.androidmail.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.util.Objects;
import java.util.function.BiConsumer;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public <T extends ViewDataBinding> T setContentView(@NonNull Activity activity, int layoutId) {
        return DataBindingUtil.setContentView(activity, layoutId);
    }


    public void setToolbar(Toolbar toolbar, boolean showBackButton) {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
        getSupportActionBar().setHomeButtonEnabled(showBackButton);
    }


    public void setToolbar(Toolbar toolbar, String title, boolean showBackButton) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
        getSupportActionBar().setHomeButtonEnabled(showBackButton);
    }


    public void setToolbar(Toolbar toolbar, String title, String subtitle, boolean showBackButton) {
        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
        getSupportActionBar().setHomeButtonEnabled(showBackButton);
    }


    public class LoadingDialog {

        private final ProgressDialog dialog;

        public LoadingDialog() {
            dialog = new ProgressDialog(BaseActivity.this);
            dialog.setCancelable(false);
        }

        public LoadingDialog setTipWord(String s) {
            dialog.setMessage(s);
            return this;
        }

        public void show() {
            dialog.show();
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager manager = BaseActivity.this.getWindowManager();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams params = Objects.requireNonNull(dialog.getWindow()).getAttributes();
            params.width = (int) (dm.widthPixels * 0.75);
            params.dimAmount = 0.4f;
            dialog.getWindow().setAttributes(params);
        }

        public void dismiss() {
            dialog.dismiss();
        }

    }


    public class MessageDialog {

        private final AlertDialog.Builder builder;

        public MessageDialog() {
            builder = new AlertDialog.Builder(BaseActivity.this);
        }

        public MessageDialog setTitle(String s) {
            builder.setTitle(s);
            return this;
        }

        public MessageDialog setMessage(String s) {
            builder.setMessage(s);
            return this;
        }

        public MessageDialog setPositiveButton(String s, BiConsumer<DialogInterface, Integer> consumer) {
            builder.setPositiveButton(s, consumer::accept);
            return this;
        }

        public MessageDialog setNegativeButton(String s, BiConsumer<DialogInterface, Integer> consumer) {
            builder.setNegativeButton(s, consumer::accept);
            return this;
        }

        public void show() {
            AlertDialog dialog = builder.create();
            dialog.show();
            WindowManager.LayoutParams params = Objects.requireNonNull(dialog.getWindow()).getAttributes();
            params.dimAmount = 0.4f;
            dialog.getWindow().setAttributes(params);
        }

    }

}