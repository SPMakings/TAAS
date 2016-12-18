package com.spm.taas.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spm.taas.AssignActivity;
import com.spm.taas.LandingActivity;
import com.spm.taas.ProfileViewActivity;
import com.spm.taas.R;
import com.spm.taas.adapters.AdminUserListAdapter;
import com.spm.taas.adapters.AssignTeacherAdapter;
import com.spm.taas.application.TassApplication;
import com.spm.taas.baseclass.TAASFragment;
import com.spm.taas.networkmanagement.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by saikatpakira on 05/11/16.
 */

public class AdminUserList extends TAASFragment {

    final String TAG = "AdminUserList";
    private RecyclerView userList = null;
    private View errorBlock = null;
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
        errorBlock = view.findViewById(R.id.error_visibility);
        approved = view.findViewById(R.id.approved);
        userList = (RecyclerView) view.findViewById(R.id.user_list_Admin);
        userList.setLayoutManager(new LinearLayoutManager(getActivity()));
        userList.setHasFixedSize(true);

        getActivity().findViewById(R.id.status_filter).setVisibility(View.GONE);

        //================

        needToApproved.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                needToApproved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tabSelected));
                approved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                if (notApprovedArray != null) {
                    AdminUserListAdapter tempAdap_ = new AdminUserListAdapter(getActivity(), notApprovedArray);
                    userList.setAdapter(tempAdap_);
                    if (notApprovedArray.size() > 0) {
                        errorBlock.setVisibility(View.GONE);
                        tempAdap_.addOnItemSelected(new AdminUserListAdapter.OnItemSelected() {

                            @Override
                            public void onAccept(final String userID_) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Teacher Accept")
                                                .setMessage("Do you want to accept this user?")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                        acceptOrReject(userID_, "Y");
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                })
                                                .show();
                                    }
                                });
                            }

                            @Override
                            public void onDetails(final String userID_) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getActivity(), ProfileViewActivity.class);
                                        i.putExtra("profile_id", userID_);
                                        startActivityForResult(i, 5000);
                                    }
                                });
                            }

                            @Override
                            public void onReject(final String userID_) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Teacher Accept")
                                                .setMessage("Do you want to reject this user?")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                        acceptOrReject(userID_, "N");
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                })
                                                .show();
                                    }
                                });
                            }
                        });
                    } else {
                        errorBlock.setVisibility(View.VISIBLE);
                    }
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
                    AdminUserListAdapter temp_ = new AdminUserListAdapter(getActivity(), approvedArray);
                    userList.setAdapter(temp_);
                    if (approvedArray.size() > 0) {
                        errorBlock.setVisibility(View.GONE);
                        temp_.addOnItemSelected(new AdminUserListAdapter.OnItemSelected() {
                            @Override
                            public void onAccept(final String userID_) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Intent i = new Intent(getActivity(), ProfileViewActivity.class);
                                        i.putExtra("profile_id", userID_);
                                        //startActivity(i);
                                        startActivityForResult(i, 5000);

                                    }
                                });

                            }

                            @Override
                            public void onDetails(String userID_) {

                            }

                            @Override
                            public void onReject(String userID_) {
                                //====no use here.
                            }
                        });
                    } else {
                        errorBlock.setVisibility(View.VISIBLE);
                    }
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

    @Override
    public void onResume() {
        super.onResume();


    }

    private void getNeedApproved() {
        errorBlock.setVisibility(View.GONE);
        showProgress();
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.getAdminUserList(TassApplication.getInstance().getUserID(), "0", "1000", "teacher", "all", "N");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hideProgress();
                if (response.code() == 200) {
                    JsonObject object = response.body();
                    notApprovedArray = object.getAsJsonArray("data");

                    AdminUserListAdapter tempAdap_ = new AdminUserListAdapter(getActivity(), notApprovedArray);
                    userList.setAdapter(tempAdap_);

                    if (notApprovedArray.size() > 0) {

                        tempAdap_.addOnItemSelected(new AdminUserListAdapter.OnItemSelected() {

                            @Override
                            public void onAccept(final String userID_) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Teacher Accept")
                                                .setMessage("Do you want to accept this user?")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                        acceptOrReject(userID_, "Y");
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                })
                                                .show();
                                    }
                                });
                            }

                            @Override
                            public void onDetails(final String userID_) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getActivity(), ProfileViewActivity.class);
                                        i.putExtra("profile_id", userID_);
                                        startActivityForResult(i, 5000);
                                    }
                                });
                            }

                            @Override
                            public void onReject(final String userID_) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Teacher Accept")
                                                .setMessage("Do you want to reject this user?")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                        acceptOrReject(userID_, "N");
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                })
                                                .show();
                                    }
                                });
                            }
                        });
                    } else {
                        errorBlock.setVisibility(View.VISIBLE);
                    }


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
        errorBlock.setVisibility(View.GONE);
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
                    AdminUserListAdapter temp_ = new AdminUserListAdapter(getActivity(), approvedArray);
                    userList.setAdapter(temp_);

                    if (approvedArray.size() > 0) {

                        temp_.addOnItemSelected(new AdminUserListAdapter.OnItemSelected() {
                            @Override
                            public void onAccept(final String userID_) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Intent i = new Intent(getActivity(), ProfileViewActivity.class);
                                        i.putExtra("profile_id", userID_);
                                        //startActivity(i);
                                        startActivityForResult(i, 5000);

                                    }
                                });

                            }

                            @Override
                            public void onDetails(String userID_) {

                            }

                            @Override
                            public void onReject(String userID_) {
                                //====no use here.
                            }
                        });
                    } else {
                        errorBlock.setVisibility(View.VISIBLE);
                    }


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

    ///=======Accept or reject.

    private void acceptOrReject(final String teacherID_, final String status) {

        showProgress();
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.getAdminAcceptRejectUser(teacherID_, status);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hideProgress();
                if (response.code() == 200) {
                    JsonObject object = response.body();
                    if (object.get("status").getAsString().equalsIgnoreCase("SUCCESS")) {
                        ((LandingActivity) getActivity()).openAdminUserList();
                    } else {
                        showError("User", "User type is not Teacher.");
                    }
                    Log.i("assign", object.toString());
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
                Log.e("assign", t.toString());
                hideProgress();
                showError("Server", t.toString());
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5000 && resultCode == RESULT_OK) {
            //=============Initial Opening.
            needToApproved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tabSelected));
            approved.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            getNeedApproved();
        }

    }
}
