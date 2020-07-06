package com.scout.hospitalapp.Models;

import com.google.gson.annotations.SerializedName;

public class ModelDepartmentRequest {
    @SerializedName("hospital_id")
    String hospitalId;
    @SerializedName("department")
    ModelDepartment modelDepartment;

    public ModelDepartmentRequest(String hospitalId, ModelDepartment modelDepartment) {
        this.hospitalId = hospitalId;
        this.modelDepartment = modelDepartment;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public ModelDepartment getModelDepartment() {
        return modelDepartment;
    }
}
