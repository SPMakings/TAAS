package com.spm.taas.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spm.taas.R;
import com.spm.taas.SplashActivity;


/**
 * Created by Saikat Pakira on 9/28/2016.
 */

public class SplashFragments extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_splash_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.splash_signin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((SplashActivity) getActivity()).openLoginPaqe();
            }
        });

        view.findViewById(R.id.splash_reg).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((SplashActivity) getActivity()).openRegisatrationPaqe();
            }
        });



    }
}
