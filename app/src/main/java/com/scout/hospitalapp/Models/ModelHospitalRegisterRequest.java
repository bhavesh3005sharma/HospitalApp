package com.scout.hospitalapp.Models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class ModelHospitalRegisterRequest {
    @SerializedName("name")
    String name;
    @SerializedName("email")
    String email;
    @SerializedName("phone_no")
    String phone_no;
    @SerializedName("address")
    String address;
    @SerializedName("year_of_establishment")
    String year_of_establishment;
    @SerializedName("departments")
    ArrayList<ModelDepartment> departments;
    @SerializedName("doctors_list")
    ArrayList<ModelDoctorInfo> doctors;
    String password;

    public ModelHospitalRegisterRequest(String name, String email,String password, String phone_no, String address, String year_of_establishment, ArrayList<ModelDepartment> departments, ArrayList<ModelDoctorInfo> doctors) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone_no = phone_no;
        this.address = address;
        this.year_of_establishment = year_of_establishment;
        this.departments = departments;
        this.doctors = doctors;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
