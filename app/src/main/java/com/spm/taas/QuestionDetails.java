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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.spm.taas.adapters.ProblemPreviewAdapter;
import com.spm.taas.application.OnImageFetched;
import com.spm.taas.application.RealPathHelper;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASActivity;
import com.spm.taas.customview.TextViewIkarosLight;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.HttpPostRequest;
import com.spm.taas.networkmanagement.KeyValuePairModel;
import com.spm.taas.networkmanagement.OkHttpFileUploadRequest;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

public class QuestionDetails extends TAASActivity implements View.OnClickListener {


    private TextViewIkarosRegular questionID, questionTitle, questionDetails,
            attachedProbFiles, ansAssignStatus, solutionDesc, attachedProbFileSoln,
            ansSolvedStatus;
    private TextViewIkarosLight problemPosstedOn;
    private View probAttachedHolder, assignTo, uploadSoln, uploadSolnBucket, solnAttachHolder;
    private LinkedList<View> question_file_set, answer_file_set;
    private String SELECTED_SUBJECT = "", SELECTED_STATUS = "", mCurrentPhotoPath = "", SELECTED_QUESTION_ID = "";

    private EditText replyDescription;
    private Spinner solnStatus;

    private RecyclerView previewHolder = null;
    private ProblemPreviewAdapter pAdapter = null;

    private OnImageFetched imageCallback = null;
    private final int STORAGE_PERMISSION_CODE = 1, PICK_IMAGE = 1000, PICK_CAMERA = 2000;
    private ArrayList<String> stateArray = null;
    private LinkedList<String> selectedImages = null;

    private boolean needRefresh = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Problem Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question_file_set = new LinkedList<View>();
        answer_file_set = new LinkedList<View>();


        previewHolder = (RecyclerView) findViewById(R.id.scrollPreview);
        previewHolder.setHasFixedSize(true);
        previewHolder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        previewHolder.setItemAnimator(new DefaultItemAnimator());
        pAdapter = new ProblemPreviewAdapter(this, new LinkedList<String>());
        previewHolder.setAdapter(pAdapter);

        pAdapter.addOnItemClicked(new ProblemPreviewAdapter.OnItemClicked() {
            @Override
            public void onClick(final String imagePath_) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pAdapter.removeItems(imagePath_);
                        if (pAdapter.getCUrrentArray().size() == 0) {
                            previewHolder.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


        replyDescription = (EditText) findViewById(R.id.ques_reply);

        solnStatus = (Spinner) findViewById(R.id.soln_tag);
        solnStatus.setAdapter(initStatusListAdapter());
        solnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_STATUS = stateArray.get(position).toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        probAttachedHolder = findViewById(R.id.prob_atch_holder);//====initial Gone
        solnAttachHolder = findViewById(R.id.soln_atch_holder);//====initial Gone
        assignTo = findViewById(R.id.assig_to);//====initial Gone
        uploadSoln = findViewById(R.id.upload_solution);//====initial Gone
        uploadSolnBucket = findViewById(R.id.uploadSolnBucket);//====initial Gone

        problemPosstedOn = (TextViewIkarosLight) findViewById(R.id.qun_posted);

        questionID = (TextViewIkarosRegular) findViewById(R.id.qun_id);
        questionTitle = (TextViewIkarosRegular) findViewById(R.id.qun_ttl);
        questionDetails = (TextViewIkarosRegular) findViewById(R.id.qun_desc);
        attachedProbFiles = (TextViewIkarosRegular) findViewById(R.id.qun_attach);
        ansAssignStatus = (TextViewIkarosRegular) findViewById(R.id.ans_assigned);
        ansSolvedStatus = (TextViewIkarosRegular) findViewById(R.id.ans_solved);//====initial Gone
        solutionDesc = (TextViewIkarosRegular) findViewById(R.id.ans_desciption);//====initial Gone

        attachedProbFileSoln = (TextViewIkarosRegular) findViewById(R.id.soln_attach);

        //========Question attach files.

        question_file_set.add(findViewById(R.id.qun_attch_1));
        question_file_set.add(findViewById(R.id.qun_attch_2));
        question_file_set.add(findViewById(R.id.qun_attch_3));
        question_file_set.add(findViewById(R.id.qun_attch_4));
        question_file_set.add(findViewById(R.id.qun_attch_5));

        for (int i = 0; i < question_file_set.size(); i++) {
            question_file_set.get(i).setOnClickListener(this);
        }

        //====================

        //========Question attach files.

        answer_file_set.add(findViewById(R.id.oln_attch_1));
        answer_file_set.add(findViewById(R.id.oln_attch_2));
        answer_file_set.add(findViewById(R.id.oln_attch_3));
        answer_file_set.add(findViewById(R.id.oln_attch_4));
        answer_file_set.add(findViewById(R.id.oln_attch_5));

        for (int i = 0; i < answer_file_set.size(); i++) {
            answer_file_set.get(i).setOnClickListener(this);
        }

        //====================


        assignTo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(QuestionDetails.this, "Working on...", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(QuestionDetails.this, AssignActivity.class);
                i.putExtra("subject", SELECTED_SUBJECT);
                i.putExtra("questionID", getIntent().getStringExtra("QUN_ID"));
                startActivityForResult(i, 100);
            }
        });


        findViewById(R.id.upload_image).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (pAdapter.getCUrrentArray().size() < 3) {
                    fetchPictureFromGallery(new OnImageFetched() {
                        @Override
                        public void onSuccess(final String path_) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("imagepath", path_);
                                    pAdapter.addItems(path_);
                                    if (previewHolder.getVisibility() == View.GONE) {
                                        previewHolder.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(final String errorMessage_) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), errorMessage_, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    showError("Upload", "At most 3 images you can upload.");
                }
            }
        });


