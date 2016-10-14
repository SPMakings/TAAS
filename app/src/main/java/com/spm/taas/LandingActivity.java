package com.spm.taas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.spm.taas.application.CircleTransform;
import com.spm.taas.application.TassApplication;
import com.spm.taas.customview.TextViewIkarosLight;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.fragments.HomeStudent;
import com.spm.taas.fragments.SplashFragments;

public class LandingActivity extends AppCompatActivity {

    private ImageView header_prof_img = null;
    private TextViewIkarosRegular header_prof_name = null;
    private TextViewIkarosLight header_prof_type = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        header_prof_img = (ImageView) findViewById(R.id.header_prof_image);
        header_prof_name = (TextViewIkarosRegular) findViewById(R.id.header_profile_name);
        header_prof_type = (TextViewIkarosLight) findViewById(R.id.header_profile_type);

        header_prof_name.setText(TassApplication.getInstance().getUserName());
        header_prof_type.setText(TassApplication.getInstance().getUserType());

        Glide
                .with(this)
                .load(TassApplication.getInstance().getUserImage())
                .centerCrop()
                .crossFade().bitmapTransform(new CircleTransform(this))
                .into(header_prof_img);


        openSplashPaqe();
    }


    private void openSplashPaqe() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.landing_fragment_bucket, new HomeStudent());
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_logput) {
            new AlertDialog.Builder(LandingActivity.this)
                    .setTitle("TAAS")
                    .setMessage("Do you want to logout?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            TassApplication.getInstance().clearPreferenceData();
                            Intent i = new Intent(LandingActivity.this, SplashActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(this, "Working on...", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
