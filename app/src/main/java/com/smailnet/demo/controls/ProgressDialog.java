package com.smailnet.demo.controls;

import android.app.Activity;

public class ProgressDialog {

    private android.app.ProgressDialog progressDialog;

    ProgressDialog(Activity activity) {
        progressDialog = new android.app.ProgressDialog(activity);
    }

    public ProgressDialog setMessage(String message) {
        progressDialog.setMessage(message);
        return this;
    }

    public ProgressDialog setCancelable(boolean cancelable) {
        progressDialog.setCancelable(cancelable);
        return this;
    }

    public void show(GetCallback getCallback) {
        progressDialog.show();
        getCallback.onFinish(progressDialog);
    }

    public interface GetCallback {
        void onFinish(android.app.ProgressDialog progressDialog);
    }
}
