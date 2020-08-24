package com.scout.hospitalapp.ViewModels;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.Repository.Remote.AppointmentRequestRepo;
import com.scout.hospitalapp.Repository.Remote.AppointmentsHistoryRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import java.util.ArrayList;

public class SearchViewModel extends ViewModel {
    AppointmentsHistoryRepo appointmentsRepo;

    public String getHospitalId(Context context) {
        return SharedPref.getLoginUserData(context).getHospitalId().getId();
    }

    public LiveData<ArrayList<ModelAppointment>> getFilterAppointments(String filterDate, String hospitalId) {
        appointmentsRepo = AppointmentsHistoryRepo.getInstance();
        return appointmentsRepo.getFilterAppointments(filterDate,hospitalId);
    }

    public LiveData<String> setStatus(String hospitalId, String appointmentId, String status) {
        AppointmentRequestRepo appointmentRequestRepo = AppointmentRequestRepo.getInstance();
        return appointmentRequestRepo.setStatus(hospitalId,appointmentId,status);
    }
}
