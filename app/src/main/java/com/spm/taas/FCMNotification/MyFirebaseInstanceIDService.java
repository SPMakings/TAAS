package com.spm.taas.FCMNotification;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spm.taas.TeacherList;
import com.spm.taas.adapters.AssignTeacherAdapter;
import com.spm.taas.application.TassApplication;
import com.spm.taas.networkmanagement.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Saikat Pakira on 15/05/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        if (!TassApplication.getInstance().getUserID().equals("")) {
            sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
        }
    }


    private void sendRegistrationToServer(String token) {
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.registerDeviceToken(TassApplication.getInstance().getUserID(),token, "A");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200) {
                    Log.i("PushFired","200");
                } else {
                    try {
                        Log.e("User", response.errorBody().string().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Log error here since request failed
                Log.e("assign", t.toString());

            }
        });
    }

}
