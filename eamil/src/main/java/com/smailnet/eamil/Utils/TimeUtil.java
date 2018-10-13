/*
 * Copyright 2018 Lake Zhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smailnet.eamil.Utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间格式转换工具
 */
public class TimeUtil {

    /**
     * 时间格式转换方法，格式为 yyyy-MM-dd
     * @param date
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(Date date){
        if (date != null){
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        }else {
            return null;
        }
    }
}
