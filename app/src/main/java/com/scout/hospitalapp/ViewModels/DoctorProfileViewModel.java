package com.scout.hospitalapp.ViewModels;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.scout.hospitalapp.Activities.DoctorProfileActivity;
import com.scout.hospitalapp.Activities.EditProfileActivity;
import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Repository.Remote.HospitalDataRepo;
import com.scout.hospitalapp.Repository.Remote.HospitalDepartmentRepo;
import com.scout.hospitalapp.Repository.Remote.HospitalDoctorsRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import com.scout.hospitalapp.response.HospitalInfoResponse;

import java.util.ArrayList;

public class DoctorProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    HospitalDoctorsRepo hospitalDoctorsRepo;
    private StorageTask mUploadTask;
    StorageReference mStorageRef;

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

    public LiveData<String> saveProfilePic(DoctorProfileActivity doctorProfileActivity, Uri mImageUri, String fileName, String id) {
        MutableLiveData<String> message = new MutableLiveData<>();
        if (mUploadTask != null && mUploadTask.isInProgress())
            message.setValue("Profile Update is in Progress");
        else {
            mStorageRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid()+"/Doctor's ProfilePic/"+id);
            StorageReference fileReference = mStorageRef.child(fileName);

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("uploadProfilePic", "success");
                                    String url = uri.toString();
                                    hospitalDoctorsRepo = HospitalDoctorsRepo.getInstance();
                                    hospitalDoctorsRepo.updateDoctorProfilePic(id,url).observe(doctorProfileActivity, new Observer<String>() {
                                        @Override
                                        public void onChanged(String s) {
                                            message.setValue(s);
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            message.setValue(e.getMessage());
                        }
                    });
        }
        return message;
    }
}