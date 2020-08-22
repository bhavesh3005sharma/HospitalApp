package com.scout.hospitalapp.Repository.Remote;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.Models.ModelBookAppointment;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import com.scout.hospitalapp.response.ResponseMessage;
import com.scout.hospitalapp.retrofit.ApiService;
import com.scout.hospitalapp.retrofit.RetrofitNetworkApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentsRepo {
    RetrofitNetworkApi networkApi = ApiService.getAPIService();
    private static AppointmentsRepo instance;
    private ArrayList<ModelRequestId> appointmentsIdsList = new ArrayList<>();
    private MutableLiveData<ArrayList<ModelAppointment>> appointmentsListLive = new MutableLiveData<>();
    private MutableLiveData<Integer> startingIndexForList = new MutableLiveData<>();

    public static AppointmentsRepo getInstance(){
        if(instance == null){
            instance = new AppointmentsRepo();
        }
        return instance;
    }

    public void loadAppointmentIdsList(String hospitalId) {
        networkApi = ApiService.getAPIService();
        networkApi.getConfirmedAppointmentsList(hospitalId).enqueue(new Callback<HospitalInfoResponse>() {
            @Override
            public void onResponse(Call<HospitalInfoResponse> call, Response<HospitalInfoResponse> response) {
                if (response.isSuccessful() && response.code()==200){
                    if (response.body().getConfirmedAppointmentsList()!=null) {
                        appointmentsIdsList.clear();
                        appointmentsIdsList.addAll(response.body().getConfirmedAppointmentsList());
                        Log.d("appointmentsIdsList",""+appointmentsIdsList.size());
                        loadAppointmentList(0);
                    }else {
                        // User have no confirmed Appointments.
                        appointmentsListLive.setValue(null);
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
        final int[] maxIndex = {20 + startingIndex};
        if(appointmentsIdsList.size() < maxIndex[0])
            maxIndex[0] = appointmentsIdsList.size();

        if(appointmentsIdsList.size()==0)
            appointmentsListLive.setValue(null);
        Log.d("StartingIndex",""+startingIndex);
        Log.d("maxIndex",""+maxIndex[0]);

        for(int i = startingIndex; i< maxIndex[0]; i++){
            int finalI = i;
            networkApi.getAppointmentsDetails(appointmentsIdsList.get(i).getId()).enqueue(new Callback<ModelAppointment>() {
                @Override
                public void onResponse(Call<ModelAppointment> call, Response<ModelAppointment> response) {
                    if(response.isSuccessful() && response.code()==200){
                        appointmentArrayList.add(response.body());
                    }
                    else {
                        appointmentsListLive.setValue(null);
                    }

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

                @Override
                public void onFailure(Call<ModelAppointment> call, Throwable t) {
                    appointmentsListLive.setValue(null);
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
            });
        }
    }

    public LiveData<ArrayList<ModelAppointment>> getAppointmentsList() { return appointmentsListLive; }

    public LiveData<Integer> getStartingIndexOfList() {
        return startingIndexForList;
    }

    public LiveData<String> bookAppointment(ModelBookAppointment appointment) {
        MutableLiveData<String> message = new MutableLiveData<>();
        networkApi = ApiService.getAPIService();
        networkApi.bookAppointment(appointment).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if (response.isSuccessful() && response.code()==200) {
                    if (response.body()!=null)
                        message.setValue(response.body().getMessage());
                    else
                        message.setValue("Appointment Booked");
                }
                else {
                    message.setValue(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                message.setValue(t.getMessage());
            }
        });
        return message;
    }
}
