package com.zhenhappy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangxd on 2016/4/6.
 */
public class EmailPattern {

    public EmailPattern() {

    }

    public boolean isEmailPattern(String str) {
        boolean flag = false;
        Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
        Matcher m = p.matcher(str);
        flag = m.matches();
        return flag;
    }

    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
