package com.spm.taas;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASActivity;
import com.spm.taas.fragments.LoginFragment;
import com.spm.taas.fragments.RegistrationFragment;
import com.spm.taas.fragments.SplashFragments;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends TAASActivity {


    private HorizontalScrollView mainBg = null;
    private int i = 0;
    private Handler handler = new Handler();
    private Runnable mainBgRunnable = null;
    private boolean movingForword = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        //=====================================
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#2E0B05"));
        }

        if (TassApplication.getInstance().getUserID().equals("")) {

            openSplashPaqe();

            mainBg = (HorizontalScrollView) findViewById(R.id.back_hori_scroll);

            mainBgRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (movingForword) {

                            //Log.i("ScrollTag", "" + mainBg.getMeasuredWidth());

                            if (mainBg.getMeasuredWidth() >= i) {
                                i++;
                                mainBg.smoothScrollTo(i, 0);
                            } else {
                                movingForword = false;
                                i = mainBg.getMeasuredWidth();
                            }
                        } else {
                            if (i > 0) {
                                i--;
                                mainBg.smoothScrollTo(i, 0);
                            } else {
                                movingForword = true;
                                i = 0;
                            }
                        }
                        handler.postDelayed(this, 100);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

        } else {
            //========Need to fire auto-login API to track user login details from admin.
            refreshLogData();
//            Intent i = new Intent(SplashActivity.this, LandingActivity.class);
//            startActivity(i);
//            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.postDelayed(mainBgRunnable, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(mainBgRunnable);
    }

    private void openSplashPaqe() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.startter_frag_holder, new SplashFragments());
        fragmentTransaction.commit();
    }


    public void openLoginPaqe() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.startter_frag_holder, new LoginFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void openRegisatrationPaqe() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.startter_frag_holder, new RegistrationFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //https://urtaas.com/app_control/refresh_log?user_id=66&device_type=android

    private void refreshLogData() {
        showProgress();
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "refresh_log?user_id=" +
                TassApplication.getInstance().getUserID() +
                "&device_type=android", new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                try {
                    if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                                Intent i = new Intent(SplashActivity.this, LandingActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    hideProgress();

                                    new AlertDialog.Builder(SplashActivity.this)
                                            .setTitle("Error")
                                            .setMessage("Unable refresh user data. Please retry!")
                                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // do nothing
                                                }
                                            })
                                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // do nothing
                                                    refreshLogData();
                                                }
                                            })
                                            .setIcon(R.mipmap.ic_launcher)
                                            .show();

                                } catch (Exception e) {
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
                runOnUiThread(new Runnable() {
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
