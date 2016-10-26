package com.spm.taas.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.spm.taas.R;
import com.spm.taas.customview.TextViewIkarosLight;
import com.spm.taas.customview.TextViewIkarosRegular;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by saikatpakira on 26/10/16.
 */

public class QuestionSpinnerAdapter extends ArrayAdapter<JSONArray> {

    private Context mContext;
    private JSONArray data;
    LayoutInflater inflater;

    public QuestionSpinnerAdapter(Context mContext, JSONArray objects) {
        super(mContext, 0, new ArrayList<JSONArray>());

        this.mContext = mContext;
        this.data = objects;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.items_spinner_question, parent, false);

        try {
            ((TextViewIkarosRegular) row.findViewById(R.id.question_header)).setText(data.getJSONObject(position).getString("title"));
            ((TextViewIkarosLight) row.findViewById(R.id.question_desc)).setText(data.getJSONObject(position).getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
        }

        return row;
    }
}
