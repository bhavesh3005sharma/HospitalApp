package com.scout.hospitalapp.Repository.Remote;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDepartmentRequest;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import com.scout.hospitalapp.retrofit.ApiService;
import com.scout.hospitalapp.retrofit.RetrofitNetworkApi;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalDepartmentRepo {
    private RetrofitNetworkApi networkApi = ApiService.getAPIService();
    private static HospitalDepartmentRepo instance;

    public static HospitalDepartmentRepo getInstance(){
        if(instance == null){
            instance = new HospitalDepartmentRepo();
        }
        return instance;
    }


    public LiveData<ArrayList<ModelDepartment>> getDepartmentsList(String hospitalId) {
        MutableLiveData<ArrayList<ModelDepartment>> listDepartments = new MutableLiveData<>();
        networkApi.getHospitalInfo(null,hospitalId).enqueue(new Callback<HospitalInfoResponse>() {
            @Override
            public void onResponse(Call<HospitalInfoResponse> call, Response<HospitalInfoResponse> response) {
                if (response.isSuccessful() && response.body()!=null)
                    listDepartments.postValue(response.body().getDepartments());
                else
                    listDepartments.postValue(null);
            }

            @Override
            public void onFailure(Call<HospitalInfoResponse> call, Throwable t) {
                listDepartments.postValue(null);
            }
        });
        return listDepartments;
    }

    public LiveData<Boolean> addDepartment(ModelDepartmentRequest request) {
        MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
        networkApi.addDepartment(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code()==200)
                    isSuccess.postValue(true);
                else isSuccess.postValue(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isSuccess.postValue(false);
            }
        });
        return isSuccess;
    }

    public LiveData<Boolean> removeDepartment(ModelDepartmentRequest request) {
        MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
        networkApi.removeDepartment(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code()==200)
                    isSuccess.postValue(true);
                else isSuccess.postValue(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isSuccess.postValue(false);
            }
        });
        return isSuccess;
    }

    public LiveData<Boolean> updateDepartment(ModelDepartmentRequest request) {
        MutableLiveData<Boolean> isUpdated = new MutableLiveData<>();
        networkApi.updateDepartment(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code()==200)
                    isUpdated.postValue(true);
                else isUpdated.postValue(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isUpdated.postValue(false);
            }
        });

        return isUpdated;
    }
}
