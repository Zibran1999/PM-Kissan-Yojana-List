package com.pmkisanyojnastatusdetail.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.pmkisanyojnastatusdetail.R;

public class PrivacyActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        webView = findViewById(R.id.policy);
        webView.loadUrl("file:///android_asset/privacy.html");
    }
}
