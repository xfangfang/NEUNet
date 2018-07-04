package com.lalala.fangs.utils;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Request;

import static com.lalala.fangs.utils.NeuNetworkCenter.client;

/**
 * Created by FANGs on 2017/8/5.
 */

public class GetJianshuMessage {

    public void getMessage(){
        new getContentTask().execute("http://www.jianshu.com/p/e59fda2cf3e1");
    }

    private class getContentTask extends AsyncTask<String, Integer, Boolean> {
        String res;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String url = params[0];

            Request request = new Request.Builder()
                    .url(url)
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

            if (res == null || !aBoolean) {
                if(listener != null){
                    listener.onMessage(null,null);
                }
            } else {
                Document doc = Jsoup.parse(res);
                try {
                    Elements items =
                            doc.select("div[class=show-content]");
                    String[] content = items.get(0).text().split(",");
                    if(listener != null) {
                        if(content[0].contains("下载")){
                            listener.onDownLoad(content[0], content[1]);
                        }else {
                            listener.onMessage(content[0], content[1]);
                        }
                    }
                }catch (Exception e){
                    if(listener != null){
                        listener.onMessage(null,null);
                    }
                }
            }
        }
    }

    public interface onGetMessageListener{
        void onMessage(String title,String url);
        void onDownLoad(String title,String url);
    }

    onGetMessageListener listener;
    public void setOngetMessageListener(onGetMessageListener listener){
        this.listener = listener;
    }
}
