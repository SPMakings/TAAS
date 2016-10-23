package com.spm.taas.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.spm.taas.R;
import com.spm.taas.adapters.CountrySpinnerAdapter;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASFragment;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.networkmanagement.HttpPostRequest;
import com.spm.taas.networkmanagement.KeyValuePairModel;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Saikat Pakira on 9/28/2016.
 */

public class RegistrationFragment extends TAASFragment {

    private View optionThree = null;
    private TextViewIkarosRegular asStudent, asTeachers;
    private Spinner countryList, expertiseList, degreeList;
    private ArrayList<String> categories = null;
    private ArrayList<String> categoriesExp = null;
    private EditText firstName, lastName, email, password, cofirtmPassword, phoneNumber, thirdFields;
    private String SELECTED_COUNTRY = "", SELECTED_DEGREE = "", SELECTED_EXPERT = "", USER_TYPE = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_registration, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        countryList = (Spinner) view.findViewById(R.id.country);
        expertiseList = (Spinner) view.findViewById(R.id.expertise);
        degreeList = (Spinner) view.findViewById(R.id.degree);

        optionThree = view.findViewById(R.id.input_thrd_fields);

        asStudent = (TextViewIkarosRegular) view.findViewById(R.id.as_student);
        asTeachers = (TextViewIkarosRegular) view.findViewById(R.id.as_teacher);

        //==============
        firstName = (EditText) view.findViewById(R.id.txtUserName);
        lastName = (EditText) view.findViewById(R.id.txtUserNameLast);
        email = (EditText) view.findViewById(R.id.textEmail);
        password = (EditText) view.findViewById(R.id.txtpassword);
        cofirtmPassword = (EditText) view.findViewById(R.id.txtpasswordCnfsm);

        phoneNumber = (EditText) view.findViewById(R.id.textPhone);
        thirdFields = (EditText) view.findViewById(R.id.thrd_fields);

        USER_TYPE = "S";
        //============


        countryList.setAdapter(new CountrySpinnerAdapter(getActivity(), TassApplication.getInstance().getCountryList()));
        countryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                getActivity().runOnUiThread(new Runnable() {
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


        asStudent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                USER_TYPE = "S";

                asStudent.setBackgroundColor(Color.WHITE);
                asStudent.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                asTeachers.setTextColor(Color.WHITE);
                asTeachers.setBackgroundColor(Color.TRANSPARENT);

                optionThree.setVisibility(View.GONE);

                thirdFields.setHint("Grade");
                thirdFields.setInputType(InputType.TYPE_CLASS_NUMBER);

            }
        });


        asTeachers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                USER_TYPE = "T";
                asTeachers.setBackgroundColor(Color.WHITE);
                asTeachers.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                asStudent.setTextColor(Color.WHITE);
                asStudent.setBackgroundColor(Color.TRANSPARENT);

                optionThree.setVisibility(View.VISIBLE);

                thirdFields.setHint("Profession");
                thirdFields.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        view.findViewById(R.id.register_me).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (firstName.getText().toString().trim().length() > 0) {
                    if (lastName.getText().toString().trim().length() > 0) {
                        if (email.getText().toString().trim().length() > 0) {
                            if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                                if (Patterns.PHONE.matcher(phoneNumber.getText().toString().trim()).matches()) {
                                    if (password.getText().toString().trim().length() > 0) {
                                        if (cofirtmPassword.getText().toString().trim().length() > 0) {
                                            if (password.getText().toString().trim().equals(cofirtmPassword.getText().toString().trim())) {


                                                if (USER_TYPE.equals("S")) {
                                                    Log.i("rdfield", thirdFields.getText().toString().trim());
                                                    if (thirdFields.getText().toString().trim().length() > 0) {
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
                                                            temp_.add("email", email.getText().toString().trim());
                                                            param_.add(temp_);


                                                            temp_ = new KeyValuePairModel();
                                                            temp_.add("password", password.getText().toString().trim());
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


                                                            Log.i("registration", "Going to register...");

                                                            regesterMe(param_);

                                                        } else {
                                                            thirdFields.setError("Grade must be 1 to 8");
                                                        }
                                                    } else {
                                                        thirdFields.setError("Enter Grade, must be 1 to 8");
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
                                                        temp_.add("email", email.getText().toString().trim());
                                                        param_.add(temp_);


                                                        temp_ = new KeyValuePairModel();
                                                        temp_.add("password", password.getText().toString().trim());
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

                                                        regesterMe(param_);
                                                    } else {
                                                        thirdFields.setError("Enter your profession.");
                                                    }
                                                }
                                            } else {
                                                cofirtmPassword.setError("Password and Confirm password is not matching.");
                                            }
                                        } else {
                                            cofirtmPassword.setError("Enter phone number.");
                                        }
                                    } else {
                                        phoneNumber.setError("Enter phone number.");
                                    }
                                } else {
                                    phoneNumber.setError("Invalid Phone Number");
                                }
                            } else {
                                email.setError("Invalid Email");
                            }
                        } else {
                            email.setError("Enter Email");
                        }
                    } else {
                        lastName.setError("Enter last name.");
                    }
                } else {
                    firstName.setError("Enter first name.");
                }
            }
        });

    }


    private ArrayAdapter<String> initDegreeAdapter() {
        categories = new ArrayList<String>();
        categories.add("High School (HS)");
        categories.add("Bachelor of Science (BS)");
        categories.add("Master of Science (MS)");
        categories.add("Ph.D");
        categories.add("Engineering");
        SELECTED_DEGREE = "High School (HS)";
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
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
        ArrayAdapter<String> dataAdapterExper = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categoriesExp);
        dataAdapterExper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapterExper;
    }


    private void regesterMe(final LinkedList<KeyValuePairModel> param_) {
        showProgress();
        HttpPostRequest request = new HttpPostRequest(TassConstants.URL_DOMAIN + "register", param_, new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("registration", jObject.toString());
                        hideProgress();
                        //{"status":"SUCCESS","message":"Verification Pending."}
                        try {
                            if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {
                                showError("Registration", "Registration successful. Now TAAS will verify your provided info as soon" +
                                        " as possible. Please wait until verification.");
                                firstName.setText("");
                                lastName.setText("");
                                email.setText("");
                                phoneNumber.setText("");
                                password.setText("");
                                cofirtmPassword.setText("");
                                thirdFields.setText("");
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
                getActivity().runOnUiThread(new Runnable() {
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

}
