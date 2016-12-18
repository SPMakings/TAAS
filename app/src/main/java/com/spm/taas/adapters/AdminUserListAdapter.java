package com.spm.taas.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.spm.taas.R;
import com.spm.taas.application.CircleTransform;
import com.spm.taas.application.TassApplication;
import com.spm.taas.customview.TextViewIkarosRegular;

import org.json.JSONArray;

/**
 * Created by saikatpakira on 06/11/16.
 */

public class AdminUserListAdapter extends RecyclerView.Adapter<AdminUserListAdapter.ViewHolder> {

    private Context mContext;
    private JsonArray userArray;
    private OnItemSelected callback_ = null;
    private LayoutInflater inflater;

    public AdminUserListAdapter(Context mContext, JsonArray userArray) {
        this.mContext = mContext;
        this.userArray = userArray;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return (new ViewHolder(inflater.inflate(R.layout.items_user_list, parent, false)));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            holder.userName.setText(userArray.get(position).getAsJsonObject().get("full_name").getAsString());
            holder.userEmail.setText(userArray.get(position).getAsJsonObject().get("email").getAsString());
            holder.user_type.setText(userArray.get(position).getAsJsonObject().get("user_type").getAsString());

            if (userArray.get(position).getAsJsonObject().get("user_type").getAsString().equalsIgnoreCase("student")) {
                holder.expertise.setText("Grade : " + userArray.get(position).getAsJsonObject().get("degree_grade").getAsString());
            } else {
                holder.expertise.setText("Expertise : " + userArray.get(position).getAsJsonObject().get("expertise").getAsString());
            }

            Glide.with(mContext)
                    .load(userArray.get(position).getAsJsonObject().get("image").getAsString())
                    .centerCrop()
                    .placeholder(R.drawable.default_place_holder)
                    .error(R.drawable.default_place_holder)
                    .crossFade().bitmapTransform(new CircleTransform(mContext))
                    .into(holder.profImage);

            if (userArray.get(position).getAsJsonObject().get("status").getAsString().equalsIgnoreCase("Approved")) {
                holder.approveHolder.setVisibility(View.GONE);

                holder.mainView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (callback_ != null) {
                            callback_.onAccept(userArray.get(position).getAsJsonObject().get("id").getAsString());
                        }
                    }
                });

            } else {
                holder.approveHolder.setVisibility(View.VISIBLE);


                holder.mainView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (callback_ != null) {
                            callback_.onDetails(userArray.get(position).getAsJsonObject().get("id").getAsString());
                        }
                    }
                });

                holder.aaprovYes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (callback_ != null) {
                            callback_.onAccept(userArray.get(position).getAsJsonObject().get("id").getAsString());
                        }
                    }
                });

                holder.approvNo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (callback_ != null) {
                            callback_.onReject(userArray.get(position).getAsJsonObject().get("id").getAsString());
                        }
                    }
                });

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return userArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextViewIkarosRegular userName, userEmail, expertise, user_type;
        private View approveHolder, aaprovYes, approvNo, mainView;
        private ImageView profImage;

        public ViewHolder(View itemView) {
            super(itemView);

            mainView = itemView;
            user_type = (TextViewIkarosRegular) itemView.findViewById(R.id.user_type);
            userName = (TextViewIkarosRegular) itemView.findViewById(R.id.user_name);
            userEmail = (TextViewIkarosRegular) itemView.findViewById(R.id.qun_id);
            expertise = (TextViewIkarosRegular) itemView.findViewById(R.id.qun_ttl);
            profImage = (ImageView) itemView.findViewById(R.id.prof_image);

            approveHolder = itemView.findViewById(R.id.qun_attch_1);
            aaprovYes = itemView.findViewById(R.id.accept_yes);
            approvNo = itemView.findViewById(R.id.accept_no);
        }
    }

    public void addOnItemSelected(final OnItemSelected callback_) {
        this.callback_ = callback_;
    }

    public interface OnItemSelected {
        void onAccept(final String userID_);

        void onDetails(final String userID_);

        void onReject(final String userID_);
    }
}
