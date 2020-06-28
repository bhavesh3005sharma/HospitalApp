package com.scout.hospitalapp.retrofit;

import com.scout.hospitalapp.Models.ModelHospitalRegisterRequest;
import com.scout.hospitalapp.response.HospitalInfoResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitNetworkApi {

    @POST("Hospital/incoming_webhook/registerHospital")
    Call<ResponseBody> registerHospital(@Body ModelHospitalRegisterRequest registerRequest);

    @GET("Hospital/incoming_webhook/getHospitalInfo")
    Call<HospitalInfoResponse> getHospitalInfo(@Query("email") String email,@Query("hospital_id") String id);

}
