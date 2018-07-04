package com.lalala.fangs.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lalala.fangs.utils.SharedPreferencesHelper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import static android.R.attr.name;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by FANGs on 2017/7/28.
 */

public class User implements Serializable, Comparable<User>{
    private String username;
    private String password;
    private int year;
    private long saveTime;
    private static final String USERINFORMATION = "USERINFORMATION";
    private static final String RECENTLOGIN = "RECENTLOGIN";

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        generateYear();
    }

    public User(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.year = user.getYear();
    }


    /**
     * 生成用户所属年级
     */
    private void generateYear(){
        try{
            year = Integer.parseInt(username.substring(0,4));
        }catch (Exception e){
            e.printStackTrace();
            year = 0;
        }
    }

    /**
     * 将本账号保存到设备中
     * @param context
     */
    public void save(Context context){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        saveTime = now.getTime();
        Log.e(TAG, "save: "+saveTime );
        SharedPreferencesHelper.saveSerializableObject(context,USERINFORMATION,username,this);
    }

    /**
     * 储存最近登陆的账号
     * @param context
     */
    public void saveRecentLogin(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(RECENTLOGIN, Context.MODE_PRIVATE).edit();
        editor.putString("user",this.username);
        editor.commit();

    }

    public static User getRecentLoginUser(Context context){
        SharedPreferences sp = context.getSharedPreferences(RECENTLOGIN, Context.MODE_PRIVATE);
        String userName = sp.getString("user","");
        ArrayList<User> userList = loadAll(context);
        for(User i:userList){
            if(i.getUsername().equals(userName)){
                return new User(i);
            }
        }
        return null;
    }


    /**
     * 从设备删除本账号
     * @param context
     */
    public void delete(Context context){
        SharedPreferencesHelper.removeObject(context,USERINFORMATION,username);
    }


    /**
     * 从设备中取出所有保存的账号
     * @return 返回用户列表
     */
    public static ArrayList<User> loadAll(Context context){
        ArrayList<User> users = new ArrayList<>();
        for(Map.Entry<String,?> entry : SharedPreferencesHelper.getAll(context,USERINFORMATION)){
            User user =
                    (User) SharedPreferencesHelper.getSerializableObject(
                            context,
                            USERINFORMATION,
                            entry.getKey() );
            if (user != null){
                users.add(user);
            }
        }
        return users;
    }

    public int getYear() {
        return year;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull User o) {
        return (int) (o.getSaveTime() - this.saveTime);
    }
}
