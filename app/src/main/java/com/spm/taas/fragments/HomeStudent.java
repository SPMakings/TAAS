package com.spm.taas.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spm.taas.R;
import com.spm.taas.adapters.DashBoardAdapter;
import com.spm.taas.application.TassApplication;
import com.spm.taas.application.TassConstants;
import com.spm.taas.baseclass.TAASFragment;
import com.spm.taas.models.DashBoardModel;
import com.spm.taas.networkmanagement.HttpGetRequest;
import com.spm.taas.networkmanagement.onHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by saikatpakira on 09/10/16.
 */

public class HomeStudent extends TAASFragment {


    private RecyclerView mainLandingView = null;
    private View loader = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frgaments_student_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainLandingView = (RecyclerView) view.findViewById(R.id.landing_dashboard);
        loader = view.findViewById(R.id.progress);

        mainLandingView.setHasFixedSize(false);
        mainLandingView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainLandingView.setItemAnimator(new DefaultItemAnimator());

        getActivity().findViewById(R.id.status_filter).setVisibility(View.GONE);


        if (TassApplication.getInstance().getLandingList().size() == 0 || TassApplication.getInstance().isNeedToRefresh()) {
            getLandingData("show_all");
        } else {
            loader.setVisibility(View.GONE);
            mainLandingView.setAdapter(new DashBoardAdapter(getContext(), TassApplication.getInstance().getLandingList()));
        }


    }


    private void getLandingData(final String filter_) {
        loader.setVisibility(View.VISIBLE);
        Log.i("dhoppp", TassConstants.URL_DOMAIN_APP_CONTROLLER + "dashboard?user_id=" + TassApplication.getInstance().getUserID() + "&filter=" + filter_);
        HttpGetRequest request = new HttpGetRequest(TassConstants.URL_DOMAIN_APP_CONTROLLER + "dashboard?user_id=" + TassApplication.getInstance().getUserID() + "&filter=" + filter_, new onHttpResponseListener() {
            @Override
            public void onSuccess(final JSONObject jObject) {
                try {
                    Log.i("dhoppp", TassConstants.URL_DOMAIN_APP_CONTROLLER + "dashboard?user_id=" + TassApplication.getInstance().getUserID() + "&filter=" + filter_);
                    Log.i("dhoppp", jObject.toString());
                    if (jObject.getString("status").equalsIgnoreCase("SUCCESS")) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loader.setVisibility(View.GONE);
                                    try {
                                        mainLandingView.setAdapter(new DashBoardAdapter(getContext(), initData(jObject.getJSONObject("data"))));
                                        TassApplication.getInstance().setNeedToRefresh(false);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loader.setVisibility(View.GONE);
                                    showError("TAAS", "" + "Failed to fetch data.");
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                        showError("TAAS", message);
                    }
                });
            }
        });
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private LinkedList<DashBoardModel> initData(final JSONObject mainObj_) throws JSONException {

        LinkedList<DashBoardModel> mainData_ = new LinkedList<DashBoardModel>();

        DashBoardModel temp_ = new DashBoardModel();
        temp_.setHeader(true);
        temp_.setHeaderColor(R.color.problems_new);
        temp_.setHeaderCount(mainObj_.getJSONObject("new_count").getString("total"));
        temp_.setHeaderName("New Problems");
        temp_.setSubjectName("Mathematics");
        temp_.setSubjectCount(mainObj_.getJSONObject("new_count").getString("Mathematics"));
        mainData_.add(temp_);


        temp_ = new DashBoardModel();
        temp_.setHeader(false);
        temp_.setSubjectName("Physics");
        temp_.setSubjectCount(mainObj_.getJSONObject("new_count").getString("Physics"));
        mainData_.add(temp_);


        temp_ = new DashBoardModel();
        temp_.setHeader(false);
        temp_.setSubjectName("Chemistry");
        temp_.setSubjectCount(mainObj_.getJSONObject("new_count").getString("Chemistry"));
        mainData_.add(temp_);


        //=============================

        temp_ = new DashBoardModel();
        temp_.setHeader(true);
        temp_.setHeaderColor(R.color.problems_solved);
        temp_.setHeaderCount(mainObj_.getJSONObject("solved_count").getString("total"));
        temp_.setHeaderName("Solved Problems");
        temp_.setSubjectName("Mathematics");
        temp_.setSubjectCount(mainObj_.getJSONObject("solved_count").getString("Mathematics"));
        mainData_.add(temp_);


        temp_ = new DashBoardModel();
        temp_.setHeader(false);
        temp_.setSubjectName("Physics");
        temp_.setSubjectCount(mainObj_.getJSONObject("solved_count").getString("Physics"));
        mainData_.add(temp_);


        temp_ = new DashBoardModel();
        temp_.setHeader(false);
        temp_.setSubjectName("Chemistry");
        temp_.setSubjectCount(mainObj_.getJSONObject("solved_count").getString("Chemistry"));
        mainData_.add(temp_);


        //=============================

        temp_ = new DashBoardModel();
        temp_.setHeader(true);
        temp_.setHeaderColor(R.color.problems_assigned);
        temp_.setHeaderCount(mainObj_.getJSONObject("assigned_count").getString("total"));
        temp_.setHeaderName("Assigned Problems");
        temp_.setSubjectName("Mathematics");
        temp_.setSubjectCount(mainObj_.getJSONObject("assigned_count").getString("Mathematics"));
        mainData_.add(temp_);


        temp_ = new DashBoardModel();
        temp_.setHeader(false);
        temp_.setSubjectName("Physics");
        temp_.setSubjectCount(mainObj_.getJSONObject("assigned_count").getString("Physics"));
        mainData_.add(temp_);


        temp_ = new DashBoardModel();
        temp_.setHeader(false);
        temp_.setSubjectName("Chemistry");
        temp_.setSubjectCount(mainObj_.getJSONObject("assigned_count").getString("Chemistry"));
        mainData_.add(temp_);

        //=============================

        temp_ = new DashBoardModel();
        temp_.setHeader(true);
        temp_.setHeaderColor(R.color.problems_cancel);
        temp_.setHeaderCount(mainObj_.getJSONObject("cancel_count").getString("total"));
        temp_.setHeaderName("Cancelled Problems");
        temp_.setSubjectName("Mathematics");
        temp_.setSubjectCount(mainObj_.getJSONObject("cancel_count").getString("Mathematics"));
        mainData_.add(temp_);


        temp_ = new DashBoardModel();
        temp_.setHeader(false);
        temp_.setSubjectName("Physics");
        temp_.setSubjectCount(mainObj_.getJSONObject("cancel_count").getString("Physics"));
        mainData_.add(temp_);


        temp_ = new DashBoardModel();
        temp_.setHeader(false);
        temp_.setSubjectName("Chemistry");
        temp_.setSubjectCount(mainObj_.getJSONObject("cancel_count").getString("Chemistry"));
        mainData_.add(temp_);

        TassApplication.getInstance().setLandingList(mainData_);

        return mainData_;
    }
}
