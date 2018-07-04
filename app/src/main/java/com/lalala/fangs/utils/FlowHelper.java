package com.lalala.fangs.utils;

/**
 * Created by FANGs on 2017/8/2.
 */

public class FlowHelper {

    public static float flowStr2FloatKb(final String flow){
        float res;
        String f = flow.replaceAll(",","").toLowerCase();

        if(f.contains("g")){
            f = f.substring(0,f.length()-1);
            res = Float.valueOf(f);
            return res*1048576;
        }else if(f.contains("m")){
            f = f.substring(0,f.length()-1);
            res = Float.valueOf(f);
            return res*1024;
        }else if(f.contains("k")){
            f = f.substring(0,f.length()-1);
            res = Float.valueOf(f);
            return res;
        }else{
            return 0;
        }
    }

    public static float flowStr2FloatGb(final String flow){
        return flowStr2FloatKb(flow)/1048576;
    }


}
