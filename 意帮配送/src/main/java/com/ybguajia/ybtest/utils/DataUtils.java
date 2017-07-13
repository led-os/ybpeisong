package com.ybguajia.ybtest.utils;

import java.util.Calendar;

/**
 * Created by yb on 2017/4/1.
 */

public class DataUtils {
    public static String md5ByDay() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        String allMd5 = CommonUtils.getMd5(String.valueOf(day));
        return allMd5.substring(allMd5.length() - 6, allMd5.length()).toUpperCase();
    }
}
