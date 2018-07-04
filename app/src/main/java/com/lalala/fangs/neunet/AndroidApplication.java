package com.lalala.fangs.neunet;

import android.app.Application;
import android.util.Log;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

/**
 * Created by FANGs on 2017/8/3.
 */

public class AndroidApplication extends Application {
    private static AndroidApplication instance;

    private static final String TAG = "AndroidApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(false);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e(TAG, "onFailure: "+s+"\n"+s1 );
            }
        });
    }

    public static AndroidApplication getInstance() {
        return instance;
    }

}
