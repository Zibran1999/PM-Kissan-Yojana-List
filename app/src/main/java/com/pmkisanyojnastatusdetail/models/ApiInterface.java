package com.pmkisanyojnastatusdetail.models;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {


    @POST("fetch_kisan_yojana_api.php")
    Call<YojanaModelList> getAllYojana();

    @POST("fetch_news_api.php")
    Call<NewsModelList> getAllNews();

    @POST("fetch_others_api.php")
    Call<YojanaModelList> getOthersData();

    @POST("fetch_quiz_questions.php")
    Call<QuizModelList> fetchQuizQuestions();

    @FormUrlEncoded
    @POST("fetch_my_status_api.php")
    Call<MyStatusModelList> fetchMyStatus(@FieldMap Map<String, String> map);

    @POST("fetch_status.php")
    Call<StatusModelList> fetchStatus();

    @FormUrlEncoded
    @POST("fetch_status_views.php")
    Call<StatusViewModelList> fetchStatusViews(@FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("fetch_preview_data.php")
    Call<PreviewModelList> getPreview(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_profile_api.php")
    Call<MessageModel> uploadProfile(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_user_profile_api.php")
    Call<ProfileModelList> getProfileData(@FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("update_profile.php")
    Call<MessageModel> updateProfile(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_status_api.php")
    Call<MessageModel> uploadStatus(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("statusSeenBy.php")
    Call<MessageModel> uploadSeenBy(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("deleteMyStatus.php")
    Call<MessageModel> deleteMyStatus(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ads_id_fetch.php")
    Call<AdsModelList> fetchAds(@Field("id") String id);

}
