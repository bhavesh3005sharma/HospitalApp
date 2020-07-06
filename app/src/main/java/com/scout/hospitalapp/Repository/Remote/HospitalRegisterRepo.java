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

public class HospitalRegisterRepo {
    RetrofitNetworkApi networkApi = ApiService.getAPIService();
    private static HospitalRegisterRepo instance;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    MutableLiveData<HospitalInfoResponse> hospitalInfo = new MutableLiveData<>();

    public static HospitalRegisterRepo getInstance(){
        if(instance == null){
            instance = new HospitalRegisterRepo();
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

    public LiveData<String> loginUser(final String email, String password) {
        final MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        networkApi.getHospitalInfo(email,null).enqueue(new Callback<HospitalInfoResponse>() {
                            @Override
                            public void onResponse(Call<HospitalInfoResponse> call, Response<HospitalInfoResponse> response) {
                                if (response.isSuccessful() || response.code()==200){
                                    // save User Data.
                                    hospitalInfo.setValue(response.body());
                                    mutableLiveData.setValue("Correct");
                                }else
                                    mutableLiveData.setValue(response.errorBody().toString());
                            }

                            @Override
                            public void onFailure(Call<HospitalInfoResponse> call, Throwable t) {
                                mutableLiveData.setValue(t.getMessage().toString());
                            }
                        });
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

    public LiveData<HospitalInfoResponse> getHospitalInfoResponse(){
        return hospitalInfo;
    }


    public LiveData<Boolean> isUserLoggedIn() {
        MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>();
        isUserLoggedIn.setValue(false);
        if (mAuth.getCurrentUser()!=null)
            isUserLoggedIn.setValue(true);
        return isUserLoggedIn;
    }
}
