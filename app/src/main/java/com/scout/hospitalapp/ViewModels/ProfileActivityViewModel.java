package com.scout.hospitalapp.ViewModels;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.scout.hospitalapp.Activities.EditProfileActivity;
import com.scout.hospitalapp.Activities.ProfileActivity;
import com.scout.hospitalapp.Repository.Remote.HospitalDataRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import com.scout.hospitalapp.response.HospitalInfoResponse;

public class ProfileActivityViewModel extends ViewModel {
    HospitalDataRepo hospitalDataRepo;
    private StorageTask mUploadTask;
    StorageReference mStorageRef;

    public LiveData<HospitalInfoResponse> loadProfileData(ProfileActivity profileActivity) {
        hospitalDataRepo = HospitalDataRepo.getInstance();
        return hospitalDataRepo.getHospitalInfoResponse(null, SharedPref.getLoginUserData(profileActivity).getHospitalId().getId());
    }

    public LiveData<String> updateProfile(HospitalInfoResponse requestData) {
        hospitalDataRepo = HospitalDataRepo.getInstance();
        return hospitalDataRepo.updateHospitalProfile(requestData);
    }

    public LiveData<String> saveProfilePic(EditProfileActivity editProfileActivity, Uri mImageUri, String fileName) {
        MutableLiveData<String> message = new MutableLiveData<>();
        if (mUploadTask != null && mUploadTask.isInProgress())
            message.setValue("Profile Update is in Progress");
        else {
            mStorageRef = FirebaseStorage.getInstance().getReference("ProfilePics"+ FirebaseAuth.getInstance().getUid());
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
                                    hospitalDataRepo = HospitalDataRepo.getInstance();
                                    HospitalInfoResponse hospitalInfoResponse = SharedPref.getLoginUserData(editProfileActivity);
                                    String id = hospitalInfoResponse.getHospitalId().getId();
                                    hospitalDataRepo.updateHospitalProfilePic(id,url).observe(editProfileActivity, new Observer<String>() {
                                        @Override
                                        public void onChanged(String s) {
                                            message.setValue(s);
                                            if (s!=null && s.equals("Profile Pic Updated Successfully")) {
                                                hospitalInfoResponse.setUrl(url);
                                                SharedPref.saveLoginUserData(editProfileActivity,hospitalInfoResponse);
                                            }
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
