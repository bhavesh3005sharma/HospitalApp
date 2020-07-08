package com.scout.hospitalapp.ViewModels;

import android.content.Context;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Repository.Remote.HospitalDepartmentRepo;
import com.scout.hospitalapp.Repository.Remote.HospitalDoctorsRepo;

import java.util.ArrayList;

public class DoctorProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    HospitalDoctorsRepo hospitalDoctorsRepo;

    public DoctorProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public String getAvailabilityType(String availabilityType_, ArrayList<String> doctorAvailability, Context context) {
        String availabilityType=availabilityType_;
        if (availabilityType.equals(context.getString(R.string.weekly)))
            availabilityType+="\nWeekdays : ";
        if (availabilityType.equals(context.getString(R.string.monthly)))
            availabilityType+="\nDates : ";
        for(int i=0; i<doctorAvailability.size();i++){
            availabilityType+=doctorAvailability.get(i);
            if (i!=doctorAvailability.size()-1)
                availabilityType+=", ";
        }
        return availabilityType;
    }

    public String getAvailabilityTime(String avgCheckupTime, ArrayList<String> doctorAvailabilityTime, Context context) {
        String availabilityTime= context.getString(R.string.checkup_time)+" : "+avgCheckupTime+"\n";
        availabilityTime+=context.getString(R.string.select_availability_time)+" : ";
        for(int i=0; i<doctorAvailabilityTime.size();i++) {
            availabilityTime += doctorAvailabilityTime.get(i);
            if (i != doctorAvailabilityTime.size() - 1)
                availabilityTime += ", ";
        }
        return availabilityTime;
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

    public LiveData<String> updateDoctor(ModelDoctorInfo doctorInfo) {
        hospitalDoctorsRepo = HospitalDoctorsRepo.getInstance();
        return hospitalDoctorsRepo.updateDoctor(doctorInfo);
    }

    public LiveData<ArrayList<ModelDepartment>> getDepartmentsList(String hospitalId) {
        HospitalDepartmentRepo departmentRepo = HospitalDepartmentRepo.getInstance();
        return departmentRepo.getDepartmentsList(hospitalId);
    }
}