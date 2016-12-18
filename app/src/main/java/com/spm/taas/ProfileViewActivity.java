package com.spm.taas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.spm.taas.application.CircleTransform;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASActivity;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.HttpPostRequest;
import com.spm.taas.networkmanagement.KeyValuePairModel;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class ProfileViewActivity extends TAASActivity {

    private ImageView profile_Image;
    private Spinner profile_activation;
    private TextViewIkarosRegular profileName, address, phoneNumber, grade, expertise, degree, userType, userEmail;
    private LinkedList<KeyValuePairModel> param_;
    private ArrayList<String> categories = null;
    private String SELECTED_DEGREE = "";
    private boolean needRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        param_ = new LinkedList<KeyValuePairModel>();

        profile_Image = (ImageView) findViewById(R.id.profile_image);
        profile_activation = (Spinner) findViewById(R.id.profile_activation);

        profileName = (TextViewIkarosRegular) findViewById(R.id.profile_name);
        address = (TextViewIkarosRegular) findViewById(R.id.profile_address);
        userEmail = (TextViewIkarosRegular) findViewById(R.id.profile_email);
        phoneNumber = (TextViewIkarosRegular) findViewById(R.id.profile_phone);
        grade = (TextViewIkarosRegular) findViewById(R.id.profile_grade);
        expertise = (TextViewIkarosRegular) findViewById(R.id.profile_expertise);
        degree = (TextViewIkarosRegular) findViewById(R.id.profile_degree);
        userType = (TextViewIkarosRegular) findViewById(R.id.profile_type);


        profile_activation.setAdapter(initDegreeAdapter());
        profile_activation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (categories.get(position).equalsIgnoreCase("Activate")) {
                    SELECTED_DEGREE = "Y";
                } else {
                    SELECTED_DEGREE = "N";
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        regesterMe();


        //=====


        findViewById(R.id.register_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ProfileViewActivity.this)
                        .setTitle("User Details")
                        .setMessage("Do you want to save ?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                                KeyValuePairModel temp_ = new KeyValuePairModel();
                                temp_.add("user_status", SELECTED_DEGREE);
                                param_.add(temp_);


                                regesterMe(param_);
                            }
                        })
                        .show();

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

    private void regesterMe() {
        showProgress();
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "user_details?logged_in_user=1&user_id=" +
                getIntent().getStringExtra("profile_id"), new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("registration", jObject.toString());
                        hideProgress();
                        //{"status":"SUCCESS","message":"Verification Pending."}
                        try {
                            if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {

                                JSONObject mainObj = jObject.getJSONObject("data");

                                profileName.setText(mainObj.getString("first_name") + " " + mainObj.getString("last_name"));
                                address.setText(mainObj.getString("address1"));
                                phoneNumber.setText(mainObj.getString("telephone"));
                                grade.setText(mainObj.getString("grade"));
                                expertise.setText(mainObj.getString("expertise"));
                                degree.setText(mainObj.getString("degree"));
                                userType.setText(mainObj.getString("user_type"));
                                userEmail.setText(mainObj.getString("email"));

                                Glide.with(getApplicationContext())
                                        .load(mainObj.getString("profile_pic"))
                                        .centerCrop()
                                        .placeholder(R.drawable.default_place_holder)
                                        .error(R.drawable.default_place_holder)
                                        .crossFade()
                                        .bitmapTransform(new CircleTransform(getApplicationContext()))
                                        .into(profile_Image);

                                KeyValuePairModel temp_ = new KeyValuePairModel();
                                temp_.add("first_name", mainObj.getString("first_name"));
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                temp_.add("last_name", mainObj.getString("last_name"));
                                param_.add(temp_);


                                temp_ = new KeyValuePairModel();
                                if (mainObj.getString("address1").equalsIgnoreCase("null")) {
                                    temp_.add("address1", "");
                                } else {
                                    temp_.add("address1", mainObj.getString("address1"));
                                }
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                if (mainObj.getString("address2").equalsIgnoreCase("null")) {
                                    temp_.add("address2", "");
                                } else {
                                    temp_.add("address2", mainObj.getString("address2"));
                                }
                                param_.add(temp_);


                                temp_ = new KeyValuePairModel();
                                if (mainObj.getString("country").equalsIgnoreCase("null")) {
                                    temp_.add("country", "99");
                                } else {
                                    temp_.add("country", mainObj.getString("country"));
                                }
                                param_.add(temp_);


                                temp_ = new KeyValuePairModel();
                                temp_.add("telephone", mainObj.getString("telephone"));
                                param_.add(temp_);


                                temp_ = new KeyValuePairModel();
                                temp_.add("grade", mainObj.getString("grade"));
                                param_.add(temp_);


//                                        temp_ = new KeyValuePairModel();
//                                        temp_.add("user_type", USER_TYPE);
//                                        param_.add(temp_);


                                //========Unused filled.
                                temp_ = new KeyValuePairModel();
                                temp_.add("email_addres", mainObj.getString("email"));
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                temp_.add("city", mainObj.getString("city"));
                                param_.add(temp_);


                                temp_ = new KeyValuePairModel();
                                temp_.add("state", mainObj.getString("state"));
                                param_.add(temp_);


                                temp_ = new KeyValuePairModel();
                                temp_.add("zipcode", mainObj.getString("zipcode"));
                                param_.add(temp_);


                                temp_ = new KeyValuePairModel();
                                temp_.add("profession", mainObj.getString("profession"));
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                temp_.add("expertise", mainObj.getString("expertise"));
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                temp_.add("degree", mainObj.getString("degree"));
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                temp_.add("dob", "");
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                temp_.add("logged_user_id", TassApplication.getInstance().getUserID());
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                temp_.add("user_id", getIntent().getStringExtra("profile_id"));
                                param_.add(temp_);


                            } else {
                                showError("Registration", jObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        showError("Registration", message);
                    }
                });
            }
        });
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    private ArrayAdapter<String> initDegreeAdapter() {
        categories = new ArrayList<String>();
        categories.add("Activate");
        categories.add("Deactivate");
        SELECTED_DEGREE = "Y";
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapter;
    }


    private void regesterMe(final LinkedList<KeyValuePairModel> param_) {
        showProgress();
        HttpPostRequest request = new HttpPostRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER + "edit_user", param_, new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        hideProgress();
                        //{"status":"SUCCESS","message":"Verification Pending."}
                        try {
                            if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {
                                needRefresh = true;
                                Log.i("registration", jObject.toString());
                                Toast.makeText(getApplicationContext(), "Successfully Updated.", Toast.LENGTH_SHORT).show();
                                onBackPressed();

                            } else {
                                showError("Registration", jObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        showError("Registration", message);
                    }
                });
            }
        });
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent i = new Intent();
        if (needRefresh) {
            setResult(RESULT_OK,i);
        } else {
            setResult(RESULT_CANCELED,i);
        }
        finish();
    }
}
