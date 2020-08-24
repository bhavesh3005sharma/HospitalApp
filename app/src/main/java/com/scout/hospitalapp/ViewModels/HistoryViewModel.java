package com.scout.hospitalapp.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.Repository.Remote.AppointmentRequestRepo;
import com.scout.hospitalapp.Repository.Remote.AppointmentsHistoryRepo;
import com.scout.hospitalapp.Repository.Remote.AppointmentsRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;

import java.util.ArrayList;

public class HistoryViewModel extends ViewModel {
    AppointmentsHistoryRepo appointmentsRepo;

    public String getHospitalId(Context context) {
        return SharedPref.getLoginUserData(context).getHospitalId().getId();
    }

    public void loadAppointmentIdsList(String hospitalId) {
        appointmentsRepo = AppointmentsHistoryRepo.getInstance();
        appointmentsRepo.loadAppointmentIdsList(hospitalId);
    }

    public LiveData<ArrayList<ModelAppointment>> getAppointmentsList() {
        appointmentsRepo = AppointmentsHistoryRepo.getInstance();
        return appointmentsRepo.getAppointmentsList();
    }

    public LiveData<Integer> getStartingIndexOfList() {
        appointmentsRepo = AppointmentsHistoryRepo.getInstance();
        return appointmentsRepo.getStartingIndexOfList();
    }

    public void loadAppointments(int startingIndex) {
        appointmentsRepo = AppointmentsHistoryRepo.getInstance();
        appointmentsRepo.loadAppointmentList(startingIndex);
    }

    public LiveData<ArrayList<ModelAppointment>> getFilterAppointments(String filterDate, String hospitalId) {
        appointmentsRepo = AppointmentsHistoryRepo.getInstance();
        return appointmentsRepo.getFilterAppointments(filterDate,hospitalId);
    }
}