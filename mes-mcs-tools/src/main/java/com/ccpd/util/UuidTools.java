package com.ccpd.util;

import java.util.UUID;

/**
 * Created by jondai on 2017/11/22.
 */
public class UuidTools {

    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-","");
    }
}
