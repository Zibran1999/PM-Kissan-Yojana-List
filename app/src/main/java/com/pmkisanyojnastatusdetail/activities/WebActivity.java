package com.pmkisanyojnastatusdetail.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.pmkisanyojnastatusdetail.databinding.ActivityWebBinding;
import com.pmkisanyojnastatusdetail.utils.CommonMethod;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    TextView title;
    ActivityWebBinding binding;
    String data;
    Dialog dialog;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MobileAds.initialize(this);
        webView = binding.webView;
        title = binding.title;
        binding.backIcon.setOnClickListener(v -> onBackPressed());
        data = getIntent().getStringExtra("url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        WebSettings webSettings = webView.getSettings();
        webSettings.getLoadsImagesAutomatically();
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);


        /* if website doesn't open proper way then we put this code in webView*/

        webView.getSettings().setSupportZoom(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        /* if website doesn't open proper way then we put this code in webview*/


        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "myPDFile.pdf");
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
        });


        dialog = CommonMethod.getDialog(this);
        dialog.show();

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("TAG", "Processing webView url click...");
                String googleDocs = "https://docs.google.com/viewer?url=";
                Log.d("url", url);
                if (URLUtil.isFileUrl(url)) {
                    view.loadUrl(googleDocs + url);
                } else {

                    view.loadUrl(url);
                }
                return true;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                Log.e("TAG", "Error: " + description);
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                dialog.dismiss();
               CommonMethod.getBannerAds(WebActivity.this, binding.adViewWebView);

                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl(data);
        webView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


}