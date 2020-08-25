package com.scout.hospitalapp.retrofit;

import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.Models.ModelBookAppointment;
import com.scout.hospitalapp.Models.ModelDepartmentRequest;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelHospitalRegisterRequest;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import com.scout.hospitalapp.response.ResponseMessage;

import java.util.ArrayList;

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

    @GET("Doctor/incoming_webhook/getDoctorInfo")
    Call<ModelDoctorInfo> getDoctorInfo(@Query("email") String email, @Query("doctor_id") String id);

    @POST("Hospital/incoming_webhook/registerDoctor")
    Call<ModelRequestId> registerDoctor(@Body ModelDoctorInfo doctorInfo);

    @GET("Hospital/incoming_webhook/removeDoctor")
    Call<ResponseBody> removeDoctor(@Query("hospital_id")String hospitalId,@Query("doctor_id")String doctorId);

    @GET("Hospital/incoming_webhook/PendingAppointments")
    Call<HospitalInfoResponse> getPendingAppointmentsList(@Query("hospital_id")String hospitalId);

    @GET("Hospital/incoming_webhook/ConfirmedAppointments")
    Call<HospitalInfoResponse> getConfirmedAppointmentsList(@Query("hospital_id")String hospitalId);

    @GET("Hospital/incoming_webhook/AppointmentsHistory")
    Call<HospitalInfoResponse> getAppointmentsHistoryList(@Query("hospital_id")String hospitalId);

    @POST("Hospital/incoming_webhook/addDepartment")
    Call<ResponseBody> addDepartment(@Body ModelDepartmentRequest request);

    @POST("Hospital/incoming_webhook/removeDepartment")
    Call<ResponseBody> removeDepartment(@Body ModelDepartmentRequest request);

    @POST("Hospital/incoming_webhook/updateDoctor")
    Call<ResponseBody> updateDoctor(@Body ModelDoctorInfo doctorInfo);

    @GET("Doctor/incoming_webhook/unavailableDates")
    Call<ModelDoctorInfo> getUnavailableDates(@Query("doctor_id") String id);

    @POST("Hospital/incoming_webhook/updateDepartment")
    Call<ResponseBody> updateDepartment(@Body ModelDepartmentRequest request);

    @GET("Hospital/incoming_webhook/AppointmentDetails")
    Call<ModelAppointment> getAppointmentsDetails(@Query("appointment_id") String appointmentId);

    @GET("Hospital/incoming_webhook/getFilterAppointments")
    Call<ArrayList<ModelAppointment>> getFilterAppointments(@Query("appointment_date") String filterDate, @Query("hospital_id")String hospitalId);

    @GET("Hospital/incoming_webhook/getFilterPastAppointments")
    Call<ArrayList<ModelAppointment>> getFilterPastAppointments(@Query("appointment_date") String filterDate, @Query("hospital_id")String hospitalId);

    @POST("Hospital/incoming_webhook/bookAppointment")
    Call<ResponseMessage> bookAppointment(@Body ModelBookAppointment appointment);

    @GET("Hospital/incoming_webhook/SetAppointmentStatus")
    Call<ResponseBody> SetAppointmentStatus(@Query("hospital_id")String hospitalId,@Query("appointment_id") String appointmentId,@Query("status")String status);

    @POST("Hospital/incoming_webhook/profileUpdate")
    Call<ResponseBody> updateHospitalProfile(@Body HospitalInfoResponse requestData);

    @GET("Hospital/incoming_webhook/profilePicUpdate")
    Call<ResponseBody> updateHospitalProfilePic(@Query("hospital_id") String id, @Query("url") String url);

    @GET("Hospital/incoming_webhook/doctorProfilePicUpdate")
    Call<ResponseBody> updateDoctorProfilePic(@Query("doctor_id") String id, @Query("url") String url);

    @GET("Hospital/incoming_webhook/updateFCMToken")
    Call<ResponseBody> updateFCMToken(@Query("email") String  email, @Query("fcm_token") String token);

    @GET("Hospital/incoming_webhook/SetAppointmentTakingSchedule")
    Call<ResponseBody> SetAppointmentTakingSchedule(@Query("hospital_id") String hospitalId, @Query("schedule")String schedule);
}
