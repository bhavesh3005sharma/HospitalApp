package com.scout.hospitalapp.ViewModels;

import android.util.Patterns;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelHospitalRegisterRequest;
import com.scout.hospitalapp.Repository.Remote.HospitalRegisterRepo;
import com.scout.hospitalapp.response.HospitalInfoResponse;

public class AuthViewModel extends ViewModel {
    HospitalRegisterRepo hospitalRegisterRepo;
    LiveData<String> message;

    public String isDetailsValid(String name, String email, String password, String phoneNo, String address, String yearOfEstablishment) {
        if(name.isEmpty())
            return "Name is Required.";
        if (email.isEmpty())
            return "Email is Required.";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Please Provide Valid Email.";
        if (password.isEmpty())
            return "Password is Required.";
        if (password.length()<6)
            return "At least 6 character Password is Required.";
        if (phoneNo.isEmpty())
            return "Phone Number is Required.";
        if (address.isEmpty())
            return "Address is Required.";
        if (yearOfEstablishment.isEmpty())
            return "Please Specify Year Of Establishment.";

        return "Correct";
    }

    public String validateDataOfDoctor(ModelDoctorInfo doctorInfo) {
        if (doctorInfo.getName().isEmpty())
            return "Please Mention Doctor's Name.";
        if (doctorInfo.getEmail().isEmpty())
            return "Email is Required.";
        if (!Patterns.EMAIL_ADDRESS.matcher(doctorInfo.getEmail()).matches())
            return "Please Provide Valid Email.";
        if (doctorInfo.getPhone_no().isEmpty())
            return "Phone Number is Required.";
        if (doctorInfo.getAddress().isEmpty())
            return "Address is Required.";
        if (doctorInfo.getCareerHistory().isEmpty())
            return "Please specify Working Experience.";
        if (doctorInfo.getLearningHistory().isEmpty())
            return "Please Mention about his Studies.";
        if (doctorInfo.getDepartment().isEmpty())
            return "Please Mention his Department.";

        return  "Correct";//Resources.getSystem().getString(R.string.correct);
    }

    public void registerHospital(ModelHospitalRegisterRequest registerRequest) {
        hospitalRegisterRepo = HospitalRegisterRepo.getInstance();
        message = hospitalRegisterRepo.registerHospital(registerRequest);
    }

    public LiveData<String> getRegisterResponse(){
        return message;
    }

    public void loginUser(String email, String password) {
        hospitalRegisterRepo = HospitalRegisterRepo.getInstance();
        message = hospitalRegisterRepo.loginUser(email,password);
    }

    public LiveData<HospitalInfoResponse> getHospitalInfoResponse(){
        return hospitalRegisterRepo.getHospitalInfoResponse();
    }
}
