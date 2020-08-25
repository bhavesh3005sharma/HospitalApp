package com.scout.hospitalapp.response;

import com.google.gson.annotations.SerializedName;
import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelRequestId;

import java.io.Serializable;
import java.util.ArrayList;

public class HospitalInfoResponse implements Serializable {
    @SerializedName("_id")
    ModelRequestId hospitalId;
    @SerializedName("name")
    String name;
    @SerializedName("schedule")
    String schedule;
    @SerializedName("email")
    String email;
    @SerializedName("phone_no")
    String phone_no;
    @SerializedName("address")
    String address;
    @SerializedName("url")
    String url;
    @SerializedName("year_of_establishment")
    String year_of_establishment;
    @SerializedName("departments")
    ArrayList<ModelDepartment> departments;
    @SerializedName("hospital_doctors")
    ArrayList<ModelRequestId> hospitalDoctors;
    @SerializedName("pending_appointment_list")
    ArrayList<ModelRequestId> pendingAppointmentsList;
    @SerializedName("confirmed_appointment_list")
    ArrayList<ModelRequestId> confirmedAppointmentsList;
    @SerializedName("past_appointment_list")
    ArrayList<ModelRequestId> pastAppointmentsList;
    @SerializedName("hospital_id")
    String hospitalStrId;

    public HospitalInfoResponse( String name,String email, String phone_no, String address, String year_of_establishment) {
        this.name = name;
        this.email = email;
        this.phone_no = phone_no;
        this.address = address;
        this.year_of_establishment = year_of_establishment;
    }

    public HospitalInfoResponse( String name,String id ,String email, String phone_no, String address, String year_of_establishment) {
        this.name = name;
        this.hospitalStrId = id;
        this.email = email;
        this.phone_no = phone_no;
        this.address = address;
        this.year_of_establishment = year_of_establishment;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<ModelRequestId> getPendingAppointmentsList() {
        return pendingAppointmentsList;
    }

    public ArrayList<ModelRequestId> getConfirmedAppointmentsList() {
        return confirmedAppointmentsList;
    }

    public ArrayList<ModelRequestId> getPastAppointmentsList() {
        return pastAppointmentsList;
    }

    public ArrayList<ModelRequestId> getHospitalDoctors() {
        return hospitalDoctors;
    }

    public void setHospitalDoctors(ArrayList<ModelRequestId> hospitalDoctors) {
        this.hospitalDoctors = hospitalDoctors;
    }

    public ModelRequestId getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(ModelRequestId hospitalId) {
        this.hospitalId = hospitalId;
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

    public String getYear_of_establishment() {
        return year_of_establishment;
    }

    public void setYear_of_establishment(String year_of_establishment) {
        this.year_of_establishment = year_of_establishment;
    }

    public ArrayList<ModelDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<ModelDepartment> departments) {
        this.departments = departments;
    }
}
