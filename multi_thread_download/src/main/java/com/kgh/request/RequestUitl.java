package com.kgh.request;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by keguihong on 2017/11/3.
 */

public class RequestUitl {

    public static void getAsync(OkHttpClient okHttpClient, String url, Callback callback) {
        getAsync(okHttpClient, url, callback, null);
    }

    public Response getSync(OkHttpClient okHttpClient, String url, Callback callback) {
        return getSync(okHttpClient, url, callback, null);
    }


    public static void postAsync(OkHttpClient okHttpClient, String url, Map<String, String> params, Callback callback, Object tag) {
        Request.Builder build = new Request.Builder();
        if (params != null && params.size() > 0) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
//                System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                builder.add(entry.getKey(), entry.getValue());
            }
            RequestBody formBody = builder.build();
            build.post(formBody);
        }
        Request request = build.url(url).tag(tag).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }


    public static Response postSync(OkHttpClient okHttpClient, String url, Map<String, String> params, Callback callback, Object tag) {
        Request.Builder build = new Request.Builder();
        if (params != null && params.size() > 0) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
//                System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                builder.add(entry.getKey(), entry.getValue());
            }
            RequestBody formBody = builder.build();
            build.post(formBody);
        }
        Request request = build.url(url).tag(tag).build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (Exception e) {

        }
        return response;
    }

    public static void getAsync(OkHttpClient okHttpClient, String url, Callback callback, Object tag) {
        Request request = new Request.Builder().url(url).tag(tag).build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    public static Response getSync(OkHttpClient okHttpClient, String url, Callback callback, Object tag) {
        Request request = new Request.Builder().url(url).tag(tag).build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (Exception e) {

        }
        return response;
    }

    public static void downloadFileByRange(OkHttpClient client, String url, long startIndex, long endIndex, Callback callback, Object tag) {
        Request request = new Request.Builder().header("RANGE", "bytes=" + startIndex + "-" + endIndex)
                .url(url)
                .tag(tag)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static void cancle(OkHttpClient okHttpClient, String tag) {
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }
}
