package com.spm.taas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spm.taas.LandingActivity;
import com.spm.taas.R;


/**
 * Created by Saikat Pakira on 9/28/2016.
 */
public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.log_me_in).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), LandingActivity.class);
                startActivity(i);
            }
        });

    }
}
