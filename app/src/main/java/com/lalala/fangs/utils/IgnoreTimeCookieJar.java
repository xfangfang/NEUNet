package com.lalala.fangs.utils;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 忽视网站上对cookie的时间设置
 * Created by FANGs on 2017/8/1.
 */

public class IgnoreTimeCookieJar extends PersistentCookieJar {

    private CookieCache cache;
    private CookiePersistor persistor;

    public IgnoreTimeCookieJar(CookieCache cache, CookiePersistor persistor) {
        super(cache,persistor);
        this.cache = cache;
        this.persistor = persistor;
        this.cache.addAll(persistor.loadAll());
    }

    @Override
    synchronized public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cache.addAll(cookies);
        persistor.saveAll(cookies);
    }

    @Override
    synchronized public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> validCookies = new ArrayList<>();
        for (Cookie currentCookie : cache) {
            if (currentCookie.matches(url)) {
                validCookies.add(currentCookie);
            }
        }
        return validCookies;
    }

    public String getCookieString(){
        String cookie = "";
        for (Cookie i:cache){
            cookie += i+";";
        }
        return cookie;
    }

}
