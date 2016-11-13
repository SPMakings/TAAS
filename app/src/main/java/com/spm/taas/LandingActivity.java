package com.spm.taas;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.spm.taas.fragments.AdminUserList;
import com.spm.taas.fragments.HomeStudent;
import com.spm.taas.fragments.ProblemSolution;
import com.spm.taas.fragments.ProblemsUpload;
import com.spm.taas.fragments.StatusFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LandingActivity extends AppCompatActivity {

    private ImageView header_prof_img = null;
    private TextViewIkarosRegular header_prof_name = null;
    private TextViewIkarosLight header_prof_type = null, uploadText = null;
    private final int STORAGE_PERMISSION_CODE = 1, PICK_IMAGE = 100, PICK_CAMERA = 200;
    private OnImageFetched imageCallback = null;
    private View footer_home, footer_upload, footer_subjects;
    private String mCurrentPhotoPath = "";
    //========
    private onNeedRefresh callback_ = null;

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
        uploadText = (TextViewIkarosLight) findViewById(R.id.upload_text);

        if (TassApplication.getInstance().getUserType().equalsIgnoreCase("admin")) {
            ((TextViewIkarosLight) findViewById(R.id.upload_text)).setText("USERS");
            ((ImageView) findViewById(R.id.upload_icon)).setBackgroundResource(R.drawable.ic_action_users);
        }

        header_prof_name.setText(TassApplication.getInstance().getUserName());
        header_prof_type.setText(TassApplication.getInstance().getUserType());

        footer_home = findViewById(R.id.landing_home);
        footer_upload = findViewById(R.id.landing_uploads);
        footer_subjects = findViewById(R.id.landing_listview);

        Glide.with(this)
                .load(TassApplication.getInstance().getUserImage())
                .centerCrop()
                .placeholder(R.drawable.default_place_holder)
                .error(R.drawable.default_place_holder)
                .crossFade().bitmapTransform(new CircleTransform(this))
                .into(header_prof_img);


        openSplashPaqe();

        footer_home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                footer_home.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.tabSelected));
                footer_upload.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                footer_subjects.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                openSplashPaqe();
            }
        });

        footer_upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                footer_upload.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.tabSelected));
                footer_home.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                footer_subjects.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));


                if (TassApplication.getInstance().getUserType().equalsIgnoreCase("admin")) {
                    openAdminUserList();
                } else if (TassApplication.getInstance().getUserType().equalsIgnoreCase("student")) {
                    openProblemUploadStudent();
                } else {
                    openSolutionUploadStudent();
                }


            }
        });

        footer_subjects.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                footer_upload.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                footer_home.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                footer_subjects.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.tabSelected));


                openListView();

//                if (TassApplication.getInstance().getUserType().equalsIgnoreCase("student")) {
//
//                } else {
//                    Toast.makeText(LandingActivity.this, "Working on...", Toast.LENGTH_SHORT).show();
//                }


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

    private void openAdminUserList() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.landing_fragment_bucket, new AdminUserList());
        fragmentTransaction.commit();
    }

    private void openSolutionUploadStudent() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.landing_fragment_bucket, new ProblemSolution());
        fragmentTransaction.commit();
    }

    public void openListView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.landing_fragment_bucket, new StatusFragment());
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
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }


    private void requestStoragePermission() {

        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

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
        ActivityCompat.requestPermissions(this, PERMISSIONS, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openIntent();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permission")
                        .setMessage("Oops! you just denied the permission. Without this permission " +
                                "TAAS can't get image from your phone or if you use camera TAAS can't save the file to your phone" +
                                ". Do you want to allow us?")
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

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_chooser, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        dialogView.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                try {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
                    startActivityForResult(cameraIntent, PICK_CAMERA);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(LandingActivity.this, "Failed to create file.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogView.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                alertDialog.dismiss();

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        alertDialog.show();

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TAAS_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".png",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
                    //Log.i("Camera", RealPathHelper.getPath(LandingActivity.this, selectedImage));

                    imageCallback.onSuccess(RealPathHelper.getPath(LandingActivity.this, selectedImage));
                }
            } catch (Exception e) {
                if (imageCallback != null) {
                    imageCallback.onError("Failed to pick this image.");
                }
            }

            // }

        } else if (requestCode == PICK_CAMERA && resultCode == RESULT_OK) {

            Log.i("Camera", mCurrentPhotoPath);

            File fl_ = new File(mCurrentPhotoPath);
            Log.i("Camera", "Exist : " + fl_.exists());
            if (fl_.exists()) {
                imageCallback.onSuccess(mCurrentPhotoPath);
            } else {
                if (imageCallback != null) {
                    imageCallback.onError("Failed to write file.");
                }
            }
        } else if (requestCode == 1000 && resultCode == RESULT_OK) {
            if (callback_ != null) {
                callback_.onRefresh();
            }
        } else {
            if (imageCallback != null) {
                imageCallback.onError("You haven't select any image.");
            }
        }

    }


    public void assignTeacher(final String qunID_, final String subject_) {
        Intent i = new Intent(LandingActivity.this, AssignActivity.class);
        i.putExtra("subject", subject_);
        i.putExtra("questionID", qunID_);
        startActivityForResult(i, 1000);
    }

    public interface onNeedRefresh {
        void onRefresh();
    }

    public void addOnNeedRefresh(onNeedRefresh callback_) {
        this.callback_ = callback_;
    }

}
