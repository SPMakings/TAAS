package com.spm.taas.networkmanagement;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by Saikat Pakira on 10/3/2016.
 */

public class OkHttpFileUploadRequest extends AsyncTask<Void, Void, Void> {

    private LinkedList<KeyValuePairModel> postData;
    private String docFile;
    private onHttpResponseListener callback;
    private String fileUploadTag = "";
    private String URL = "", result = "", exception = "", resCode = "";

    public OkHttpFileUploadRequest(final LinkedList<KeyValuePairModel> postData,
                                   final String fileUploadTag,
                                   final String docFile,
                                   final String URL, onHttpResponseListener callback) {
        this.postData = postData;
        this.fileUploadTag = fileUploadTag;
        this.docFile = docFile;
        this.URL = URL;
        this.callback = callback;
    }


    @Override
    protected Void doInBackground(Void... voids) {

        exception = "";
        OkHttpClient client;

        client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        MultipartBody.Builder mBilder = new MultipartBody.Builder().setType(MultipartBody.FORM);


        File temp_ = new File(docFile);
        mBilder.addFormDataPart(
                fileUploadTag,
                temp_.getName().toString(),
                RequestBody.create(MediaType.parse("image/*"), temp_));

        Log.i("response", temp_.getName().toString());


        for (int i = 0; i < postData.size(); i++) {
            mBilder.addFormDataPart(postData.get(i).getKey(), postData.get(i).getValue());
        }

        MultipartBody requestBody = mBilder.build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();

        try {
            okhttp3.Response response = client.newCall(request).execute();
            result = response.body().string();
            resCode = String.valueOf(response.code());
        } catch (IOException e) {
            e.printStackTrace();
            exception = e.toString();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (exception.equals("")) {
            try {
                callback.onSuccess(new JSONObject(result));
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onError(result);
            }
        } else {
            callback.onError(exception);
        }
    }


}
