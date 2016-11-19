package com.spm.taas;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.spm.taas.adapters.CountrySpinnerAdapter;
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


public class EditProfile extends TAASActivity {

    private TextViewIkarosRegular asStudent, asTeachers;
    private Spinner countryList, expertiseList, degreeList;
    private ArrayList<String> categories = null;
    private ArrayList<String> categoriesExp = null;
    private EditText firstName, lastName, address1, address2, password, cofirtmPassword, phoneNumber, thirdFields;
    private String SELECTED_COUNTRY = "", SELECTED_DEGREE = "", SELECTED_EXPERT = "", USER_TYPE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //==========
        countryList = (Spinner) findViewById(R.id.country);
        expertiseList = (Spinner) findViewById(R.id.expertise);
        degreeList = (Spinner) findViewById(R.id.degree);
        asStudent = (TextViewIkarosRegular) findViewById(R.id.as_student);
        asTeachers = (TextViewIkarosRegular) findViewById(R.id.as_teacher);
        //==============
        firstName = (EditText) findViewById(R.id.txtUserName);
        lastName = (EditText) findViewById(R.id.txtUserNameLast);
        address1 = (EditText) findViewById(R.id.textAddress1);
        address2 = (EditText) findViewById(R.id.textAddress2);
        password = (EditText) findViewById(R.id.txtpassword);
        cofirtmPassword = (EditText) findViewById(R.id.txtpasswordCnfsm);

        phoneNumber = (EditText) findViewById(R.id.textPhone);
        thirdFields = (EditText) findViewById(R.id.thrd_fields);

        //============

        if (TassApplication.getInstance().getUserType().equalsIgnoreCase("teacher")) {

            //findViewById(R.id.student_box).setVisibility(View.GONE);
            findViewById(R.id.teacher_box).setVisibility(View.VISIBLE);
            USER_TYPE = "T";
            thirdFields.setHint("Profession");
            thirdFields.setInputType(InputType.TYPE_CLASS_TEXT);

        } else if (TassApplication.getInstance().getUserType().equalsIgnoreCase("student")) {
            //findViewById(R.id.student_box).setVisibility(View.VISIBLE);
            USER_TYPE = "S";

            thirdFields.setHint("Grade");
            thirdFields.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            //======Admin.
        }

        //==========

        countryListManagement();


        degreeList.setAdapter(initDegreeAdapter());
        degreeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_DEGREE = categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        expertiseList.setAdapter(initExpertiseAdapter());
        expertiseList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_EXPERT = categoriesExp.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //===
        makePrefilled();

        //========

