package com.scout.hospitalapp.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.Repository.Remote.AppointmentRequestRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;

import java.util.ArrayList;

public class AppointmentsViewModel extends ViewModel {
    private AppointmentRequestRepo appointmentRepo;

    public String getHospitalId(Context context) {
        return SharedPref.getLoginUserData(context).getHospitalId().getId();
    }

    public void loadAppointmentRequestsIdsList(String hospitalId) {
        appointmentRepo = AppointmentRequestRepo.getInstance();
        appointmentRepo.loadAppointmentRequestsIdsList(hospitalId);
    }

    public LiveData<ArrayList<ModelAppointment>> getAppointmentsList() {
        appointmentRepo = AppointmentRequestRepo.getInstance();
        return appointmentRepo.getAppointmentsList();
    }

    public LiveData<Integer> getStartingIndexOfList() {
        appointmentRepo = AppointmentRequestRepo.getInstance();
        return appointmentRepo.getStartingIndexOfList();
    }

    public void loadAppointments(int startingIndex) {
        appointmentRepo = AppointmentRequestRepo.getInstance();
        appointmentRepo.loadAppointmentList(startingIndex);
    }

    public void setStatus(String hospitalId, String appointmentId, String status) {
        appointmentRepo = AppointmentRequestRepo.getInstance();
        appointmentRepo.setStatus(hospitalId,appointmentId,status);
    }
}
