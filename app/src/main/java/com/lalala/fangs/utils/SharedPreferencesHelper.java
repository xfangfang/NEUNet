package com.lalala.fangs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;



/**
 * Created by FANGs on 2017/7/28.
 * 序列化保存用户信息的工具类
 */

public class SharedPreferencesHelper {

    /**
     * 保存序列化对象到本地
     *
     * @param context 上下文
     * @param fileName SP本地存储路径
     * @param key 保存的key
     * @param object 保存的对象
     */
    public static void saveSerializableObject(Context context, String fileName, String key, Object object){
        try {
            SharedPreferences.Editor spEdit = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(object);//将对象序列化写入byte缓存
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组

            spEdit.putString(key, bytesToHexString);
            spEdit.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray 输入字节数组
     * @return 返回字符串
     */
    private static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & aBArray);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 从本地反序列化获取对象
     *
     * @param context 上下文
     * @param fileName SP本地存储路径
     * @param key 保存的key
     * @return 返回对象
     */
    public static Object getSerializableObject(Context context, String fileName, String key){
        try {
            SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            if (sp.contains(key)) {
                String string = sp.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    return is.readObject();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * desc:将16进制的数据转为数组
     *
     * @param data 输入的字符串数据
     * @return 返回字节数组
     */
    private static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); //两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   // 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; // A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); //两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); // 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; // A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }


    /**
     * 取所有保存的内容
     * @param context 上下文信息
     * @param fileName 文件名
     * @return 返回保存的Key集合
     */
    public static Set<? extends Map.Entry<String, ?>> getAll(Context context, String fileName){
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getAll().entrySet();
    }

    /**
     * 移除保存的某个对象
     * @param context 上下文信息
     * @param fileName 文件名
     * @param key 对象储存的Key
     */
    public static void removeObject(Context context, String fileName,String key){
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }


}
