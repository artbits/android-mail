package com.github.artbits.androidmail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.widget.Toast;

import java.util.Date;

public class Utils {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd HH:mm");


    public static boolean isNullOrEmpty(Object... args) {
        for (Object o : args) {
            if (o == null) {
                return true;
            }
            if (o instanceof String && ((String) o).isEmpty()) {
                return true;
            }
        }
        return false;
    }


    public static void toast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


    public static String getDate(long time) {
        return format.format(new Date(time));
    }

}
