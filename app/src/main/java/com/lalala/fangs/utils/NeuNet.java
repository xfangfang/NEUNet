package com.lalala.fangs.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.lalala.fangs.utils.State.ALL_EXIT_SUCCESS;
import static com.lalala.fangs.utils.State.ALREADY_CONNECTED;
import static com.lalala.fangs.utils.State.EMPTY_PASSWORD;
import static com.lalala.fangs.utils.State.EMPTY_USERNAME;
import static com.lalala.fangs.utils.State.EXIT_ERROR;
import static com.lalala.fangs.utils.State.EXIT_SUCCESS;
import static com.lalala.fangs.utils.State.IS_OVERDUE;
import static com.lalala.fangs.utils.State.LOGIN_ERROR;
import static com.lalala.fangs.utils.State.LOGIN_SUCCESS;
import static com.lalala.fangs.utils.State.NOT_CONNECTED;
import static com.lalala.fangs.utils.State.NOT_RESPONSE;
import static com.lalala.fangs.utils.State.NOT_WIFI;
import static com.lalala.fangs.utils.State.NO_WIFI;
import static com.lalala.fangs.utils.State.PC_LOGIN_SUCCESS;
import static com.lalala.fangs.utils.State.USER_DIASBLE;
import static com.lalala.fangs.utils.State.WRONG_PASSWORD;
import static com.lalala.fangs.utils.State.WRONG_USERNAME;

/**
 * Created by FANGs on 2017/2/23.
 */

public class NeuNet {
    private Context context;
    private OnLoginExitStateListener listener;
    private OnInforListener inforListener;
    private ClearableCookieJar cookieJar;


    public NeuNet(Context c) {
        this.context = c;
        cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
    }

    public void clearCookie() {
        cookieJar.clear();
    }

    public void exit(final String userStr, final String passwordStr, boolean all) {
        if (normalCheckDone(userStr, passwordStr)) {
            new exitTask(all).execute(userStr, passwordStr);
        }
    }

    public void login(final String userStr, final String passwordStr, String OS) {
        if (normalCheckDone(userStr, passwordStr)) {
            //            Windows NT 10.0
            //            Android
            new loginTask().execute(userStr, passwordStr, OS);
        }
    }

    public void getInfor() {
        if (netCheck()) {
            new getInforTask().execute();
        }
    }

    private static final String TAG = "NeuNet";

    private boolean normalCheckDone(final String userStr, final String passwordStr) {
        if (listener == null) {
            return false;
        }
        if (userStr.equals("")) {
            listener.getState(EMPTY_USERNAME);
            return false;
        } else if (passwordStr.equals("")) {
            listener.getState(EMPTY_PASSWORD);
            return false;
        }
        return netCheck();
    }

    public boolean netCheck() {
        ConnectivityManager coon = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = coon.getActiveNetworkInfo();
        if (info == null) {
            listener.getState(NO_WIFI);
            return false;
        } else if (info.getType() != ConnectivityManager.TYPE_WIFI) {
            listener.getState(NOT_WIFI);
            return false;
        }
        return true;
    }

    private class exitTask extends AsyncTask<String, Integer, Boolean> {

        String res;
        boolean all;

        exitTask(boolean all) {
            this.all = all;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String updateUrl = "https://ipgw.neu.edu.cn/include/auth_action.php";
            OkHttpClient client = new OkHttpClient().newBuilder().
                    sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                    .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                    .build();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("action", "logout")
                    .add("username", params[0])
                    .add("ajax", "1");
            if (all) {
                builder.add("password", params[1]);
            }
            FormBody body = builder.build();
            Request request = new Request.Builder().url(updateUrl).post(body).build();
            okhttp3.Response response;
            try {
                response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (res == null || !aBoolean) {
                listener.getState(EXIT_ERROR);
            } else if (res.contains("error")) {
                listener.getState(WRONG_PASSWORD);
            } else if (res.contains("未曾连接")) {
                listener.getState(NOT_CONNECTED);
            } else {
                if (all) {
                    listener.getState(ALL_EXIT_SUCCESS);
                } else {
                    listener.getState(EXIT_SUCCESS);
                }
            }
        }
    }

    private class loginTask extends AsyncTask<String, Integer, Boolean> {

