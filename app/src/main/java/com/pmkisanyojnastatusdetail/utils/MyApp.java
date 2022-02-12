package com.pmkisanyojnastatusdetail.utils;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.evernote.android.job.JobManager;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;
import com.pmkisanyojnastatusdetail.activities.SplashActivity;
import com.pmkisanyojnastatusdetail.models.AdsModel;
import com.pmkisanyojnastatusdetail.models.AdsModelList;
import com.pmkisanyojnastatusdetail.models.ApiInterface;
import com.pmkisanyojnastatusdetail.models.ApiWebServices;

import java.util.Objects;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApp extends Application  {

    private static final String ONESIGNAL_APP_ID = "9a77bdda-945f-4f86-bf6a-5c564559c350";
    public static MyApp mInstance;
    ApiInterface apiInterface;

    public MyApp() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        fetchAds();
        Paper.init(mInstance);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setNotificationOpenedHandler(new ExampleNotificationOpenedHandler());
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        JobManager.create(this).addJobCreator(new FireJobCreator());

    }
    private void fetchAds() {

        apiInterface = ApiWebServices.getApiInterface();
        Call<AdsModelList> call = apiInterface.fetchAds("PM Kisan Yojana List");
        call.enqueue(new Callback<AdsModelList>() {
            @Override
            public void onResponse(@NonNull Call<AdsModelList> call, @NonNull Response<AdsModelList> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getData() != null) {
                        for (AdsModel ads : response.body().getData()) {
                            Paper.book().write(Prevalent.bannerAds, ads.getBanner());
                            Paper.book().write(Prevalent.interstitialAds, ads.getInterstitial());
                            Paper.book().write(Prevalent.nativeAds, ads.getNativeADs());

                        }

                    }
                } else {
                    Log.d("adsError", response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<AdsModelList> call, @NonNull Throwable t) {
                Log.d("adsError", t.getMessage());
            }
        });
    }

    private class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            Intent intent = new Intent(MyApp.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

}
