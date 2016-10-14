package com.spm.taas.networkmanagement;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by saikatpakira on 10/10/16.
 */

public class HttpGetRequest extends AsyncTask<Void, Void, Void> {

    private String URL = "", exception = "";
    private JSONObject result = null;
    private onHttpResponseListener callback = null;

    public HttpGetRequest(final String URL, final onHttpResponseListener callback) {
        this.URL = URL;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .get()
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
