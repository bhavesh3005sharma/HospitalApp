package com.scout.hospitalapp.Repository.Remote;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scout.hospitalapp.Models.ModelDateTime;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import com.scout.hospitalapp.retrofit.ApiService;
import com.scout.hospitalapp.retrofit.RetrofitNetworkApi;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalDoctorsRepo {
    private RetrofitNetworkApi networkApi = ApiService.getAPIService();
    private static HospitalDoctorsRepo instance;
    private MutableLiveData<ArrayList<ModelDoctorInfo>> doctorsList = new MutableLiveData<>();
    private MutableLiveData<ModelRequestId> registeredDoctorId = new MutableLiveData<>();
    private ArrayList<ModelDoctorInfo> arrayList = new ArrayList<>();
    private ArrayList<ModelRequestId> hospitalDoctorsList = new ArrayList<>();

    public static HospitalDoctorsRepo getInstance(){
        if(instance == null){
            instance = new HospitalDoctorsRepo();
        }
        return instance;
    }

    public LiveData<ArrayList<ModelDoctorInfo>> getDoctorsList(String hospitalId) {

        networkApi.getHospitalInfo(null,hospitalId).enqueue(new Callback<HospitalInfoResponse>() {
            @Override
            public void onResponse(Call<HospitalInfoResponse> call, Response<HospitalInfoResponse> response) {
                assert response.body() != null;
                if (response.body().getHospitalDoctors()!=null) {
                    hospitalDoctorsList = response.body().getHospitalDoctors();
                    arrayList.clear();

                    for (ModelRequestId hospitalDoctor : hospitalDoctorsList) {
                        getDoctorDetails(hospitalDoctor.getId());
                    }
                    if (hospitalDoctorsList.size()==0)
                        doctorsList.postValue(null);
                }else doctorsList.postValue(null);
            }

            @Override
            public void onFailure(Call<HospitalInfoResponse> call, Throwable t) {
                doctorsList.postValue(null);
            }
        });
        return doctorsList;
    }

    private void getDoctorDetails(String id) {
        networkApi.getDoctorInfo(null,id).enqueue(new Callback<ModelDoctorInfo>() {
            @Override
            public void onResponse(Call<ModelDoctorInfo> call, Response<ModelDoctorInfo> response) {
                arrayList.add(response.body());
                if (hospitalDoctorsList.size() == arrayList.size()) {
                    doctorsList.setValue(arrayList);
                }
            }

            @Override
            public void onFailure(Call<ModelDoctorInfo> call, Throwable t) {
            }
        });
    }

    public void registerDoctor(ModelDoctorInfo doctorInfo) {
        networkApi.registerDoctor(doctorInfo).enqueue(new Callback<ModelRequestId>() {
            @Override
            public void onResponse(Call<ModelRequestId> call, Response<ModelRequestId> response) {
               getDoctorsList(doctorInfo.getHospitalStringId());
                // Response code is not working well and response body is also null.
                if (response.isSuccessful() && response.code()==200 && response.body()!=null)
                    registeredDoctorId.postValue(response.body());
                else
                    registeredDoctorId.postValue(null);
            }

            @Override
            public void onFailure(Call<ModelRequestId> call, Throwable t) {
                registeredDoctorId.postValue(null);
            }
        });
    }

    public LiveData<ModelRequestId> getDoctorRegisterId() {
        return registeredDoctorId;
    }

    public LiveData<Boolean> removeDoctor(String doctorId, String hospitalId) {
        MutableLiveData<Boolean> isDoctorRemoved = new MutableLiveData<>();
        networkApi.removeDoctor(hospitalId,doctorId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code()==200)
                    isDoctorRemoved.postValue(true);
                else isDoctorRemoved.postValue(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isDoctorRemoved.postValue(false);
            }
        });
        return isDoctorRemoved;
    }

    public LiveData<String> updateDoctor(ModelDoctorInfo doctorInfo) {
        MutableLiveData<String> result = new MutableLiveData<>();
        networkApi.updateDoctor(doctorInfo).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code()==200)
                    result.postValue("Doctor Updated Successfully");
                else
                    result.postValue(response.errorBody().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.postValue(t.getMessage());
            }
        });
        return result;
    }

    public LiveData<ArrayList<ModelDateTime>> getUnavailableDates(ModelDoctorInfo doctorProfileInfo) {
        MutableLiveData<ArrayList<ModelDateTime>> result = new MutableLiveData<>();
        networkApi = ApiService.getAPIService();
        networkApi.getUnavailableDates(doctorProfileInfo.getDoctorId().getId()).enqueue(new Callback<ModelDoctorInfo>() {
            @Override
            public void onResponse(Call<ModelDoctorInfo> call, Response<ModelDoctorInfo> response) {
                if(response.isSuccessful() && response.code()==200){
                    result.setValue(response.body().getUnAvailableDates());
                }
                else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ModelDoctorInfo> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }
}