        String res;
        String userName;
        String password;
        boolean isPc;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            userName = params[0];
            password = params[1];
            isPc = ! params[2].toLowerCase().contains("android");

            Log.e(TAG, "doInBackground: login user "+userName );

            // get lt and execution
            String eOneUrl = "https://pass.neu.edu.cn/tpass/login?service=https://ipgw.neu.edu.cn/srun_cas.php";
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                    .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                    .cookieJar(cookieJar)
                    .build();
            FormBody body = new FormBody.Builder().build();
            Request request = new Request.Builder()
                    .url(eOneUrl)
                    .addHeader("User-Agent", params[2])
                    .post(body)
                    .build();
            okhttp3.Response response;
            try {
                response = client.newCall(request).execute();
                res = response.body().string();
                try {
                    Document doc = Jsoup.parse(res);
                    String lt = doc.select("#lt").first().val();
                    String execution = doc.select("input[name='execution']").first().val();

                    Log.e(TAG,"lt: "+lt);
                    Log.e(TAG,"exec: "+execution);

                    // login
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("rsa", userName+password+lt)
                            .add("username",userName)
                            .add("password", password)
                            .add("lt", lt)
                            .add("ul", ""+userName.length())
                            .add("pl", ""+password.length())
                            .add("_eventId","submit")
                            .add("execution",execution);
                    body = builder.build();
                    request = new Request.Builder()
                            .url(eOneUrl)
                            .addHeader("User-Agent", params[2])
                            .post(body)
                            .build();
                    response = client.newCall(request).execute();
                    res = response.body().string();
                    return true;
                } catch (Exception e){
                    Log.e(TAG, "doInBackground: allready login" );
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.e(TAG, "onPostExecute: " + res);

            if (res == null) {
                listener.getState(LOGIN_ERROR);
            } else if (res.contains(userName)) {
                if(isPc){
                    listener.getState(PC_LOGIN_SUCCESS);
                }else {
                    listener.getState(LOGIN_SUCCESS);
                }
            } else if (res.contains("锁定")) {
                listener.getState(WRONG_PASSWORD);
            } else if (res.contains("E2616")) {
                listener.getState(IS_OVERDUE);
            } else if (res.contains("E2606")) {
                listener.getState(USER_DIASBLE);
            } else if (res.contains("E2620")) {
                listener.getState(ALREADY_CONNECTED);
            } else if(res.contains("Portal not response") || res.contains("访问被拒绝")){
                listener.getState(NOT_RESPONSE);
            }else {
                listener.getState(LOGIN_ERROR);
            }
        }
    }

    private class getInforTask extends AsyncTask<String, Integer, Boolean> {
        String res;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String k = String.valueOf((int) (10000 + Math.random() * (99999 - 10000 + 1)));
            String updateUrl = "https://ipgw.neu.edu.cn/include/auth_action.php";
            OkHttpClient client = new OkHttpClient().newBuilder().
                    sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                    .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                    .build();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("action", "get_online_info")
                    .add("k", k)
                    .add("key", k);
            FormBody body = builder.build();
            Request request = new Request.Builder().url(updateUrl).post(body).build();
            okhttp3.Response response;
            try {
                response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.e(TAG, "onPostExecute: info "+res );

            if (res == null) {
                inforListener.getInfor(null, null, null);
            } else if (res.equals("not_online")) {
                inforListener.getInfor(null, null, null);
            } else {
                String[] data = res.split(",");
                if (data.length != 6) {
                    inforListener.getInfor(null, null, null);
                } else {
                    Log.e(TAG, "onPostExecute: 流量" + data[0]);
                    double flowTemp = Double.parseDouble(data[0]);
                    String money = data[2];
                    String flow = String.valueOf((int) (flowTemp / 1048576));
                    String ip = data[5];
                    inforListener.getInfor(flow, money, ip);
                }
            }
        }
    }

    public void setOnLoginExitStateListener(OnLoginExitStateListener listener) {
        this.listener = listener;
    }

    public void setOnInforListener(OnInforListener listener) {
        this.inforListener = listener;
    }

    public interface OnLoginExitStateListener {
        void getState(int state);
    }

    public interface OnInforListener {
        void getInfor(String flow, String money, String ip);
    }

}
