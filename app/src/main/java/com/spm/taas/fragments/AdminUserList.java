package com.spm.taas.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spm.taas.R;
import com.spm.taas.adapters.AdminUserListAdapter;
import com.spm.taas.application.TassApplication;
import com.spm.taas.baseclass.TAASFragment;
import com.spm.taas.networkmanagement.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by saikatpakira on 05/11/16.
 */

public class AdminUserList extends TAASFragment {

    final String TAG = "AdminUserList";
    private RecyclerView userList = null;
    private View needToApproved, approved;
    private JsonArray notApprovedArray = null, approvedArray = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list_admin, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        needToApproved = view.findViewById(R.id.need_approved);
        approved = view.findViewById(R.id.approved);
        userList = (RecyclerView) view.findViewById(R.id.user_list_Admin);
        userList.setLayoutManager(new LinearLayoutManager(getActivity()));
        userList.setHasFixedSize(true);


        //================

        needToApproved.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                needToApproved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tabSelected));
                approved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                if (notApprovedArray != null) {
                    userList.setAdapter(new AdminUserListAdapter(getActivity(), notApprovedArray));
                } else {
                    getNeedApproved();
                }
            }
        });

        approved.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                approved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tabSelected));
                needToApproved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));

                if (approvedArray != null) {
                    userList.setAdapter(new AdminUserListAdapter(getActivity(), approvedArray));
                } else {
                    getApproved();
                }

            }
        });

        //=============Initial Opening.
        needToApproved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tabSelected));
        approved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        getNeedApproved();
    }


    private void getNeedApproved() {
        showProgress();
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.getAdminUserList(TassApplication.getInstance().getUserID(), "0", "1000", "all", "all", "N");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hideProgress();
                if (response.code() == 200) {
                    JsonObject object = response.body();
                    notApprovedArray = object.getAsJsonArray("data");
                    Log.i(TAG, "Length : " + notApprovedArray.size());
                    userList.setAdapter(new AdminUserListAdapter(getActivity(), notApprovedArray));
                } else {
                    try {
                        showError("User", response.errorBody().string().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                hideProgress();
                showError("Server", t.toString());
            }
        });
    }

    private void getApproved() {

        showProgress();
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.getAdminUserList(TassApplication.getInstance().getUserID(), "0", "1000", "all", "all", "Y");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hideProgress();
                if (response.code() == 200) {
                    JsonObject object = response.body();
                    approvedArray = object.getAsJsonArray("data");
                    userList.setAdapter(new AdminUserListAdapter(getActivity(), approvedArray));
                } else {
                    try {
                        showError("User", response.errorBody().string().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                hideProgress();
                showError("Server", t.toString());
            }
        });
    }
}
