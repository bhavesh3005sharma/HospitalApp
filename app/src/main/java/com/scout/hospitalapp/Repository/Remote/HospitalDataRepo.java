package com.scout.hospitalapp.Repository.Remote;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.scout.hospitalapp.Models.ModelHospitalRegisterRequest;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import com.scout.hospitalapp.retrofit.ApiService;
import com.scout.hospitalapp.retrofit.RetrofitNetworkApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalDataRepo {
    RetrofitNetworkApi networkApi = ApiService.getAPIService();
    private static HospitalDataRepo instance;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    MutableLiveData<HospitalInfoResponse> hospitalInfo = new MutableLiveData<>();

    public static HospitalDataRepo getInstance(){
        if(instance == null){
            instance = new HospitalDataRepo();
        }
        return instance;
    }

    public LiveData<String> registerHospital(final ModelHospitalRegisterRequest registerRequest) {
        final MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        mAuth.createUserWithEmailAndPassword(registerRequest.getEmail(),registerRequest.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mAuth.signOut();
                            if (task.isSuccessful()){
                                networkApi.registerHospital(registerRequest).enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful() && response.code()==200){
                                            mutableLiveData.setValue("Registered Successfully\n Verification Email Sent Verify to Login.");
                                        }else
                                            mutableLiveData.setValue(response.errorBody().toString());
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.d("Error..", t.getMessage());
                                        mutableLiveData.setValue(t.getMessage());
                                    }
                                });
                            }else
                                mutableLiveData.setValue(task.getException().getMessage());
                        }
                    });

                }else
                    mutableLiveData.setValue(task.getException().getMessage());
            }
        });

        return mutableLiveData;
    }

    public LiveData<String> isCorrectUser(final String email, String password) {
        final MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        mutableLiveData.setValue("Correct");
                    }
                    else{
                        mutableLiveData.setValue("User not Verified\n  check Email");
                    }
                }else{
                    mutableLiveData.setValue(task.getException().getMessage());
                }
            }
        });

        return mutableLiveData;
    }

    public LiveData<HospitalInfoResponse> getHospitalInfoResponse(String email, String id){
        networkApi.getHospitalInfo(email,id).enqueue(new Callback<HospitalInfoResponse>() {
            @Override
            public void onResponse(Call<HospitalInfoResponse> call, Response<HospitalInfoResponse> response) {
                if (response.isSuccessful() || response.code()==200){
                    // save User Data.
                    hospitalInfo.setValue(response.body());
                }else
                    hospitalInfo.setValue(null);
            }

            @Override
            public void onFailure(Call<HospitalInfoResponse> call, Throwable t) {
                hospitalInfo.setValue(null);
            }
        });
        return hospitalInfo;
    }


    public LiveData<Boolean> isUserLoggedIn() {
        MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>();
        isUserLoggedIn.setValue(false);
        if (mAuth.getCurrentUser()!=null)
            isUserLoggedIn.setValue(true);
        return isUserLoggedIn;
    }

    public LiveData<String> updateHospitalProfile(HospitalInfoResponse requestData) {
        MutableLiveData<String> message = new MutableLiveData<>();
        networkApi = ApiService.getAPIService();
        networkApi.updateHospitalProfile(requestData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code()==200)
                    message.setValue("Profile Updated Successfully");
                else
                    message.setValue(response.errorBody().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                message.setValue(t.getMessage());
            }
        });
        return message;
    }

    public LiveData<String> updateHospitalProfilePic(String id, String url) {
        MutableLiveData<String> message = new MutableLiveData<>();
        networkApi = ApiService.getAPIService();
        networkApi.updateHospitalProfilePic(id,url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code()==200)
                    message.setValue("Profile Pic Updated Successfully");
                else
                    message.setValue(response.errorBody().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                message.setValue(t.getMessage());
            }
        });
        return message;
    }

    public void saveFcmToken(String email, String token) {
        ApiService.getAPIService().updateFCMToken(email,token)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.code()==200)
                            Log.d("Token","Saved Successfully\n"+token);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    public LiveData<String> setSchedule(String schedule, String hospitalId) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        networkApi = ApiService.getAPIService();
        networkApi.SetAppointmentTakingSchedule(hospitalId,schedule).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code()==200){
                    // Appointment Status Updated Successfully.
                    mutableLiveData.setValue("Status Updated Successfully");
                }else
                    mutableLiveData.setValue(response.errorBody().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mutableLiveData.setValue(t.getMessage());
            }
        });
        return mutableLiveData;
    }
}
