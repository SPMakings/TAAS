package com.spm.taas.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.spm.taas.R;
import com.spm.taas.application.CircleTransform;
import com.spm.taas.customview.TextViewIkarosRegular;

/**
 * Created by saikatpakira on 09/11/16.
 */

public class AssignTeacherAdapter extends RecyclerView.Adapter<AssignTeacherAdapter.ViewHolder> {

    private Context mContext;
    private JsonArray userArray;
    private LayoutInflater inflater;
    private OnSelectListener callback_ = null;

    public AssignTeacherAdapter(Context mContext, JsonArray userArray) {
        this.mContext = mContext;
        this.userArray = userArray;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public AssignTeacherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return (new AssignTeacherAdapter.ViewHolder(inflater.inflate(R.layout.items_user_list, parent, false)));
    }

    @Override
    public void onBindViewHolder(AssignTeacherAdapter.ViewHolder holder, final int position) {

        try {
            holder.userName.setText(userArray.get(position).getAsJsonObject().get("full_name").getAsString());
            holder.userEmail.setText(userArray.get(position).getAsJsonObject().get("email").getAsString());
            holder.user_type.setText(userArray.get(position).getAsJsonObject().get("user_type").getAsString());
            holder.expertise.setText("Expertise : " + userArray.get(position).getAsJsonObject().get("expertise").getAsString());


            Glide.with(mContext)
                    .load(userArray.get(position).getAsJsonObject().get("image").getAsString())
                    .centerCrop()
                    .placeholder(R.drawable.default_place_holder)
                    .error(R.drawable.default_place_holder)
                    .crossFade().bitmapTransform(new CircleTransform(mContext))
                    .into(holder.profImage);

            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback_ != null) {
                        callback_.onSelect(userArray.get(position).getAsJsonObject().get("id").getAsString());
                    }
                }
            });

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
        private ImageView profImage;
        private View mainView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            user_type = (TextViewIkarosRegular) itemView.findViewById(R.id.user_type);
            userName = (TextViewIkarosRegular) itemView.findViewById(R.id.user_name);
            userEmail = (TextViewIkarosRegular) itemView.findViewById(R.id.qun_id);
            expertise = (TextViewIkarosRegular) itemView.findViewById(R.id.qun_ttl);
            profImage = (ImageView) itemView.findViewById(R.id.prof_image);

        }
    }


    public void addOnSelectListener(final OnSelectListener callback_) {
        this.callback_ = callback_;
    }

    public interface OnSelectListener {
        void onSelect(final String teacherID);
    }
}
