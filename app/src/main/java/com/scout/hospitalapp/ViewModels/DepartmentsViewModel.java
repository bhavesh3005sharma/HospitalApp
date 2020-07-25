package com.scout.hospitalapp.ViewModels;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.scout.hospitalapp.Fragments.DepartmentsFragment;
import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDepartmentRequest;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.Repository.Remote.HospitalDepartmentRepo;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import java.util.ArrayList;

public class DepartmentsViewModel extends ViewModel {
    HospitalDepartmentRepo hospitalDepartmentRepo;
    private LiveData<ArrayList<ModelDepartment>> departmentsList;

    public ModelRequestId getHospitalId(Context context) {
        return SharedPref.getLoginUserData(context).getHospitalId();
    }

    public LiveData<ArrayList<ModelDepartment>> getDepartmentsList(String hospitalId) {
        hospitalDepartmentRepo = HospitalDepartmentRepo.getInstance();
        departmentsList = hospitalDepartmentRepo.getDepartmentsList(hospitalId);
        return departmentsList;
    }

    public LiveData<Boolean> addDepartment(ModelDepartmentRequest request) {
        hospitalDepartmentRepo = HospitalDepartmentRepo.getInstance();
        return hospitalDepartmentRepo.addDepartment(request);
    }

    public LiveData<Boolean> removeDepartment(ModelDepartmentRequest request) {
        hospitalDepartmentRepo = HospitalDepartmentRepo.getInstance();
        return hospitalDepartmentRepo.removeDepartment(request);
    }

    public LiveData<Boolean> updateDepartment(ModelDepartmentRequest request) {
        hospitalDepartmentRepo = HospitalDepartmentRepo.getInstance();
        return hospitalDepartmentRepo.updateDepartment(request);
    }
}