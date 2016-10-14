package com.spm.taas.application;

import android.app.Application;
import android.content.SharedPreferences;

import org.json.JSONArray;

/**
 * Created by saikatpakira on 11/10/16.
 */

public class TassApplication extends Application {

    private static TassApplication mInstance;

    private SharedPreferences tassPreference = null;
    private JSONArray countryList = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
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


    private void initPreference() {
        if (tassPreference == null) {
            tassPreference = getSharedPreferences("SPM_TASS", MODE_PRIVATE);
        }
    }


    public void setUserData(final String userEmail, final String userName, final String userType, final String userID) {
        initPreference();
        SharedPreferences.Editor edit = tassPreference.edit();
        edit.putString("user_id", userID);
        edit.putString("user_email", userEmail);
        edit.putString("user_name", userName);
        edit.putString("user_type", userType);
        edit.commit();
    }

    public void clearPreferenceData() {
        initPreference();
        SharedPreferences.Editor edit = tassPreference.edit();
        edit.clear();
        edit.commit();
    }


    public String getUserID(){
        initPreference();
        return tassPreference.getString("user_id","");
    }


    public JSONArray getCountryList() {
        return countryList;
    }

    public void setCountryList(JSONArray countryList) {
        this.countryList = countryList;
    }
}
