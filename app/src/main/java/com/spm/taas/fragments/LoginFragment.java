package com.spm.taas.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.spm.taas.LandingActivity;
import com.spm.taas.R;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASFragment;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


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

    }


    private void logMeIn(final String email, final String password) {
        showProgress();
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN + "login?email=" + email + "&password=" + password, new onHttpResponseListener() {
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

}
