package com.spm.taas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spm.taas.adapters.AdminUserListAdapter;
import com.spm.taas.adapters.AssignTeacherAdapter;
import com.spm.taas.application.TassApplication;
import com.spm.taas.baseclass.TAASActivity;
import com.spm.taas.networkmanagement.ApiInterface;

import org.json.JSONArray;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignActivity extends TAASActivity {


    private RecyclerView teacherList;
    private JsonArray teacherArray = null;
    private boolean needRefresh = false;

    private int lazyLoadingCunter = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Assign Teacher");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        teacherList = (RecyclerView) findViewById(R.id.teacher_list);
        teacherList.setHasFixedSize(true);
        teacherList.setLayoutManager(new LinearLayoutManager(this));
        getApproved();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getApproved() {

        showProgress();
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.getAdminUserList(TassApplication.getInstance().getUserID(),
                "0", "1000", "teacher", getIntent().getStringExtra("subject"), "Y");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hideProgress();
                if (response.code() == 200) {
                    JsonObject object = response.body();
                    teacherArray = object.getAsJsonArray("data");
                    AssignTeacherAdapter tempAdap_ = new AssignTeacherAdapter(AssignActivity.this, teacherArray);
                    teacherList.setAdapter(tempAdap_);
                    tempAdap_.addOnSelectListener(new AssignTeacherAdapter.OnSelectListener() {
                        @Override
                        public void onSelect(final String teacherID) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(AssignActivity.this)
                                            .setTitle("Assign Teacher")
                                            .setMessage("Do you want to assign this teacher?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    assignATeacher(teacherID, getIntent().getStringExtra("questionID"));
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

    private void assignATeacher(final String teacherID_, final String questionID_) {


        Log.i("assign", "AdminID : " + TassApplication.getInstance().getUserID());
        Log.i("assign", "teacherID_ : " + teacherID_);
        Log.i("assign", "questionID_ : " + questionID_);

        showProgress();
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.getAdminAsignQuestion(TassApplication.getInstance().getUserID(), teacherID_, questionID_);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hideProgress();
                if (response.code() == 200) {
                    JsonObject object = response.body();

                    if (object.get("status").getAsString().equalsIgnoreCase("SUCCESS")) {
                        needRefresh = true;
                        onBackPressed();
                    } else {
                        showError("User", "Failed to assign the teacher.");
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
                Log.e("assign", t.toString());
                hideProgress();
                showError("Server", t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent();
        if (needRefresh) {
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_CANCELED, i);
        }
        finish();
    }
}
