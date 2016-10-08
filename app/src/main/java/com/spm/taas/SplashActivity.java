package com.spm.taas;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.spm.taas.fragments.LoginFragment;
import com.spm.taas.fragments.RegistrationFragment;
import com.spm.taas.fragments.SplashFragments;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        //=====================================
        openSplashPaqe();
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
