package com.spm.taas.networkmanagement;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("app_control/user_list")
    Call<JsonObject> getAdminUserList(@Query("user_id") String userID_,
                                      @Query("start") String startIndex_,
                                      @Query("row_count") String rowCount_,
                                      @Query("user_type") String userType_,
                                      @Query("subject") String subject_,
                                      @Query("status_type") String approved_);

    @GET("app_control/assign_teacher")
    Call<JsonObject> getAdminAsignQuestion(@Query("admin_id") String userID_,
                                           @Query("teacher_id") String startIndex_,
                                           @Query("email_id") String rowCount_);


    @GET("app_control/accept_or_reject")
    Call<JsonObject> getAdminAcceptRejectUser(@Query("teacher_id") String userID_,
                                              @Query("status") String startIndex_);


}
