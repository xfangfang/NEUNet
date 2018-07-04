package com.lalala.fangs.utils;

import android.util.SparseArray;

/**
 * Created by FANGs on 2017/7/28.
 * 登陆状态码
 */

public class State {
    public static final int NO_WIFI = 1;
    public static final int NOT_WIFI = 2;
    public static final int EMPTY_USERNAME = 3;
    public static final int EMPTY_PASSWORD = 4;
    public static final int WRONG_PASSWORD = 5;
    public static final int NOT_CONNECTED = 6;
    public static final int EXIT_SUCCESS = 7;
    public static final int EXIT_ERROR = 8;
    public static final int IS_OVERDUE = 9;
    public static final int WRONG_USERNAME = 10;
    public static final int LOGIN_ERROR = 11;
    public static final int USER_DIASBLE = 12;
    public static final int ALREADY_CONNECTED = 13;
    public static final int LOGIN_SUCCESS = 14;
    public static final int WROGNG_VERIFYCODE = 15;
    public static final int EMPTY_VERIFYCODE = 16;
    public static final int WRONG_USER_OR_PASSWORD = 17;
    public static final int CHANGE_PASSWORD_ERROR = 18;
    public static final int CHANGE_PASSWORD_SUCCESS = 19;
    public static final int OLD_PASSWORD_IS_EMPTY = 20;
    public static final int NEW_PASSWORD_IS_EMPTY = 21;
    public static final int OLD_PASSWORD_IS_TOO_SHORT = 22;
    public static final int OLD_PASSWORD_IS_TOO_LONG = 23;
    public static final int NEW_PASSWORD_IS_TOO_SHORT = 24;
    public static final int NEW_PASSWORD_IS_TOO_LONG = 25;
    public static final int NEW_PASSWORD_IS_NOT_THE_SAME = 26;
    public static final int NEW_PASSWORD_IS_THE_SAME_AS_OLD_ONE = 27;
    public static final int CHANGE_USER_STATE_SUCCESS = 28;
    public static final int CHANGE_USER_STATE_FAILED = 29;
    public static final int PC_LOGIN_SUCCESS = 30;
    public static final int ALL_EXIT_SUCCESS = 31;
    public static final int NOT_RESPONSE = 32;




    public static SparseArray<String> getStateMap(){
        SparseArray<String> res = new SparseArray<>();
        res.put(NO_WIFI, "没有\n连接WIFI");
        res.put(NOT_WIFI, "需要\n连接WIFI");
        res.put(NOT_CONNECTED, "似乎\n未曾连接");
        res.put(EMPTY_USERNAME, "空白的账号");
        res.put(EMPTY_PASSWORD, "空白的密码");
        res.put(EMPTY_VERIFYCODE, "空白的验证码");
        res.put(EXIT_SUCCESS, "OFFLINE");
        res.put(ALL_EXIT_SUCCESS, "ALL DEVICES\nOFFLINE");
        res.put(LOGIN_SUCCESS, "ONLINE");
        res.put(PC_LOGIN_SUCCESS, "PC\nONLINE");
        res.put(WRONG_PASSWORD, "密码\n错误");
        res.put(EXIT_ERROR, "退出\n错误");
        res.put(IS_OVERDUE, "欠费");
        res.put(WRONG_USERNAME, "没有\n这个用户");
        res.put(LOGIN_ERROR, "登录\n错误");
        res.put(USER_DIASBLE, "用户\n不可用");
        res.put(ALREADY_CONNECTED, "正在\n异地登陆");
        res.put(WROGNG_VERIFYCODE, "错误的验证码");
        res.put(WRONG_USER_OR_PASSWORD, "用户名或密码错误");
        res.put(CHANGE_PASSWORD_ERROR,"修改密码失败");
        res.put(CHANGE_PASSWORD_SUCCESS,"修改密码成功");
        res.put(OLD_PASSWORD_IS_EMPTY,"旧密码是空白的");
        res.put(NEW_PASSWORD_IS_EMPTY,"新密码是空白的");
        res.put(OLD_PASSWORD_IS_TOO_SHORT,"旧密码应该大于等于6位");
        res.put(OLD_PASSWORD_IS_TOO_LONG,"旧密码应该小于等于64位");
        res.put(NEW_PASSWORD_IS_TOO_SHORT,"新密码应该大于等于6位");
        res.put(NEW_PASSWORD_IS_TOO_LONG,"新密码应该小于等于64位");
        res.put(NEW_PASSWORD_IS_NOT_THE_SAME,"两次输入的密码不同");
        res.put(NEW_PASSWORD_IS_THE_SAME_AS_OLD_ONE,"新密码不应该与旧密码相同");
        res.put(CHANGE_USER_STATE_SUCCESS,"修改用户状态成功");
        res.put(CHANGE_USER_STATE_FAILED,"修改用户状态失败");
        res.put(NOT_RESPONSE,"服务器\n没理你");
        return res;
    }
}
