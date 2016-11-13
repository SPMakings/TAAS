package com.spm.taas.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.spm.taas.R;
import com.spm.taas.application.TassApplication;
import com.spm.taas.customview.TextViewIkarosLight;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.models.DashBoardModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;

/**
 * Created by saikatpakira on 27/10/16.
 */

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolderDash> {

    private Context mContext;
    private JSONArray mainData;
    private LayoutInflater inflater = null;
    private OnItemClicked callback = null;

    public StatusAdapter(final Context mContext, final JSONArray mainData) {

        this.mContext = mContext;
        this.mainData = mainData;
        inflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getItemViewType(int position) {

        try {
            if (TassApplication.getInstance().getUserType().equalsIgnoreCase("admin") &&
                    mainData.getJSONObject(position).getString("assign_date").toString().equalsIgnoreCase("null")) {
                return 1;
            } else {
                return 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
//        return super.getItemViewType(position);
    }

    @Override
    public StatusAdapter.ViewHolderDash onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return (new StatusAdapter.ViewHolderDash(inflater.inflate(R.layout.item_status_admin, parent, false), viewType));

        } else {
            return (new StatusAdapter.ViewHolderDash(inflater.inflate(R.layout.items_status, parent, false), viewType));

        }
    }

    @Override
    public void onBindViewHolder(StatusAdapter.ViewHolderDash holder, final int position) {


        try {

//            if (TassApplication.getInstance().getUserType().equalsIgnoreCase("teacher")) {
//                holder.assigned.setVisibility(View.GONE);
//            }


            holder.headerText.setText(mainData.getJSONObject(position).getString("title"));

            String[] temp_ = mainData.getJSONObject(position).getString("date").split(" ");
            holder.date.setText("posted on " + temp_[0] + " of Grade " + mainData.getJSONObject(position).getString("grade"));


            if (getItemViewType(position) == 1) {

                holder.assignHolder.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (callback != null) {

                            try {
                                callback.onAssign(mainData.getJSONObject(position).getString("question_id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                if (mainData.getJSONObject(position).getString("assign_date").toString().equalsIgnoreCase("null")) {

                    holder.assigned.setText("Not Assigned");
                    holder.solved.setText("Not Solved");
                    holder.accepted.setText("Not Accepted");
                    holder.assigned.setBackgroundResource(R.drawable.rounded_corner_red);
                    holder.solved.setBackgroundResource(R.drawable.rounded_corner_yellow);
                    holder.accepted.setBackgroundResource(R.drawable.rounded_corner_yellow);

                } else if (mainData.getJSONObject(position).getString("uploaded_on").toString().equalsIgnoreCase("null")) {

                    String[] temp2_ = mainData.getJSONObject(position).getString("assign_date").split(" ");
                    holder.assigned.setText("A : " + temp2_[0]);

                    holder.solved.setText("Not Solved");
                    holder.accepted.setText("Not Accepted");
                    holder.assigned.setBackgroundResource(R.drawable.rounded_corner_green);
                    holder.solved.setBackgroundResource(R.drawable.rounded_corner_red);
                    holder.accepted.setBackgroundResource(R.drawable.rounded_corner_yellow);

                } else if (mainData.getJSONObject(position).getString("is_viewed").toString().equalsIgnoreCase("0")) {

                    String[] temp2_ = mainData.getJSONObject(position).getString("assign_date").split(" ");
                    holder.assigned.setText("A : " + temp2_[0]);

                    String[] temp3_ = mainData.getJSONObject(position).getString("uploaded_on").split(" ");
                    holder.solved.setText("S : " + temp2_[0]);

                    holder.accepted.setText("Not Accepted");
                    holder.assigned.setBackgroundResource(R.drawable.rounded_corner_green);
                    holder.solved.setBackgroundResource(R.drawable.rounded_corner_green);
                    holder.accepted.setBackgroundResource(R.drawable.rounded_corner_red);
                } else {

                    String[] temp2_ = mainData.getJSONObject(position).getString("assign_date").split(" ");
                    holder.assigned.setText("A : " + temp2_[0]);

                    String[] temp3_ = mainData.getJSONObject(position).getString("uploaded_on").split(" ");
                    holder.solved.setText("S : " + temp3_[0]);

                    holder.accepted.setText("Accepted");
                    holder.assigned.setBackgroundResource(R.drawable.rounded_corner_green);
                    holder.solved.setBackgroundResource(R.drawable.rounded_corner_green);
                    holder.accepted.setBackgroundResource(R.drawable.rounded_corner_green);
                }

            }


            holder.mainVIew.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (callback != null) {

                        try {
                            callback.onClikced(mainData.getJSONObject(position).getString("question_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mainData.length();
    }


    public static class ViewHolderDash extends RecyclerView.ViewHolder {

        private TextViewIkarosRegular headerText, assigned, solved, accepted;
        private TextViewIkarosLight date, subject;
        private View mainVIew;
        private LinearLayout assignHolder;

        public ViewHolderDash(View itemView, int viewType) {
            super(itemView);

            mainVIew = itemView;
            headerText = (TextViewIkarosRegular) itemView.findViewById(R.id.headertext);
            date = (TextViewIkarosLight) itemView.findViewById(R.id.datetext);

            if (viewType == 1) {
                assignHolder = (LinearLayout) itemView.findViewById(R.id.assig_to);
            } else {
                assigned = (TextViewIkarosRegular) itemView.findViewById(R.id.assigned);
                solved = (TextViewIkarosRegular) itemView.findViewById(R.id.solved);
                accepted = (TextViewIkarosRegular) itemView.findViewById(R.id.accepted);
            }
        }
    }


    public interface OnItemClicked {
        void onClikced(final String qunID);

        void onAssign(final String qunID);
    }

    public void addOnItemClicked(final OnItemClicked callback) {
        this.callback = callback;
    }
}
