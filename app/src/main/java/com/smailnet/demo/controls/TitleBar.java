package com.smailnet.demo.controls;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smailnet.demo.R;

public class TitleBar {

    private ImageView title_bar_back_iv;
    private TextView title_bar_title_tv;
    private ImageView title_bar_function_iv;

    public interface OnSingleClickListener{
        void onFunction();
    }

    public interface OnMultipleClickListener{
        void onBack();
        void onFunction();
    }

    private void initView(final Activity activity){
        title_bar_back_iv = activity.findViewById(R.id.title_bar_back_iv);
        title_bar_function_iv = activity.findViewById(R.id.title_bar_function_iv);
        title_bar_title_tv = activity.findViewById(R.id.title_bar_title_tv);
    }

    public void display(Activity activity, String title, int imageResource, OnSingleClickListener listener) {
        initView(activity);
        title_bar_title_tv.setText(title);
        title_bar_function_iv.setImageResource(imageResource);
        title_bar_back_iv.setVisibility(View.GONE);
        title_bar_function_iv.setOnClickListener(v -> listener.onFunction());
    }

    public void display(Activity activity, String title, int imageResource, OnMultipleClickListener listener) {
        initView(activity);
        title_bar_title_tv.setText(title);
        title_bar_function_iv.setImageResource(imageResource);
        title_bar_back_iv.setOnClickListener(v -> listener.onBack());
        title_bar_function_iv.setOnClickListener(v -> listener.onFunction());
    }

    public void display(Activity activity, String title) {
        initView(activity);
        title_bar_title_tv.setText(title);
        title_bar_function_iv.setVisibility(View.GONE);
        title_bar_back_iv.setOnClickListener(v -> activity.finish());
    }

}
