package com.scout.hospitalapp.Models;

import com.google.gson.annotations.SerializedName;

public class ModelDoctorInfo {
    @SerializedName("_id")
    ModelRequestId doctorId;
    @SerializedName("name")
    String name;
    @SerializedName("email")
    String email;
    @SerializedName("phone_no")
    String phone_no;
    @SerializedName("address")
    String address;
    @SerializedName("department")
    String department;
    @SerializedName("career_history")
    String careerHistory;
    @SerializedName("learning_history")
    String learningHistory;

    public ModelDoctorInfo(String name, String email, String phone_no, String address, String department, String careerHistory, String learningHistory) {
        this.name = name;
        this.email = email;
        this.phone_no = phone_no;
        this.address = address;
        this.department = department;
        this.careerHistory = careerHistory;
        this.learningHistory = learningHistory;
    }

    public ModelRequestId getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(ModelRequestId doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCareerHistory() {
        return careerHistory;
    }

    public void setCareerHistory(String careerHistory) {
        this.careerHistory = careerHistory;
    }

    public String getLearningHistory() {
        return learningHistory;
    }

    public void setLearningHistory(String learningHistory) {
        this.learningHistory = learningHistory;
    }
}
