package com.lalala.fangs.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.lalala.fangs.data.User;
import com.lalala.fangs.data.table.DetailLogItem;
import com.lalala.fangs.data.table.FinancialCheckoutItem;
import com.lalala.fangs.data.table.FinancialPayItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.lalala.fangs.utils.NeuNetworkCenter.TABLE_TYPE.DETAIL_LOG;
import static com.lalala.fangs.utils.NeuNetworkCenter.TABLE_TYPE.FINANCIAL_CHECKOUT_LIST;
import static com.lalala.fangs.utils.NeuNetworkCenter.TABLE_TYPE.FINANCIAL_PAY_LIST;
import static com.lalala.fangs.utils.State.CHANGE_PASSWORD_ERROR;
import static com.lalala.fangs.utils.State.CHANGE_PASSWORD_SUCCESS;
import static com.lalala.fangs.utils.State.CHANGE_USER_STATE_FAILED;
import static com.lalala.fangs.utils.State.CHANGE_USER_STATE_SUCCESS;
import static com.lalala.fangs.utils.State.EMPTY_PASSWORD;
import static com.lalala.fangs.utils.State.EMPTY_USERNAME;
import static com.lalala.fangs.utils.State.EMPTY_VERIFYCODE;
import static com.lalala.fangs.utils.State.LOGIN_ERROR;
import static com.lalala.fangs.utils.State.LOGIN_SUCCESS;
import static com.lalala.fangs.utils.State.NEW_PASSWORD_IS_EMPTY;
import static com.lalala.fangs.utils.State.NEW_PASSWORD_IS_NOT_THE_SAME;
import static com.lalala.fangs.utils.State.NEW_PASSWORD_IS_THE_SAME_AS_OLD_ONE;
import static com.lalala.fangs.utils.State.NEW_PASSWORD_IS_TOO_LONG;
import static com.lalala.fangs.utils.State.NEW_PASSWORD_IS_TOO_SHORT;
import static com.lalala.fangs.utils.State.NOT_RESPONSE;
import static com.lalala.fangs.utils.State.NOT_WIFI;
import static com.lalala.fangs.utils.State.NO_WIFI;
import static com.lalala.fangs.utils.State.OLD_PASSWORD_IS_EMPTY;
import static com.lalala.fangs.utils.State.OLD_PASSWORD_IS_TOO_LONG;
import static com.lalala.fangs.utils.State.OLD_PASSWORD_IS_TOO_SHORT;
import static com.lalala.fangs.utils.State.WROGNG_VERIFYCODE;
import static com.lalala.fangs.utils.State.WRONG_USER_OR_PASSWORD;

/**
 * Created by FANGs on 2017/7/12.
 */

public class NeuNetworkCenter {

    private Context context;
    private User user;
    private IgnoreTimeCookieJar cookieJar;
    private String csrf_token;
    static OkHttpClient client;

    public NeuNetworkCenter(Context c, User user) {
        this.context = c;
        this.user = user;

        cookieJar =
                new IgnoreTimeCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
//                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
//                .cookieJar(cookieJar)
//                .build();

    }

    public void getMoreInfor() {
        if (netCheck()) {
            new getMoreInforTask().execute(false);
        }
    }

    public void getMoreInfor(boolean startNewSession) {
        if (netCheck()) {
            new getMoreInforTask().execute(startNewSession);
        }
    }

    public void clearCookie() {
        cookieJar.clear();
    }

    public void dropDevice(String id, View v) {
        if (netCheck()) {
            new dropDeviceTask(v).execute(id);
        }
    }

    private class getMoreInforTask extends AsyncTask<Boolean, Integer, Boolean> {
        String res;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if (params[0]) {
                cookieJar.clear();
            }
//            String eOneUrl = "https://pass.neu.edu.cn/tpass/login?service=http://ipgw.neu.edu.cn:8800/sso/default/neusoft";
            String eOneUrl = "https://pass.neu.edu.cn/tpass/login";

