package com.scout.hospitalapp.ViewModels;

import android.content.Context;
import android.content.res.Resources;
import android.util.Patterns;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Repository.Remote.HospitalDoctorsRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import java.util.ArrayList;

public class DoctorsViewModel extends ViewModel {
    HospitalDoctorsRepo hospitalDoctorsRepo;
    private LiveData<ArrayList<ModelDoctorInfo>> doctorsList;

    public DoctorsViewModel() {
        doctorsList = new MutableLiveData<>();
    }

    public ModelRequestId getHospitalId(Context context) {
        return SharedPref.getLoginUserData(context).getHospitalId();
    }

    public LiveData<ArrayList<ModelDoctorInfo>> getDoctors(String hospitalId) {
        hospitalDoctorsRepo = HospitalDoctorsRepo.getInstance();
        doctorsList = hospitalDoctorsRepo.getDoctorsList(hospitalId);
        return doctorsList;
    }

    public LiveData<ArrayList<String>> getDepartmentsList(Context context) {
        ArrayList<ModelDepartment> departmentArrayList = SharedPref.getLoginUserData(context).getDepartments();
        ArrayList<String> departNames = new ArrayList<>();
        for (ModelDepartment department : departmentArrayList)
            departNames.add(department.getDepartmentName());
        MutableLiveData<ArrayList<String>> data = new MutableLiveData<>();
        data.setValue(departNames);
        return data;
    }

    public void registerDoctor(ModelDoctorInfo doctorInfo) {
        hospitalDoctorsRepo = HospitalDoctorsRepo.getInstance();
        hospitalDoctorsRepo.registerDoctor(doctorInfo);
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

    public LiveData<ModelRequestId> getIsDoctorRegistered() {
        return hospitalDoctorsRepo.getDoctorRegisterId();
    }

    public LiveData<Boolean> deleteDoctor(String doctorId, String hospitalId) {
        hospitalDoctorsRepo = HospitalDoctorsRepo.getInstance();
        return hospitalDoctorsRepo.removeDoctor(doctorId,hospitalId);
    }
}