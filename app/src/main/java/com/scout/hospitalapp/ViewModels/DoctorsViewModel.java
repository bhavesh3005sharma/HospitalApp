package com.scout.hospitalapp.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelHospitalDoctor;
import com.scout.hospitalapp.Repository.Remote.HospitalDoctorsRepo;
import com.scout.hospitalapp.Repository.Remote.HospitalRegisterRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import com.scout.hospitalapp.response.HospitalInfoResponse;

import java.util.ArrayList;
import java.util.Objects;

public class DoctorsViewModel extends ViewModel {
    HospitalDoctorsRepo hospitalDoctorsRepo;
    private LiveData<ArrayList<ModelDoctorInfo>> doctorsList;

    public DoctorsViewModel() {
        doctorsList = new MutableLiveData<>();
    }

    public String getHospitalId(Context context) {
        String id = SharedPref.getLoginUserData(context).getHospitalId().getId();
        return id;
    }

    public LiveData<ArrayList<ModelDoctorInfo>> getDoctors(String hospitalId) {
        hospitalDoctorsRepo = HospitalDoctorsRepo.getInstance();
        doctorsList = hospitalDoctorsRepo.getDoctorsList(hospitalId);
        return doctorsList;
    }
}