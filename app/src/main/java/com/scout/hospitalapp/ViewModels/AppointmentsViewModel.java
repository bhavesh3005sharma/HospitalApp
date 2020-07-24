package com.scout.hospitalapp.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.Repository.Remote.AppointmentRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;

import java.util.ArrayList;

public class AppointmentsViewModel extends ViewModel {
    private AppointmentRepo appointmentRepo;

    public String getHospitalId(Context context) {
        return SharedPref.getLoginUserData(context).getHospitalId().getId();
    }

    public void loadAppointmentRequestsIdsList(String hospitalId) {
        appointmentRepo = AppointmentRepo.getInstance();
        appointmentRepo.loadAppointmentRequestsIdsList(hospitalId);
    }

    public LiveData<ArrayList<ModelAppointment>> getAppointmentsList() {
        appointmentRepo = AppointmentRepo.getInstance();
        return appointmentRepo.getAppointmentsList();
    }

    public LiveData<Integer> getStartingIndexOfList() {
        appointmentRepo = AppointmentRepo.getInstance();
        return appointmentRepo.getStartingIndexOfList();
    }

    public void loadAppointments(int startingIndex) {
        appointmentRepo = AppointmentRepo.getInstance();
        appointmentRepo.loadAppointmentList(startingIndex);
    }

    public void setStatus(String hospitalId, String appointmentId, String status) {
        appointmentRepo = AppointmentRepo.getInstance();
        appointmentRepo.setStatus(hospitalId,appointmentId,status);
    }
}
