package com.spm.taas.networkmanagement;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by saikatpakira on 13/10/16.
 */

public class HttpPostRequest extends AsyncTask<Void, Void, Void> {

    private String URL = "", exception = "";
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

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder requestBilder = new FormBody.Builder();
        for (int i = 0; i < keyValue.size(); i++) {
            requestBilder.addEncoded(keyValue.get(i).getKey(), keyValue.get(i).getValue());
        }

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBilder.build())
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();
            result = new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            exception = e.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            exception = e.toString();
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
