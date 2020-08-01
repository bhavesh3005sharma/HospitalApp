package com.scout.hospitalapp.ViewModels;

import android.util.Patterns;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelHospitalRegisterRequest;
import com.scout.hospitalapp.Repository.Remote.HospitalDataRepo;
import com.scout.hospitalapp.response.HospitalInfoResponse;

public class AuthViewModel extends ViewModel {
    HospitalDataRepo hospitalRegisterRepo;
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
        if (doctorInfo.getDepartment().equals("Please Select Department"))
            return "Please Mention his Department.";
        if (doctorInfo.getAvgCheckupTime().isEmpty())
            return "Please Specify Average Checkup Time.";
        if (doctorInfo.getAvailabilityType()==null || doctorInfo.getAvailabilityType().isEmpty())
            return "Please Specify Doctor's Availability Type.";
        if (!doctorInfo.getAvailabilityType().equals("Daily") && doctorInfo.getDoctorAvailability().isEmpty()) {
            if (doctorInfo.getAvailabilityType().equals("Weekly"))
                return "Please Specify Available Week Days.";
            if (doctorInfo.getAvailabilityType().equals("Monthly"))
                return "Please Specify Available Dates.";
        }
        if (doctorInfo.getDoctorAvailabilityTime().isEmpty())
            return "Please Specify Doctor's Availability Time.";

        return  "Correct";
    }

    public void registerHospital(ModelHospitalRegisterRequest registerRequest) {
        hospitalRegisterRepo = HospitalDataRepo.getInstance();
        message = hospitalRegisterRepo.registerHospital(registerRequest);
    }

    public LiveData<String> getRegisterResponse(){
        return message;
    }

    public void loginUser(String email, String password) {
        hospitalRegisterRepo = HospitalDataRepo.getInstance();
        message = hospitalRegisterRepo.loginUser(email,password);
    }

    public LiveData<HospitalInfoResponse> getHospitalInfoResponse(String email, String id){
        hospitalRegisterRepo = HospitalDataRepo.getInstance();
        return hospitalRegisterRepo.getHospitalInfoResponse(email,id);
    }

    public LiveData<Boolean> isUserLoggedIn(){
        hospitalRegisterRepo = HospitalDataRepo.getInstance();
        return hospitalRegisterRepo.isUserLoggedIn();
    }
}
