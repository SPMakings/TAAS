package com.spm.taas;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.logging.Logger;

public class WebViewActivity extends AppCompatActivity {

    private WebView mainWebView = null;
    private boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //=============================

        mainWebView = (WebView) findViewById(R.id.taas_webview);
        mainWebView.getSettings().setJavaScriptEnabled(true);
        mainWebView.getSettings().setBuiltInZoomControls(true);
        mainWebView.getSettings().setDisplayZoomControls(false);
        mainWebView.loadUrl(getIntent().getStringExtra("WEB_LINK"));

        mainWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

//                if (progress == 1) {
//                    loader.setVisibility(View.VISIBLE);
//                } else if (progress == 100) {
//                    loader.setVisibility(View.GONE);
//                } else {
//                    progressCounter.setText("" + progress + " %");
//                }

            }


        });

        mainWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wView, String url) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Log.i("mainWebView", "shouldOverrideUrlLoading depri : " + url);
                }

                return super.shouldOverrideUrlLoading(wView, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.i("mainWebView", "shouldOverrideUrlLoading : " + request.getUrl().toString());
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("mainWebView", "onPageFinished : " + url);

                if (url.equalsIgnoreCase("https://urtaas.com/app_control/success")) {
                    isSuccess = true;
                    onBackPressed();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        if (isSuccess) {
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_OK, i);
        }
        finish();
    }
}