        uploadSoln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadSolnBucket.getVisibility() == View.GONE) {
                    uploadSolnBucket.setVisibility(View.VISIBLE);
                } else {
                    if (replyDescription.getText().toString().trim().length() > 0) {
                        if (pAdapter.getCUrrentArray().size() > 0) {
                            if (!SELECTED_QUESTION_ID.equalsIgnoreCase("")) {
                                new AlertDialog.Builder(QuestionDetails.this)
                                        .setTitle("Posting Problem")
                                        .setMessage("Do you want to post this solution?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {


                                                LinkedList<KeyValuePairModel> param_ = new LinkedList<KeyValuePairModel>();
                                                KeyValuePairModel temp_ = new KeyValuePairModel();
                                                temp_.add("reply", replyDescription.getText().toString().trim());
                                                param_.add(temp_);


                                                temp_ = new KeyValuePairModel();
                                                temp_.add("status", SELECTED_STATUS);
                                                param_.add(temp_);

                                                Log.i("response", TassApplication.getInstance().getUserID());

                                                temp_ = new KeyValuePairModel();
                                                temp_.add("teacher", TassApplication.getInstance().getUserID());
                                                param_.add(temp_);

                                                temp_ = new KeyValuePairModel();
                                                temp_.add("question_id", SELECTED_QUESTION_ID);
                                                param_.add(temp_);

                                                selectedImages = pAdapter.getCUrrentArray();
                                                //====API Fire.
                                                postMySoln(param_);

                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                            } else {
                                showError("Question Status", "There is no assigned question of selected subjec for you to post solution.");
                            }
                        } else {
                            showError("Problem", "You have to post at least 1 image(Max 3) of your problem.");
                        }
                    } else {
                        replyDescription.setError("Enter your reply to student.");
                    }
                }
            }
        });

        getQuestionDetails(getIntent().getStringExtra("QUN_ID"));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        if (needRefresh) {
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_CANCELED, i);
        }
        finish();
    }

    private void getQuestionDetails(final String questionID_) {

        showProgress();

        Log.i("result_main", TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "question_details?problem_id=" +
                questionID_ + "&user_id=" +
                TassApplication.getInstance().getUserID());


        HttpGetRequest request_ = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "question_details?problem_id=" +
                questionID_ + "&user_id=1",
                new onHttpResponseListener() {
                    @Override
                    public void onSuccess(final JSONObject jObject) {

                        Log.i("result_main", jObject.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();

                                try {
                                    SELECTED_SUBJECT = jObject.getJSONObject("question_details").getString("subject");
                                    SELECTED_QUESTION_ID = jObject.getJSONObject("question_details").getString("question_id");
                                    questionID.setText("Question ID : " + SELECTED_QUESTION_ID + "    Subject : " + SELECTED_SUBJECT);
                                    questionTitle.setText("Question Title : " + jObject.getJSONObject("question_details").getString("question_title"));
                                    questionDetails.setText("Description : " + jObject.getJSONObject("question_details").getString("question_desc"));

                                    problemPosstedOn.setText("Posted on " + jObject.getJSONObject("question_details").getString("email_date"));

                                    attachedProbFiles.setText("Attached Files (" + jObject.getJSONObject("question_details").getJSONArray("attachment").length() + ")");

                                    if (jObject.getJSONObject("question_details").getJSONArray("attachment").length() > 0) {
                                        probAttachedHolder.setVisibility(View.VISIBLE);
                                        //=========Question Attachments Management.

                                        JSONArray quesAtach = jObject.getJSONObject("question_details").getJSONArray("attachment");

                                        for (int i = 0; i < question_file_set.size(); i++) {
                                            if (quesAtach.length() > i) {
                                                question_file_set.get(i).setVisibility(View.VISIBLE);
                                                question_file_set.get(i).setTag("" + quesAtach.getString(i));
                                            } else {
                                                question_file_set.get(i).setVisibility(View.GONE);
                                            }
                                        }

                                    } else {
                                        probAttachedHolder.setVisibility(View.GONE);
                                    }
                                    //========Question Details Done.=============================

                                    if (jObject.getJSONObject("question_details").getString("assign_date").toString().equalsIgnoreCase("null")) {

                                        ansAssignStatus.setText("Question is not assigned yet.");
                                        ansSolvedStatus.setVisibility(View.GONE);
                                        assignTo.setVisibility(View.VISIBLE);
                                        attachedProbFileSoln.setVisibility(View.GONE);

                                        if (TassApplication.getInstance().getUserType().equalsIgnoreCase("admin")) {
                                            assignTo.setVisibility(View.VISIBLE);
                                            //====Add code.
                                        }

                                    } else {
                                        //attachedProbFileSoln.setVisibility(View.VISIBLE);
                                        assignTo.setVisibility(View.GONE);
                                        ansAssignStatus.setText("Assigned to " +
                                                jObject.getJSONObject("question_details").getString("assigned_teacher") +
                                                " on " +
                                                jObject.getJSONObject("question_details").getString("assign_date"));

                                    }

                                    //========Assign Details Done.=============================


                                    Log.i("result_main", jObject.getJSONObject("question_details").getString("assign_date").toString());

                                    try {
                                        JSONObject solutionJSONBlock = jObject.getJSONObject("solution_details");
                                        //======
                                        ansSolvedStatus.setText("Solution Uploaded on  " + solutionJSONBlock.getString("uploaded_on"));
                                        ansSolvedStatus.setVisibility(View.VISIBLE);
                                        solutionDesc.setText("Reply : " + solutionJSONBlock.getString("tutor_comment"));
                                        solutionDesc.setVisibility(View.VISIBLE);
                                        JSONArray solnFile_ = solutionJSONBlock.getJSONObject("sol_attachment").getJSONArray("file");
                                        attachedProbFileSoln.setText("Attached Files (" + solnFile_.length() + ")");
                                        attachedProbFileSoln.setVisibility(View.VISIBLE);

                                        if (solnFile_.length() > 0) {

                                            solnAttachHolder.setVisibility(View.VISIBLE);
                                            //=========Solution Attachments Management.
                                            for (int i = 0; i < answer_file_set.size(); i++) {
                                                if (solnFile_.length() > i) {
                                                    answer_file_set.get(i).setVisibility(View.VISIBLE);
                                                    answer_file_set.get(i).setTag("" + solnFile_.getString(i));
                                                } else {
                                                    answer_file_set.get(i).setVisibility(View.GONE);
                                                }
                                            }

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.i("result_main", "Error: Solution jeson eliment become array cause no values are there.");
                                        ansSolvedStatus.setVisibility(View.VISIBLE);
                                        ansSolvedStatus.setText("Question is not solved yet.");

                                        if (TassApplication.getInstance().getUserType().equalsIgnoreCase("teacher")) {
                                            uploadSoln.setVisibility(View.VISIBLE);
                                            //====Add code.
                                        }
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
                                showError("Question Details", message);
                            }
                        });
                    }
                });
        request_.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onClick(View v) {

        Log.i("dhoppp", String.valueOf(v.getTag()));
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("" + String.valueOf(v.getTag())));
        startActivity(i);

//        Toast.makeText(this, "Working on...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            needRefresh = true;
            assignTo.setVisibility(View.GONE);
            getQuestionDetails(getIntent().getStringExtra("QUN_ID"));
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            //content://com.google.android.apps.photos.contentpr
            Log.i("Image", selectedImage.toString());

            try {
                if (imageCallback != null) {
                    //Log.i("Camera", RealPathHelper.getPath(QuestionDetails.this, selectedImage));

                    imageCallback.onSuccess(RealPathHelper.getPath(QuestionDetails.this, selectedImage));
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
        } else {
            if (imageCallback != null) {
                imageCallback.onError("You haven't select any image.");
            }
        }
    }


    //=======Image fetching code.

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
                    Toast.makeText(QuestionDetails.this, "Failed to create file.", Toast.LENGTH_SHORT).show();
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

    private ArrayAdapter<String> initStatusListAdapter() {
        stateArray = new ArrayList<String>();
        stateArray.add("Solved");
        stateArray.add("Cancelled");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SELECTED_STATUS = "solved";
        return dataAdapter;
    }

    private void postSolnFile(final LinkedList<KeyValuePairModel> data_, final String fileUploadTag_) {

        OkHttpFileUploadRequest request = new OkHttpFileUploadRequest(data_,
                fileUploadTag_, selectedImages.get(0),
                TassConstants.URL_DOMAIN_APP_CONTROLLER + "reply_image_upload", new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Log.i("response", jObject.toString());
                        try {
                            if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {

                                selectedImages.remove(0);
                                if (selectedImages.size() > 0) {
                                    hideProgress();
                                    showProgress("Uploading files " + (selectedImages.size() + 1));
                                    postSolnFile(data_, "userfile");
                                } else {
                                    hideProgress();
                                    replyDescription.setText("");
                                    pAdapter.getCUrrentArray().clear();
                                    pAdapter.notifyDataSetChanged();
                                    previewHolder.setVisibility(View.GONE);
                                    TassApplication.getInstance().setNeedToRefresh(true);
                                    Toast.makeText(getApplicationContext(), "Solution uploaded successfully.", Toast.LENGTH_SHORT).show();

                                    uploadSolnBucket.setVisibility(View.GONE);
                                    uploadSoln.setVisibility(View.GONE);

                                    needRefresh = true;
                                    getQuestionDetails(getIntent().getStringExtra("QUN_ID"));
                                }

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

    private void postMySoln(final LinkedList<KeyValuePairModel> data_) {
        showProgress();
        HttpPostRequest rewuest_ = new HttpPostRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER + "reply_student",
                data_, new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // hideProgress();

                        try {
                            if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {

                                LinkedList<KeyValuePairModel> param_ = new LinkedList<KeyValuePairModel>();
                                KeyValuePairModel temp_ = new KeyValuePairModel();
                                temp_.add("reply_id", jObject.getString("reply_id"));
                                param_.add(temp_);
                                TassApplication.getInstance().setNeedToRefresh(true);
                                postSolnFile(param_, "userfile");

                            } else {
                                hideProgress();
                                showError("Error", "Failed to post your problem.");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showError("Error", e.toString());
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
        rewuest_.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
