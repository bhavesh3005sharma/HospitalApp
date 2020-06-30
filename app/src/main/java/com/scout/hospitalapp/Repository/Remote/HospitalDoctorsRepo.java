package com.scout.hospitalapp.Repository.Remote;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelHospitalDoctor;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import com.scout.hospitalapp.retrofit.ApiService;
import com.scout.hospitalapp.retrofit.RetrofitNetworkApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalDoctorsRepo {
    RetrofitNetworkApi networkApi = ApiService.getAPIService();
    private static HospitalDoctorsRepo instance;
    private MutableLiveData<ArrayList<ModelDoctorInfo>> doctorsList = new MutableLiveData<>();
    private ArrayList<ModelDoctorInfo> arrayList = new ArrayList<>();
    ArrayList<ModelHospitalDoctor> hospitalDoctorsList = new ArrayList<>();

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
                hospitalDoctorsList = response.body().getHospitalDoctors();
                arrayList.clear();

                for(ModelHospitalDoctor hospitalDoctor : hospitalDoctorsList){
                    getDoctorDetails(hospitalDoctor.getDoctorId().getId());
                }
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
}
