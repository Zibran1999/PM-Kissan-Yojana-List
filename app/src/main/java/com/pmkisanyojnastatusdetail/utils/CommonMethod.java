package com.pmkisanyojnastatusdetail.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.pmkisanyojnastatusdetail.BuildConfig;
import com.pmkisanyojnastatusdetail.R;
import com.pmkisanyojnastatusdetail.models.ApiInterface;
import com.pmkisanyojnastatusdetail.models.ApiWebServices;
import com.pmkisanyojnastatusdetail.models.MessageModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommonMethod extends Job {
    public static InterstitialAd mInterstitialAd;
    public static int jobId;
    ApiInterface apiInterface;

    public static void contactUs(Context context) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setPackage("com.google.android.gm");
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"help.pmkisanyojana@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Hello");
        i.putExtra(Intent.EXTRA_TEXT, "I need some help regarding ");
        try {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    public static void schedule(String statusId, String statusImg) {
        int pos;
        PersistableBundleCompat bundle = new PersistableBundleCompat();
        bundle.putString(Prevalent.UID, statusId);
        bundle.putString(Prevalent.STATUS_IMAGE, statusImg);
        pos = new JobRequest.Builder(Prevalent.JOB_TAG_DELETE_STATUS)
                .setExact(TimeUnit.HOURS.toMillis(23))
                .setExtras(bundle)
                .build()
                .schedule();

        jobId = pos;

    }


    public static void shareApp(Context context) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void whatsApp(Context context) throws UnsupportedEncodingException, PackageManager.NameNotFoundException {

        String contact = "+91 9411902490"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact + "&text=" + URLEncoder.encode("Hello, I need some help regarding ", "UTF-8");
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(url));
            context.startActivity(i);

        } catch (PackageManager.NameNotFoundException e) {
            try {
                PackageManager pm = context.getPackageManager();
                pm.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            } catch (PackageManager.NameNotFoundException exception) {
                e.printStackTrace();
                Toast.makeText(context, "WhatsApp is not installed on this Device.", Toast.LENGTH_SHORT).show();

            }

//            whatsApp(context, "com.whatsapp.w4b");
        }


    }

    public static void rateApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        myAppLinkToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Dialog getDialog(Context context) {
        Dialog loadingDialog;
        loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.item_bg));
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    public static void interstitialAds(Context context) {
        MobileAds.initialize(context);
        String id = Paper.book().read(Prevalent.interstitialAds);
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, id, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "InteronAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAGError", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
    public static void getBannerAds(Context context, RelativeLayout container) {
        String id = Paper.book().read(Prevalent.bannerAds);
        MobileAds.initialize(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(context);
        container.addView(adView);
        adView.setAdUnitId(id);
        adView.setAdSize(AdSize.BANNER);
        adView.loadAd(adRequest);
        container.setVisibility(View.VISIBLE);
    }

    public static void cancelJob(int jobId) {
        JobManager.instance().cancelAll();

    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {

        String sId = params.getExtras().getString(Prevalent.UID, "");
        String statusImg = params.getExtras().getString(Prevalent.STATUS_IMAGE, "");
        Log.d("runJob", sId + " " + statusImg);
        Map<String, String> map = new HashMap<>();
        map.put("statusId", sId);
        map.put("statusImg", "User_Status_Images/" + statusImg);
        Log.d("jobSchedule", " " + jobId);
        deleteMyStatus(map);
        return Result.SUCCESS;
    }

    private void deleteMyStatus(Map<String, String> map) {
        apiInterface = ApiWebServices.getApiInterface();
        Call<MessageModel> call = apiInterface.deleteMyStatus(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {

                assert response.body() != null;
                if (response.isSuccessful()) {

                    Log.d("deleteStatus", response.body().getMessage());

                } else {
                    Log.d("StatusError", response.body().getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Log.d("deleteStatusError", t.getMessage());


            }
        });

    }
}
