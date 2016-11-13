package com.spm.taas.networkmanagement;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by saikatpakira on 13/10/16.
 */

public class HttpPostRequest extends AsyncTask<Void, Void, Void> {

    private String URL = "", exception = "", respose_ = "";
    private JSONObject result = null;
    private onHttpResponseListener callback = null;
    private LinkedList<KeyValuePairModel> keyValue = null;

    public HttpPostRequest(final String URL, final LinkedList<KeyValuePairModel> keyValue, final onHttpResponseListener callback) {
        this.URL = URL;
        this.callback = callback;
        this.keyValue = keyValue;
    }

    @Override
    protected Void doInBackground(Void... params) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        FormBody.Builder requestBilder = new FormBody.Builder();
        for (int i = 0; i < keyValue.size(); i++) {
            requestBilder.addEncoded(keyValue.get(i).getKey(), keyValue.get(i).getValue());
        }

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBilder.build())
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            respose_ = response.body().string();
            Log.i("response", respose_);
            result = new JSONObject(respose_);
        } catch (IOException e) {
            e.printStackTrace();
            exception = e.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            exception = respose_;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (exception.equals("")) {
            callback.onSuccess(result);
        } else {
            callback.onError(exception);
        }
    }

}
