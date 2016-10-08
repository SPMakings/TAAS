package com.spm.taas.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.spm.taas.R;
import com.spm.taas.customview.TextViewIkarosRegular;

/**
 * Created by Saikat Pakira on 9/28/2016.
 */

public class RegistrationFragment extends Fragment {

    private View optionThree = null;
    private TextViewIkarosRegular asStudent, asTeachers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_registration, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        optionThree = view.findViewById(R.id.option_three);

        asStudent = (TextViewIkarosRegular) view.findViewById(R.id.as_student);
        asTeachers = (TextViewIkarosRegular) view.findViewById(R.id.as_teacher);

        asStudent.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                asStudent.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                asStudent.setTextColor(Color.WHITE);

                asTeachers.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                asTeachers.setBackgroundColor(Color.TRANSPARENT);

                optionThree.setVisibility(View.GONE);

            }
        });


        asTeachers.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                asTeachers.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                asTeachers.setTextColor(Color.WHITE);

                asStudent.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                asStudent.setBackgroundColor(Color.TRANSPARENT);

                optionThree.setVisibility(View.VISIBLE);
            }
        });

    }
}
