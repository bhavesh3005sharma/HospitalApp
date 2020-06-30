package com.scout.hospitalapp.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelHospitalDoctor {
    @SerializedName("department")
    String department;
    @SerializedName("doctor_id")
    ModelRequestId doctorId;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public ModelRequestId getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(ModelRequestId doctorId) {
        this.doctorId = doctorId;
    }
}
