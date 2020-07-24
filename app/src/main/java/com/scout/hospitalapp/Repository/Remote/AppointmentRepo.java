package com.scout.hospitalapp.Repository.Remote;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import com.scout.hospitalapp.retrofit.ApiService;
import com.scout.hospitalapp.retrofit.RetrofitNetworkApi;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentRepo {
    RetrofitNetworkApi networkApi = ApiService.getAPIService();
    private static AppointmentRepo instance;
    ArrayList<ModelRequestId> appointmentsIdsList = new ArrayList<>();
    MutableLiveData<ArrayList<ModelAppointment>> appointmentsListLive = new MutableLiveData<>();
    MutableLiveData<Integer> startingIndexForList = new MutableLiveData<>();

    public static AppointmentRepo getInstance(){
        if(instance == null){
            instance = new AppointmentRepo();
        }
        return instance;
    }
    
    public void loadAppointmentRequestsIdsList(String hospitalId) {
        networkApi.getPendingAppointmentsList(hospitalId).enqueue(new Callback<HospitalInfoResponse>() {
            @Override
            public void onResponse(Call<HospitalInfoResponse> call, Response<HospitalInfoResponse> response) {
                if (response.isSuccessful() && response.code()==200){
                    if (response.body().getPendingAppointmentsList()!=null) {
                        appointmentsIdsList.clear();
                        appointmentsIdsList.addAll(response.body().getPendingAppointmentsList());
                        loadAppointmentList(0);
                    }
                } else {
                    appointmentsListLive.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<HospitalInfoResponse> call, Throwable t) {
                appointmentsListLive.setValue(null);
            }
        });
    }

    public void loadAppointmentList(int startingIndex) {
        networkApi = ApiService.getAPIService();
        ArrayList<ModelAppointment> appointmentArrayList = new ArrayList<>();
        final int[] maxIndex = {10 + startingIndex};
        if(appointmentsIdsList.size() < maxIndex[0])
            maxIndex[0] = appointmentsIdsList.size();

        if(appointmentsIdsList.size()==0)
            appointmentsListLive.setValue(null);

        for(int i = startingIndex; i< maxIndex[0]; i++){
            int finalI = i;
            networkApi.getAppointmentsDetails(appointmentsIdsList.get(i).getId()).enqueue(new Callback<ModelAppointment>() {
                @Override
                public void onResponse(Call<ModelAppointment> call, Response<ModelAppointment> response) {
                    if(response.isSuccessful() && response.code()==200){
                        appointmentArrayList.add(response.body());

                        if(finalI == maxIndex[0] -1){
                            int newStartingIndex;
                            if (maxIndex[0]<appointmentsIdsList.size())
                                newStartingIndex = maxIndex[0];
                            else
                                newStartingIndex = -1;
                            appointmentsListLive.setValue(appointmentArrayList);
                            startingIndexForList.setValue(newStartingIndex);
                        }
                    }
                    else {
                        appointmentsListLive.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<ModelAppointment> call, Throwable t) {
                    appointmentsListLive.setValue(null);
                }
            });
        }
    }

    public LiveData<ArrayList<ModelAppointment>> getAppointmentsList() { return appointmentsListLive; }

    public LiveData<Integer> getStartingIndexOfList() {
        return startingIndexForList;
    }

    public void setStatus(String hospitalId, String appointmentId, String status) {
        networkApi = ApiService.getAPIService();
        networkApi.SetAppointmentStatus(hospitalId,appointmentId,status).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code()==200){
                    // Appointment Status Updated Successfully.
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
