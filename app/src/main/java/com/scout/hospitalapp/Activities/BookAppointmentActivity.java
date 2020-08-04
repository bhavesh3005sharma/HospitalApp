package com.scout.hospitalapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.scout.hospitalapp.Models.ModelBookAppointment;
import com.scout.hospitalapp.Models.ModelDateTime;
import com.scout.hospitalapp.Models.ModelIntent;
import com.scout.hospitalapp.Models.ModelUnAvailableDates;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.BookAppointmentsViewModel;
import com.scout.hospitalapp.ViewModels.ProfileActivityViewModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookAppointmentActivity extends AppCompatActivity implements View.OnClickListener , DatePickerDialog.OnDateSetListener, ChipGroup.OnCheckedChangeListener{
    @BindView(R.id.textInputPatientName) TextInputLayout textInputPatientName;
    @BindView(R.id.cardDoctorInfo) CardView cardDoctorInfo;
    @BindView(R.id.text_doctor_name) TextView textInputDoctorName;
    @BindView(R.id.text_specialization) TextView textSpecialisation;
    @BindView(R.id.textPhoneNo) TextView textPhoneNo;
    @BindView(R.id.textInputDisease) TextInputLayout textInputDisease;
    @BindView(R.id.textInputAge) TextInputLayout textInputAge;
    @BindView(R.id.textViewSelectDate) TextView textViewSelectDate;
    @BindView(R.id.textViewInfoSelectDate) TextView textViewInfoSelectDate;
    @BindView(R.id.buttonBookAppointment) Button buttonBookAppointment;
    @BindView(R.id.select_doctor) Button selectDoctor;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.choice_chip_group) ChipGroup chipGroup;

    Unbinder unbinder;
    int check=0;
    ModelIntent modelIntent;
    String selectedTime = null;
    BookAppointmentsViewModel viewModel;
    com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog;
    ArrayList<ModelDateTime> partiallyUnavailableDates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        unbinder = ButterKnife.bind(this);
        viewModel = (BookAppointmentsViewModel) ViewModelProviders.of(BookAppointmentActivity.this).get(BookAppointmentsViewModel.class);

        getSupportActionBar().setTitle("Book Appointment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        modelIntent = (ModelIntent) getIntent().getSerializableExtra("modelIntent");
        if (modelIntent==null)
            modelIntent = new ModelIntent();
        setUpUi();
        chipGroup.setOnCheckedChangeListener(this);

        buttonBookAppointment.setOnClickListener(this);
        textViewSelectDate.setOnClickListener(this);
        selectDoctor.setOnClickListener(this);
    }

    private void onDoctorSelected() {
        String patientName1 = textInputPatientName.getEditText().getText().toString().trim();
        String disease1 = textInputDisease.getEditText().getText().toString().trim();
        String age1 = textInputAge.getEditText().getText().toString().trim();

        if (isValidData(patientName1,disease1,age1)){
            ModelBookAppointment modelBookAppointment = new ModelBookAppointment(patientName1,disease1,age1);

            modelIntent.setBookAppointmentData(modelBookAppointment);
            startActivity(new Intent(BookAppointmentActivity.this, DoctorsActivity.class).putExtra("modelIntent",modelIntent));
            finish();
        }
    }

    public void openDatePicker(){
        Calendar now = Calendar.getInstance();
         datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                BookAppointmentActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
         HelperClass.showProgressbar(progressBar);
         viewModel.getAppointmentDates(BookAppointmentActivity.this, modelIntent.getDoctorProfileInfo());
         viewModel.getUnavailableDates().observe(this, new Observer<ModelUnAvailableDates>() {
             @Override
             public void onChanged(ModelUnAvailableDates modelUnAvailableDates) {
                 Calendar now = Calendar.getInstance();
                 datePickerDialog.setSelectableDays(modelUnAvailableDates.getAvailableDates());
                 datePickerDialog.setDisabledDays(modelUnAvailableDates.getCompletelySlotUnavailableDates());
                 datePickerDialog.setMinDate(now);
                 now.add(Calendar.MONTH,2);
                 datePickerDialog.setMaxDate(now);

                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                     datePickerDialog.setAccentColor(getColor(R.color.colorPrimary));
                 }
                 datePickerDialog.setOkColor(Color.WHITE);
                 datePickerDialog.setCancelColor(Color.WHITE);
                 datePickerDialog.show(getSupportFragmentManager(),"DATE_PICKER");
                 HelperClass.hideProgressbar(progressBar);

                 partiallyUnavailableDates.clear();
                 partiallyUnavailableDates.addAll(modelUnAvailableDates.getPartiallyUnavailableDates());
                 HelperClass.hideProgressbar(progressBar);
             }
         });
    }

    private void setUpUi() {
        if (modelIntent!=null && modelIntent.getBookAppointmentData()!=null) {
            textInputPatientName.getEditText().setText(modelIntent.getBookAppointmentData().getPatientName());
            textInputDisease.getEditText().setText(modelIntent.getBookAppointmentData().getDisease());
            textInputAge.getEditText().setText(modelIntent.getBookAppointmentData().getAge());
            if (modelIntent.getBookAppointmentData().getAppointmentDate()!=null)
                textViewSelectDate.setText(modelIntent.getBookAppointmentData().getAppointmentDate());
        }

        if(modelIntent!=null && modelIntent.getDoctorProfileInfo()!=null){
            cardDoctorInfo.setVisibility(View.VISIBLE);
            textViewSelectDate.setVisibility(View.VISIBLE);
            textViewInfoSelectDate.setVisibility(View.VISIBLE);
            textViewSelectDate.setText(getString(R.string.select_date));
            selectDoctor.setText(getString(R.string.change_doctor));
            textInputDoctorName.setText(modelIntent.getDoctorProfileInfo().getName());
            textSpecialisation.setText(modelIntent.getDoctorProfileInfo().getDepartment());
            textPhoneNo.setText(modelIntent.getDoctorProfileInfo().getPhone_no());
        }else {
            cardDoctorInfo.setVisibility(View.GONE);
            selectDoctor.setText(getString(R.string.select_doctor));
            textViewSelectDate.setVisibility(View.GONE);
            textViewInfoSelectDate.setVisibility(View.GONE);
            chipGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBookAppointment :
                String patientName = textInputPatientName.getEditText().getText().toString().trim();
                String doctorName = textInputDoctorName.getText().toString().trim();
                String disease = textInputDisease.getEditText().getText().toString().trim();
                String age = textInputAge.getEditText().getText().toString().trim();
                String date = textViewSelectDate.getText().toString().trim();

                if (isValidData(patientName,disease,age)){
                    if(modelIntent.getDoctorProfileInfo()==null){
                        HelperClass.toast(this, "Select Doctor for Appointment.");
                        return;
                    }
                    if (date.equals(getString(R.string.select_date)) || selectedTime==null) {
                        HelperClass.toast(this, "Select Date and Time.");
                        return;
                    }
                    HelperClass.showProgressbar(progressBar);
                    String doctorId = modelIntent.getDoctorProfileInfo().getDoctorId().getId();
                    String hospitalId = SharedPref.getLoginUserData(this).getHospitalId().getId();
                    String hospitalName = modelIntent.getDoctorProfileInfo().getHospitalName();
                    String avgCheckupTime = modelIntent.getDoctorProfileInfo().getAvgCheckupTime();
                    long thresholdLimit = viewModel.getThresholdLimit(selectedTime,avgCheckupTime);

                    ModelBookAppointment appointment = new ModelBookAppointment(patientName, doctorName, hospitalName, disease, age, date,
                            getString(R.string.accepted), "", doctorId, hospitalId,selectedTime,thresholdLimit);

                    viewModel.bookAppointment(appointment).observe(this, new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            HelperClass.toast(BookAppointmentActivity.this,s);
                            HelperClass.hideProgressbar(progressBar);
                        }
                    });
                }
                break;

            case R.id.textViewSelectDate :
                openDatePicker();
                break;

            case R.id.select_doctor :
                onDoctorSelected();
                break;
        }
    }

    private boolean isValidData(String patientName, String disease, String age) {
        if (patientName.isEmpty()) {
            textInputPatientName.setError("Name is Mandatory");
            textInputPatientName.requestFocus();
            return false;
        }else textInputPatientName.setError(null);

        if (disease.isEmpty()) {
            textInputDisease.setError("Please Specify Problem");
            textInputDisease.requestFocus();
            return false;
        }else textInputDisease.setError(null);

        if (age.isEmpty()) {
            textInputAge.setError("Age is Mandatory");
            textInputAge.requestFocus();
            return false;
        }
        else if (Integer.parseInt(age)>120) {
            textInputAge.setHelperText("Please Provide Valid Age.");
            textInputAge.requestFocus();
            return false;
        } else textInputAge.setError(null);
        return true;
    }

    private void setTime(String date) {
        ArrayList CompleteTime = modelIntent.getDoctorProfileInfo().getDoctorAvailabilityTime();
        ArrayList AvailableTimes = new ArrayList();
        if (partiallyUnavailableDates.size()==0)
            AvailableTimes.addAll(CompleteTime);
        for(ModelDateTime dateTime : partiallyUnavailableDates){
            if(dateTime.getDate().equals(date)){
                ArrayList unAvailableTimes = dateTime.getUnavailableTimes();
                for(int i=0;i<CompleteTime.size();i++){
                    for(int j=0;j<unAvailableTimes.size();j++){
                        if(CompleteTime.get(i).equals(unAvailableTimes.get(j)))
                            break;
                        if(j==unAvailableTimes.size()-1)
                            AvailableTimes.add(CompleteTime.get(i));
                    }
                }
            }
        }
        if (AvailableTimes.size()==0)
            setChipGroup(CompleteTime);
        else
            setChipGroup(AvailableTimes);
    }

    private void setChipGroup(ArrayList<String> timeList) {
        chipGroup.setVisibility(View.VISIBLE);
        chipGroup.removeAllViews();

        for (String time : timeList){
            Chip chip = new Chip(BookAppointmentActivity.this);
            chip.setText(time);
            chip.setCheckedIconResource(R.drawable.ic_check);
            chip.setChipBackgroundColorResource(R.color.colorPrimary);
            chip.setTextColor(Color.WHITE);
            chip.setCheckable(true);
            chipGroup.addView(chip);
        }
    }

    @Override
    public void onCheckedChanged(ChipGroup group, int checkedId) {
        if (checkedId==-1) {
            selectedTime = null;
            return;
        }
        Chip chip = findViewById(checkedId);
        selectedTime = chip.getText().toString().trim();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {
        month++;
        String dayOfMonth = ""+day;
        String monthOfYear = ""+month;
        if (dayOfMonth.length() == 1)
            dayOfMonth = "0" + dayOfMonth;

        if (monthOfYear.length()==1)
            monthOfYear = "0"+ month;
        String date = dayOfMonth+"-"+monthOfYear+"-"+year;
        textViewSelectDate.setText(date);
        buttonBookAppointment.setVisibility(View.VISIBLE);

        setTime(date);
    }
}
