package com.spm.taas;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

import com.spm.taas.fragments.LoginFragment;
import com.spm.taas.fragments.RegistrationFragment;
import com.spm.taas.fragments.SplashFragments;


public class SplashActivity extends AppCompatActivity {


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
        openSplashPaqe();


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#2E0B05"));
        }

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


}
