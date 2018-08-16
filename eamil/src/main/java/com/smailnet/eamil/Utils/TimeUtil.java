package com.smailnet.eamil.Utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    @SuppressLint("SimpleDateFormat")
    public static String getDate(Date date){
        if (date != null){
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        }else {
            return null;
        }
    }
}