        findViewById(R.id.register_me).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (firstName.getText().toString().trim().length() > 0) {
                    if (lastName.getText().toString().trim().length() > 0) {
                        if (Patterns.PHONE.matcher(phoneNumber.getText().toString().trim()).matches()) {
                            Log.i("rdfield", thirdFields.getText().toString().trim());
                            if (thirdFields.getText().toString().trim().length() > 0) {
                                if (USER_TYPE.equals("S")) {
                                    if (Integer.parseInt(thirdFields.getText().toString().trim()) > 0
                                            && Integer.parseInt(thirdFields.getText().toString().trim()) < 9) {

                                        LinkedList<KeyValuePairModel> param_ = new LinkedList<KeyValuePairModel>();
                                        KeyValuePairModel temp_ = new KeyValuePairModel();
                                        temp_.add("firstname", firstName.getText().toString().trim());
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("lastname", lastName.getText().toString().trim());
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("address1", address1.getText().toString().trim());
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("address2", address2.getText().toString().trim());
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("country", SELECTED_COUNTRY);
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("phone", phoneNumber.getText().toString().trim());
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("grade", thirdFields.getText().toString().trim());
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("user_type", USER_TYPE);
                                        param_.add(temp_);


                                        //========Unused filled.
                                        temp_ = new KeyValuePairModel();
                                        temp_.add("email_addres", TassApplication.getInstance().getUserEmail());
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("city", "");
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("state", "");
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("zipcode", "");
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("profession", "");
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("expertise", "");
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("degree", "");
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("dob", "");
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("user_status", "Y");
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("logged_user_id", TassApplication.getInstance().getUserID());
                                        param_.add(temp_);


                                        Log.i("registration", "Going to register...");

                                        regesterMe(param_);

                                    } else {
                                        thirdFields.setError("Grade must be 1 to 8");
                                    }
                                } else {

                                    if (thirdFields.getText().toString().trim().length() > 0) {

                                        LinkedList<KeyValuePairModel> param_ = new LinkedList<KeyValuePairModel>();
                                        KeyValuePairModel temp_ = new KeyValuePairModel();
                                        temp_.add("firstname", firstName.getText().toString().trim());
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("lastname", lastName.getText().toString().trim());
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("address1", address1.getText().toString().trim());
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("address2", address2.getText().toString().trim());
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("country", SELECTED_COUNTRY);
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("phone", phoneNumber.getText().toString().trim());
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("profession", thirdFields.getText().toString().trim());
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("grade", "");
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("user_type", USER_TYPE);
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("degree", SELECTED_DEGREE);
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("expertise", SELECTED_EXPERT);
                                        param_.add(temp_);


                                        //========Unused filled.
                                        temp_ = new KeyValuePairModel();
                                        temp_.add("email_addres", TassApplication.getInstance().getUserEmail());
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("city", "");
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("state", "");
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("zipcode", "");
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("dob", "");
                                        param_.add(temp_);


                                        temp_ = new KeyValuePairModel();
                                        temp_.add("user_status", "Y");
                                        param_.add(temp_);

                                        temp_ = new KeyValuePairModel();
                                        temp_.add("logged_user_id", TassApplication.getInstance().getUserID());
                                        param_.add(temp_);

                                        regesterMe(param_);
                                    } else {
                                        thirdFields.setError("Enter your profession.");
                                    }
                                }
                            } else {
                                if (USER_TYPE.equals("S")) {
                                    thirdFields.setError("Enter Grade");
                                } else {
                                    thirdFields.setError("Enter Profession");
                                }
                            }
                        } else {
                            phoneNumber.setError("Invalid Phone Number");
                        }
                    } else {
                        lastName.setError("Enter Last Name");
                    }
                } else {
                    firstName.setError("Enter First Name");
                }
            }
        });


    }


    private void makePrefilled() {
        try {
            JSONObject userObj_ = TassApplication.getInstance().getUserDataJSON();


            Log.i("makePrefilled", userObj_.toString());

            firstName.setText(userObj_.getString("first_name"));
            lastName.setText(userObj_.getString("last_name"));
            phoneNumber.setText(userObj_.getString("telephone"));
            address1.setText(userObj_.getString("address1"));
            address2.setText(userObj_.getString("address2"));

            if (TassApplication.getInstance().getUserType().equalsIgnoreCase("teacher")) {
                thirdFields.setText(userObj_.getString("profession"));
            } else if (TassApplication.getInstance().getUserType().equalsIgnoreCase("student")) {
                thirdFields.setText(userObj_.getString("grade"));
            } else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    private ArrayAdapter<String> initDegreeAdapter() {
        categories = new ArrayList<String>();
        categories.add("High School (HS)");
        categories.add("Bachelor of Science (BS)");
        categories.add("Master of Science (MS)");
        categories.add("Ph.D");
        categories.add("Engineering");
        SELECTED_DEGREE = "High School (HS)";
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapter;
    }


    private ArrayAdapter<String> initExpertiseAdapter() {
        categoriesExp = new ArrayList<String>();
        categoriesExp.add("Physics");
        categoriesExp.add("Chemistry");
        categoriesExp.add("Mathematics");
        categoriesExp.add("Math+Phy");
        categoriesExp.add("Math+Chem");
        categoriesExp.add("Phy+Chem");
        categoriesExp.add("Phy+Chem+Math");
        SELECTED_EXPERT = "Physics";
        ArrayAdapter<String> dataAdapterExper = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesExp);
        dataAdapterExper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapterExper;
    }


    private void regesterMe(final LinkedList<KeyValuePairModel> param_) {
        showProgress();
        HttpPostRequest request = new HttpPostRequest(TassConstants.URL_DOMAIN + "register", param_, new onHttpResponseListener() {
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

                                TassApplication.getInstance().setUserData(mainObj.getString("email"),
                                        mainObj.getString("first_name") + " " + mainObj.getString("last_name"),
                                        mainObj.getString("user_type"),
                                        mainObj.getString("user_id"),
                                        mainObj.getString("image"));

                                TassApplication.getInstance().setUserDataJSON(mainObj.toString());

                                makePrefilled();

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

    private void countryListManagement() {
        if (TassApplication.getInstance().getCountryList() == null) {

            showProgress();
            HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN + "countries", new onHttpResponseListener() {
                @Override
                public void onSuccess(final JSONObject jObject) {

                    try {
                        TassApplication.getInstance().setCountryList(jObject.getJSONArray("data"));
                        hideProgress();
                        countryList.setAdapter(new CountrySpinnerAdapter(EditProfile.this, TassApplication.getInstance().getCountryList()));
                        countryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            SELECTED_COUNTRY = TassApplication.getInstance().getCountryList().getJSONObject(position).getString("id");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(final String message) {
                    Log.i("v", message);
                    hideProgress();

                }
            });
            request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            countryList.setAdapter(new CountrySpinnerAdapter(EditProfile.this, TassApplication.getInstance().getCountryList()));
            countryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                SELECTED_COUNTRY = TassApplication.getInstance().getCountryList().getJSONObject(position).getString("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }
}
