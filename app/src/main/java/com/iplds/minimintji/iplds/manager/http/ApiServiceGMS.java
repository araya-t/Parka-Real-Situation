package com.iplds.minimintji.iplds.manager.http;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ApiServiceGMS {
    //send trigger to GMS to change the status of parking slot
    @FormUrlEncoded
    @PUT("positions/{position_id}/parking")
    Call<Void> changeStatusOldVersion(@Path("position_id") int position_id,
                                      @Field("is_available") String is_available);


    //send trigger to GMS to change the status of parking slot
    // without sending is_available

    @PUT("positions/{position_id}/parking")
    Call<Void> changeStatus(@Path("position_id") int position_id);
}
