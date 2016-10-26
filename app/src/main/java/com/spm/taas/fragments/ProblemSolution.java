package com.spm.taas.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.spm.taas.LandingActivity;
import com.spm.taas.R;
import com.spm.taas.adapters.ProblemPreviewAdapter;
import com.spm.taas.adapters.QuestionSpinnerAdapter;
import com.spm.taas.application.OnImageFetched;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASFragment;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.HttpPostRequest;
import com.spm.taas.networkmanagement.KeyValuePairModel;
import com.spm.taas.networkmanagement.OkHttpFileUploadRequest;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Created by saikatpakira on 25/10/16.
 */

public class ProblemSolution extends TAASFragment {


    private RecyclerView previewHolder = null;
    private ProblemPreviewAdapter pAdapter = null;
    private ArrayList<String> stateArray = null, subjectsArray = null;
    private String SELECTED_STATUS = "", SELECTED_QUESTION_ID = "", SELECTED_SUBJECT = "";
    private Spinner questionList = null, statusList = null, subjectList = null;
    private EditText sunjectTitle;
    private LinkedList<String> selectedImages = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solution_upload, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionList = (Spinner) view.findViewById(R.id.question_list);
        statusList = (Spinner) view.findViewById(R.id.status_list);
        subjectList = (Spinner) view.findViewById(R.id.subject_list);

        sunjectTitle = (EditText) view.findViewById(R.id.question_title);

        previewHolder = (RecyclerView) view.findViewById(R.id.scrollPreview);
        previewHolder.setHasFixedSize(true);
        previewHolder.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        previewHolder.setItemAnimator(new DefaultItemAnimator());
        pAdapter = new ProblemPreviewAdapter(getActivity(), new LinkedList<String>());
        previewHolder.setAdapter(pAdapter);


        statusList.setAdapter(initStatusListAdapter());
        statusList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_STATUS = stateArray.get(position).toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectList.setAdapter(initSubjectAdapter());
        subjectList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_SUBJECT = subjectsArray.get(position).toLowerCase();
                getQuestionList(SELECTED_SUBJECT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pAdapter.addOnItemClicked(new ProblemPreviewAdapter.OnItemClicked() {
            @Override
            public void onClick(final String imagePath_) {
                getActivity().runOnUiThread(new Runnable() {
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

        view.findViewById(R.id.upload_image).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (pAdapter.getCUrrentArray().size() < 3) {
                    ((LandingActivity) getActivity()).fetchPictureFromGallery(new OnImageFetched() {
                        @Override
                        public void onSuccess(final String path_) {
                            getActivity().runOnUiThread(new Runnable() {
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), errorMessage_, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    showError("Upload", "At most 3 images you can upload.");
                }
            }
        });


        view.findViewById(R.id.save_problems).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (sunjectTitle.getText().toString().trim().length() > 0) {
                    if (pAdapter.getCUrrentArray().size() > 0) {
                        if (!SELECTED_QUESTION_ID.equalsIgnoreCase("")) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Posting Problem")
                                    .setMessage("Do you want to post this solution?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {


                                            LinkedList<KeyValuePairModel> param_ = new LinkedList<KeyValuePairModel>();
                                            KeyValuePairModel temp_ = new KeyValuePairModel();
                                            temp_.add("reply", sunjectTitle.getText().toString().trim());
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
                    sunjectTitle.setError("Enter your reply to student.");
                }
            }
        });
    }


    private ArrayAdapter<String> initStatusListAdapter() {
        stateArray = new ArrayList<String>();
        stateArray.add("Solved");
        stateArray.add("Cancelled");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stateArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SELECTED_STATUS = "solved";
        return dataAdapter;
    }

    private ArrayAdapter<String> initSubjectAdapter() {
        subjectsArray = new ArrayList<String>();
        subjectsArray.add("Physics");
        subjectsArray.add("Chemistry");
        subjectsArray.add("Mathematics");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, subjectsArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SELECTED_STATUS = "physics";
        return dataAdapter;
    }

    private void postSolnFile(final LinkedList<KeyValuePairModel> data_, final String fileUploadTag_) {

        OkHttpFileUploadRequest request = new OkHttpFileUploadRequest(data_,
                fileUploadTag_, selectedImages.get(0),
                TassConstants.URL_DOMAIN_APP_CONTROLLER + "reply_student", new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                getActivity().runOnUiThread(new Runnable() {
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
                                    sunjectTitle.setText("");
                                    pAdapter.getCUrrentArray().clear();
                                    pAdapter.notifyDataSetChanged();
                                    previewHolder.setVisibility(View.GONE);
                                    TassApplication.getInstance().setNeedToRefresh(true);
                                    Toast.makeText(getActivity(), "Problem posted successfully.", Toast.LENGTH_SHORT).show();
                                    getQuestionList(SELECTED_SUBJECT);
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
                getActivity().runOnUiThread(new Runnable() {
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
                getActivity().runOnUiThread(new Runnable() {
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
                                postSolnFile(data_, "userfile");

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
                getActivity().runOnUiThread(new Runnable() {
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

    private void getQuestionList(final String subject_) {
        showProgress();
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "get_email_list?user_id=" +
                TassApplication.getInstance().getUserID() +
                "&subject=" + subject_,
                new onHttpResponseListener() {
                    @Override
                    public void onSuccess(final JSONObject jObject) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
//                                Log.i("dhoperchop", TassConstants.URL_DOMAIN_APP_CONTROLLER +
//                                        "get_email_list?user_id=" +
//                                        TassApplication.getInstance().getUserID() +
//                                        "&subject=" + subject_);
//                                Log.i("dhoperchop", jObject.toString());
                                try {
                                    if (jObject.getJSONArray("data").length() > 0) {
                                        questionList.setAdapter(new QuestionSpinnerAdapter(getActivity(), jObject.getJSONArray("data")));
                                        questionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                try {
                                                    SELECTED_QUESTION_ID = jObject.getJSONArray("data").getJSONObject(position).getString("question_id").toLowerCase();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                    } else {
                                        showError("Question Status", "There is no assigned question of " + subject_.toUpperCase() + " for you to post solution. Please select another subject for questions.");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    showError("Error", e.toString());
                                    SELECTED_QUESTION_ID = "";
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String message) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                                showError("Error", message);
                                SELECTED_QUESTION_ID = "";
                            }
                        });
                    }
                });
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


}
