package com.spm.taas;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASActivity;
import com.spm.taas.customview.TextViewIkarosLight;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.logging.Logger;

public class QuestionDetails extends TAASActivity implements View.OnClickListener {


    private TextViewIkarosRegular questionID, questionTitle, questionDetails, attachedProbFiles, ansAssignStatus,
            ansSolvedStatus, paymentRedLine;
    private TextViewIkarosLight problemPosstedOn;
    private View probAttachedHolder, makePayment, assignTo;
    private LinkedList<View> question_file_set, answer_file_set;
    private String SELECTED_SUBJECT = "";


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


        probAttachedHolder = findViewById(R.id.prob_atch_holder);
        makePayment = findViewById(R.id.make_payment);
        assignTo = findViewById(R.id.assig_to);

        problemPosstedOn = (TextViewIkarosLight) findViewById(R.id.qun_posted);

        paymentRedLine = (TextViewIkarosRegular) findViewById(R.id.payment_red_line);
        questionID = (TextViewIkarosRegular) findViewById(R.id.qun_id);
        questionTitle = (TextViewIkarosRegular) findViewById(R.id.qun_ttl);
        questionDetails = (TextViewIkarosRegular) findViewById(R.id.qun_desc);
        attachedProbFiles = (TextViewIkarosRegular) findViewById(R.id.qun_attach);
        ansAssignStatus = (TextViewIkarosRegular) findViewById(R.id.ans_assigned);
        ansSolvedStatus = (TextViewIkarosRegular) findViewById(R.id.ans_solved);

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


        if (TassApplication.getInstance().getUserType().equalsIgnoreCase("student")) {
            //=====Need to set payment amount.

            //========
            paymentRedLine.setVisibility(View.VISIBLE);
            makePayment.setVisibility(View.VISIBLE);
        } else {
            if (TassApplication.getInstance().getUserType().equalsIgnoreCase("admin")) {
                assignTo.setVisibility(View.VISIBLE);
            }
        }

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

        getQuestionDetails(getIntent().getStringExtra("QUN_ID"));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    private void getQuestionDetails(final String questionID_) {

        showProgress();

        Log.i("result_main", TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "question_details?problem_id=" +
                questionID_ + "&user_id=" +
                TassApplication.getInstance().getUserID());


        HttpGetRequest request_ = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "question_details?problem_id=" +
                questionID_ + "&user_id=" +
                TassApplication.getInstance().getUserID(),
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
                                    questionID.setText("Question ID : " + jObject.getJSONObject("question_details").getString("question_id") + "    Subject : " + SELECTED_SUBJECT);
                                    questionTitle.setText("Question Title : " + jObject.getJSONObject("question_details").getString("question_title"));
                                    questionDetails.setText("Description : " + jObject.getJSONObject("question_details").getString("question_desc"));

                                    problemPosstedOn.setText("Posted on " + jObject.getJSONObject("question_details").getString("email_date"));

                                    attachedProbFiles.setText("Attached Files (" + jObject.getJSONObject("question_details").getJSONArray("attachment").length() + ")");

                                    if (jObject.getJSONObject("question_details").getJSONArray("attachment").length() > 0) {
                                        probAttachedHolder.setVisibility(View.VISIBLE);
                                    } else {
                                        probAttachedHolder.setVisibility(View.GONE);
                                    }

                                    Log.i("result_main", jObject.getJSONObject("question_details").getString("assign_date").toString());

                                    if (jObject.getJSONObject("question_details").getString("assign_date").toString().equalsIgnoreCase("null")) {
                                        ansAssignStatus.setText("Question is not assigned yet.");
                                        ansSolvedStatus.setVisibility(View.GONE);
                                        makePayment.setVisibility(View.GONE);
                                        assignTo.setVisibility(View.VISIBLE);
                                    } else {
                                        assignTo.setVisibility(View.GONE);

                                        ansAssignStatus.setText("Assigned to " +
                                                jObject.getJSONObject("question_details").getString("assigned_teacher") +
                                                " on " +
                                                jObject.getJSONObject("question_details").getString("assign_date"));

                                        try {
                                            if (jObject.getJSONObject("solution_details").getString("uploaded_on").toString().equalsIgnoreCase("null")) {
                                                ansSolvedStatus.setText("Question is not solved yet.");
                                                //makePayment.setVisibility(View.GONE);
                                            } else {
                                                ansAssignStatus.setText("Assigned to " +
                                                        jObject.getJSONObject("solution_details").getString("uploaded_on"));
                                                if (jObject.getJSONObject("solution_details").getString("sol_status").equalsIgnoreCase("Not Accessible")) {
                                                    makePayment.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        } catch (Exception e) {
                                            Log.i("result_main", "Error: Solution jeson eliment become array cause no values are there.");
                                            ansSolvedStatus.setVisibility(View.VISIBLE);
                                            ansSolvedStatus.setText("Question is not solved yet.");
                                        }

                                    }

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


                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    ansSolvedStatus.setText("Question is not solved yet.");
                                    makePayment.setVisibility(View.GONE);
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
            getQuestionDetails(getIntent().getStringExtra("QUN_ID"));
        }
    }
}
