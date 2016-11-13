package com.spm.taas.application;

import android.app.Application;
import android.content.SharedPreferences;

import com.cloudinary.Cloudinary;
import com.spm.taas.models.DashBoardModel;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by saikatpakira on 11/10/16.
 */

public class TassApplication extends Application {

    private static TassApplication mInstance;

    private SharedPreferences tassPreference = null;
    private JSONArray countryList = null;
    private boolean needToRefresh = false;

    private static Retrofit retrofit = null;
    private Cloudinary cloudinary = null;

    private LinkedList<DashBoardModel> landingList = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        landingList = new LinkedList<DashBoardModel>();
        if (cloudinary == null) {
            // cloudinary = new Cloudinary("cloudinary://124414451557244:cOKO0XAc8VNEMhU6xoGZzNcGaXA@spmakings");
            Map config = new HashMap();
            config.put("cloud_name", "spmakings");
            cloudinary = new Cloudinary(config);
        }
    }


    public static synchronized Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(TassConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public LinkedList<DashBoardModel> getLandingList() {
        return landingList;
    }

    public void setLandingList(LinkedList<DashBoardModel> landingList) {
        this.landingList = landingList;
    }

    public static synchronized TassApplication getInstance() {
        return mInstance;
    }


    public boolean isAlreadyLoggedIn() {
        initPreference();
        if (tassPreference.getString("user_id", "").equals("")) {
            return false;
        } else {
            return true;
        }
    }


    public boolean isNeedToRefresh() {
        return needToRefresh;
    }

    public void setNeedToRefresh(boolean needToRefresh) {
        this.needToRefresh = needToRefresh;
    }

    private void initPreference() {
        if (tassPreference == null) {
            tassPreference = getSharedPreferences("SPM_TASS", MODE_PRIVATE);
        }
    }


    public void setUserData(final String userEmail, final String userName, final String userType, final String userID, final String userImage) {
        initPreference();
        SharedPreferences.Editor edit = tassPreference.edit();
        edit.putString("user_id", userID);
        edit.putString("user_email", userEmail);
        edit.putString("user_name", userName);
        edit.putString("user_type", userType);
        edit.putString("user_image", userImage);
        edit.commit();
    }

    public void clearPreferenceData() {
        initPreference();
        SharedPreferences.Editor edit = tassPreference.edit();
        edit.clear();
        edit.commit();
        //=====Clear dashbord data.
        if (landingList != null) {
            landingList.clear();
        }

    }


    public String getUserID() {
        initPreference();
        return tassPreference.getString("user_id", "");
    }

    public String getUserName() {
        initPreference();
        return tassPreference.getString("user_name", "");
    }

    public String getUserImage() {
        initPreference();
        return tassPreference.getString("user_image", "");
    }

    public String getUserType() {
        initPreference();
        return tassPreference.getString("user_type", "");
    }


    public JSONArray getCountryList() {
        return countryList;
    }

    public void setCountryList(JSONArray countryList) {
        this.countryList = countryList;
    }


    public Cloudinary getCloudinator() {
        if (cloudinary == null) {
            // cloudinary = new Cloudinary("cloudinary://124414451557244:cOKO0XAc8VNEMhU6xoGZzNcGaXA@spmakings");
            Map config = new HashMap();
            config.put("cloud_name", "spmakings");
            cloudinary = new Cloudinary(config);
        }
        return cloudinary;
    }
}
