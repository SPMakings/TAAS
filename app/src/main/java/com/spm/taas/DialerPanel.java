package com.spm.taas;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.spm.taas.services.RingManagerService;

public class DialerPanel extends AppCompatActivity {

    private Button acceptButton=null,rejecButton=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        acceptButton=(Button)findViewById(R.id.acceptcall);
        rejecButton=(Button)findViewById(R.id.rejectcall);

        rejecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DialerPanel.this, RingManagerService.class);
                i.setAction("com.sp.taas.ACTION_STOP");
                startService(i);
            }
        });

    }

}
