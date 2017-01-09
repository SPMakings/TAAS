package com.spm.taas.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.spm.taas.LandingActivity;
import com.spm.taas.R;
import com.spm.taas.adapters.ProblemPreviewAdapter;
import com.spm.taas.adapters.QuestionSpinnerAdapter;
import com.spm.taas.adapters.StatusAdapter;
import com.spm.taas.application.OnImageFetched;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASFragment;
import com.spm.taas.customview.TextViewIkarosRegular;
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
    private Spinner statusList = null, subjectList = null;
    private EditText sunjectTitle;
    private LinkedList<String> selectedImages = null;
    private Dialog dlog_ = null;
    private RecyclerView questionView_ = null;
    private TextViewIkarosRegular tapToSelectQuestion = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solution_upload, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusList = (Spinner) view.findViewById(R.id.status_list);
        subjectList = (Spinner) view.findViewById(R.id.subject_list);
        tapToSelectQuestion = (TextViewIkarosRegular) view.findViewById(R.id.question_list);

        sunjectTitle = (EditText) view.findViewById(R.id.question_title);

        previewHolder = (RecyclerView) view.findViewById(R.id.scrollPreview);
        previewHolder.setHasFixedSize(true);
        previewHolder.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        previewHolder.setItemAnimator(new DefaultItemAnimator());
        pAdapter = new ProblemPreviewAdapter(getActivity(), new LinkedList<String>());
        previewHolder.setAdapter(pAdapter);


        getActivity().findViewById(R.id.status_filter).setVisibility(View.GONE);


        tapToSelectQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionChooser();
            }
        });


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
                            showError("Question Status", "Select a question to post this answer.");
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
                TassConstants.URL_DOMAIN_APP_CONTROLLER + "reply_image_upload", new onHttpResponseListener() {
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
        //=======change here
        //https://urtaas.com/app_control/get_solved_email?teacher_id=63&subject=physics&start=0&count=100
        showProgress();
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "get_solved_email?teacher_id=" +
                TassApplication.getInstance().getUserID() +
                "&subject=" + subject_ + "&start=0&count=1000",
                new onHttpResponseListener() {
                    @Override
                    public void onSuccess(final JSONObject jObject) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
//                                Log.i("dhoperchop", jObject.toString());
                                try {
                                    if (jObject.getJSONArray("data").length() > 0) {


                                        StatusAdapter statAdapter = new StatusAdapter(getActivity(), jObject.getJSONArray("data"));
                                        questionView_.setAdapter(statAdapter);

                                        statAdapter.addOnItemClicked(new StatusAdapter.OnItemClicked() {
                                            @Override
                                            public void onClikced(final String qunID) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (dlog_ != null) {
                                                            dlog_.dismiss();
                                                        }
                                                        SELECTED_QUESTION_ID = qunID;
                                                        tapToSelectQuestion.setText("Selected Question ID: " + SELECTED_QUESTION_ID);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onAssign(String qunID) {
                                                //====No use here.
                                            }
                                        });


                                    } else {
                                        if (dlog_ != null) {
                                            dlog_.dismiss();
                                        }
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

    private void questionChooser() {
        dlog_ = new Dialog(getActivity(), R.style.MaterialDialogSheet);
        dlog_.setCanceledOnTouchOutside(true);
        final View dlog_view_ = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_question_chooser, null);
        dlog_.setContentView(dlog_view_);
        dlog_.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dlog_.getWindow().setGravity(Gravity.BOTTOM);

        dlog_view_.findViewById(R.id.back_me_dlog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dlog_ != null) {
                    dlog_.dismiss();
                }

                if (SELECTED_QUESTION_ID.equals("")) {
                    tapToSelectQuestion.setText("Tap to select question.");
                } else {
                    tapToSelectQuestion.setText("Selected Question ID: " + SELECTED_QUESTION_ID);
                }
            }
        });

        dlog_view_.findViewById(R.id.cross_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dlog_ != null) {
                    dlog_.dismiss();
                }

                if (SELECTED_QUESTION_ID.equals("")) {
                    tapToSelectQuestion.setText("Tap to select question.");
                } else {
                    tapToSelectQuestion.setText("Selected Question ID: " + SELECTED_QUESTION_ID);
                }
            }
        });


        questionView_ = (RecyclerView) dlog_view_.findViewById(R.id.chooser_dialog_list);
        questionView_.setHasFixedSize(true);
        questionView_.setLayoutManager(new LinearLayoutManager(getActivity()));

        dlog_.show();

        SELECTED_QUESTION_ID = "";
        getQuestionList(SELECTED_SUBJECT);
    }


}
