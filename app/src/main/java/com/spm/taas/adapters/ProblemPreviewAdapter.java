package com.spm.taas.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.spm.taas.R;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.models.DashBoardModel;

import java.util.LinkedList;

/**
 * Created by saikatpakira on 23/10/16.
 */

public class ProblemPreviewAdapter extends RecyclerView.Adapter<ProblemPreviewAdapter.ViewHolderDash> {

    private Context mContext;
    private LinkedList<String> mainData;
    private LayoutInflater inflater = null;
    private OnItemClicked callback = null;

    public ProblemPreviewAdapter(final Context mContext, final LinkedList<String> mainData) {

        this.mContext = mContext;
        this.mainData = mainData;
        inflater = LayoutInflater.from(mContext);

    }


    @Override
    public ProblemPreviewAdapter.ViewHolderDash onCreateViewHolder(ViewGroup parent, int viewType) {
        return (new ProblemPreviewAdapter.ViewHolderDash(inflater.inflate(R.layout.items_image_preview, parent, false), viewType));
    }

    @Override
    public void onBindViewHolder(ProblemPreviewAdapter.ViewHolderDash holder, final int position) {


        Glide.with(mContext)
                .load(mainData.get(position))
                .crossFade()
                .into(holder.headerView);

        holder.headerText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClick(mainData.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mainData.size();
    }

    public static class ViewHolderDash extends RecyclerView.ViewHolder {

        private View headerText;
        private ImageView headerView = null;

        public ViewHolderDash(View itemView, int type_) {
            super(itemView);

            headerText = itemView.findViewById(R.id.delete);
            headerView = (ImageView) itemView.findViewById(R.id.preview);
        }
    }

    public interface OnItemClicked {
        void onClick(final String imagePath_);
    }

    public void addOnItemClicked(OnItemClicked callback) {
        this.callback = callback;
    }

    public void addItems(final String path_) {
        mainData.add(0, path_);
        notifyDataSetChanged();
    }

    public void removeItems(final String path_) {
        mainData.remove(path_);
        notifyDataSetChanged();
    }

    public LinkedList<String> getCUrrentArray() {
        return mainData;
    }
}