            FormBody body = new FormBody.Builder().build();
            Request request = new Request.Builder()
                    .url(eOneUrl)
                    .post(body)
                    .build();
            okhttp3.Response response;
            try {
                response = client.newCall(request).execute();
                res = response.body().string();
                Document doc = Jsoup.parse(res);
                try {
                    String lt = doc.select("#lt").first().val();
                    String execution = doc.select("input[name='execution']").first().val();
                    Log.e(TAG, "doInBackground: need login" );
                    Log.e(TAG,"lt: "+lt);
                    Log.e(TAG,"exec: "+execution);
                    // login
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("rsa", user.getUsername()+user.getPassword()+lt)
                            .add("username",user.getUsername())
                            .add("password", user.getPassword())
                            .add("lt", lt)
                            .add("ul", ""+user.getUsername().length())
                            .add("pl", ""+user.getPassword().length())
                            .add("_eventId","submit")
                            .add("execution",execution);
                    body = builder.build();
                    request = new Request.Builder()
                            .url(eOneUrl)
                            .post(body)
                            .build();
                    response = client.newCall(request).execute();
                    res = response.body().string();
                }
                catch (Exception e){
                    // 已经登录了
                    Log.e(TAG, "doInBackground: allready login" );
                    return true;
                }
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: access eone error" );
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
//            Log.e(TAG, "get more infor onPostExecute: "+res );
            if (!aBoolean) {
                cookieJar.clear();
                if (onUserStatusListener != null) {
                    onUserStatusListener.stateChange(LOGIN_ERROR);
                }
                return;
            }
            if (onUserStatusListener != null) {
                if (res.contains("访问被拒绝")){
                    onUserStatusListener.stateChange(NOT_RESPONSE);
                    return;
                }

            }
            try {
                Document doc = Jsoup.parse(res);
                Element csrf = doc.select("[name=csrf-token]").get(0);
                csrf_token = csrf.attr("content");
                if (onUserStatusListener != null) {
                    Log.e(TAG, "onPostExecute: 已经登陆");
                    onUserStatusListener.stateChange(LOGIN_SUCCESS);
                }
                findProductInformation(res);
                findOnlineDevices(res);
                getPauseInfor(res);
            } catch (Exception e) {
                if (onUserStatusListener != null) {
                    onUserStatusListener.stateChange(LOGIN_ERROR);
                }
                e.printStackTrace();
            }

            Log.e(TAG, "onPostExecute: more info done" );
        }
    }

    private static final String TAG = "NeuNetworkCenter";


    private OnUserStatus onUserStatusListener;

    public interface OnUserStatus {
        void stateChange(int code);

        void changePasswordState(int code, String password);

        void getOnlineDevice(String user, String ip, String os, String time, String id);

        void getProductInformation(String method,
                                   String usedFlow,
                                   String usedTime,
                                   String usedCounter,
                                   String consume,
                                   String wallet);

        void offline(boolean isSuccess, View v);

        void getPauseInfor(boolean pause);

        void pauseState(int code);

    }

    public void setOnUserStatusListener(OnUserStatus listener) {
        this.onUserStatusListener = listener;
    }

    public void changePassword(String old, String new1, String new2) {
        if (onUserStatusListener == null) return;
        if (netCheck()) {
            if (old.isEmpty()) {
                onUserStatusListener.changePasswordState(OLD_PASSWORD_IS_EMPTY, null);
            } else if (old.length() < 6) {
                onUserStatusListener.changePasswordState(OLD_PASSWORD_IS_TOO_SHORT, null);
            } else if (old.length() > 64) {
                onUserStatusListener.changePasswordState(OLD_PASSWORD_IS_TOO_LONG, null);
                return;
            } else {
                if (new1.isEmpty() && new2.isEmpty()) {
                    onUserStatusListener.changePasswordState(NEW_PASSWORD_IS_EMPTY, null);
                } else if (!new1.equals(new2)) {
                    onUserStatusListener.changePasswordState(NEW_PASSWORD_IS_NOT_THE_SAME, null);
                } else if (new1.length() < 6) {
                    onUserStatusListener.changePasswordState(NEW_PASSWORD_IS_TOO_SHORT, null);
                } else if (new1.length() > 64) {
                    onUserStatusListener.changePasswordState(NEW_PASSWORD_IS_TOO_LONG, null);
                } else if (new1.equals(old)) {
                    onUserStatusListener.changePasswordState(NEW_PASSWORD_IS_THE_SAME_AS_OLD_ONE, null);
                } else {
                    new changePasswordTask().execute(old, new1, new2);
                }
            }
        }
    }

    private class changePasswordTask extends AsyncTask<String, Integer, Boolean> {
        String res;
        String password;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String url = "http://ipgw.neu.edu.cn:8800/user/chgpwd/index";
            password = params[1];

