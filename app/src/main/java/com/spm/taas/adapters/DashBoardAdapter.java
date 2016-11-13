package com.spm.taas.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spm.taas.R;
import com.spm.taas.customview.TextViewIkarosRegular;
import com.spm.taas.models.DashBoardModel;

import java.util.LinkedList;

/**
 * Created by saikatpakira on 10/10/16.
 */

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.ViewHolderDash> {

    private Context mContext;
    private LinkedList<DashBoardModel> mainData;
    private LayoutInflater inflater = null;

    public DashBoardAdapter(final Context mContext, final LinkedList<DashBoardModel> mainData) {

        this.mContext = mContext;
        this.mainData = mainData;
        inflater = LayoutInflater.from(mContext);

    }


    @Override
    public ViewHolderDash onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == 1) {//======Header
            return (new ViewHolderDash(inflater.inflate(R.layout.items_with_header_landing, parent, false), viewType));
        } else {
            return (new ViewHolderDash(inflater.inflate(R.layout.items_landing_subject, parent, false), viewType));
        }

    }

    @Override
    public void onBindViewHolder(ViewHolderDash holder, int position) {


        holder.subjectText.setText(mainData.get(position).getSubjectName());
        holder.subjectCount.setText(mainData.get(position).getSubjectCount());
        //Log.i("tipikal", "Count : " + mainData.get(position).getHeaderCount() + " Position : " + position);

        if (mainData.get(position).isHeader()) {
            holder.headerText.setText(mainData.get(position).getHeaderName());
            holder.headerCount.setText(mainData.get(position).getHeaderCount());
            holder.headerView.setCardBackgroundColor(ContextCompat.getColor(mContext, mainData.get(position).getHeaderColor()));
        }

    }

    @Override
    public int getItemCount() {
        return mainData.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (mainData.get(position).isHeader()) {
            return 1;
        } else {
            return 0;
        }
    }

    public static class ViewHolderDash extends RecyclerView.ViewHolder {

        private TextViewIkarosRegular headerText, headerCount, subjectText, subjectCount;
        private CardView headerView = null;

        public ViewHolderDash(View itemView, int type_) {
            super(itemView);

            subjectText = (TextViewIkarosRegular) itemView.findViewById(R.id.subject_name);
            subjectCount = (TextViewIkarosRegular) itemView.findViewById(R.id.subject_count);

            if (type_ == 1) {
                headerText = (TextViewIkarosRegular) itemView.findViewById(R.id.header_text);
                headerCount = (TextViewIkarosRegular) itemView.findViewById(R.id.header_count);
                headerView = (CardView) itemView.findViewById(R.id.card_view);
            }

        }
    }
}
