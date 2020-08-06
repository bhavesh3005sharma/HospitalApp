package com.scout.hospitalapp.ViewModels;

import android.content.Context;
import android.net.Uri;
import android.util.Patterns;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.Repository.Remote.HospitalDepartmentRepo;
import com.scout.hospitalapp.Repository.Remote.HospitalDoctorsRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import java.util.ArrayList;

public class DoctorsViewModel extends ViewModel {
    private HospitalDoctorsRepo hospitalDoctorsRepo;
    private LiveData<ArrayList<ModelDoctorInfo>> doctorsList;
    private LiveData<ArrayList<ModelDepartment>> departmentsList;

    public DoctorsViewModel() {
        doctorsList = new MutableLiveData<>();
    }

    public ModelRequestId getHospitalId(Context context) {
        return SharedPref.getLoginUserData(context).getHospitalId();
    }

    public String getHospitalName(Context context) {
        return SharedPref.getLoginUserData(context).getName();
    }

    public LiveData<ArrayList<ModelDoctorInfo>> getDoctors(String hospitalId) {
        hospitalDoctorsRepo = HospitalDoctorsRepo.getInstance();
        doctorsList = hospitalDoctorsRepo.getDoctorsList(hospitalId);
        return doctorsList;
    }

    public LiveData<ArrayList<ModelDepartment>> getDepartmentsList(String hospitalId) {
        HospitalDepartmentRepo departmentRepo = HospitalDepartmentRepo.getInstance();
        departmentsList = departmentRepo.getDepartmentsList(hospitalId);
        return departmentsList;
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

    public int getTimeDifference(String s) {
        String[] result,time1,time2;
        result = s.split("-");
        time1 = result[0].split(":");
        time2 = result[1].split(":");
        time1[0] = time1[0].replaceAll("\\s+", "");
        time2[0] = time2[0].replaceAll("\\s+", "");
        time1[1] = time1[1].replaceAll("\\s+", "");
        time2[1] = time2[1].replaceAll("\\s+", "");
        int h1,h2,m1,m2;
        h1 = Integer.valueOf(time1[0]);
        h2 = Integer.valueOf(time2[0]);
        m1 = Integer.valueOf(time1[1]);
        m2 = Integer.valueOf(time2[1]);

        if(h2>h1 && m2<m1){
            h2--;
            m2+=60;
        }
        return ((h2-h1)*60)+(m2-m1);
    }
}