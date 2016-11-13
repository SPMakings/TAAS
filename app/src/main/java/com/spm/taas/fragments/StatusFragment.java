package com.spm.taas.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spm.taas.LandingActivity;
import com.spm.taas.QuestionDetails;
import com.spm.taas.R;
import com.spm.taas.adapters.StatusAdapter;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASFragment;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by saikatpakira on 27/10/16.
 */

public class StatusFragment extends TAASFragment {

    private RecyclerView statusListing = null;
    private View physics, chemistry, mathematics;
    private StatusAdapter statAdapter = null;
    private JSONArray physicsStatus = null, chemStatus = null, mathStatus = null;
    private String currentSelcted = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        physics = view.findViewById(R.id.status_physics);
        chemistry = view.findViewById(R.id.status_chemistry);
        mathematics = view.findViewById(R.id.status_math);

        statusListing = (RecyclerView) view.findViewById(R.id.question_status);
        statusListing.setHasFixedSize(true);
        statusListing.setLayoutManager(new LinearLayoutManager(getActivity()));

        //pageStatus = PAGE_STATUS.PHYSICS;
        getQuestionListing("physics");
        currentSelcted = "physics";

        physics.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //pageStatus = PAGE_STATUS.PHYSICS;
                physics.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tabSelected));
                chemistry.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                mathematics.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));


                if (physicsStatus != null) {
                    statAdapter = new StatusAdapter(getActivity(), physicsStatus);
                    statusListing.setAdapter(statAdapter);

                    if (statAdapter != null) {

                        statAdapter.addOnItemClicked(new StatusAdapter.OnItemClicked() {
                            @Override
                            public void onClikced(final String qunID) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getActivity(), QuestionDetails.class);
                                        i.putExtra("QUN_ID", qunID);
                                        startActivity(i);
                                    }
                                });
                            }

                            @Override
                            public void onAssign(final String qunID) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LandingActivity) getActivity()).assignTeacher(qunID, "physics");
                                    }
                                });
                            }
                        });
                    }

                } else {
                    getQuestionListing("physics");
                }

                currentSelcted = "physics";

            }
        });


        chemistry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //pageStatus = PAGE_STATUS.CHEMISTRY;
                physics.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                chemistry.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tabSelected));
                mathematics.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));

                if (chemStatus != null) {
                    statAdapter = new StatusAdapter(getActivity(), chemStatus);
                    statusListing.setAdapter(statAdapter);

                    if (statAdapter != null) {

                        statAdapter.addOnItemClicked(new StatusAdapter.OnItemClicked() {
                            @Override
                            public void onClikced(final String qunID) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getActivity(), QuestionDetails.class);
                                        i.putExtra("QUN_ID", qunID);
                                        startActivity(i);
                                    }
                                });
                            }

                            @Override
                            public void onAssign(final String qunID) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LandingActivity) getActivity()).assignTeacher(qunID, "chemistry");
                                    }
                                });
                            }
                        });
                    }

                } else {
                    getQuestionListing("chemistry");
                }

                currentSelcted = "chemistry";
            }
        });

        mathematics.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //pageStatus = PAGE_STATUS.MATHEMITCS;
                physics.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                chemistry.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                mathematics.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tabSelected));

                if (mathStatus != null) {
                    statAdapter = new StatusAdapter(getActivity(), mathStatus);
                    statusListing.setAdapter(statAdapter);

                    if (statAdapter != null) {

                        statAdapter.addOnItemClicked(new StatusAdapter.OnItemClicked() {
                            @Override
                            public void onClikced(final String qunID) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getActivity(), QuestionDetails.class);
                                        i.putExtra("QUN_ID", qunID);
                                        startActivity(i);
                                    }
                                });
                            }

                            @Override
                            public void onAssign(final String qunID) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LandingActivity) getActivity()).assignTeacher(qunID, "mathematics");
                                    }
                                });
                            }
                        });
                    }

                } else {
                    getQuestionListing("mathematics");
                }

                currentSelcted = "mathematics";

            }
        });


        //==========

        ((LandingActivity) getActivity()).addOnNeedRefresh(new LandingActivity.onNeedRefresh() {
            @Override
            public void onRefresh() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getQuestionListing(currentSelcted);
                    }
                });
            }
        });

    }


    private void getQuestionListing(final String subject_) {
        Log.i("aaign", "getQuestionListing : " + subject_);
        showProgress();
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "get_email_list?user_id=" +
                TassApplication.getInstance().getUserID() +
                "&subject=" +
                subject_ + "&start=0&count=1000",
                new onHttpResponseListener() {
                    @Override
                    public void onSuccess(final JSONObject jObject) {
                        //Log.i("result_main", jObject.toString());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgress();
                                    try {
                                        if (jObject.getJSONArray("data").length() > 0) {

                                            if (subject_.equalsIgnoreCase("physics")) {

                                                physicsStatus = jObject.getJSONArray("data");
                                                statAdapter = new StatusAdapter(getActivity(), physicsStatus);
                                                statusListing.setAdapter(statAdapter);

                                            } else if (subject_.equalsIgnoreCase("chemistry")) {

                                                chemStatus = jObject.getJSONArray("data");
                                                statAdapter = new StatusAdapter(getActivity(), chemStatus);
                                                statusListing.setAdapter(statAdapter);

                                            } else {

                                                mathStatus = jObject.getJSONArray("data");
                                                statAdapter = new StatusAdapter(getActivity(), mathStatus);
                                                statusListing.setAdapter(statAdapter);

                                            }

                                            if (statAdapter != null) {

                                                statAdapter.addOnItemClicked(new StatusAdapter.OnItemClicked() {
                                                    @Override
                                                    public void onClikced(final String qunID) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Intent i = new Intent(getActivity(), QuestionDetails.class);
                                                                i.putExtra("QUN_ID", qunID);
                                                                startActivity(i);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onAssign(final String qunID) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                ((LandingActivity) getActivity()).assignTeacher(qunID, subject_);
                                                            }
                                                        });
                                                    }
                                                });
                                            }


                                        } else {
                                            showError("Status", "No question found for this subject.");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        showError("Status", e.toString());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(final String message) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgress();
                                    showError("Status", message);
                                }
                            });
                        }
                    }
                });
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

}
