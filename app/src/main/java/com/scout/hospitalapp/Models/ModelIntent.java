package com.scout.hospitalapp.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelIntent implements Serializable {
    private ModelBookAppointment bookAppointmentData;
    private ModelDoctorInfo doctorProfileInfo;

    public ModelBookAppointment getBookAppointmentData() {
        return bookAppointmentData;
    }

    public void setBookAppointmentData(ModelBookAppointment bookAppointmentData) {
        this.bookAppointmentData = bookAppointmentData;
    }

    public ModelDoctorInfo getDoctorProfileInfo() {
        return doctorProfileInfo;
    }

    public void setDoctorProfileInfo(ModelDoctorInfo doctorProfileInfo) {
        this.doctorProfileInfo = doctorProfileInfo;
    }
}
