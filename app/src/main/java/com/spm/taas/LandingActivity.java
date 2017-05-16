package com.spm.taas;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.spm.taas.application.CircleTransform;
import com.spm.taas.application.OnImageFetched;
import com.spm.taas.application.RealPathHelper;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASActivity;
import com.spm.taas.customview.TextViewIkarosLight;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.fragments.AdminUserList;
import com.spm.taas.fragments.HomeStudent;
import com.spm.taas.fragments.ProblemSolution;
import com.spm.taas.fragments.ProblemsUpload;
import com.spm.taas.fragments.StatusFragment;
import com.spm.taas.networkmanagement.ApiInterface;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.HttpPostRequest;
import com.spm.taas.networkmanagement.KeyValuePairModel;
import com.spm.taas.networkmanagement.OkHttpFileUploadRequest;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingActivity extends TAASActivity {

    private ImageView header_prof_img = null;
    private TextViewIkarosRegular header_prof_name = null;
    private TextViewIkarosLight header_prof_type = null, uploadText = null;
    private final int STORAGE_PERMISSION_CODE = 1, PICK_IMAGE = 100, PICK_CAMERA = 200;
    private OnImageFetched imageCallback = null;
    private View footer_home, footer_upload, footer_subjects;
    private String mCurrentPhotoPath = "";
    //========
    private onNeedRefresh callback_ = null;

    //=======Chnage Passsword Dilaog.

    private AlertDialog dlog_ = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        Log.i("Firebase", "" + FirebaseInstanceId.getInstance().getToken());

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
                .crossFade()
                .bitmapTransform(new CircleTransform(this))
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


        //======FCM Registration
        if (!TassApplication.getInstance().getUserID().equals("")) {
            sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
        }

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

    public void openAdminUserList() {
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
                            logMeOut();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (item.getItemId() == R.id.action_profile) {

            Intent i = new Intent(LandingActivity.this, EditProfile.class);
            startActivity(i);

        } else if (item.getItemId() == R.id.action_password) {
            editPassword();
        } else if (item.getItemId() == R.id.action_videocall) {

            //Intent i = new Intent(LandingActivity.this, VideoChatActivity.class);
            Intent i = new Intent(LandingActivity.this, TeacherList.class);
            startActivity(i);

        } else {
            //Toast.makeText(this, "Working on...", Toast.LENGTH_SHORT).show();
            editImage();
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isReadStorageAllowed() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private void requestStoragePermission() {

        final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Oops! you just denied the permission. Without this permission " +
                            "TAAS can't upload image from your phone or camera. Do you want to allow us?")
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(LandingActivity.this, PERMISSIONS, STORAGE_PERMISSION_CODE);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, STORAGE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
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

        if (imageCallback != null) {
            if (isReadStorageAllowed()) {
                openIntent();
            } else {
                requestStoragePermission();
            }
        }

    }

    private void openIntent() {

        Log.i("imagepath", "Called....");


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_chooser, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        dialogView.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    try {
                        Uri photoURI = FileProvider.getUriForFile(LandingActivity.this,
                                "com.spm.taas.fileprovider",
                                photoFile);
                        cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, PICK_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LandingActivity.this, "Unable to create Image file.", Toast.LENGTH_SHORT).show();
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
//                            public void onClick(DialogInterface dialog_forgot_password, int which) {
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
        } else if (requestCode == 5000 && resultCode == RESULT_OK) {
            if (TassApplication.getInstance().getUserType().equalsIgnoreCase("admin")) {
                openAdminUserList();
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

    public void questionDetails(final String qunID) {
        Intent i = new Intent(LandingActivity.this, QuestionDetails.class);
        i.putExtra("QUN_ID", qunID);
        startActivityForResult(i, 1000);
    }

    public interface onNeedRefresh {
        void onRefresh();
    }

    public void addOnNeedRefresh(onNeedRefresh callback_) {
        this.callback_ = callback_;
    }

    private void editImage() {

        AlertDialog.Builder bilder_ = new AlertDialog.Builder(LandingActivity.this);
        final View dlogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_image, null);
        bilder_.setView(dlogView);


        Glide.with(this)
                .load(TassApplication.getInstance().getUserImage())
                .placeholder(R.drawable.default_place_holder)
                .error(R.drawable.default_place_holder)
                .crossFade()
                .into(((ImageView) dlogView.findViewById(R.id.edit_profimage)));

        dlogView.findViewById(R.id.upload_solution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchPictureFromGallery(new OnImageFetched() {
                    @Override
                    public void onSuccess(final String path_) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                new AlertDialog.Builder(LandingActivity.this)
                                        .setTitle("TAAS")
                                        .setMessage("Do you want to set this image as your profile image?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing to close the dialog_forgot_password.

                                                LinkedList<KeyValuePairModel> param_ = new LinkedList<KeyValuePairModel>();
                                                KeyValuePairModel temp_ = new KeyValuePairModel();
                                                temp_.add("user_id", TassApplication.getInstance().getUserID());
                                                param_.add(temp_);

                                                postProfileImage(param_, "user_image", path_, ((ImageView) dlogView.findViewById(R.id.edit_profimage)));

                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();

                            }
                        });
                    }

                    @Override
                    public void onError(final String errorMessage_) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LandingActivity.this, errorMessage_, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


            }
        });


        AlertDialog dlog_ = bilder_.create();
        dlog_.show();
    }


    private void editPassword() {

        AlertDialog.Builder bilder_ = new AlertDialog.Builder(LandingActivity.this);
        final View dlogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        bilder_.setView(dlogView);

        final EditText oldPassword_ = (EditText) dlogView.findViewById(R.id.old_pass);
        final EditText newPassword_ = (EditText) dlogView.findViewById(R.id.new_pass);
        final EditText confPassword_ = (EditText) dlogView.findViewById(R.id.conf_pass);


        dlogView.findViewById(R.id.upload_solution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (oldPassword_.getText().toString().trim().length() > 0) {
                    if (newPassword_.getText().toString().trim().length() > 0) {
                        if (confPassword_.getText().toString().trim().length() > 0) {
                            if (confPassword_.getText().toString().trim().equals(newPassword_.getText().toString().trim())) {

                                LinkedList<KeyValuePairModel> param_ = new LinkedList<KeyValuePairModel>();
                                KeyValuePairModel temp_ = new KeyValuePairModel();
                                temp_.add("old_password", oldPassword_.getText().toString().trim());
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                temp_.add("new_password", newPassword_.getText().toString().trim());
                                param_.add(temp_);

                                temp_ = new KeyValuePairModel();
                                temp_.add("user_id", TassApplication.getInstance().getUserID());
                                param_.add(temp_);

                                dlog_.dismiss();
                                changePassword(param_);

                            } else {
                                confPassword_.setError("New password and confirm password is not equal.");
                            }
                        } else {
                            confPassword_.setError("Enter Confirm Password.");
                        }
                    } else {
                        newPassword_.setError("Enter New Password.");
                    }
                } else {
                    oldPassword_.setError("Enter Old Password.");
                }
            }
        });

        dlog_ = bilder_.create();
        dlog_.show();
    }


    private void changePassword(final LinkedList<KeyValuePairModel> param_) {
        showProgress();
        HttpPostRequest request = new HttpPostRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER + "change_password", param_, new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("registration", jObject.toString());
                        hideProgress();
                        //{"status":"SUCCESS","message":"Verification Pending."}
                        try {
                            if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {
                                Toast.makeText(LandingActivity.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                showError("Change Password", jObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
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

    private void logMeOut() {
        showProgress();
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "logout?user_id=" +
                TassApplication.getInstance().getUserID() +
                "&device_type=android", new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                try {
                    if (jObject.getString("status").equalsIgnoreCase("SUCCESS") || jObject.getString("status").equalsIgnoreCase("FAIL")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                                TassApplication.getInstance().clearPreferenceData();
                                Intent i = new Intent(LandingActivity.this, SplashActivity.class);
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

                                    new AlertDialog.Builder(LandingActivity.this)
                                            .setTitle("Error")
                                            .setMessage("Unable to Logout. Try Again!")
                                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // do nothing
                                                }
                                            })
                                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // do nothing
                                                    logMeOut();
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


    private void postProfileImage(final LinkedList<KeyValuePairModel> data_, final String fileUploadTag_, final String filePath_, final ImageView previewImage) {

        showProgress();
        OkHttpFileUploadRequest request = new OkHttpFileUploadRequest(data_,
                fileUploadTag_, filePath_,
                TassConstants.URL_DOMAIN + "image_upload", new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Log.i("response", jObject.toString());
                        try {
                            if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {

                                //======need to refresh profile image.


                                TassApplication.getInstance().setUserImage(filePath_);

                                Glide.with(getApplicationContext())
                                        .load(TassApplication.getInstance().getUserImage())
                                        .placeholder(R.drawable.default_place_holder)
                                        .error(R.drawable.default_place_holder)
                                        .crossFade()
                                        .into(previewImage);

                                Glide.with(getApplicationContext())
                                        .load(TassApplication.getInstance().getUserImage())
                                        .centerCrop()
                                        .placeholder(R.drawable.default_place_holder)
                                        .error(R.drawable.default_place_holder)
                                        .crossFade()
                                        .bitmapTransform(new CircleTransform(getApplicationContext()))
                                        .into(header_prof_img);


                            } else {
                                showError("Error", "Failed to post this problems.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        showError("Error", message);
                    }
                });
            }
        });
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void sendRegistrationToServer(String token) {
        ApiInterface apiService = TassApplication.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.registerDeviceToken(TassApplication.getInstance().getUserID(), token, "A");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200) {
                    Log.i("PushFired", "200");
                } else {
                    try {
                        Log.e("User", response.errorBody().string().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Log error here since request failed
                Log.e("assign", t.toString());

            }
        });
    }

}
