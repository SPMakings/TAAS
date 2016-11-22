package com.spm.taas.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.spm.taas.LandingActivity;
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

import java.io.IOException;

/**
 * Created by saikatpakira on 27/10/16.
 */

public class StatusFragment extends TAASFragment {

    private RecyclerView statusListing = null;
    private View physics, chemistry, mathematics;
    private StatusAdapter statAdapter = null;
    private EditText searchText = null;
    private JSONArray physicsStatus = null, chemStatus = null, mathStatus = null;
    private String currentSelcted = "", cuurentSearch = "by_title";


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

        searchText = (EditText) view.findViewById(R.id.qun_search);

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
                                        ((LandingActivity) getActivity()).questionDetails(qunID);
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
                                        ((LandingActivity) getActivity()).questionDetails(qunID);
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
                                        ((LandingActivity) getActivity()).questionDetails(qunID);
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


        //=============Search Management.


        view.findViewById(R.id.search_filter).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_search_qun, null);
                dialogBuilder.setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();

                if (cuurentSearch.equals("by_title")) {
                    dialogView.findViewById(R.id.title_tick).setVisibility(View.VISIBLE);
                    dialogView.findViewById(R.id.id_tick).setVisibility(View.GONE);
                } else {
                    dialogView.findViewById(R.id.title_tick).setVisibility(View.GONE);
                    dialogView.findViewById(R.id.id_tick).setVisibility(View.VISIBLE);
                }


                dialogView.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                        cuurentSearch = "by_title";
                        dialogView.findViewById(R.id.title_tick).setVisibility(View.VISIBLE);
                        dialogView.findViewById(R.id.id_tick).setVisibility(View.GONE);


                    }
                });

                dialogView.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        alertDialog.dismiss();

                        cuurentSearch = "by_id";
                        dialogView.findViewById(R.id.title_tick).setVisibility(View.GONE);
                        dialogView.findViewById(R.id.id_tick).setVisibility(View.VISIBLE);

                    }
                });

                alertDialog.show();

            }
        });


        view.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (searchText.getText().toString().trim().length() > 0) {
                    getQuestionListingBySearch(currentSelcted, searchText.getText().toString().trim());
                } else {
                    if (currentSelcted.equalsIgnoreCase("mathematics")) {
                        mathematics.performClick();
                    } else if (currentSelcted.equalsIgnoreCase("chemistry")) {
                        chemistry.performClick();
                    } else {
                        physics.performClick();
                    }
                }
            }
        });

    }


    private void getQuestionListing(final String subject_) {
        Log.i("aaign", "getQuestionListing : " + TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "get_email_list?user_id=" +
                TassApplication.getInstance().getUserID() +
                "&subject=" +
                subject_ + "&start=0&count=1000");
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
                                                                ((LandingActivity) getActivity()).questionDetails(qunID);
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


    private void getQuestionListingBySearch(final String subject_, final String searchKey_) {


        //https://urtaas.com/app_control/search_question?
        // subject=physics&user_id=1&search_key=test&search_filter=by_title&start=0&count=10&status=Y
        Log.i("aaign", "getQuestionListing : " + TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "search_question?user_id=" +
                TassApplication.getInstance().getUserID() +
                "&subject=" +
                subject_ + "&search_key=" +
                searchKey_ +
                "&start=0&count=1000&search_filter=" +
                cuurentSearch + "&status=all");

        showProgress();
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER +
                "search_question?user_id=" +
                TassApplication.getInstance().getUserID() +
                "&subject=" +
                subject_ + "&search_key=" +
                searchKey_ +
                "&start=0&count=1000&search_filter=" +
                cuurentSearch + "&status=Y",
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


                                            if (jObject.getJSONArray("data").length() > 0) {
                                                statAdapter = new StatusAdapter(getActivity(), jObject.getJSONArray("data"));
                                                statusListing.setAdapter(statAdapter);
                                            }


                                            if (statAdapter != null) {

                                                statAdapter.addOnItemClicked(new StatusAdapter.OnItemClicked() {
                                                    @Override
                                                    public void onClikced(final String qunID) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                ((LandingActivity) getActivity()).questionDetails(qunID);
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
