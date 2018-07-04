package com.lalala.fangs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FANGs on 2017/7/31.
 */

public class TimeHelper {

    /**
     * 时间戳转时间
     * @param time 输入时间戳
     * @return 输出格式 2016-09-07 18:16:02
     */
    public static String unixTime2time(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }

    /**
     * 时间转时间戳
     * @param time 输入格式 2016-09-07 18:16:02
     * @return 对应的时间戳
     */
    public static String time2unixTime(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }
}
