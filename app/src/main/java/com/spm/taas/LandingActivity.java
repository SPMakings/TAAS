package com.spm.taas;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.spm.taas.application.CircleTransform;
import com.spm.taas.application.OnImageFetched;
import com.spm.taas.application.RealPathHelper;
import com.spm.taas.application.TassApplication;
import com.spm.taas.customview.TextViewIkarosLight;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.fragments.HomeStudent;
import com.spm.taas.fragments.ProblemSolution;
import com.spm.taas.fragments.ProblemsUpload;
import com.spm.taas.fragments.SplashFragments;

public class LandingActivity extends AppCompatActivity {

    private ImageView header_prof_img = null;
    private TextViewIkarosRegular header_prof_name = null;
    private TextViewIkarosLight header_prof_type = null;
    private final int STORAGE_PERMISSION_CODE = 1, PICK_IMAGE = 100;
    private OnImageFetched imageCallback = null;
    private View footer_home, footer_upload;

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

        footer_home = findViewById(R.id.landing_home);
        footer_upload = findViewById(R.id.landing_uploads);

        Glide.with(this)
                .load(TassApplication.getInstance().getUserImage())
                .centerCrop()
                .crossFade().bitmapTransform(new CircleTransform(this))
                .into(header_prof_img);


        openSplashPaqe();

        findViewById(R.id.landing_home).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                footer_home.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.tabSelected));
                footer_upload.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                openSplashPaqe();
            }
        });

        findViewById(R.id.landing_uploads).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                footer_upload.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.tabSelected));
                footer_home.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                if (TassApplication.getInstance().getUserType().equalsIgnoreCase("student")) {
                    openProblemUploadStudent();
                } else {
                    openSolutionUploadStudent();
                }
            }
        });

        findViewById(R.id.landing_listview).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(LandingActivity.this, "Working on...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openSplashPaqe() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.landing_fragment_bucket, new HomeStudent());
        fragmentTransaction.commit();
    }


    private void openProblemUploadStudent() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.landing_fragment_bucket, new ProblemsUpload());
        fragmentTransaction.commit();
    }

    private void openSolutionUploadStudent() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.landing_fragment_bucket, new ProblemSolution());
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


    private boolean isReadStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }


    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Oops! you just denied the permission. Without this permission " +
                            "TAAS can't get image from your phone. Do you want to allow us?")
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    requestStoragePermission();
                }
            }).show();

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openIntent();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permission")
                        .setMessage("Oops! you just denied the permission. Without this permission " +
                                "TAAS can't get image from your phone. Do you want to allow us?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestStoragePermission();
                    }
                }).show();

            }
        }
    }

    public void fetchPictureFromGallery(OnImageFetched imageCallback) {
        this.imageCallback = imageCallback;
        if (isReadStorageAllowed()) {
            openIntent();
        } else {
            requestStoragePermission();
        }
    }

    private void openIntent() {
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            //content://com.google.android.apps.photos.contentpr
            Log.i("Image", selectedImage.toString());

//            if (selectedImage.toString().startsWith("content://com.google.android")) {
//                new AlertDialog.Builder(LandingActivity.this)
//                        .setTitle("TAAS")
//                        .setMessage("This image doesn't exist in your device storage. " +
//                                "This image is in Google Cloud. Download the image manually, then upload it.")
//                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                                if (imageCallback != null) {
//                                    imageCallback.onError("Cloud Image.");
//                                }
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
//            } else {
//                Log.i("Image", RealPathHelper.getPath(LandingActivity.this, selectedImage));

            try {
                if (imageCallback != null) {
                    imageCallback.onSuccess(RealPathHelper.getPath(LandingActivity.this, selectedImage));
                }
            } catch (Exception e) {
                if (imageCallback != null) {
                    imageCallback.onError("Failed to pick this image.");
                }
            }

            // }

        } else {
            if (imageCallback != null) {
                imageCallback.onError("You haven't select any image.");
            }
        }

    }


}
