package com.scout.hospitalapp.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.scout.hospitalapp.Activities.ProfileActivity;
import com.scout.hospitalapp.Repository.Remote.HospitalDataRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import com.scout.hospitalapp.response.HospitalInfoResponse;

public class ProfileActivityViewModel extends ViewModel {
    HospitalDataRepo hospitalDataRepo;

    public LiveData<HospitalInfoResponse> loadProfileData(ProfileActivity profileActivity) {
        hospitalDataRepo = HospitalDataRepo.getInstance();
        return hospitalDataRepo.getHospitalInfoResponse(null, SharedPref.getLoginUserData(profileActivity).getHospitalId().getId());
    }
}
