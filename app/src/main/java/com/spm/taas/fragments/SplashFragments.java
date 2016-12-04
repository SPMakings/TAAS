package com.spm.taas.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.spm.taas.R;
import com.spm.taas.SplashActivity;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Saikat Pakira on 9/28/2016.
 */

public class SplashFragments extends Fragment {

    private ProgressBar pBar = null;
    private View actionViewgroup = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_splash_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pBar = (ProgressBar) view.findViewById(R.id.progress);
        actionViewgroup = view.findViewById(R.id.action_bucket);

        view.findViewById(R.id.splash_signin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((SplashActivity) getActivity()).openLoginPaqe();
            }
        });

        view.findViewById(R.id.splash_reg).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((SplashActivity) getActivity()).openRegisatrationPaqe();
            }
        });


        if (TassApplication.getInstance().getCountryList() == null) {
            fetchCountryList();
        } else {
            //Log.i("Country", "Country is there " + TassApplication.getInstance().getCountryList());
            pBar.setVisibility(View.INVISIBLE);
            actionViewgroup.setVisibility(View.VISIBLE);
        }

    }

    private void fetchCountryList() {
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN + "countries", new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                try {
                    TassApplication.getInstance().setCountryList(jObject.getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pBar.setVisibility(View.INVISIBLE);
                            actionViewgroup.setVisibility(View.VISIBLE);
                        }
                    });
                }

            }

            @Override
            public void onError(final String message) {
                Log.i("v", message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pBar.setVisibility(View.INVISIBLE);

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Error")
                                    .setMessage("Failed to fetch necessary data. Please retry!")
                                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                            fetchCountryList();
                                        }
                                    })
                                    .setIcon(R.mipmap.ic_launcher)
                                    .show();

                        }
                    });
                }

            }
        });
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
