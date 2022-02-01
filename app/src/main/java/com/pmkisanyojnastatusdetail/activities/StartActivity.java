package com.pmkisanyojnastatusdetail.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pmkisanyojnastatusdetail.databinding.ActivityStartBinding;
import com.pmkisanyojnastatusdetail.utils.CommonMethod;

public class StartActivity extends AppCompatActivity {

    public static int count = 1;
    ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        MobileAds.initialize(this);
//        CommonMethod.interstitialAds(StartActivity.this);
//        CommonMethod.getBannerAds(this, binding.adView);
        binding.startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        binding.shareBtn.setOnClickListener(v -> CommonMethod.shareApp(this));

        binding.rateBtn.setOnClickListener(v -> {
            CommonMethod.rateApp(this);
        });

    }


}