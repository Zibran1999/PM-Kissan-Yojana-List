package com.pmkisanyojnastatusdetail.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.pmkisanyojnastatusdetail.databinding.ActivityNewsBinding;
import com.pmkisanyojnastatusdetail.models.ModelFactory;
import com.pmkisanyojnastatusdetail.models.PageViewModel;
import com.pmkisanyojnastatusdetail.models.PreviewModel;
import com.pmkisanyojnastatusdetail.utils.CommonMethod;

import java.util.HashMap;
import java.util.Map;

public class NewsActivity extends AppCompatActivity {
    ImageView newsImg, backIcon;
    TextView newsTitle, newsDesc;
    String id, img, desc, title;
    ActivityNewsBinding binding;
    PageViewModel pageViewModel;
    Map<String, String> map = new HashMap<>();
    LottieAnimationView lottieAnimationView;
//    InterstitialAd interstitialAd;
    Dialog dialog;
    int pos;

//    String finalEnglishString, finalHindiString;

    /*ads variable*/
    /*ads variable*/

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        MobileAds.initialize(NewsActivity.this);
        newsImg = binding.newsImg;
        newsTitle = binding.newsTitle;
        newsDesc = binding.newsDesc;
        backIcon = binding.backIcon;
        lottieAnimationView = binding.lottieHome;
        id = getIntent().getStringExtra("id");
        dialog = CommonMethod.getDialog(this);
        dialog.show();
        pos = getIntent().getIntExtra("pos", 0);

        map.put("previewId", id);

        newsTitle.setVisibility(View.VISIBLE);
        newsImg.setVisibility(View.VISIBLE);
        newsDesc.setVisibility(View.VISIBLE);
        backIcon.setOnClickListener(v -> onBackPressed());
        pageViewModel = new ViewModelProvider(this, new ModelFactory(this.getApplication(), map)).get(PageViewModel.class);
        pageViewModel.getPreviewData().observe(this, previewModelList -> {
            dialog.show();
            if (!previewModelList.getData().isEmpty()) {
                dialog.dismiss();
                title = getIntent().getStringExtra("title");
                img = getIntent().getStringExtra("img");
                Glide.with(this).load("https://gedgetsworld.in/PM_Kisan_Yojana/News_Images/" + img).into(newsImg);
                newsTitle.setText(title);
//                CommonMethod.getBannerAds(this, binding.adViewNews);
                for (PreviewModel m : previewModelList.getData()) {
                    if (m.getPreviewId().equals(id)) {
                        String replaceString = m.getDesc().replaceAll("<.*?>", "");

                        newsDesc.setText(replaceString);
                        newsDesc.setVisibility(View.VISIBLE);


                    }
                }

            } else {
                dialog.dismiss();
                newsImg.setVisibility(View.GONE);
                newsTitle.setVisibility(View.GONE);
                newsDesc.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
    }
}