            RequestBody formBody = new FormBody.Builder()
                    .add("_csrf", csrf_token)
                    .add("ModifyPasswordForm[old_password]", params[0])
                    .add("ModifyPasswordForm[user_password]", params[1])
                    .add("ModifyPasswordForm[user_password2]", params[2])
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
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
                if (onUserStatusListener != null) {
                    onUserStatusListener.changePasswordState(CHANGE_PASSWORD_ERROR, null);
                }
            } else {
                if (onUserStatusListener != null) {
                    if (res.contains("失败")) {
                        onUserStatusListener.changePasswordState(CHANGE_PASSWORD_ERROR, null);
                    } else {
                        onUserStatusListener.changePasswordState(CHANGE_PASSWORD_SUCCESS, password);
                    }
                }

            }
        }
    }

    public void pauseUser(boolean pause) {
        if (netCheck()) {
            new pauseUserAccountTask(pause).execute();
        }
    }

    private class pauseUserAccountTask extends AsyncTask<String, Integer, Boolean> {
        String res;
        boolean pause;

        pauseUserAccountTask(boolean pause) {
            this.pause = pause;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String url;
            if (pause) {
                url = "http://ipgw.neu.edu.cn:8800/home/base/pause";
            } else {
                url = "http://ipgw.neu.edu.cn:8800/home/base/open";
            }

            RequestBody formBody = new FormBody.Builder()
                    .add("_csrf", csrf_token)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
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

            if (onUserStatusListener == null) return;

            if (res == null || !aBoolean) {
                onUserStatusListener.pauseState(CHANGE_USER_STATE_FAILED);
            } else {
                if (res.contains("修改用户状态成功")) {
                    onUserStatusListener.pauseState(CHANGE_USER_STATE_SUCCESS);
                } else {
                    onUserStatusListener.pauseState(CHANGE_USER_STATE_FAILED);
                }

            }
        }
    }

    private class dropDeviceTask extends AsyncTask<String, Integer, Boolean> {
        String res;
        View view;

        dropDeviceTask(View v) {
            view = v;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String url = "http://ipgw.neu.edu.cn:8800/home/base/drop";
            String id = params[0];

            RequestBody formBody = new FormBody.Builder()
                    .add("id", id)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("X-CSRF-Token", csrf_token)
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .post(formBody)
                    .build();
            okhttp3.Response response;
            try {
                response = client.newCall(request).execute();
                res = response.body().string();
                return res.equals("{\"code\":\"1\"}");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean) {
                Log.e(TAG, "onPostExecute: " + res);
                if (onUserStatusListener != null) {
                    onUserStatusListener.offline(true, view);
                }
            } else {
                if (onUserStatusListener != null) {
                    onUserStatusListener.offline(false, view);
                }
            }
        }

    }

    private void findOnlineDevices(String res) {
        Document doc = Jsoup.parse(res);
        try {
            Elements devices = doc.select("div [class=panel-body] tr:gt(0)");

            for (Element i : devices) {
                Elements datas = i.select("td");
                String user = datas.get(0).text();
                String ip = datas.get(1).text();
                String prodect = datas.get(2).text();
                String time = datas.get(3).text();
                String OS = datas.get(4).text();
                String fee = datas.get(5).text();
                String id = i.select("td a").get(0).attr("id");

                if (onUserStatusListener != null) {
                    onUserStatusListener.getOnlineDevice(user, ip, OS, time, id);
                }

            }

        } catch (Exception e) {
            Log.e(TAG, "onPostExecute: " + e);
        }
    }

    private void getPauseInfor(String res) {
        //    span[class=font-green]
        //    用户正常状态
        //    span[class=font-red]
        //    用户暂停状态

        if (onUserStatusListener == null) return;

        Document doc = Jsoup.parse(res);
        try {
            Elements items = doc.select("span[class=font-green]");
            if (items.size() == 0) {
                onUserStatusListener.getPauseInfor(true);
            } else {
                onUserStatusListener.getPauseInfor(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findProductInformation(String res) {
        Document doc = Jsoup.parse(res);
        try {
            Elements items =
                    doc.select("div [class=panel-body visible-md visible-lg]div [class=table-responsive] table td");
            String method = items.get(2).text();
            String usedFlow = items.get(3).text();
            String usedTime = items.get(4).text();
            String usedCounter = items.get(5).text();
            String consume = items.get(6).text();
            String wallet = items.get(7).text();

            if (onUserStatusListener != null) {
                onUserStatusListener.getProductInformation(method,
                        usedFlow,
                        usedTime,
                        usedCounter,
                        consume,
                        wallet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean normalCheckDone(final String userStr, final String passwordStr) {
        if (onUserStatusListener == null) {
            return false;
        }
        if (userStr.equals("")) {
            onUserStatusListener.stateChange(EMPTY_USERNAME);
            return false;
        } else if (passwordStr.equals("")) {
            onUserStatusListener.stateChange(EMPTY_PASSWORD);
            return false;
        }
        ConnectivityManager coon = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = coon.getActiveNetworkInfo();
        if (info == null) {
            onUserStatusListener.stateChange(NO_WIFI);
            return false;
        } else if (info.getType() != ConnectivityManager.TYPE_WIFI) {
            onUserStatusListener.stateChange(NOT_WIFI);
            return false;
        }
        return true;
    }

    enum TABLE_TYPE {FINANCIAL_CHECKOUT_LIST, FINANCIAL_PAY_LIST, DETAIL_LOG}

    public void getFinancialCheckoutList() {
        if (netCheck()) {
            new getDataTask(FINANCIAL_CHECKOUT_LIST).execute("http://ipgw.neu.edu.cn:8800/financial/checkout/list?page=", "1");
        }
    }

    public void getDetailLog() {
        if (netCheck()) {
            new getDataTask(DETAIL_LOG).execute("http://ipgw.neu.edu.cn:8800/log/detail/index?page=", "1");
        }
    }

    public void getFinancialPayList() {
        if (netCheck()) {
            new getDataTask(FINANCIAL_PAY_LIST).execute("http://ipgw.neu.edu.cn:8800/financial/pay/list?page=", "1");
        }
    }

    private class getDataTask extends AsyncTask<String, Integer, Boolean> {
        String res;
        TABLE_TYPE type;
        int page;
        String url;

        getDataTask(TABLE_TYPE type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String url = params[0];
            String page = params[1];
            this.page = Integer.valueOf(page);
            this.url = url;

            Request request = new Request.Builder()
                    .url(url + page)
                    .get()
                    .build();
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

            if (onUserStatusListener == null) return;

            if (res == null || !aBoolean) {
                Log.e(TAG, "onPostExecute: 访问失败");
            } else {
                paraseData(type, url, page, res);
            }
        }
    }

    private void paraseData(TABLE_TYPE type, String url, int page, String res) {
        Document doc = Jsoup.parse(res);
        try {
            Elements items =
                    doc.select("div[class=summary] b");
            String now = items.get(0).text().replace(",", "").split("-")[1];
            int Inow = Integer.valueOf(now);
            String sum = items.get(1).text().replace(",", "");
            int Isum = Integer.valueOf(sum);

            f(doc, type);
            Log.e(TAG, "paraseData: 表格加载中" + now + "/" + sum);

            if (Inow < Isum) {
                new getDataTask(type).execute(url, String.valueOf(page + 1));
            } else {
                switch (type) {
                    case FINANCIAL_CHECKOUT_LIST:
                        FinancialCheckoutItem.setSum(Isum);
                        if (onLogListener != null) {
                            onLogListener.onFinancialCheckoutLog(financialList);
                        }
                        break;
                    case DETAIL_LOG:
                        DetailLogItem.setSum(Isum);
                        break;
                    case FINANCIAL_PAY_LIST:
                        FinancialPayItem.setSum(Isum);
                        if (onLogListener != null) {
                            onLogListener.onFinancialPayLog(financialPayList);
                        }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<FinancialCheckoutItem> financialList = new ArrayList<>();
    private ArrayList<FinancialPayItem> financialPayList = new ArrayList<>();
    private ArrayList<DetailLogItem> detailLogList = new ArrayList<>();


    private void f(Document doc, TABLE_TYPE type) {
        try {
            Elements items = doc.select("table tr[data-key]");
            for (Element item : items) {
                Elements i = item.select("td");
                switch (type) {
                    case FINANCIAL_CHECKOUT_LIST:
                        financialList.add(new FinancialCheckoutItem(
                                i.get(0).text(),
                                i.get(1).text(),
                                i.get(2).text(),
                                i.get(3).text(),
                                i.get(4).text(),
                                i.get(5).text(),
                                i.get(6).text(),
                                i.get(7).text(),
                                i.get(8).text(),
                                i.get(9).text())
                        );
                        break;
                    case DETAIL_LOG:
                        detailLogList.add(new DetailLogItem(
                                i.get(0).text(),
                                i.get(1).text(),
                                i.get(2).text(),
                                i.get(3).text(),
                                i.get(4).text(),
                                i.get(5).text(),
                                i.get(6).text(),
                                i.get(7).text()));
                        break;
                    case FINANCIAL_PAY_LIST:
                        financialPayList.add(new FinancialPayItem(
                                i.get(0).text(),
                                i.get(1).text(),
                                i.get(2).text(),
                                i.get(3).text(),
                                i.get(4).text(),
                                i.get(5).text(),
                                i.get(6).text(),
                                i.get(7).text(),
                                i.get(8).text()));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnLogListener onLogListener;

    public void setOnLogListener(OnLogListener listener) {
        this.onLogListener = listener;
    }

    public interface OnLogListener {
        void onFinancialCheckoutLog(ArrayList<FinancialCheckoutItem> financialCheckoutList);

        void onFinancialPayLog(ArrayList<FinancialPayItem> financialPayList);
    }

    private boolean netCheck() {
        ConnectivityManager coon = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = coon.getActiveNetworkInfo();
        if (info == null) {
            if (onUserStatusListener != null) {
                onUserStatusListener.stateChange(NO_WIFI);
            }
            return false;
        } else if (info.getType() != ConnectivityManager.TYPE_WIFI) {
            if (onUserStatusListener != null) {
                onUserStatusListener.stateChange(NOT_WIFI);
            }
            return false;
        }
        return true;
    }

    // TODO: 2017/7/31 每次访问网络检查Wi-Fi状态
}
