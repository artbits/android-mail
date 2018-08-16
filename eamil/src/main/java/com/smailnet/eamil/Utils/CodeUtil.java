package com.smailnet.eamil.Utils;

import javax.mail.internet.MimeUtility;

public class CodeUtil {

    public static String conver(String str) {
        try {
            if (str.startsWith("=?GB") || str.startsWith("=?gb")
                    || str.startsWith("=?UTF") || str.startsWith("=?utf")) {
                str = MimeUtility.decodeText(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}
