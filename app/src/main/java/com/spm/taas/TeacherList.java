package com.spm.taas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spm.taas.adapters.AssignTeacherAdapter;
import com.spm.taas.adapters.ContactListAdapter;
import com.spm.taas.application.TassApplication;
import com.spm.taas.baseclass.TAASActivity;
import com.spm.taas.networkmanagement.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherList extends TAASActivity {

    private RecyclerView teacherList = null;

    private View phyTab = null, chemTab = null, mathTab = null;
    private String currentSubjectSelected = "physics";
    private int chunkSartCount = 0;
    private final int chunkSize = 20;
    private boolean isLoading = false;

    private ContactListAdapter tempAdap_ = null;

    private View loaderToggel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //=============

        loaderToggel = findViewById(R.id.loader_toggel);

        phyTab = findViewById(R.id.phy_teacher);
        chemTab = findViewById(R.id.chem_teacher);
        mathTab = findViewById(R.id.math_teacher);

        phyTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phyTab.setBackgroundColor(ContextCompat.getColor(TeacherList.this, R.color.tabSelected));
                chemTab.setBackgroundColor(ContextCompat.getColor(TeacherList.this, R.color.colorPrimary));
                mathTab.setBackgroundColor(ContextCompat.getColor(TeacherList.this, R.color.colorPrimary));


                currentSubjectSelected = "physics";
                chunkSartCount = 0;
                tempAdap_.setInEnd(true);
                tempAdap_.clearData();
                getApproved();
            }
        });


        chemTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chemTab.setBackgroundColor(ContextCompat.getColor(TeacherList.this, R.color.tabSelected));
                phyTab.setBackgroundColor(ContextCompat.getColor(TeacherList.this, R.color.colorPrimary));
                mathTab.setBackgroundColor(ContextCompat.getColor(TeacherList.this, R.color.colorPrimary));

                currentSubjectSelected = "chemistry";
                chunkSartCount = 0;
                tempAdap_.setInEnd(true);
                tempAdap_.clearData();
                getApproved();
            }
        });


        mathTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mathTab.setBackgroundColor(ContextCompat.getColor(TeacherList.this, R.color.tabSelected));
                phyTab.setBackgroundColor(ContextCompat.getColor(TeacherList.this, R.color.colorPrimary));
                chemTab.setBackgroundColor(ContextCompat.getColor(TeacherList.this, R.color.colorPrimary));

                currentSubjectSelected = "mathematics";
                chunkSartCount = 0;
                tempAdap_.setInEnd(true);
                tempAdap_.clearData();
                getApproved();
            }
        });

        teacherList = (RecyclerView) findViewById(R.id.teacher_list);
        tempAdap_ = new ContactListAdapter(TeacherList.this, (new JsonArray()));
        teacherList.setHasFixedSize(true);
        teacherList.setAdapter(tempAdap_);
        teacherList.setLayoutManager(new LinearLayoutManager(this));
        teacherList.setItemAnimator(new DefaultItemAnimator());

        tempAdap_.addOnSelectListener(new ContactListAdapter.OnSelectListener() {
            @Override
            public void onSelect(final String teacherID) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(TeacherList.this)
                                .setTitle("Video Call")
                                .setMessage("Do you want to call this teacher?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestForCall(teacherID);
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
            public void onEnd(int currentCount) {

                if (!isLoading) {
                    chunkSartCount = (currentCount - 1);
                    getApproved();
                }


            }
        });


        getApproved();
    }


    private void getApproved() {

        Log.i("onResponse", "chunkSartCount : " + chunkSartCount);

        loaderToggel.setVisibility(View.GONE);
        isLoading = true;
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.getAdminUserList("1",
                String.valueOf(chunkSartCount), String.valueOf(chunkSize), "teacher", currentSubjectSelected, "Y");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                loaderToggel.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    JsonObject object = response.body();
                    //Log.i("onResponse", object.toString());
                    JsonArray teacherArray = object.getAsJsonArray("data");
                    if (teacherArray.size() < chunkSartCount) {
                        tempAdap_.setInEnd(true);
                    }
                    tempAdap_.changedData(teacherArray);
                    isLoading = false;
                } else {
                    try {
                        showError("User", response.errorBody().string().toString());
                        isLoading = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Log error here since request failed
                Log.e("assign", t.toString());
                loaderToggel.setVisibility(View.VISIBLE);
                showError("Server", t.toString());
                isLoading = false;
            }
        });
    }


    private void requestForCall(final String teacherID) {

        Log.i("onResponse", "chunkSartCount : " + chunkSartCount);
        isLoading = true;
        showProgress();
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.requestVideoCall(TassApplication.getInstance().getUserID(), teacherID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hideProgress();
                if (response.code() == 200) {
                    JsonObject object = response.body();
                    Log.i("onResponse", object.toString());
                } else {
                    try {
                        showError("User", response.errorBody().string().toString());
                        isLoading = false;
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
                isLoading = false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
