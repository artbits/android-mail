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

import android.text.TextUtils;

/**
 * 检查host和port是否为空
 */
public class ConfigCheckUtil {

    /**
     * 判断host和port是否都不为空
     * @param host
     * @param port
     * @return
     */
    public static boolean getResult(String host, String port){
        //return (host != null) && (port != null);
        return !TextUtils.isEmpty(host) && !TextUtils.isEmpty(port);
    }

}
