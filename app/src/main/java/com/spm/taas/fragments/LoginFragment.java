package com.spm.taas.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.spm.taas.LandingActivity;
import com.spm.taas.R;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASFragment;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.HttpPostRequest;
import com.spm.taas.networkmanagement.KeyValuePairModel;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;


/**
 * Created by Saikat Pakira on 9/28/2016.
 */

public class LoginFragment extends TAASFragment {


    private EditText emailText, passwordText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailText = (EditText) view.findViewById(R.id.txtUserName);
        passwordText = (EditText) view.findViewById(R.id.txtpassword);

        view.findViewById(R.id.log_me_in).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailText.getText().toString().trim()).matches()) {
                if (emailText.getText().toString().trim().length() > 0) {
                    if (passwordText.getText().toString().trim().length() > 0) {
                        try {
                            logMeIn(URLEncoder.encode(emailText.getText().toString().trim(), "UTF-8"),
                                    URLEncoder.encode(passwordText.getText().toString().trim(), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        emailText.setError("Enter Password");
                    }
                } else {
                    emailText.setError("Invalid Email Id");
                }
            }
        });


        view.findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordDialog();
            }
        });

    }


    private void logMeIn(final String email, final String password) {
        showProgress();
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN + "login?email=" + email + "&device_type=android&password=" + password, new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                try {
                    if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {
                        JSONObject mainObj = jObject.getJSONObject("data").getJSONObject("user");

                        TassApplication.getInstance().setUserData(mainObj.getString("email"),
                                mainObj.getString("first_name") + " " + mainObj.getString("last_name"),
                                mainObj.getString("user_type"),
                                mainObj.getString("user_id"),
                                mainObj.getString("image"));

                        TassApplication.getInstance().setUserDataJSON(mainObj.toString());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                                Intent i = new Intent(getActivity(), LandingActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    hideProgress();
                                    showError("Login", "" + jObject.getString("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        showError("Login", message);
                    }
                });
            }
        });
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void forgotPasswordDialog() {

        AlertDialog.Builder bilder_ = new AlertDialog.Builder(getActivity());
        final View dlogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_forgot_password, null);
        bilder_.setView(dlogView);
        final AlertDialog dlog_ = bilder_.create();

        final EditText oldPassword_ = (EditText) dlogView.findViewById(R.id.email_forgot);

        dlogView.findViewById(R.id.send_forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(oldPassword_.getText().toString().trim()).matches()) {


                    dlog_.dismiss();

                    LinkedList<KeyValuePairModel> param_ = new LinkedList<KeyValuePairModel>();
                    KeyValuePairModel temp_ = new KeyValuePairModel();
                    temp_.add("email", oldPassword_.getText().toString().trim());
                    param_.add(temp_);

                    forgotPasswordAPI(param_);

                } else {
                    oldPassword_.setError("Invalid Email");
                }
            }
        });

        dlog_.show();
    }

    private void forgotPasswordAPI(final LinkedList<KeyValuePairModel> param_) {
        showProgress();
        HttpPostRequest request = new HttpPostRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER + "forget_password", param_, new onHttpResponseListener() {
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
                                showError("Forgot Password", "A reset password link is mailed to you.");
                            } else {
                                showError("Forgot Password", jObject.getString("message"));
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
                        showError("Change Password", message);
                    }
                });
            }
        });
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
