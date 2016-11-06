package com.spm.taas.baseclass;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.spm.taas.R;
import com.spm.taas.customview.TextViewIkarosRegular;

/**
 * Created by saikatpakira on 01/11/16.
 */

public class TAASActivity extends AppCompatActivity {

    private AlertDialog alertDialog = null;


    public void showError(final String title, final String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void showProgress() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dilaog_loder, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    public void showProgress(final String message) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dilaog_loder, null);
        ((TextViewIkarosRegular) dialogView.findViewById(R.id.progress_text)).setText(message);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();
        alertDialog.show();

    }


    public void hideProgress() {
        alertDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
