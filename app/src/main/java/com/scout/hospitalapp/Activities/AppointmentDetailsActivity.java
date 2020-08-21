package com.scout.hospitalapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AppointmentDetailsActivity extends AppCompatActivity {
    @BindView(R.id.textViewAppointmentId) TextView textViewAppointmentId;
    @BindView(R.id.patientName) TextView patientName;
    @BindView(R.id.doctorName) TextView doctorName;
    @BindView(R.id.hospitalName) TextView hospitalName;
    @BindView(R.id.textSymptoms) TextView textSymptoms;
    @BindView(R.id.date) TextView date;
    @BindView(R.id.textViewAge) TextView textViewAge;
    @BindView(R.id.time) TextView time;
    @BindView(R.id.serialNo) TextView serialNo;
    @BindView(R.id.textViewStatus) TextView textViewStatus;

    Unbinder unbinder;
    ModelAppointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);
        unbinder = ButterKnife.bind(this);

        appointment = (ModelAppointment) getIntent().getSerializableExtra("modelAppointment");
        
        setUpUi();
        setUpToolbar();
    }

    private void setUpUi() {
        if (appointment!=null){
            patientName.setText(appointment.getPatientName());
            doctorName.setText(appointment.getDoctorName());
            hospitalName.setText(appointment.getHospitalName());
            textSymptoms.setText(appointment.getDisease());
            date.setText(appointment.getAppointmentDate());
            textViewAge.setText(appointment.getAge());
            time.setText(appointment.getAppointmentTime());
            textViewAppointmentId.setText(getString(R.string.appointment_id)+" "+appointment.getAppointmentId().getId());
            if (appointment.getSerialNumber()!=null && !appointment.getSerialNumber().isEmpty())
                serialNo.setText(getString(R.string.serial_number)+" "+appointment.getSerialNumber());
            else 
                serialNo.setText(getString(R.string.serial_number_not_assigned));
            
            setStatus(appointment.getStatus());
        }
    }

    private void setStatus(String status) {
        textViewStatus.setVisibility(View.VISIBLE);
        if (status.equals(getString(R.string.accepted)) || appointment.getStatus().equals(getString(R.string.completed))){
            textViewStatus.setText(appointment.getStatus());
            textViewStatus.setTextColor(Color.WHITE);
            textViewStatus.setBackgroundResource(R.drawable.accepted_backgrounded);
        }
        if (status.equals(getString(R.string.rejected))){
            textViewStatus.setText(appointment.getStatus());
            textViewStatus.setTextColor(Color.WHITE);
            textViewStatus.setBackgroundResource(R.drawable.rejected_backgrounded);
        }
        if (status.equals(getString(R.string.pending)) || appointment.getStatus().equals(getString(R.string.not_attempted))){
            textViewStatus.setText(appointment.getStatus());
            textViewStatus.setTextColor(Color.BLACK);
            textViewStatus.setBackgroundResource(R.drawable.pending_backgrounded);
        }
    }

    private void setUpToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Appointment Details");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
